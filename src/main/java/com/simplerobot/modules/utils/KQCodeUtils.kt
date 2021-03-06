/*
 *
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  simple-robot-module-cqcodeutils
 *  File     KQCodeUtils.kt
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

import com.simplerobot.modules.utils.codes.FastKQCode
import com.simplerobot.modules.utils.codes.MapKQCode

/*
& -> &amp;
[ -> &#91;
] -> &#93;
 */
/*
& -> &amp;
[ -> &#91;
] -> &#93;
, -> &#44;
 */

private val CQ_SPLIT_REGEX: Regex = Regex(CQ_KV)

/** CQ Decoder */
@Suppress("MemberVisibilityCanBePrivate")
object CQDecoder {

    @JvmStatic
    val instance
        get() = this

    /** 非CQ码文本消息解义 */
    fun decodeText(str: String): String =
        str.replace("&#91;", "[")
            .replace("&#93;", "]")
            .replace("&#09;", "\t")
            .replace("&#10;", "\r")
            .replace("&#13;", "\n")
            .replace("&amp;", "&")

    /** 非CQ码文本消息解义，如果[str]为null则返回null */
    fun decodeTextOrNull(str: String?): String? = str?.let { decodeText(it) }


    /** CQ码参数值消息解义 */
    fun decodeParams(str: String): String =
        str.replace("&#91;", "[")
            .replace("&#93;", "]")
            .replace("&#44;", ",")
            .replace("&#09;", "\t")
            .replace("&#10;", "\r")
            .replace("&#13;", "\n")
            .replace("&amp;", "&")

    /** CQ码参数值消息解义，如果[str]为null则返回null */
    fun decodeParamsOrNull(str: String?): String? = str?.let { decodeParams(it) }

}

/** CQ Encoder */
@Suppress("MemberVisibilityCanBePrivate")
object CQEncoder {

    @JvmStatic
    val instance
        get() = this

    /** 非CQ码文本消息转义 */
    fun encodeText(str: String): String =
        str.replace("&", "&amp;")
            .replace("[", "&#91;")
            .replace("]", "&#93;")
            .replace("\t", "&#09;")
            .replace("\r", "&#10;")
            .replace("\n", "&#13;")

    /** 非CQ码文本消息转义。如果[str]为null则返回null */
    fun encodeTextOrNull(str: String?): String? = str?.let { encodeText(it) }

    /** CQ码参数值消息转义 */
    fun encodeParams(str: String): String =
        str.replace("&", "&amp;")
            .replace("[", "&#91;")
            .replace("]", "&#93;")
            .replace("\t", "&#09;")
            .replace("\r", "&#10;")
            .replace("\n", "&#13;")

    /** CQ码参数值消息转义。如果[str]为null则返回null */
    fun encodeParamsOrNull(str: String?): String? = str?.let { encodeParams(it) }

}


/**
 * CQ码的操作工具类
 * 相对于[com.forte.qqrobot.utils.CQCodeUtil], 此工具类的泛用性更高, 其内部会存在一些以字符串操作为主的方法。
 * 当然，也会提供一些与[com.forte.qqrobot.beans.cqcode.CQCode]等基础工具的相互操作的方法。
 */
object KQCodeUtils {


    /**
     *  获取一个String为载体的[模板][CodeTemplate]
     *  @see KQCodeStringTemplate
     */
    val stringTemplate: CodeTemplate<String> get() = KQCodeStringTemplate

    /**
     *  获取[KQCode]为载体的[模板][CodeTemplate]
     *  @see KQCodeTemplate
     */
    val kqCodeTemplate: CodeTemplate<KQCode> get() = KQCodeTemplate

    /**
     * 构建一个String为载体类型的[构建器][CodeBuilder]
     */
    fun getStringBuilder(type: String): CodeBuilder<String> = CodeBuilder.stringBuilder(type)


    /**
     * 构建一个[KQCode]为载体类型的[构建器][CodeBuilder]
     */
    fun getKQCodeBuilder(type: String): CodeBuilder<KQCode> = CodeBuilder.kqCodeBuilder(type)


    @JvmStatic
    val instance
        get() = this

    /**
     * 仅通过一个类型获取一个CQ码。例如`\[CQ:hi]`
     */
    fun toCq(type: String): String {
        return "$CQ_HEAD$type$CQ_END"
    }

    /**
     * 将参数转化为CQ码字符串.
     * 如果[encode] == true, 则会对[pair]的值进行[转义][CQEncoder.encodeParams]
     *
     * @since 1.0-1.11
     */
    @JvmOverloads
    fun toCq(type: String, encode: Boolean = true, vararg pair: Pair<String, Any>): String {
        val pre = "${CQ_HEAD}$type"
        return if (pair.isNotEmpty()) {
            pair.joinToString(CQ_SPLIT, "$pre$CQ_SPLIT", CQ_END) {
                "${it.first}$CQ_KV${
                    if (encode) CQEncoder.encodeParams(it.second.toString()) else it.second
                }"
            }
        } else {
            pre + CQ_END
        }
    }

    /**
     * 将参数转化为CQ码字符串
     * @since 1.0-1.11
     */
    @JvmOverloads
    fun toCq(type: String, encode: Boolean = true, map: Map<String, *>): String {
        val pre = "$CQ_HEAD$type"
        return if (map.isNotEmpty()) {
            map.entries.joinToString(
                CQ_SPLIT,
                "$pre$CQ_SPLIT",
                CQ_END
            ) {
                "${it.key}$CQ_KV${
                    if (encode) CQEncoder.encodeParams(it.value.toString()) else it.value
                }"
            }
        } else {
            pre + CQ_END
        }
    }

    /**
     * 将参数转化为CQ码字符串, [params]的格式应当是`xxx=xxx`
     * 如果[encode] == true, 则说明需要对`=`后的值进行转义。
     * 如果[encode] == false, 则不会对参数值进行转义，直接拼接为CQ字符串
     * @since 1.8.0
     */
    @JvmOverloads
    fun toCq(type: String, encode: Boolean = true, vararg params: String): String {
        // 如果参数为空
        return if (params.isNotEmpty()) {
            if (encode) {
                toCq(type, encode, *params.map {
                    val split: List<String> = it.split(CQ_SPLIT_REGEX, 2)
                    split[0] to split[1]
                }.toTypedArray())
            } else {
                // 不需要转义, 直接进行字符串拼接
                "$CQ_HEAD$type$CQ_SPLIT${params.joinToString(CQ_SPLIT)}$CQ_END"
            }
        } else {
            "$CQ_HEAD$type$CQ_END"
        }
    }

    /**
     * 获取无参数的[KQCode]
     * @param type cq码的类型
     */
    fun toKq(type: String): KQCode = EmptyKQCode(type)

    /**
     * 根据[Map]类型参数转化为[KQCode]实例
     *
     * @param type cq码的类型
     * @param params 参数列表
     */
    fun toKq(type: String, params: Map<String, *>): KQCode {
        return if (params.isEmpty()) {
            EmptyKQCode(type)
        } else {
            MapKQCode(type, params.asSequence().map { it.key to it.value.toString() }.toMap())
        }
    }


    /**
     * 根据参数转化为[KQCode]实例
     * @param type cq码的类型
     * @param params 参数列表
     */
    fun toKq(type: String, vararg params: Pair<String, *>): KQCode {
        return if (params.isEmpty()) {
            EmptyKQCode(type)
        } else {
            MapKQCode(type, params.asSequence().map { it.first to it.second.toString() }.toMap())
        }
    }


    /**
     * 根据参数转化为[KQCode]实例
     * @param type cq码的类型
     * @param paramText 参数列表, 例如："qq=123"
     */
    @JvmOverloads
    fun toKq(type: String, encode: Boolean = false, vararg paramText: String): KQCode {
        return if (paramText.isEmpty()) {
            EmptyKQCode(type)
        } else {
            if (encode) {
                FastKQCode.byCode(toCq(type, encode, *paramText))
            } else {
                MapKQCode.byParamString(type, *paramText)
            }
        }
    }

    /**
     * 将一段字符串根据字符串与CQ码来进行切割。
     * 不会有任何转义操作。
     * @since 1.1-1.11
     */
    fun split(text: String): List<String> = split(text) { this }

    /**
     * 将一段字符串根据字符串与CQ码来进行切割,
     * 并可以通过[postMap]对切割后的每条字符串进行后置处理。
     *
     * 不会有任何转义操作。
     *
     * @param text 文本字符串
     * @param postMap 后置转化函数
     * @since 1.8.0
     */
    inline fun <T> split(text: String, postMap: String.() -> T): List<T> {
        // 准备list
        val list: MutableList<T> = mutableListOf()

        val het = CQ_HEAD
        val ent = CQ_END

        // 查找最近一个[CQ:字符
        var h = text.indexOf(het)
        var le = -1
        var e = -1
        while (h >= 0) {
            // 从头部开始查询尾部
            if (e != -1) {
                le = e
            }
            e = text.indexOf(ent, h)
            h = if (e < 0) {
                // 没找到，查找下一个[CQ:
                text.indexOf(het, h + 1)
            } else {
                // 找到了，截取。
                // 首先截取前一段
                if (h > 0 && (le + 1) != h) {
                    list.add(text.substring(le + 1, h).postMap())
                }
                // 截取cq码
                list.add(text.substring(h, e + 1).postMap())
                // 重新查询
                text.indexOf(het, e)
            }
        }
        if (list.isEmpty()) {
            list.add(text.postMap())
        }
        if (e != text.length - 1) {
            if (e >= 0) {
                list.add(text.substring(e + 1, text.length).postMap())
            }
        }
        return list
    }

    /**
     * 从消息字符串中提取出CQ码字符串
     * @param text 消息字符串
     * @param index 第几个索引位的CQ码，默认为0，即第一个
     * @since 1.1-1.11
     */
    @JvmOverloads
    fun getCq(text: String, type: String = "", index: Int = 0): String? {
        if (index < 0) {
            throw IndexOutOfBoundsException("$index")
        }

        var i = -1
        var ti: Int
        var e = 0
        val het = CQ_HEAD + type
        val ent = CQ_END

        do {
            ti = text.indexOf(het, e)
            if (ti >= 0) {
                e = text.indexOf(ent, ti)
                if (e >= 0) {
                    i++
                } else {
                    e = ti + 1
                }
            }
        } while (ti >= 0 && i < index)
        return if (i == index) {
            text.substring(ti, e + 1)
        } else {
            null
        }
    }

    /**
     * 从消息字符串中提取出CQ码字符串
     * @param text 消息字符串
     * @param index 第几个索引位的CQ码，默认为0，即第一个
     * @since 1.1-1.11
     */
//    @JvmOverloads
    fun getCq(text: String, index: Int = 0): String? = getCq(text = text, type = "", index = index)


    /**
     * 提取字符串中的全部CQ码字符串
     * @since 1.1-1.11
     */
    @JvmOverloads
    fun getCqs(text: String, type: String = ""): List<String> = getCqs(text, type) { it }

    /**
     * 提取字符串中的全部CQ码字符串
     * @since 1.8.0
     */
    @JvmOverloads
    inline fun <T> getCqs(text: String, type: String = "", map: (String) -> T): List<T> {
        var ti: Int
        var e = 0
        val het = CQ_HEAD + type
        val ent = CQ_END
        // temp list
        val list: MutableList<T> = mutableListOf()

        do {
            ti = text.indexOf(het, e)
            if (ti >= 0) {
                e = text.indexOf(ent, ti)
                if (e >= 0) {
                    list.add(map(text.substring(ti, e + 1)))
                } else {
                    e = ti + 1
                }
            }
        } while (ti >= 0 && e >= 0)

        return list
    }

    /**
     * 获取文本中的CQ码的参数。
     * 如果文本为null、找不到对应索引的CQ码、找不到此key，返回null；如果找到了key但是无参数，返回空字符串
     *
     * 默认情况下获取第一个CQ码的参数
     * @since 1.1-1.11
     */
    fun getParam(text: String, paramKey: String, index: Int = 0): String? =
        getParam(text = text, paramKey = paramKey, type = "", index = index)

    /**
     * 获取文本中的CQ码的参数。
     * 如果文本为null、找不到对应索引的CQ码、找不到此key，返回null；如果找到了key但是无参数，返回空字符串
     *
     * 默认情况下获取第一个CQ码的参数
     * @since 1.1-1.11
     */
    @JvmOverloads
    fun getParam(text: String, paramKey: String, type: String = "", index: Int = 0): String? {
        val cqHead = CQ_HEAD + type
        val cqEnd = CQ_END
        val cqSpl = CQ_SPLIT

        var from = -1
        var end = -1
        var i = -1
        do {
            from = text.indexOf(cqHead, from + 1)
            if (from >= 0) {
                // 寻找结尾
                end = text.indexOf(cqEnd, from)
                if (end >= 0) {
                    i++
                }
            }
        } while (from >= 0 && i < index)

        // 索引对上了
        if (i == index) {
            // 从from开始找参数
            val paramFind = ",$paramKey="
            val phi = text.indexOf(paramFind, from)
            if (phi < 0) {
                return null
            }
            // 找到了之后，找下一个逗号，如果没有，就用最终结尾的位置
            val startIndex = phi + paramFind.length
            var pei = text.indexOf(cqSpl, startIndex)
            // 超出去了
            if (pei < 0 || pei > end) {
                pei = end
            }
            if (startIndex > text.lastIndex || startIndex > pei) {
                return null
            }
            return text.substring(startIndex, pei)
        } else {
            return null
        }
    }

    /**
     * 获取文本字符串中CQ码字符串的迭代器
     * @since 1.1-1.11
     * @param text 存在CQ码正文的文本
     * @param type 要获取的CQ码的类型，如果为空字符串则视为所有，默认为所有。
     */
    @JvmOverloads
    fun getCqIter(text: String, type: String = ""): Iterator<String> = CqTextIterator(text, type)


    /**
     * 为一个CQ码字符串得到他的key迭代器
     * @param code cq码字符串
     * @since 1.8.0
     */
    fun getCqKeyIter(code: String): Iterator<String> = CqParamKeyIterator(code)

    /**
     * 为一个CQ码字符串得到他的value迭代器
     * @param code cq码字符串
     * @since 1.8.0
     */
    fun getCqValueIter(code: String): Iterator<String> = CqParamValueIterator(code)


    /**
     * 为一个CQ码字符串得到他的key-value的键值对迭代器
     * @param code cq码字符串
     * @since 1.8.0
     */
    fun getCqPairIter(code: String): Iterator<Pair<String, String>> = CqParamPairIterator(code)


    /**
     * @see getKqs
     */
    @Suppress("DEPRECATION")
    @Deprecated("param 'decode' not required.")
    fun getKqs(text: String, type: String, decode: Boolean): List<KQCode> {
        val iter = getCqIter(text, type)
        val list = mutableListOf<KQCode>()
        iter.forEach { list.add(KQCode.of(it, decode)) }
        return list
    }

    /**
     * 以[getCqIter]方法为基础获取字符串中全部的Kqs对象
     * @since 1.1-1.11
     * @param text 存在CQ码正文的文本
     * @param type 要获取的CQ码的类型，如果为空字符串则视为所有，默认为所有。
     */
    @JvmOverloads
    fun getKqs(text: String, type: String = ""): List<KQCode> {
        val iter: Iterator<String> = getCqIter(text, type)
        val list: MutableList<KQCode> = mutableListOf()
        iter.forEach { list.add(KQCode.of(it)) }
        return list
    }


    /**
     * @see getKq
     */
    @Suppress("DEPRECATION")
    @Deprecated("param 'decode' not required.")
    fun getKq(text: String, type: String = "", index: Int = 0, decode: Boolean = true): KQCode? {
        val cq = getCq(text, type, index) ?: return null
        return KQCode.of(cq, decode)
    }

    /**
     * 获取指定索引位的cq码，并封装类KQ对象
     * @param text 存在CQ码的正文
     * @param type 要获取的CQ码的类型，默认为所有类型
     * @param index 获取的索引位的CQ码，默认为0，即第一个
     */
    @JvmOverloads
    fun getKq(text: String, type: String = "", index: Int = 0): KQCode? {
        val cq: String = getCq(text, type, index) ?: return null
        return KQCode.of(cq)
    }

    /**
     * 获取指定索引位的cq码，并封装类KQ对象
     */
    @Suppress("MemberVisibilityCanBePrivate")
    fun getKq(text: String, index: Int = 0): KQCode? = getKq(text = text, type = "", index = index)

    /**
     * 移除CQ码，可指定类型
     * 具体使用参考[remove] 和 [removeByType]
     * @since 1.2-1.12
     */
    private fun removeCode(
        type: String,
        text: String,
        trim: Boolean = true,
        ignoreEmpty: Boolean = true,
        delimiter: CharSequence = ""
    ): String {
        when {
            text.isEmpty() -> {
                return text
            }
            else -> {
                val sb = StringBuilder(text.length)
                // 移除所有的CQ码
                val head = CQ_HEAD + type
                val end = CQ_END

                var hi: Int = -1
                var ei = -1
                var nextHi: Int
                var sps = 0
                var sub: String
                var next: Char

                if (text.length < head.length + end.length) {
                    return text
                }

                if (!text.contains(head)) {
                    return text
                }

                do {
                    hi++
                    hi = text.indexOf(head, hi)
                    next = text[hi + head.length]
                    // 如果text存在内容，则判断：下一个不是逗号或者结尾
                    if (type.isNotEmpty() && (next != ',' && next.toString() != end)) {
                        continue
                    }
                    if (hi >= 0) {
                        // 有一个头
                        // 寻找下一个尾
                        ei = text.indexOf(end, hi)
                        if (ei > 0) {
                            // 有一个尾，看看下一个头是不是在下一个尾之后
                            nextHi = text.indexOf(head, hi + 1)
                            // 如果中间包着一个头，则这个头作为当前头
                            if (nextHi in 0 until ei) {
                                hi = nextHi
                            }
                            if (hi > 0) {
                                if (sps > 0) {
                                    sps++
                                }
                                sub = text.substring(sps, hi)
                                if (!ignoreEmpty || (ignoreEmpty && sub.isNotBlank())) {
                                    if (trim) {
                                        sub = sub.trim()
                                    }
                                    if (sb.isNotEmpty()) {
                                        sb.append(delimiter)
                                    }
                                    sb.append(sub)
                                }
                                sps = ei
                            } else if (hi == 0) {
                                sps = ei

                            }
                        }
                    }
                } while (hi >= 0 && ei > 0)

                // 没有头了
                if (sps != text.lastIndex) {
                    sub = text.substring(sps + 1)
                    if (!ignoreEmpty || (ignoreEmpty && sub.isNotBlank())) {
                        if (trim) {
                            sub = sub.trim()
                        }
                        if (sb.isNotEmpty()) {
                            sb.append(delimiter)
                        }
                        sb.append(sub)
                    }
                }
                return sb.toString()
            }
        }
    }

    /**
     * 移除字符串中的所有的CQ码，返回字符串
     * 必须是完整的\[CQ...]
     * @param text 文本正文
     * @param trim 是否对文本执行trim，默认为true
     * @param ignoreEmpty 如果字符为纯空白字符，是否忽略
     * @param delimiter 切割字符串
     */
    @JvmOverloads
    fun remove(
        text: String,
        trim: Boolean = true,
        ignoreEmpty: Boolean = true,
        delimiter: CharSequence = ""
    ): String {
        return removeCode("", text, trim, ignoreEmpty, delimiter)
    }

    /**
     * 移除某个类型的字符串中的所有的CQ码，返回字符串
     * 必须是完整的\[CQ...]
     * @param type CQ码的类型
     * @param text 文本正文
     * @param trim 是否对文本执行trim，默认为true
     * @param ignoreEmpty 如果字符为纯空白字符，是否忽略
     * @param delimiter 切割字符串
     */
    @JvmOverloads
    fun removeByType(
        type: String,
        text: String,
        trim: Boolean = true,
        ignoreEmpty: Boolean = true,
        delimiter: CharSequence = ""
    ): String {
        return removeCode(type, text, trim, ignoreEmpty, delimiter)
    }


}






