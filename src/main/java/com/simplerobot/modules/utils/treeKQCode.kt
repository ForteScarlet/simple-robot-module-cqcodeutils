/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  mod-cqcodeutils
 * File     treeKQCode.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 *
 */
package com.simplerobot.modules.utils

import java.util.*

/*
 * Created by lcy on 2020/8/21.
 * @author lcy
 */

/* ******************************************************
 *
 *  kq code by tree map
 *  基于[TreeMap]作为载体的[MapKQCode]子类
 *
 *******************************************************/
open class TreeKQCode
protected constructor(override val params: TreeMap<String, String>, type: String) : MutableMapKQCode(params, type) {
    constructor(type: String) : this(TreeMap(), type)
    constructor(type: String, params: Map<String, String>) : this(TreeMap(params), type)
    constructor(type: String, vararg params: Pair<String, String>) : this(TreeMap(mapOf(*params)), type)
    constructor(type: String, vararg params: String) : this(TreeMap(mapOf(*params.map {
        val split = it.split(Regex("="), 2)
        split[0] to split[1]
    }.toTypedArray())), type)

    override fun mutable(): MutableKQCode = this
    override fun immutable(): KQCode = this

}
