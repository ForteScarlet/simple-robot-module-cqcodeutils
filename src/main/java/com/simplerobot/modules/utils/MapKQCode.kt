// /*
//  * Copyright (c) 2020. ForteScarlet All rights reserved.
//  * Project  mod-cqcodeutils
//  * File     mapKQCode.kt
//  *
//  * You can contact the author through the following channels:
//  * github https://github.com/ForteScarlet
//  * gitee  https://gitee.com/ForteScarlet
//  * email  ForteScarlet@163.com
//  * QQ     1149159218
//  *
//  */
// package com.simplerobot.modules.utils
//
// /*
//  * Created by lcy on 2020/8/21.
//  * @author lcy
//  */
//
// /* ******************************************************
//  *
//  *  kq code by map
//  *  基于[Map]作为载体的[KQCode]实例
//  *
//  *******************************************************/
//
// /**
//  * CQ码封装类, 以[Map]作为参数载体
//  * @since 1.0-1.11
//  */
// open class MapKQCode
// protected constructor(open val params: Map<String, String>, override var type: String) :
//         KQCode,
//         Map<String, String> by params {
//     constructor(type: String) : this(emptyMap(), type)
//     constructor(type: String, params: Map<String, String>) : this(params.toMap(), type)
//     constructor(type: String, vararg params: Pair<String, String>) : this(mapOf(*params), type)
//     constructor(type: String, vararg params: String) : this(mapOf(*params.map {
//         val split = it.split(Regex("="), 2)
//         split[0] to split[1]
//     }.toTypedArray()), type)
//
//     /** internal constructor for mutable kqCode */
//     internal constructor(mutableKQCode: MutableKQCode) : this(mutableKQCode.toMap(), mutableKQCode.type)
//
//     companion object Of {
//         /**
//          * 根据CQ码字符串获取[MapKQCode]实例
//          */
//         @JvmStatic
//         @JvmOverloads
//         fun byCode(code: String, decode: Boolean = true): MapKQCode {
//             var tempText = code.trim()
//             // 不是[CQ:开头，或者不是]结尾都不行
//             if (!tempText.startsWith("[CQ:") || !tempText.endsWith("]")) {
//                 throw IllegalArgumentException("not starts with '[CQ:' or not ends with ']'")
//             }
//             // 是[CQ:开头，]结尾，切割并转化
//             tempText = tempText.substring(4, tempText.lastIndex)
//
//             val split = tempText.split(Regex(" *, *"))
//
//             val type = split[0]
//
//             return if (split.size > 1) {
//                 if (decode) {
//                     // 参数解码
//                     val map = split.subList(1, split.size).map {
//                         val sp = it.split(Regex("="), 2)
//                         sp[0] to (CQDecoder.decodeParams(sp[1]) ?: "")
//                     }.toMap()
//                     MapKQCode(map, type)
//                 } else {
//                     MapKQCode(type, *split.subList(1, split.size).toTypedArray())
//                 }
//             } else {
//                 MapKQCode(type)
//             }
//         }
//     }
//
//     /**
//      * Returns the length of this character sequence.
//      */
//     override val length: Int
//         get() = toString().length
//
//
//     /**
//      * 获取转义后的字符串
//      */
//     override fun getNoDecode(key: String) = CQEncoder.encodeParams(this[key])
//
//     /**
//      * Returns the character at the specified [index] in this character sequence.
//      * @throws [IndexOutOfBoundsException] if the [index] is out of bounds of this character sequence.
//      * Note that the [String] implementation of this interface in Kotlin/JS has unspecified behavior
//      * if the [index] is out of its bounds.
//      */
//     override fun get(index: Int): Char = toString()[index]
//
//     /**
//      * Returns a new character sequence that is a subsequence of this character sequence,
//      * starting at the specified [startIndex] and ending right before the specified [endIndex].
//      *
//      * @param startIndex the start index (inclusive).
//      * @param endIndex the end index (exclusive).
//      */
//     override fun subSequence(startIndex: Int, endIndex: Int): CharSequence = toString().subSequence(startIndex, endIndex)
//
//     /** toString */
//     override fun toString(): String = KQCodeUtils.toCq(type, this)
//
//
//     /** 从KQCode转到CQCode */
//     override fun toCQCode(): com.forte.qqrobot.beans.cqcode.CQCode = com.forte.qqrobot.beans.cqcode.CQCode.of(type, mutableMapOf(*params.entries.map { it.key to CQEncoder.encodeParams(it.value) }.toTypedArray()))
//
//
//     /**
//      * 转化为参数可变的[MutableKQCode]
//      */
//     override fun mutable(): MutableKQCode = MutableMapKQCode(this)
//
//     /**
//      * 转化为不可变类型[KQCode]
//      */
//     override fun immutable(): KQCode = this
//
//
//     override fun equals(other: Any?): Boolean {
//         if (this === other) return true
//         if (javaClass != other?.javaClass) return false
//
//         other as MapKQCode
//
//         if (params != other.params) return false
//         if (type != other.type) return false
//
//         return true
//     }
//
//     override fun hashCode(): Int {
//         var result = params.hashCode()
//         result = 31 * result + type.hashCode()
//         return result
//     }
// }
//
// /**
//  * [KQCode]对应的可变类型, 以[MutableMap]作为载体
//  */
// @Suppress("DELEGATED_MEMBER_HIDES_SUPERTYPE_OVERRIDE")
// open class MutableMapKQCode
// protected constructor(override val params: MutableMap<String, String>, type: String) :
//         MapKQCode(params, type),
//         MutableKQCode,
//         MutableMap<String, String> by params {
//     constructor(type: String) : this(mutableMapOf(), type)
//     constructor(type: String, params: Map<String, String>) : this(params.toMutableMap(), type)
//     constructor(type: String, vararg params: Pair<String, String>) : this(mutableMapOf(*params), type)
//     constructor(type: String, vararg params: String) : this(mutableMapOf(*params.map {
//         val split = it.split(Regex("="), 2)
//         split[0] to split[1]
//     }.toTypedArray()), type)
//
//     /** internal constructor for kqCode */
//     internal constructor(kqCode: KQCode) : this(kqCode.toMutableMap(), kqCode.type)
//
//     /**
//      * 转化为参数可变的[MutableKQCode]
//      */
//     override fun mutable(): MutableKQCode = this
//
//     /**
//      * 转化为不可变类型[KQCode]
//      */
//     override fun immutable(): KQCode = MapKQCode(this)
// }
