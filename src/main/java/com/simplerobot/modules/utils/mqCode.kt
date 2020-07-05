/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  mod-cqcodeutils
 * File     mqCode.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 *
 */

package com.simplerobot.modules.utils


/** MQ code 封装数据类。
 * 不可变数据类。
 *
 */
data class MQCode(val type: String, val param: String?): CharSequence {

    /**
     * string value
     */
    private val value: String = "[mirai:$type${if(param != null) ":$param" else "" }]"


    override fun toString(): String = value

    /**
     * 转化为KQCode对象。
     */
    fun toKQCode() = if(param != null){
        // 做一些特殊的处理
        when(type){
            // atall 转化为 at
            "atall" -> AtAll
            "at" -> KQCode(type, "qq" to param)
            // 图片
            "image" -> {
                KQCode(type, type to param, "file" to param)
            }
            // voice
            "record" -> {
                KQCode(type, type to param, "file" to param)
            }
            "face" -> {
                KQCode(type, type to param, "id" to param)
            }
            else -> KQCode(type, type to param)
        }
    }else{
        KQCode(type)
    }

    /**
     * 不做任何特殊的判断与处理的转化
     * @see toKQCode
     */
    fun toKQCodeDoNothing() = if(param != null){
        KQCode(type, type to param)
    }else {
        KQCode(type)
    }

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
}
