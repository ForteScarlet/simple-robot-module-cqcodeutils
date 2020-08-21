/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  mod-cqcodeutils
 * File     fastKQCode.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 *
 */
package com.simplerobot.modules.utils

import com.forte.qqrobot.beans.cqcode.CQCode

/*
 * Created by lcy on 2020/8/21.
 * @author lcy
 */



/* ******************************************************
 *
 *  kq code by string split
 *  基于[KQCodeUtils]的字符串操作的[KQCode]实例
 *
 *******************************************************/

/**
 * 基于[KQCodeUtils]的字符串操作的[KQCode]实例
 * 不会对cq码字符串的格式进行校验。
 */
open class FastKQCode(code: String) : KQCode {
    private val _codeText: String = code.trim()
    private val _type: String
    private val _size: Int

    private val empty: Boolean
    private val cqHead: String
    private val startIndex: Int
    private val endIndex: Int

    init {
        if (!_codeText.startsWith(CQ_HEAD) || !_codeText.endsWith(CQ_END)) {
            throw IllegalArgumentException("text \"$_codeText\" is not a cq code text.")
        }
        // get type from string
        startIndex = CQ_HEAD.length
        endIndex = _codeText.lastIndex
        val firstSplitIndex = _codeText.indexOf(CQ_SPLIT, startIndex)
        val typeEndIndex = if (firstSplitIndex < 0) _codeText.length else firstSplitIndex
        _type = _codeText.substring(startIndex, firstSplitIndex)
        cqHead = CQ_HEAD + _type
        empty = _codeText.contains(CQ_SPLIT)
        // 计算 key-value的个数, 即计算CQ_KV的个数
        val kvChar = CQ_KV.first()
        _size = _codeText.count { it == kvChar }
    }

    protected open val codeText: String = _codeText
    override fun toString(): String = _codeText
    override val length: Int = _codeText.length
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
    override fun toCQCode(): CQCode = CQCode.of(codeText)

    /**
     * 转化为可变参的[MutableKQCode]
     */
    override fun mutable(): MutableKQCode {
        TODO("Not yet implemented")
    }

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
        val encodeValue: String = CQEncoder.encodeParams(value)!!
        return codeText.contains("$CQ_KV$encodeValue$CQ_SPLIT") || codeText.contains("$CQ_KV$encodeValue$CQ_END")
    }

    /**
     * 获取一个解码后的值
     */
    override operator fun get(key: String): String? = CQDecoder.decodeParams(getParam(key))

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
     * 获取参数
     * 得到的值不是反转义的值。如果需要，再转义
     * 参考于[KQCodeUtils.getParam]
     * @see KQCodeUtils.getParam
     */
    private fun getParam(key: String): String? {
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
        return codeText.substring(startIndex, pei)
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
            return _codeText.contains(kv)
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
        override fun iterator(): Iterator<Map.Entry<String, String>> = CqParamEntryIterator(_codeText)
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
            return _codeText.contains("$element$CQ_KV")
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
            return CqParamKeyIterator(_codeText)
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
            return _codeText.contains("$CQ_KV${CQEncoder.encodeParams(element)}")
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


        override fun iterator(): Iterator<String> {
            return CqParamValueIterator(_codeText)
        }
    }


}


/**
 * [FastKQCode]的可变参数子类，通过操作字符串来控制变量
 */
class MutableStringKQCode {
    init {
        // 可变的就有点麻烦了...
        TODO()
    }
}
