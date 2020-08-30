/*
 *
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  simple-robot-module-cqcodeutils
 *  File     KQCodeDsl.kt
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
 * @author ForteScarlet <ForteScarlet@163.com>
 * 2020/8/12
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.BINARY)
@DslMarker
annotation class KQCodeDsl


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
