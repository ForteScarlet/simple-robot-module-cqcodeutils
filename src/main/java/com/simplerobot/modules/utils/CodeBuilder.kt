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

@file:Suppress("RedundantInnerClassModifier", "RedundantVisibilityModifier")

package com.simplerobot.modules.utils

import com.simplerobot.modules.utils.CodeBuilder.CodeBuilderKey
import com.simplerobot.modules.utils.codes.MapKQCode

/**
 *
 * 针对于CQ码的构造器.
 *
 * 通过此构造器的实例, 来以
 * [builder][CodeBuilder].[key(String)][key].[value(Any?)][CodeBuilderKey.value].[build][CodeBuilder.build]
 * 的形式快速构造一个CQ码实例并作为指定载体返回。
 *
 * @author ForteScarlet <ForteScarlet@163.com>
 * @since 1.8.0
 **/
public interface CodeBuilder<T> {

    /**
     * type类型
     */
    val type: String

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

    /**
     * 提供两个已经实现的模板的
     */
    companion object {
        /**
         * 获取一个以字符串为cq码载体的[构建器][CodeBuilder]实例, 需要提供[类型][type]参数
         */
        fun stringBuilder(type: String): CodeBuilder<String> = StringCodeBuilder(type)

        /**
         * 获取一个以[KQCode]为cq码载体的[构建器][CodeBuilder]实例, 需要提供[类型][type]参数
         */
        fun kqCodeBuilder(type: String): CodeBuilder<KQCode> = KQCodeBuilder(type)
    }

}


//**************************************
//*          string builder
//**************************************


/**
 * 以`String`为载体的[CodeBuilder]实现类, 需要在构建实例的时候指定[类型][type]
 *
 * 以对字符串的拼接为主要构建形式, 且不是线程安全的。
 *
 * 如果[encode] == true, 则会对value值进行转义
 */
public class StringCodeBuilder
@JvmOverloads
constructor(override val type: String, private val encode: Boolean = true) : CodeBuilder<String> {
    /** [StringBuilder] */
    private val appender: StringBuilder = StringBuilder(CQ_HEAD).append(type)

    /** [StringCodeBuilderKey]实例 */
    private val builderKey: StringCodeBuilderKey = StringCodeBuilderKey()

    /** 当前等待设置的key值 */
    private var key: String? = null

    /**
     * 指定一个code的key, 并通过这个key设置一个value.
     */
    override fun key(key: String): CodeBuilder.CodeBuilderKey<String> {
        return builderKey.also { this.key = key }
    }

    /**
     * 构建一个cq码, 并以其载体实例String返回.
     */
    override fun build(): String = appender.toString() + CQ_END


    /**
     * [StringCodeBuilder]中[CodeBuilder.CodeBuilderKey]的实现类。
     * 此类在[StringCodeBuilder]中只会存在一个实例，因此 **线程不安全**。
     */
    private inner class StringCodeBuilderKey : CodeBuilder.CodeBuilderKey<String> {
        /**
         * 为当前Key设置一个value值并返回.
         */
        override fun value(value: Any?): CodeBuilder<String> {
            return key?.let { k ->
                appender.append(CQ_SPLIT).append(k).append(CQ_KV)
                if (value != null) {
                    appender.append(CQEncoder.encodeParams(value.toString()))
                }
                this@StringCodeBuilder
            }?.also { this@StringCodeBuilder.key = null }
                ?: throw NullPointerException("The 'key' has not been specified.")
        }

        /**
         * 为当前Key设置一个空的value值并返回.
         */
        override fun emptyValue(): CodeBuilder<String> = value(null)
    }

}


//**************************************
//*         KQCode Builder
//**************************************

/**
 * 以[KQCode]为载体的[CodeBuilder]实现类, 需要在构建实例的时候指定[类型][type]
 *
 * 通过[哈希表][MutableMap]来进行[KQCode]的构建, 且不是线程安全的。
 */
public class KQCodeBuilder(override val type: String) : CodeBuilder<KQCode> {

    /** 当前参数map */
    private val params: MutableMap<String, String> = mutableMapOf()

    /** 当前等待设置的key值 */
    private var key: String? = null

    /** [KQCodeBuilderKey]实例 */
    private val builderKey: KQCodeBuilderKey = KQCodeBuilderKey()

    /**
     * 指定一个code的key, 并通过这个key设置一个value.
     */
    override fun key(key: String): CodeBuilder.CodeBuilderKey<KQCode> {
        return builderKey.also { this.key = key }
    }

    /**
     * 构建一个cq码, 并以其载体实例[T]返回.
     */
    override fun build(): KQCode {
        return MapKQCode.byMap(type, params.toMap())
    }

    /**
     * 以[KQCode]作为载体的[CodeBuilder.CodeBuilderKey]实现类
     */
    private inner class KQCodeBuilderKey : CodeBuilder.CodeBuilderKey<KQCode> {
        /**
         * 为当前Key设置一个value值并返回.
         */
        override fun value(value: Any?): CodeBuilder<KQCode> {
            return key?.let { k ->
                params[k] = value?.toString() ?: ""
                this@KQCodeBuilder
            }?.also { this@KQCodeBuilder.key = null }
                ?: throw NullPointerException("The 'key' has not been specified.")
        }

        /**
         * 为当前Key设置一个空的value值并返回.
         */
        override fun emptyValue(): CodeBuilder<KQCode> = value("")
    }

}
