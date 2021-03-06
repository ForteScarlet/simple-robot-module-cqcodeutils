/*
 *
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  simple-robot-module-cqcodeutils
 *  File     KQCodeMessageUtils.kt
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

package com.simplerobot.modules.utils


/**
 * 针对于消息与CQ码之间的一些简易操作。
 * 例如将消息或者CQ码拆分为json格式等。
 */
object KQCodeJsonUtils {

    /**
     * 将一串消息切割并转化为Json格式的map。
     * 例如：
     * `文本[CQ:at,qq=123456789]` 会被转化为：
     * `
     * [
     *      {
     *          type: "text",
     *          data: {
     *              "text": "文本"
     *          }
     *      },
     *      {
     *          type: "at",
     *          data: {
     *              "qq": "123456789"
     *          }
     *      }
     *  ]
     * `
     */
    fun toJsonMap(message: String): List<Map<String, Any>> {
        return KQCodeUtils.split(message).asSequence()
                .map {
                    if (it.startsWith("[CQ")) {
                        // cq
                        it.cqToJsonMap()
                    } else {
                        // text
                        it.textToJsonMap()
                    }
                }.toList()
    }


    /**
     * 将一个kqCode转化为指定的map风格
     * `[CQ:at,qq=123456]` -> `{"type": "at", "data": {"qq":"123456789"}}`
     */
    fun toJsonMap(code: KQCode): Map<String, Any> {
        val type = "type" to code.type
        val data = "data" to code as Map<String, String>
        return mapOf(type, data)
    }


}

/**
 * 纯文本转化为json格式的map
 */
internal fun String.textToJsonMap(): Map<String, Any> = mapOf("type" to "text", "data" to mapOf("text" to this))

/**
 * CQ码类型转化为json格式的map
 */
internal fun String.cqToJsonMap(): Map<String, Any> {
    val code = KQCode.of(this)
    val type = "type" to code.type
    val data = "data" to code.toMap()
    return mapOf(type, data)
}
