/*
 *
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  simple-robot-module-cqcodeutils
 *  File     KQCode.kt
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
@file:JvmName("CqSymbolConstant")

package com.simplerobot.modules.utils

import com.simplerobot.modules.utils.codes.FastKQCode
import com.simplerobot.modules.utils.codes.MapKQCode


const val CQ_HEAD = "[CQ:"
const val CQ_END = "]"
const val CQ_SPLIT = ","
const val CQ_KV = "="


/**
 * 定义一个不可变的KQCode标准接口
 * - KQCode实例应当实现[Map]接口，使其可以作为一个**不可变**Map使用。
 * - KQCode实例应当实现[CharSequence]接口，其可以作为一个字符序列以得到CQ码字符串
 *
 * 其参数是不可变的，如果需要一个可变参数的实例，参考方法[mutable]与其返回的接口类型[MutableKQCode]
 * 如果想要获得一个纯空参的实例，参考[EmptyKQCode]
 *
 * 建议子类通过私有构造+ 静态/伴生对象 方法来获取实例，例如 [MapKQCode.byCode] [FastKQCode.byCode]
 * 而不是直接通过构造方法。
 *
 * @since 1.8.0
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
    @Deprecated("'com.forte.qqrobot.beans.cqcode.CQCode' will be removed")
    fun toCQCode(): com.forte.qqrobot.beans.cqcode.CQCode

    /**
     * 与其他字符序列拼接为[Msgs]实例
     */
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

        /**
         * 得到一个空参的[KQCode]实例。
         */
        @JvmStatic
        fun ofType(type: String): KQCode = EmptyKQCode(type)

        /**
         * 通过cq码字符串得到一个[KQCode]实例
         */
        @JvmStatic
        fun of(code: String): KQCode = FastKQCode.byCode(code)

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
            return FastKQCode.byCode(text)
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
 * 一个纯空参的[KQCode]实例。
 */
data class EmptyKQCode(override val type: String): KQCode {

    private val _codeText = "[CQ:$type]"

    override fun toString(): String = _codeText

    /**
     * 从[KQCode]转化为[com.forte.qqrobot.beans.cqcode.CQCode]
     */
    @Suppress("OverridingDeprecatedMember")
    override fun toCQCode(): com.forte.qqrobot.beans.cqcode.CQCode = com.forte.qqrobot.beans.cqcode.CQCode.of(type)

    /**
     * 转化为可变参的[MutableKQCode]
     */
    override fun mutable(): MutableKQCode = MapKQCode.mutableByCode(_codeText)

    /**
     * 转化为不可变类型[KQCode]
     */
    override fun immutable(): KQCode = this
    override val entries: Set<Map.Entry<String, String>> = emptySet()
    override val keys: Set<String> = emptySet()
    override val size: Int = 0
    override val values: Collection<String> = emptyList()
    override fun containsKey(key: String): Boolean = false
    override fun containsValue(value: String): Boolean = false
    override operator fun get(key: String): String? = null
    override fun getNoDecode(key: String): String? = null
    override val length: Int = _codeText.length
    override operator fun get(index: Int): Char = _codeText[index]
    override fun isEmpty(): Boolean = true
    override fun subSequence(startIndex: Int, endIndex: Int): CharSequence = _codeText.subSequence(startIndex, endIndex)
}







//**************************************
//*           for DSL
//**************************************



