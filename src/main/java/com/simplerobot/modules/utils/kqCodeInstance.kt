/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  mod-cqcodeutils
 * File     KQCode.kt
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


/* ******************************************************
 *
 *  kq code by map
 *  基于[Map]作为载体的[KQCode]实例
 *
 *******************************************************/

/**
 * CQ码封装类, 以[Map]作为参数载体
 * @since 1.0-1.11
 */
open class MapKQCode
internal constructor(open val params: Map<String, String>, override var type: String) :
        KQCode,
        Map<String, String> by params {
    constructor(type: String) : this(emptyMap(), type)
    constructor(type: String, params: Map<String, String>) : this(params.toMap(), type)
    constructor(type: String, vararg params: Pair<String, String>) : this(mapOf(*params), type)
    constructor(type: String, vararg params: String) : this(mapOf(*params.map {
        val split = it.split(Regex("="), 2)
        split[0] to split[1]
    }.toTypedArray()), type)

    /** internal constructor for mutable kqCode */
    internal constructor(mutableKQCode: MutableKQCode) : this(mutableKQCode.toMap(), mutableKQCode.type)

    /**
     * Returns the length of this character sequence.
     */
    override val length: Int
        get() = toString().length


    /**
     * 获取转义后的字符串
     */
    override fun getNoDecode(key: String) = CQEncoder.encodeParams(this[key])

    /**
     * Returns the character at the specified [index] in this character sequence.
     * @throws [IndexOutOfBoundsException] if the [index] is out of bounds of this character sequence.
     * Note that the [String] implementation of this interface in Kotlin/JS has unspecified behavior
     * if the [index] is out of its bounds.
     */
    override fun get(index: Int): Char = toString()[index]

    /**
     * Returns a new character sequence that is a subsequence of this character sequence,
     * starting at the specified [startIndex] and ending right before the specified [endIndex].
     *
     * @param startIndex the start index (inclusive).
     * @param endIndex the end index (exclusive).
     */
    override fun subSequence(startIndex: Int, endIndex: Int): CharSequence = toString().subSequence(startIndex, endIndex)

    /** toString */
    override fun toString(): String = KQCodeUtils.toCq(type, this)


    /** 从KQCode转到CQCode */
    override fun toCQCode(): com.forte.qqrobot.beans.cqcode.CQCode = com.forte.qqrobot.beans.cqcode.CQCode.of(type, mutableMapOf(*params.entries.map { it.key to CQEncoder.encodeParams(it.value) }.toTypedArray()))


    /**
     * 转化为参数可变的[MutableKQCode]
     */
    override open fun mutable(): MutableKQCode = MutableMapKQCode(this)

    /**
     * 转化为不可变类型[KQCode]
     */
    override open fun immutable(): KQCode = this


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MapKQCode

        if (params != other.params) return false
        if (type != other.type) return false

        return true
    }

    override fun hashCode(): Int {
        var result = params.hashCode()
        result = 31 * result + type.hashCode()
        return result
    }

    companion object {
        private val paramSplitRegex: Regex = Regex(" *, *")
        private val paramKeyValueSplitRegex: Regex = Regex("=")

        /**
         * 从cq码字符串转到KQCode
         * @since 1.1-1.11
         * infix since 1.2-1.11
         * @param text CQ码字符串的正文
         * @param decode 因为这段CQ码字符串可能已经转义过了，此处是否指定其转化的时候解码一次。默认为true
         */
        @JvmStatic
        @JvmOverloads
        fun of(text: String, decode: Boolean = true): KQCode {
            var tempText = text.trim()
            // 不是[CQ:开头，或者不是]结尾都不行
            if(!tempText.startsWith("[CQ:") || !tempText.endsWith("]")){
                throw IllegalArgumentException("not starts with '[CQ:' or not ends with ']'")
            }
            // 是[CQ:开头，]结尾，切割并转化
            tempText = tempText.substring(4, tempText.lastIndex)

            val split = tempText.split(paramSplitRegex)

            val type = split[0]

            return if(split.size > 1){
                if(decode){
                    // 参数解码
                    val map = split.subList(1, split.size).map {
                        val sp = it.split(paramKeyValueSplitRegex, 2)
                        sp[0] to (CQDecoder.decodeParams(sp[1]) ?: "")
                    }.toMap()
                    MapKQCode(map, type)
                }else{
                    MapKQCode(type, *split.subList(1, split.size).toTypedArray())
                }
            }else{
                MapKQCode(type)
            }

        }

        /**
         * 从CQCode转到KQCode
         * @since 1.0-1.11
         * infix since 1.2-1.11
         */
        @JvmStatic
        infix fun of(cqCode: com.forte.qqrobot.beans.cqcode.CQCode): KQCode = MapKQCode(cqCode.cqCodeTypesName, cqCode)
    }

}

/**
 * [KQCode]对应的可变类型, 以[MutableMap]作为载体
 */
@Suppress("DELEGATED_MEMBER_HIDES_SUPERTYPE_OVERRIDE")
open class MutableMapKQCode
protected constructor(override val params: MutableMap<String, String>, type: String) :
        MapKQCode(params, type),
        MutableKQCode,
        MutableMap<String, String> by params {
    constructor(type: String) : this(mutableMapOf(), type)
    constructor(type: String, params: Map<String, String>) : this(params.toMutableMap(), type)
    constructor(type: String, vararg params: Pair<String, String>) : this(mutableMapOf(*params), type)
    constructor(type: String, vararg params: String) : this(mutableMapOf(*params.map {
        val split = it.split(Regex("="), 2)
        split[0] to split[1]
    }.toTypedArray()), type)

    /** internal constructor for kqCode */
    internal constructor(kqCode: KQCode) : this(kqCode.toMutableMap(), kqCode.type)

    /**
     * 转化为参数可变的[MutableKQCode]
     */
    override fun mutable(): MutableKQCode = this

    /**
     * 转化为不可变类型[KQCode]
     */
    override fun immutable(): KQCode = MapKQCode(this)
}


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
open class StringKQCode(code: String): KQCode {
    protected val codeText: String = code.trim()

    private val empty: Boolean
    override val type: String
    private val cqHead: String
    private val startIndex: Int
    private val endIndex: Int

    init {
        if (!codeText.startsWith(CQ_HEAD) || !codeText.endsWith(CQ_END)) {
            throw IllegalArgumentException("text \"$codeText\" is not a cq code text.")
        }
        // get type from string
        startIndex = CQ_HEAD.length
        endIndex = codeText.lastIndex
        val firstSplitIndex = codeText.indexOf(CQ_SPLIT, startIndex)
        val typeEndIndex = if (firstSplitIndex < 0) codeText.length else firstSplitIndex
        type = codeText.substring(startIndex, firstSplitIndex)
        cqHead = CQ_HEAD + type
        empty = codeText.contains(CQ_SPLIT)
    }

    override fun toString(): String = codeText
    override val length: Int = codeText.length


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
     * Returns a read-only [Set] of all key/value pairs in this map.
     */
    override val entries: Set<Map.Entry<String, String>>
        get() = TODO("Not yet implemented")

    /**
     * Returns a read-only [Set] of all keys in this map.
     */
    override val keys: Set<String>
        get() = TODO("Not yet implemented")

    /**
     * Returns the number of key/value pairs in the map.
     */
    override val size: Int
        get() = TODO("Not yet implemented")

    /**
     * Returns a read-only [Collection] of all values in this map. Note that this collection may contain duplicate values.
     */
    override val values: Collection<String>
        get() = TODO("Not yet implemented")

    /**
     * Returns `true` if the map contains the specified [key].
     */
    override fun containsKey(key: String): Boolean {
        TODO("Not yet implemented")
    }

    /**
     * Returns `true` if the map maps one or more keys to the specified [value].
     */
    override fun containsValue(value: String): Boolean {
        TODO("Not yet implemented")
    }

    /**
     * 获取一个解码后的值
     */
    override fun get(key: String): String? = CQDecoder.decodeParams(getParam(key))

    /**
     * 获取指定字符
     * @see CharSequence.get
     */
    override fun get(index: Int): Char = codeText[index]

    /**
     * 如果不存在[CQ_SPLIT]，即参数切割符，则说明不存在参数
     */
    override fun isEmpty(): Boolean = empty

    /**
     * Returns a new character sequence that is a subsequence of this character sequence,
     * starting at the specified [startIndex] and ending right before the specified [endIndex].
     *
     * @param startIndex the start index (inclusive).
     * @param endIndex the end index (exclusive).
     */
    override fun subSequence(startIndex: Int, endIndex: Int): CharSequence = codeText.subSequence(startIndex, endIndex)

    /**
     * 获取参数
     * 得到的值不是反转义的值。如果需要，再转义
     */
    private fun getParam(key: String): String? {
//        var from = -1
//        var end = -1
//        var i = -1
//        do {
//            from = kqCodeText.indexOf(cqHead, from + 1)
//            if (from >= 0) {
//                // 寻找结尾
//                end = kqCodeText.indexOf(CQ_END, from)
//                if (end >= 0) {
//                    i++
//                }
//            }
//        } while (from >= 0 && i < index)

        // 索引对上了
//        if (i == index) {
        // 从from开始找参数
        val paramFind = ",$key="
        val phi: Int = codeText.indexOf(paramFind, startIndex)
        if (phi < 0) {
            return null
        }
        // 找到了之后，找下一个逗号，如果没有，就用最终结尾的位置
        val startIndex = phi + paramFind.length
        var pei = codeText.indexOf(CQ_SPLIT, startIndex)
        // 超出去了
        if (pei < 0 || pei > endIndex) {
            pei = endIndex
        }
        if (startIndex > codeText.lastIndex || startIndex > pei) {
            return null
        }
        return codeText.substring(startIndex, pei)
//        } else {
//            return null
//        }
    }




}



