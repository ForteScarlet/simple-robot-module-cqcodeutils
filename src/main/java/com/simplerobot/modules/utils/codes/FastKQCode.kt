/*
 *
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  simple-robot-module-cqcodeutils
 *  File     FastKQCode.kt
 *  data     2020-08-30
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 *
 *
 */

package com.simplerobot.modules.utils.codes

import com.simplerobot.modules.utils.*
import com.simplerobot.modules.utils.CqParamEntryIterator
import com.simplerobot.modules.utils.CqParamKeyIterator
import com.simplerobot.modules.utils.CqParamValueIterator


/* ******************************************************
 *
 *  kq code by string split
 *  基于[KQCodeUtils]的字符串操作的[KQCode]实例
 *
 *******************************************************/

/**
 * 基于字符串操作的[KQCode]实例
 *
 * [FastKQCode]没有对应以字符串操作为主的[MutableKQCode], 因此他的[mutable]方法将会使用[MutableMapKQCode]
 *
 * 相比于[MapKQCode], [FastKQCode]在实例化与较短CQ码处理的消耗会更低，
 * 但是无论如何[FastKQCode.get]的速度也永远比不上hash表的速度。
 * 但是对于直接通过字符串构建一个[KQCode]来讲，[FastKQCode]无疑是消耗更低的选择。
 *
 * [FastKQCode]构建速度更快、资源消耗更少, 获取静态参数(例如[toString]、[size])的速度更快。
 * [FastKQCode]的构建、获取静态参数速度相比较于[MapKQCode]有着几百倍的差距。
 *
 * 但是在获取、迭代与遍历时相比较于[MapKQCode]略逊一筹。
 *
 *
 */
open class FastKQCode private constructor(private val code: String) : KQCode {
    private val _type: String
    private val _size: Int

    private val empty: Boolean
    private val cqHead: String
    private val startIndex: Int
    private val endIndex: Int

    init {
        if (!this.code.startsWith(CQ_HEAD) || !this.code.endsWith(CQ_END)) {
            throw IllegalArgumentException("text \"${this.code}\" is not a cq code text.")
        }
        // get type from string
        startIndex = CQ_HEAD.length
        endIndex = this.code.lastIndex
        val firstSplitIndex: Int = this.code.indexOf(CQ_SPLIT, startIndex).let {
            if(it < 0) endIndex else it
        }
        // val typeEndIndex = if (firstSplitIndex < 0) _codeText.length else firstSplitIndex
        _type = this.code.substring(startIndex, firstSplitIndex)
        cqHead = CQ_HEAD + _type
        empty = this.code.contains(CQ_SPLIT)
        // 计算 key-value的个数, 即计算CQ_KV的个数
        val kvChar: Char = CQ_KV.first()
        _size = this.code.count { it == kvChar }
    }

    private val codeText: String = this.code
    override fun toString(): String = this.code
    override val length: Int = this.code.length
    override val size: Int = _size
    override val type: String = _type


    /**
     * 获取转义前的值。一般普通的[get]方法得到的是反转义后的。
     * 此处为保留原本的值不做转义。
     */
    override fun getNoDecode(key: String): String? = getParam(key)

    /**
     * 从[KQCode]转化为[com.forte.qqrobot.beans.cqcode.CQCode]
     */
    @Suppress("OverridingDeprecatedMember")
    override fun toCQCode(): com.forte.qqrobot.beans.cqcode.CQCode = com.forte.qqrobot.beans.cqcode.CQCode.of(codeText)

    /**
     * 转化为可变参的[MutableKQCode]
     */
    override fun mutable(): MutableKQCode = MapKQCode.mutableByCode(this.toString())

    /**
     * 转化为不可变类型[KQCode]
     */
    override fun immutable(): KQCode = this

    /**
     * 查询cq码字符串中是否存在指定的key
     */
    override fun containsKey(key: String): Boolean {
        if (empty) return false
        return codeText.contains("$CQ_SPLIT$key$CQ_KV")
    }

    /**
     * 查询cq码字符串中是否存在对应的值。
     * 经过转义后进行字符串查询
     */
    override fun containsValue(value: String): Boolean {
        if (empty) return false
        val encodeValue: String = CQEncoder.encodeParams(value)
        return codeText.contains("$CQ_KV$encodeValue$CQ_SPLIT") || codeText.contains("$CQ_KV$encodeValue$CQ_END")
    }

    /**
     * 获取一个解码后的值
     */
    override operator fun get(key: String): String? = CQDecoder.decodeParamsOrNull(getParam(key))

    /**
     * 获取指定字符
     * @see CharSequence.get
     */
    override operator fun get(index: Int): Char = codeText[index]

    /**
     * 如果不存在[CQ_SPLIT]，即参数切割符，则说明不存在参数
     */
    override fun isEmpty(): Boolean = empty

    /**
     * Returns a new character sequence that is a subsequence of this character sequence,
     * starting at the specified [startIndex] and ending right before the specified [endIndex].
     *
     * @see CharSequence.subSequence
     * @param startIndex the start index (inclusive).
     * @param endIndex the end index (exclusive).
     */
    override fun subSequence(startIndex: Int, endIndex: Int): CharSequence = codeText.subSequence(startIndex, endIndex)


    /**
     * 缓存上一次的查询结果
     * 线程不安全的
     */
    private var paramBuffer: Pair<String, String>? = null

    /**
     * 获取参数
     * 得到的值不是反转义的值。如果需要，再转义
     * 参考于[KQCodeUtils.getParam]
     * @see KQCodeUtils.getParam
     */
    private fun getParam(key: String): String? {
        val bufferFirst = paramBuffer?.first
        val bufferSecond = paramBuffer?.second
        if(bufferFirst != null && bufferFirst == key){
            return bufferSecond
        }
        val paramFind = "$CQ_SPLIT$key$CQ_KV"
        val phi: Int = codeText.indexOf(paramFind, startIndex)
        if (phi < 0) {
            return null
        }
        val startIndex: Int = phi + paramFind.length
        var pei: Int = codeText.indexOf(CQ_SPLIT, startIndex)
        if (pei < 0 || pei > endIndex) {
            pei = endIndex
        }
        if (startIndex > codeText.lastIndex || startIndex > pei) {
            return null
        }
        val subParam = codeText.substring(startIndex, pei)
        paramBuffer = key to subParam
        return subParam
    }

    /**
     * Returns a read-only [Set] of all key/value pairs in this map.
     */
    override val entries: Set<Map.Entry<String, String>>
        get() = FastKqEntrySet()


    /**
     * [FastKQCode] 的 set内联类
     */
    private inner class FastKqEntrySet : Set<Map.Entry<String, String>> {
        /** 键值对的长度 */
        override val size: Int get() = _size

        /**
         * 查看是否包含了某个键值对
         */
        override fun contains(element: Map.Entry<String, String>): Boolean {
            if (empty) return false

            val kv = element.key + CQ_KV + CQEncoder.encodeParams(element.value)
            return this@FastKQCode.code.contains(kv)
        }

        /**
         * 查看是否包含所有的键值对
         */
        override fun containsAll(elements: Collection<Map.Entry<String, String>>): Boolean {
            if (empty) return false

            for (element in elements) {
                if (!contains(element)) return false
            }
            return true
        }

        /**
         * 是否为空set
         */
        override fun isEmpty(): Boolean = this@FastKQCode.empty

        /**
         * 键值对迭代器
         */
        override fun iterator(): Iterator<Map.Entry<String, String>> = CqParamEntryIterator(this@FastKQCode.code)
    }


    /**
     * Returns a read-only [Set] of all keys in this map.
     */
    override val keys: Set<String>
        get() = FastKqKeySet()


    /**
     * [keys]的实现内部类
     */
    private inner class FastKqKeySet : Set<String> {
        override val size: Int get() = _size

        /**
         * 是否包含某个key
         */
        override fun contains(element: String): Boolean {
            if (empty) return false
            // 判断是否包含：element= 这个字符串
            return this@FastKQCode.code.contains("$element$CQ_KV")
        }

        /**
         * 是否contains all
         */
        override fun containsAll(elements: Collection<String>): Boolean {
            if (empty) return false
            for (element in elements) {
                if (!contains(element)) return false
            }
            return true
        }

        override fun isEmpty(): Boolean = this@FastKQCode.empty


        override fun iterator(): Iterator<String> {
            return CqParamKeyIterator(this@FastKQCode.code)
        }

    }


    /**
     * Returns a read-only [Collection] of all values in this map. Note that this collection may contain duplicate values.
     */
    override val values: Collection<String>
        get() = FastKqValues()


    /**
     * [values]的实现。
     * 不是任何[List]
     */
    private inner class FastKqValues : Collection<String> {
        /**
         * Returns the size of the collection.
         */
        override val size: Int get() = _size

        /**
         * Checks if the specified element is contained in this collection.
         */
        override fun contains(element: String): Boolean {
            if (empty) return false
            // 判断有没有 '=element' 字符串
            return this@FastKQCode.code.contains("$CQ_KV${CQEncoder.encodeParams(element)}")
        }

        /**
         * Checks if all elements in the specified collection are contained in this collection.
         */
        override fun containsAll(elements: Collection<String>): Boolean {
            if (empty) return false

            for (element in elements) {
                if (!contains(element)) return false
            }
            return true
        }

        /**
         * Returns `true` if the collection is empty (contains no elements), `false` otherwise.
         */
        override fun isEmpty(): Boolean = this@FastKQCode.empty


        /**
         * iterator
         */
        override fun iterator(): Iterator<String> {
            return CqParamValueIterator(this@FastKQCode.code)
        }
    }


    companion object Of {
        /**
         * 得到[FastKQCode]实例的工厂方法。
         * [code]应该是一个cq码字符串.
         */
        @JvmStatic
        fun byCode(code: String): FastKQCode = FastKQCode(code.trim())
    }

}





