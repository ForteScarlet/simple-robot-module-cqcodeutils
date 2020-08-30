/*
 *
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  simple-robot-module-cqcodeutils
 *  File     CodeBuilder.kt
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
 *
 * 针对于CQ码的构造器.
 *
 * 通过此构造器的实例, 来以
 * [builder][CodeBuilder].[key(String)][key].[value(Any?)][CodeBuilderKey.value].[build][CodeBuilder.build]
 * 的形式快速构造一个CQ码实例并作为指定载体返回。
 *
 *
 * @author ForteScarlet <[email]ForteScarlet@163.com>
 * @since JDK1.8
 **/
interface CodeBuilder<T> {

    /**
     * 指定一个code的key, 并通过这个key设置一个value.
     */
    fun key(key: String): CodeBuilderKey<T>

    /**
     * 构建一个cq码, 并以其载体实例[T]返回.
     */
    fun build(): T

    /**
     * [CodeBuilder]在一次指定了[Key][key]之后得到的Key值载体.
     * 通过调用此类的[value]方法来得到自身所在的[CodeBuilder]
     *
     * 此类一般来讲是属于一次性临时类.
     *
     */
    interface CodeBuilderKey<T> {
        /**
         * 为当前Key设置一个value值并返回.
         */
        fun value(value: Any?): CodeBuilder<T>

        /**
         * 为当前Key设置一个空的value值并返回.
         */
        fun emptyValue(): CodeBuilder<T>
    }
}