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

@file:Suppress("unused")

package com.simplerobot.modules.utils

const val CQ_HEAD = "[CQ:"
const val CQ_END = "]"
const val CQ_SPLIT = ","
const val CQ_KV = "="

internal val paramSplitRegex: Regex = Regex(" *, *")
internal val paramKeyValueSplitRegex: Regex = Regex("=")

/**
 * 定义一个不可变的KQCode标准接口
 * - KQCode实例应当实现[Map]接口，使其可以作为一个**不可变**Map使用。
 * - KQCode实例应当实现[CharSequence]接口，其可以作为一个字符序列以得到CQ码字符串
 */
interface KQCode: Map<String, String>, CharSequence {
    /**
     * 获取Code的类型。例如`at`
     */
    val type: String

    /**
     * 获取转义前的值。一般普通的[get]方法得到的是反转义后的。
     * 此处为保留原本的值不做转义。
     */
    fun getNoDecode(key: String): String?

    /**
     * 从[KQCode]转化为[com.forte.qqrobot.beans.cqcode.CQCode]
     */
    fun toCQCode(): com.forte.qqrobot.beans.cqcode.CQCode

    /**
     * 与其他字符序列拼接为[Msgs]实例
     */
//    @JvmDefault
    operator fun plus(other: CharSequence): Msgs = Msgs(collection = listOf(this, other))

    /**
     * 转化为可变参的[MutableKQCode]
     */
    fun mutable(): MutableKQCode

    /**
     * 转化为不可变类型[KQCode]
     */
    fun immutable(): KQCode


    companion object Of {

        @JvmStatic
        fun of(text: String): KQCode = FastKQCode(text)

        /**
         * 从cq码字符串转到KQCode
         *
         * 1.8.0开始默认使用[FastKQCode]作为静态工厂方法的[KQCode]实例载体。
         * [FastKQCode]是以字符串操作为基础的，因此不需要进行额外的转义。
         *
         * @since 1.1-1.11
         * @since 1.8.0
         * @param text CQ码字符串的正文
         * @param decode 因为这段CQ码字符串可能已经转义过了，此处是否指定其转化的时候解码一次。默认为true
         */
        @JvmStatic
        @Deprecated("just use of(text)", ReplaceWith("FastKQCode(text)", "com.simplerobot.modules.utils.FastKQCode"))
        fun of(text: String, decode: Boolean = true): KQCode {
            return FastKQCode(text)
//            var tempText = text.trim()
//            // 不是[CQ:开头，或者不是]结尾都不行
//            if (!tempText.startsWith("[CQ:") || !tempText.endsWith("]")) {
//                throw IllegalArgumentException("not starts with '[CQ:' or not ends with ']'")
//            }
//            // 是[CQ:开头，]结尾，切割并转化
//            tempText = tempText.substring(4, tempText.lastIndex)
//
//            val split = tempText.split(Regex(" *, *"))
//
//            val type = split[0]
//
//            return if (split.size > 1) {
//                if (decode) {
//                    // 参数解码
//                    val map = split.subList(1, split.size).map {
//                        val sp = it.split(Regex("="), 2)
//                        sp[0] to (CQDecoder.decodeParams(sp[1]) ?: "")
//                    }.toMap()
//                    MapKQCode(map, type)
//                } else {
//                    MapKQCode(type, *split.subList(1, split.size).toTypedArray())
//                }
//            } else {
//                MapKQCode(type)
//            }

        }

        /**
         * 从CQCode转到KQCode
         * CQCode的转化使用[MapKQCode]作为载体。
         * @since 1.0-1.11
         * infix since 1.2-1.11
         */
        @JvmStatic
        infix fun of(cqCode: com.forte.qqrobot.beans.cqcode.CQCode): KQCode = MapKQCode(cqCode.cqCodeTypesName, cqCode)
    }


}

/**
 * 定义一个可变的KQCode标准接口
 * - MutableKQCode实例应当实现[MutableMap]接口，使其可以作为一个 **可变** Map使用。
 */
interface MutableKQCode: KQCode, MutableMap<String, String>









/**
 * Params
 * @since 1.0-1.11
 */
@KQCodeDsl
open class Params {
    private val plist: MutableList<Pair<String, String>> = mutableListOf()
    var param: Pair<String, String>
        get() = plist.last()
        set(value) { plist.add(value) }

    operator fun set(param: String, value: String) {
        this.param = param to value
    }

    /** 添加全部 */
    open fun addTo(kqCode: KQCode): KQCode {
        kqCode.mutable().putAll(plist)
        return kqCode
    }

    override fun toString(): String = plist.toString()
}

/**
 * Builder
 * @since 1.0-1.11
 */
@KQCodeDsl
class Builder {
    var type: String = ""
    internal val _params = Params()
    var param: Pair<String, String>
        get() = _params.param
        set(value) { _params.param = value }
    /** 添加全部 */
    fun build(): KQCode {
        val kqCode = MapKQCode(type)
        _params.addTo(kqCode)
        return kqCode
    }

    override fun toString(): String = "$type:$_params"
}



/**
 * DSL构建KQCode， 例如
 * ```
 *kqCode("at") {
 *param = "key1" to "1"
 *param = "key2" to "2"
 *param = "key3" to "3"
 *param = "key4" to "4"
 *}
 *
 * 其最终构建结果是[MapKqCode]实例。
 *  ```
 * @since 1.0-1.11
 */
@KQCodeDsl
fun kqCode(type: String, block: Params.() -> Unit): KQCode {
    val kqCode = MapKQCode(type)
    return Params().apply(block).addTo(kqCode)
}

/**
 * DSL构建KQCode的参数列表
 * @since 1.0-1.11
 */
@KQCodeDsl
fun kqCode(block: Builder.() -> Unit) = Builder().apply(block).build()

/**
 * DSL构建Builder中的params, 例如
 * ```
kqCode {
type = "at"
params {
param = "qq" to "1149"
param = "file" to "cq.jpg"
}
}
 * ```
 * @since 1.0-1.11
 */
@KQCodeDsl
fun Builder.params(block: Params.() -> Unit) {
    this._params.apply(block)
}
