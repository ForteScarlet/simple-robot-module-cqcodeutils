/*
 *
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  simple-robot-module-cqcodeutils
 *  File     KQCodeObjects.kt
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

import com.simplerobot.modules.utils.codes.FastKQCode
import com.simplerobot.modules.utils.codes.MapKQCode

/*
    提供一些可以作为单例使用的[KQCode]实例
 */

/**
 * at all
 * `[CQ:at,qq=all]`
 */
object AtAll : KQCode by FastKQCode.byCode("[CQ:at,qq=all]")

/**
 * rps 猜拳
 * 发送用的猜拳
 * `[CQ:rps]`
 */
object Rps : KQCode by EmptyKQCode("rps")


/**
 * dice 骰子
 * 发送用的骰子
 * `[CQ:dice]`
 */
object Dice : KQCode by EmptyKQCode("dice")


/**
 * 窗口抖动，戳一戳
 * `[CQ:shake]`
 */
object Shake : KQCode by EmptyKQCode("shake")

/**
 * 匿名消息
 * `[CQ:anonymous,ignore=true]` - 匿名发消息（仅支持群消息使用）
 *
 * 此匿名消息为ignore=true的
 *
 * 本CQ码需加在消息的开头。
 * 当ignore为true时，代表不强制使用匿名，如果匿名失败将转为普通消息发送。
 * 当ignore为false或ignore参数被忽略时，代表强制使用匿名，如果匿名失败将取消该消息的发送。
 * 举例：
 * [CQ:anonymous,ignore=true]
 * [CQ:anonymous]
 *
 * @see AnonymousCompulsory
 *
 */
object Anonymous : KQCode by FastKQCode.byCode("[CQ:anonymous,ignore=true]")

/**
 * ignore参数为false的[Anonymous]
 */
object AnonymousNoIgnore : KQCode by FastKQCode.byCode("[CQ:anonymous,ignore=false]")

/**
 * 强制的匿名CQ码
 * @see Anonymous
 */
object AnonymousCompulsory : KQCode by EmptyKQCode("[CQ:anonymous]")






