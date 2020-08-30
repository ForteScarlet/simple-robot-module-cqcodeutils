/*
 *
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  simple-robot-module-cqcodeutils
 *  File     MapKQCode.kt
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

@file:Suppress("unused")

package com.simplerobot.modules.utils.codes

import com.simplerobot.modules.utils.*


/* ******************************************************
 *
 *  kq code by map
 *  基于[Map]作为载体的[KQCode]实例
 *
 *******************************************************/

private val MAP_SPLIT_REGEX = Regex("=")

/**
 * CQ码封装类, 以[Map]作为参数载体
 *
 * [MapKQCode]通过[Map]保存各项参数，其对应的[可变类型][MutableKQCode]实例为[MutableMapKQCode],
 * 通过[mutable]与[immutable]进行相互转化。
 *
 * 相比较于[FastKQCode], [MapKQCode]在进行获取、迭代与遍历的时候表现尤佳，
 * 尤其是参数获取相比较于[FastKQCode]的参数获取速度有好几百倍的差距。
 *
 * 但是在实例构建与静态参数获取的时候相比于[FastKQCode]略逊一筹。
 *
 * @since 1.0-1.11
 * @since 1.8.0
 */
open class MapKQCode
internal constructor(open val params: Map<String, String>, override var type: String) :
        KQCode,
        Map<String, String> by params {
    internal constructor(type: String) : this(emptyMap(), type)
    internal constructor(type: String, params: Map<String, String>) : this(params.toMap(), type)
    internal constructor(type: String, vararg params: Pair<String, String>) : this(mapOf(*params), type)
    internal constructor(type: String, vararg params: String) : this(mapOf(*params.map {
        val split = it.split(MAP_SPLIT_REGEX, 2)
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


    /**
     * toString的值记录。因为是不可变类，因此toString是不会变的
     * 在获取的时候才会去实际计算，且仅计算一次。
     */
    private val _toString: String by lazy { KQCodeUtils.toCq(type, map = this) }

    /** toString */
    override fun toString(): String = _toString



    /** 从KQCode转到CQCode */
    @Suppress("OverridingDeprecatedMember")
    override fun toCQCode(): com.forte.qqrobot.beans.cqcode.CQCode = com.forte.qqrobot.beans.cqcode.CQCode.of(type, mutableMapOf(*params.entries.map { it.key to CQEncoder.encodeParams(it.value) }.toTypedArray()))


    /**
     * 转化为参数可变的[MutableKQCode]
     */
    override fun mutable(): MutableKQCode = MutableMapKQCode(this)

    /**
     * 转化为不可变类型[KQCode]
     */
    override fun immutable(): KQCode = this


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MapKQCode

        if (params != other.params) return false
        if (type != other.type) return false

        return true
    }

    override fun hashCode(): Int {
        var result: Int = params.hashCode()
        result = 31 * result + type.hashCode()
        return result
    }

    /** [MapKQCode] companion object. */
    companion object Of {
        /** 参数切割用的正则 */
        private val TEMP_SPLIT_REGEX = Regex(" *, *")
        /**
         * 将CQ码字符串切割为参数列表
         * 返回的键值对为 `type to split`
         */
        @Suppress("NOTHING_TO_INLINE")
        private inline fun splitCode(code: String): Pair<String, List<String>> {
            var tempText = code.trim()
            // 不是[CQ:开头，或者不是]结尾都不行
            if (!tempText.startsWith("[CQ:") || !tempText.endsWith("]")) {
                throw IllegalArgumentException("not starts with '[CQ:' or not ends with ']'")
            }
            // 是[CQ:开头，]结尾，切割并转化
            tempText = tempText.substring(4, tempText.lastIndex)

            val split = tempText.split(TEMP_SPLIT_REGEX)
            val type = split[0]
            return type to split
        }

        /**
         * 根据CQ码字符串获取[MapKQCode]实例
         */
        @JvmStatic
        @JvmOverloads
        fun byCode(code: String, decode: Boolean = true): MapKQCode {
            val (type, split) = splitCode(code)

            return if (split.size > 1) {
                if (decode) {
                    // 参数解码
                    val map = split.subList(1, split.size).map {
                        val sp = it.split(Regex("="), 2)
                        sp[0] to (CQDecoder.decodeParams(sp[1]) ?: "")
                    }.toMap()
                    MapKQCode(map, type)
                } else {
                    MapKQCode(type, *split.subList(1, split.size).toTypedArray())
                }
            } else {
                MapKQCode(type)
            }
        }

        /** 通过map参数获取 */
        @JvmStatic
        fun byMap(type: String, params: Map<String, String>): MapKQCode = MapKQCode(type, params)
        /** 通过键值对获取 */
        @JvmStatic
        fun byPair(type: String, vararg params: Pair<String, String>): MapKQCode = MapKQCode(type, *params)
        /** 通过键值对字符串获取 */
        @JvmStatic
        fun byParamString(type: String, vararg params: String): MapKQCode = MapKQCode(type, *params)

        /**
         * 根据CQ码字符串获取[MapKQCode]实例
         */
        @JvmStatic
        @JvmOverloads
        fun mutableByCode(code: String, decode: Boolean = true): MutableMapKQCode {
            val (type, split) = splitCode(code)

            return if (split.size > 1) {
                if (decode) {
                    // 参数解码
                    val map: MutableMap<String, String> = split.subList(1, split.size).map {
                        val sp = it.split(Regex("="), 2)
                        sp[0] to (CQDecoder.decodeParams(sp[1]) ?: "")
                    }.toMap().toMutableMap()
                    MutableMapKQCode(map, type)
                } else {
                    MutableMapKQCode(type, *split.subList(1, split.size).toTypedArray())
                }
            } else {
                MutableMapKQCode(type)
            }
        }


        /** 通过map参数获取 */
        @JvmStatic
        fun mutableByMap(type: String, params: Map<String, String>): MutableMapKQCode = MutableMapKQCode(type, params)
        /** 通过键值对获取 */
        @JvmStatic
        fun mutableByPair(type: String, vararg params: Pair<String, String>): MutableMapKQCode = MutableMapKQCode(type, *params)
        /** 通过键值对字符串获取 */
        @JvmStatic
        fun mutableByParamString(type: String, vararg params: String): MutableMapKQCode = MutableMapKQCode(type, *params)
    }

}

/**
 * [KQCode]对应的可变类型, 以[MutableMap]作为载体
 *
 * 目前来讲唯一的[MutableKQCode]实例. 通过[MutableMap]作为参数载体需要一定程度的资源消耗，
 * 因此我认为最好应该避免频繁大量的使用[可变类型][MutableMap].
 *
 * 如果想要动态的构建一个[KQCode], 也可以试试[CodeBuilder],
 * 其中[StringCodeBuilder]则以字符串操作为主而避免了构建内部[Map]
 *
 * 但是无论如何, 都最好在构建之前便决定好参数
 *
 * @since 1.8.0
 */
@Suppress("DELEGATED_MEMBER_HIDES_SUPERTYPE_OVERRIDE")
class MutableMapKQCode
internal constructor(override val params: MutableMap<String, String>, type: String) :
        MapKQCode(params, type),
        MutableKQCode,
        MutableMap<String, String> by params {
    internal constructor(type: String) : this(mutableMapOf(), type)
    internal constructor(type: String, params: Map<String, String>) : this(params.toMutableMap(), type)
    internal constructor(type: String, vararg params: Pair<String, String>) : this(mutableMapOf(*params), type)
    internal constructor(type: String, vararg params: String) : this(mutableMapOf(*params.map {
        val split = it.split(MAP_SPLIT_REGEX, 2)
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

    /** toString */
    override fun toString(): String = KQCodeUtils.toCq(type, map = this)
}
