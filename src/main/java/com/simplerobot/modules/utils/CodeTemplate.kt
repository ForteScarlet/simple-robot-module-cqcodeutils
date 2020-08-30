/*
 *
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  simple-robot-module-cqcodeutils
 *  File     CodeTemplate.kt
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

package com.simplerobot.modules.utils

import com.simplerobot.modules.utils.codes.MapKQCode

/**
 * 定义特殊码的一些模板方法，例如at等。
 * 返回值类型全部相同
 * 主要基于cq码规范定义。
 */
interface CodeTemplate<T> {
    /**
     * at别人
     * @see at
     */
    @JvmDefault fun at(code: Long): T = at(code.toString())

    /**
     * at别人
     */
    fun at(code: String): T

    /**
     * at所有人
     */
    fun atAll(): T

    /**
     * face
     */
    fun face(id: String): T
    @JvmDefault fun face(id: Long): T = face(id.toString())

    /**
     * big face
     */
    fun bface(id: String): T
    @JvmDefault fun bface(id: Long): T = bface(id.toString())

    /**
     * small face
     */
    fun sface(id: String): T
    @JvmDefault fun sface(id: Long): T = sface(id.toString())

    /**
     * image
     * @param file id
     * @param destruct 闪图
     */
    fun image(file: String, destruct: Boolean): T
    @JvmDefault fun image(id: String): T = image(id, false)


    /**
     * 语言
     * [CQ:record,[file]={1},[magic]={2}] - 发送语音
     * {1}为音频文件名称，音频存放在酷Q目录的data\record\下
     * {2}为是否为变声，若该参数为true则显示变声标记。该参数可被忽略。
     * 举例：[CQ:record,file=1.silk，magic=true]（发送data\record\1.silk，并标记为变声）
     */
    fun record(file: String, magic: Boolean): T
    @JvmDefault fun record(id: String): T = record(id, false)



    /**
     * rps 猜拳
     * [CQ:rps,[type]={1}] - 发送猜拳魔法表情
     * {1}为猜拳结果的类型，暂不支持发送时自定义。该参数可被忽略。
     * 1 - 猜拳结果为石头
     * 2 - 猜拳结果为剪刀
     * 3 - 猜拳结果为布
     */
    fun rps(type: String): T
    fun rps(): T
    @JvmDefault fun rps(type: Int) = rps(type.toString())


    /**
     * 骰子
     * [CQ:dice,type={1}] - 发送掷骰子魔法表情
     * {1}对应掷出的点数，暂不支持发送时自定义。该参数可被忽略。
     */
    fun dice(): T
    fun dice(type: String): T
    @JvmDefault fun dice(type: Int) = dice(type.toString())

    /**
     * 戳一戳（原窗口抖动，仅支持好友消息使用）
     */
    fun shake(): T

    /**
     * 匿名消息
     */
    @Deprecated("匿名消息不再是一个必须的CQ码类型, 因此不再提供模板方法，将会在未来版本中删除, 因此请尽可能避免使用。")
    fun anonymous(ignore: Boolean): T
    @JvmDefault
    @Suppress("DeprecatedCallableAddReplaceWith", "DEPRECATION")
    @Deprecated("匿名消息不再是一个必须的CQ码类型, 因此不再提供模板方法，将会在未来版本中删除, 因此请尽可能避免使用。")
    fun anonymous(): T = anonymous(false)

    /**
     * 音乐
     * [CQ:music,type={1},id={2},style={3}]
     * {1} 音乐平台类型，目前支持qq、163
     * {2} 对应音乐平台的数字音乐id
     * {3} 音乐卡片的风格。仅 Pro 支持该参数，该参数可被忽略。
     * 注意：音乐只能作为单独的一条消息发送
     * 例子
     * [CQ:music,type=qq,id=422594]（发送一首QQ音乐的“Time after time”歌曲到群内）
     * [CQ:music,type=163,id=28406557]（发送一首网易云音乐的“桜咲く”歌曲到群内）
     */
    fun music(type: String, id: String, style: String?): T
    @JvmDefault fun music(type: String, id: String): T = music(type, id, null)

    /**
     * [CQ:music,type=custom,url={1},audio={2},title={3},content={4},image={5}] - 发送音乐自定义分享
     * 注意：音乐自定义分享只能作为单独的一条消息发送
     * @param url   {1}为分享链接，即点击分享后进入的音乐页面（如歌曲介绍页）。
     * @param audio {2}为音频链接（如mp3链接）。
     * @param title  {3}为音乐的标题，建议12字以内。
     * @param content  {4}为音乐的简介，建议30字以内。该参数可被忽略。
     * @param image  {5}为音乐的封面图片链接。若参数为空或被忽略，则显示默认图片。
     *
     */
    fun customMusic(url: String, audio: String, title: String, content: String?, image: String?): T
    @JvmDefault fun customMusic(url: String, audio: String, title: String): T = customMusic(url, audio, title, null, null)


    /**
     * [CQ:share,url={1},title={2},content={3},image={4}] - 发送链接分享
     * {1}为分享链接。
     * {2}为分享的标题，建议12字以内。
     * {3}为分享的简介，建议30字以内。该参数可被忽略。
     * {4}为分享的图片链接。若参数为空或被忽略，则显示默认图片。
     * 注意：链接分享只能作为单独的一条消息发送
     */
    fun share(url: String, title: String, content: String?, image: String?): T
    @JvmDefault fun share(url: String, title: String): T = share(url, title, null, null)


    /**
     * 地点
     * [CQ:location,lat={1},lon={2},title={3},content={4}]
     * {1} 纬度
     * {2} 经度
     * {3} 分享地点的名称
     * {4} 分享地点的具体地址
     */
    fun location(lat: String, lon: String, title: String, content: String): T


}


//**************************************
//*         String template
//**************************************


/**
 * 基于 [KQCodeTemplate] 的模板实现, 以`string`作为cq码载体。
 * 默认内置于[KQCodeUtils.kqCodeTemplate]
 */
object KQCodeStringTemplate: CodeTemplate<String> {
    @JvmStatic
    val instance get() = this
    private val utils: KQCodeUtils = KQCodeUtils
    private const val AT_ALL: String = "[CQ:at,qq=all]"
    /**
     * at别人
     */
    override fun at(code: String): String = "[CQ:at,qq=$code]" //utils.toCq("at", "qq" to code)

    /**
     * at所有人
     */
    override fun atAll(): String = AT_ALL

    /**
     * face
     */
    override fun face(id: String): String = "[CQ:face,id=$id]" // utils.toCq("face", "id" to id)

    /**
     * big face
     */
    override fun bface(id: String): String = "[CQ:bface,id=$id]" //utils.toCq("bface", "id" to id)

    /**
     * small face
     */
    override fun sface(id: String): String = "[CQ:sface,id=$id]" //utils.toCq("sface", "id" to id)

    /**
     * image
     * @param file file/url/id
     * @param destruct true=闪图
     */
    override fun image(file: String, destruct: Boolean): String = "[CQ:image,file=$file,destruct=$destruct]"//utils.toCq("image", "file" to id, "destruct" to destruct)



    /**
     * 语言
     * [CQ:record,file={1},magic={2}] - 发送语音
     * {1}为音频文件名称，音频存放在酷Q目录的data\record\下
     * {2}为是否为变声，若该参数为true则显示变声标记。该参数可被忽略。
     * 举例：[CQ:record,file=1.silk，magic=true]（发送data\record\1.silk，并标记为变声）
     */
    override fun record(file: String, magic: Boolean): String =
        "[CQ:record,file=$file,magic=$magic]"// utils.toCq("record", "file" to id, "magic" to magic)


    /**
     * const val for rps
     */
    private const val RPS = "[CQ:rps]"

    /**
     * rps 猜拳
     * [CQ:rps,type={1}] - 发送猜拳魔法表情
     * {1}为猜拳结果的类型，暂不支持发送时自定义。该参数可被忽略。
     * 1 - 猜拳结果为石头
     * 2 - 猜拳结果为剪刀
     * 3 - 猜拳结果为布
     */
    override fun rps(): String = RPS



    /**
     * rps 猜拳
     * [CQ:rps,type={1}] - 发送猜拳魔法表情
     * {1}为猜拳结果的类型，暂不支持发送时自定义。该参数可被忽略。
     * 1 - 猜拳结果为石头
     * 2 - 猜拳结果为剪刀
     * 3 - 猜拳结果为布
     */
    override fun rps(type: String): String = "[CQ:rps,type=$type]"//utils.toCq("rps", "type" to type)

    /**
     * const val for dice
     */
    private const val DICE = "[CQ:dice]"

    /**
     * 骰子
     * [CQ:dice,type={1}] - 发送掷骰子魔法表情
     * {1}对应掷出的点数，暂不支持发送时自定义。该参数可被忽略。
     */
    override fun dice(): String = DICE


    /**
     * 骰子
     * [CQ:dice,type={1}] - 发送掷骰子魔法表情
     * {1}对应掷出的点数，暂不支持发送时自定义。该参数可被忽略。
     */
    override fun dice(type: String): String = "[CQ:dice,type=$type]" // utils.toCq("dice", "type" to type)


    /**
     * const val for shake
     */
    private const val SHAKE = "[CQ:shake]"

    /**
     * 戳一戳（原窗口抖动，仅支持好友消息使用）
     */
    override fun shake(): String = SHAKE


    private const val ANONYMOUS_TRUE = "[CQ:anonymous,ignore=true]"
    private const val ANONYMOUS_FALSE = "[CQ:anonymous]"

    /**
     * 匿名消息
     * [CQ:anonymous,ignore={1}] - 匿名发消息（仅支持群消息使用）
     * 本CQ码需加在消息的开头。
     * 当{1}为true时，代表不强制使用匿名，如果匿名失败将转为普通消息发送。
     * 当{1}为false或ignore参数被忽略时，代表强制使用匿名，如果匿名失败将取消该消息的发送。
     * 举例：
     * [CQ:anonymous,ignore=true]
     * [CQ:anonymous]
     */
    override fun anonymous(ignore: Boolean): String = if(ignore) ANONYMOUS_TRUE else ANONYMOUS_FALSE


    /**
     * 音乐
     * [CQ:music,type={1},id={2},style={3}]
     * {1} 音乐平台类型，目前支持qq、163
     * {2} 对应音乐平台的数字音乐id
     * {3} 音乐卡片的风格。仅 Pro 支持该参数，该参数可被忽略。
     * 注意：音乐只能作为单独的一条消息发送
     * 例子
     * [CQ:music,type=qq,id=422594]（发送一首QQ音乐的“Time after time”歌曲到群内）
     * [CQ:music,type=163,id=28406557]（发送一首网易云音乐的“桜咲く”歌曲到群内）
     */
    override fun music(type: String, id: String, style: String?): String {
        return if (style != null) {
            "[CQ:music,type=$type,id=$id,style=$style]" // utils.toCq("music", "type" to type, "id" to id, "style" to style)
        }else{
            "[CQ:music,type=$type,id=$id]" // utils.toCq("music", "type" to type, "id" to id)
        }
    }

    /**
     * [CQ:music,type=custom,url={1},audio={2},title={3},content={4},image={5}] - 发送音乐自定义分享
     * 注意：音乐自定义分享只能作为单独的一条消息发送
     * @param url   {1}为分享链接，即点击分享后进入的音乐页面（如歌曲介绍页）。
     * @param audio {2}为音频链接（如mp3链接）。
     * @param title  {3}为音乐的标题，建议12字以内。
     * @param content  {4}为音乐的简介，建议30字以内。该参数可被忽略。
     * @param image  {5}为音乐的封面图片链接。若参数为空或被忽略，则显示默认图片。
     *
     */
    override fun customMusic(url: String, audio: String, title: String, content: String?, image: String?): String {
        return if(content != null && image != null){
            "[CQ:music,type=custom,url=$url,audio=$audio,title=$title,content=$content,image=$image]"
            // utils.toCq("music", "type" to "custom", "url" to url, "audio" to audio,
            // "title" to title, "content" to content, "image" to image)
        }else{
            val list: MutableList<Pair<String, Any>> = mutableListOf("type" to "custom", "url" to url, "audio" to audio, "title" to title)
            content?.run {
                list.add("content" to this)
            }
            image?.run {
                list.add("image" to this)
            }
            utils.toCq("music", pair = list.toTypedArray())
        }
    }

    /**
     * [CQ:share,url={1},title={2},content={3},image={4}] - 发送链接分享
     * {1}为分享链接。
     * {2}为分享的标题，建议12字以内。
     * {3}为分享的简介，建议30字以内。该参数可被忽略。
     * {4}为分享的图片链接。若参数为空或被忽略，则显示默认图片。
     * 注意：链接分享只能作为单独的一条消息发送
     */
    override fun share(url: String, title: String, content: String?, image: String?): String {
        return if(content != null && image != null){
            "[CQ:share,url=$url,title=$title,content=$content,image=$image]"
            // utils.toCq("share", "url" to url, "title" to title, "content" to content, "image" to image)
        }else{
            val list: MutableList<Pair<String, Any>> = mutableListOf("url" to url, "title" to title)
            content?.run {
                list.add("content" to this)
            }
            image?.run {
                list.add("image" to this)
            }
            utils.toCq("share", pair = list.toTypedArray())
        }
    }

    /**
     * 地点
     * [CQ:location,lat={1},lon={2},title={3},content={4}]
     * {1} 纬度
     * {2} 经度
     * {3} 分享地点的名称
     * {4} 分享地点的具体地址
     */
    override fun location(lat: String, lon: String, title: String, content: String): String =
        "[CQ:location,lat=$lat,lon=$lon,title=$title,content=$content]"
    // utils.toCq("location", "lat" to lat, "lon" to lon, "title" to title, "content" to content)
}



//**************************************
//*         KQ template
//**************************************



/**
 * 基于 [KQCodeTemplate] 的模板实现, 以[KQCode]作为CQ码载体。
 * 默认内置于[KQCodeUtils.kqCodeTemplate]
 */
object KQCodeTemplate: CodeTemplate<KQCode> {
    @JvmStatic
    val instance get() = this
    /**
     * at别人
     */
    override fun at(code: String): KQCode = MapKQCode("at", "qq" to code)

//    /** kq for all */
//    private val AT_ALL: KQCode = AtAll

    /**
     * at所有人
     */
    override fun atAll(): KQCode = AtAll

    /**
     * face
     */
    override fun face(id: String): KQCode = MapKQCode("face", "id" to id)

    /**
     * big face
     */
    override fun bface(id: String): KQCode = MapKQCode("bface", "id" to id)

    /**
     * small face
     */
    override fun sface(id: String): KQCode = MapKQCode("sface", "id" to id)

    /**
     * image
     * @param file id
     * @param destruct true=闪图
     */
    override fun image(file: String, destruct: Boolean): KQCode  = MapKQCode("image", "file" to file, "destruct" to destruct.toString())


    /**
     * 语言
     * [CQ:record,file={1},magic={2}] - 发送语音
     * {1}为音频文件名称，音频存放在酷Q目录的data\record\下
     * {2}为是否为变声，若该参数为true则显示变声标记。该参数可被忽略。
     * 举例：[CQ:record,file=1.silk，magic=true]（发送data\record\1.silk，并标记为变声）
     */
    override fun record(file: String, magic: Boolean): KQCode = MapKQCode("record", "file" to file, "magic" to magic.toString())

//    /** rps */
//    private val RPS: KQCode = Rps

    /**
     * rps 猜拳
     * [CQ:rps,type={1}] - 发送猜拳魔法表情
     * {1}为猜拳结果的类型，暂不支持发送时自定义。该参数可被忽略。
     * 1 - 猜拳结果为石头
     * 2 - 猜拳结果为剪刀
     * 3 - 猜拳结果为布
     */
    override fun rps(): KQCode = Rps


    /**
     * rps 猜拳
     * [CQ:rps,type={1}] - 发送猜拳魔法表情
     * {1}为猜拳结果的类型，暂不支持发送时自定义。该参数可被忽略。
     * 1 - 猜拳结果为石头
     * 2 - 猜拳结果为剪刀
     * 3 - 猜拳结果为布
     */
    override fun rps(type: String): KQCode = MapKQCode("rps", "type" to type)

//    /** dice */
//    private val DICE: KQCode = Dice

    /**
     * 骰子
     * [CQ:dice,type={1}] - 发送掷骰子魔法表情
     * {1}对应掷出的点数，暂不支持发送时自定义。该参数可被忽略。
     */
    override fun dice(): KQCode = Dice


    /**
     * 骰子
     * [CQ:dice,type={1}] - 发送掷骰子魔法表情
     * {1}对应掷出的点数，暂不支持发送时自定义。该参数可被忽略。
     *
     * @see dice
     *
     */
    override fun dice(type: String): KQCode = MapKQCode("dice", "type" to type)


//    private val SHAKE: KQCode = Shake

    /**
     * 戳一戳（原窗口抖动，仅支持好友消息使用）
     */
    override fun shake(): KQCode = Shake


//    private val ANONYMOUS_TRUE = Anonymous
//    private val ANONYMOUS_FALSE = AnonymousCompulsory

    /**
     * 匿名消息
     * [CQ:anonymous,ignore={1}] - 匿名发消息（仅支持群消息使用）
     * 本CQ码需加在消息的开头。
     * 当{1}为true时，代表不强制使用匿名，如果匿名失败将转为普通消息发送。
     * 当{1}为false或ignore参数被忽略时，代表强制使用匿名，如果匿名失败将取消该消息的发送。
     * 举例：
     * [CQ:anonymous,ignore=true]
     * [CQ:anonymous]
     */
    override fun anonymous(ignore: Boolean): KQCode = if(ignore) Anonymous else AnonymousCompulsory


    /**
     * 音乐
     * [CQ:music,type={1},id={2},style={3}]
     * {1} 音乐平台类型，目前支持qq、163
     * {2} 对应音乐平台的数字音乐id
     * {3} 音乐卡片的风格。仅 Pro 支持该参数，该参数可被忽略。
     * 注意：音乐只能作为单独的一条消息发送
     * 例子
     * [CQ:music,type=qq,id=422594]（发送一首QQ音乐的“Time after time”歌曲到群内）
     * [CQ:music,type=163,id=28406557]（发送一首网易云音乐的“桜咲く”歌曲到群内）
     */
    override fun music(type: String, id: String, style: String?): KQCode {
        return if (style != null) {
            MapKQCode("music", "type" to type, "id" to id, "style" to style)
        }else{
            MapKQCode("music", "type" to type, "id" to id)
        }
    }

    /**
     * [CQ:music,type=custom,url={1},audio={2},title={3},content={4},image={5}] - 发送音乐自定义分享
     * 注意：音乐自定义分享只能作为单独的一条消息发送
     * @param url   {1}为分享链接，即点击分享后进入的音乐页面（如歌曲介绍页）。
     * @param audio {2}为音频链接（如mp3链接）。
     * @param title  {3}为音乐的标题，建议12字以内。
     * @param content  {4}为音乐的简介，建议30字以内。该参数可被忽略。
     * @param image  {5}为音乐的封面图片链接。若参数为空或被忽略，则显示默认图片。
     *
     */
    override fun customMusic(url: String, audio: String, title: String, content: String?, image: String?): KQCode {
        return if(content != null && image != null){
            MapKQCode("music", "type" to "custom", "url" to url, "audio" to audio, "title" to title, "content" to content, "image" to image)
        }else{
            val list: MutableList<Pair<String, String>> = mutableListOf("type" to "custom", "url" to url, "audio" to audio, "title" to title)
            content?.run {
                list.add("content" to this)
            }
            image?.run {
                list.add("image" to this)
            }
            MapKQCode("music", *list.toTypedArray())
        }
    }

    /**
     * [CQ:share,url={1},title={2},content={3},image={4}] - 发送链接分享
     * {1}为分享链接。
     * {2}为分享的标题，建议12字以内。
     * {3}为分享的简介，建议30字以内。该参数可被忽略。
     * {4}为分享的图片链接。若参数为空或被忽略，则显示默认图片。
     * 注意：链接分享只能作为单独的一条消息发送
     */
    override fun share(url: String, title: String, content: String?, image: String?): KQCode {
        return if(content != null && image != null){
            MapKQCode("share", "url" to url, "title" to title, "content" to content, "image" to image)
        }else{
            val list: MutableList<Pair<String, String>> = mutableListOf("url" to url, "title" to title)
            content?.run {
                list.add("content" to this)
            }
            image?.run {
                list.add("image" to this)
            }
            MapKQCode("share", *list.toTypedArray())
        }
    }

    /**
     * 地点
     * [CQ:location,lat={1},lon={2},title={3},content={4}]
     * {1} 纬度
     * {2} 经度
     * {3} 分享地点的名称
     * {4} 分享地点的具体地址
     */
    override fun location(lat: String, lon: String, title: String, content: String): KQCode = MapKQCode("location", "lat" to lat, "lon" to lon, "title" to title, "content" to content)



}

