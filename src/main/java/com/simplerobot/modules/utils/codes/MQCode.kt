/*
 *
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  simple-robot-module-cqcodeutils
 *  File     MQCode.kt
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

import com.simplerobot.modules.utils.KQCode


/**
 * mirai code 封装数据类。
 *
 * mirai现在支持mirai码转义。
 *
 * 参考:
 * - [mirai-serialization](https://github.com/mamoe/mirai/tree/1.1.0/mirai-serialization)
 * - [mirai-serialization-doc](https://github.com/mamoe/mirai/blob/1.1.0/docs/mirai-code-specification.md)
 *
 */
open class MQCode
@JvmOverloads
constructor(val type: String, val param: Array<String> = arrayOf()) : CharSequence {
    constructor(type: String, singleParam: String?):
            this(type, singleParam?.let { arrayOf(it) } ?: arrayOf())

    /**
     * string value
     */
    private val value: String
        get() = "[mirai:$type${if (param.isNotEmpty()) ":$paramJoin" else ""}]"

    private val paramJoin: String
        get() = param.joinToString(",")


    override fun toString(): String = value

    /**
     * 设置某个索引上的参数值。
     */
    fun setParam(index: Int, value: String) {
        param[index] = value
    }

    /**
     * 获取某个索引上的参数值
     */
    fun getParam(index: Int): String = param[index]

    /**
     * 参数的数量
     */
    val size: Int get() = param.size

    /**
     * 参数列表是否为空
     */
    val paramEmpty: Boolean = param.isEmpty()

    /**
     * 转化为KQCode对象。
     */
    fun toKQCode() = if (param.isNotEmpty()) {
        val paramList: MutableList<Pair<String, String>> = mutableListOf()
        paramList.add(type to param.joinToString(","))
        param.forEachIndexed { i, p ->
            paramList.add("param$i" to p)
        }
        MapKQCode(type, *paramList.toTypedArray())
    } else {
        MapKQCode(type)
    }

    /**
     * 不做任何特殊的判断与处理的转化
     * @see toKQCode
     */
    @Deprecated("use toKQCode() plz.", ReplaceWith("toKQCode()"))
    fun toKQCodeDoNothing(): KQCode = toKQCode()


    /**
     * Returns the length of this character sequence.
     */
    override val length: Int
        get() = value.length


    /**
     * get char from string
     * @see value
     * @see toString
     * @see CharSequence.get
     */
    override operator fun get(index: Int): Char = value[index]

    /**
     * sub sequence
     * @see value
     * @see toString
     * @see CharSequence.subSequence
     */
    override fun subSequence(startIndex: Int, endIndex: Int): CharSequence = value.subSequence(startIndex, endIndex)
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MQCode

        if (type != other.type) return false
        if (!param.contentEquals(other.param)) return false
        if (value != other.value) return false

        return true
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + param.contentHashCode()
        result = 31 * result + value.hashCode()
        return result
    }
}
