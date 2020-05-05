package com.simplerobot.modules.utils

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

/** decoder */
object CQDecoder {

    @JvmStatic
    val instance = CQDecoder

    /** 非CQ码文本消息解义 */
    fun decodeText(str: String?) =
            str?.replace("&amp;", "&")
                    ?.replace("&#91;", "[")
                    ?.replace("&#93;", "]")

    /** CQ码参数值消息解义 */
    fun decodeParams(str: String?) =
            str?.replace("&amp;", "&")
                    ?.replace("&#91;", "[")
                    ?.replace("&#93;", "]")
                    ?.replace("&#44;", ",")
}

/** encoder */
object CQEncoder {

    @JvmStatic
    val instance = CQEncoder

    /** 非CQ码文本消息转义 */
    fun encodeText(str: String?) =
            str?.replace("&", "&amp;")
                    ?.replace("[", "&#91;")
                    ?.replace("]", "&#93;")

    /** CQ码参数值消息转义 */
    fun encodeParams(str: String?) =
            str?.replace("&", "&amp;")
                    ?.replace("[", "&#91;")
                    ?.replace("]", "&#93;")
                    ?.replace(",", "&#44;")


}

/**
 * CQ码的操作工具类
 * 相对于[com.forte.qqrobot.utils.CQCodeUtil], 此工具类的泛用性更高, 其内部会存在一些以字符串操作为主的方法。
 * 当然，也会提供一些与[com.forte.qqrobot.beans.cqcode.CQCode]等基础工具的相互操作的方法。
 */
object KQCodeUtils {

    private const val CQ_HEAD = "[CQ:"
    private const val CQ_END = "]"
    private const val CQ_SPLIT = ","
    private const val CQ_KV = "="

    @JvmStatic
    val instance = KQCodeUtils

    /**
     * 将参数转化为CQ码字符串
     * @since 1.0-1.11
     */
    fun toCq(type: String, vararg pair: Pair<String, Any>): String {
        val pre = "${CQ_HEAD}$type"
        return if (pair.isNotEmpty()) {
            pair.joinToString(CQ_SPLIT, "$pre$CQ_SPLIT", CQ_END) { "${it.first}$CQ_KV${CQEncoder.encodeParams(it.second.toString())}" }
        } else {
            pre + CQ_END
        }
    }

    /**
     * 将参数转化为CQ码字符串
     * @since 1.0-1.11
     */
    fun toCq(type: String, map: Map<String, *>): String {
        val pre = "${CQ_HEAD}$type"
        return if (map.isNotEmpty()) {
            map.entries.joinToString(CQ_SPLIT, "$pre$CQ_SPLIT", CQ_END) { "${it.key}$CQ_KV${CQEncoder.encodeParams(it.value.toString())}" }
        } else {
            pre + CQ_END
        }
    }

    /**
     * 将参数转化为CQ码字符串, [params]的格式应当是xxx=xxx xxx=xxx
     * @since 1.0-1.11
     */
    fun toCq(type: String, vararg params: String) = toCq(type, *params.map {
        val split = it.split(Regex("="), 2)
        split[0] to split[1]
    }.toTypedArray())


    /**
     * 将一段字符串根据字符串与CQ码来进行切割。
     * 不会有任何转义操作。
     * @since 1.1-1.11
     */
    fun split(text: String?): Array<String>? {
        if (text == null) {
            return null
        } else {
            // 准备list
            val list = mutableListOf<String>()

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
                if (e < 0) {
                    // 没找到，查找下一个[CQ:
                    h = text.indexOf(het, h + 1)
                } else {
                    // 找到了，截取。
                    // 首先截取前一段
                    if (h > 0 && (le + 1) != h) {
                        list.add(text.substring(le + 1, h))
                    }
                    // 截取cq码
                    list.add(text.substring(h, e + 1))
                    // 重新查询
                    h = text.indexOf(het, e)
                }
            }
            if (list.isEmpty()) {
                list.add(text)
            }
            if (e != text.length - 1) {
                if (e < 0) {
                    list.add(text.substring(le + 1, text.length))
                } else {
                    list.add(text.substring(e + 1, text.length))
                }
            }
            return list.toTypedArray()
        }
    }

    /**
     * 从消息字符串中提取出CQ码字符串
     * @param text 消息字符串
     * @param index 第几个索引位的CQ码，默认为0，即第一个
     * @since 1.1-1.11
     */
    @JvmOverloads
    fun getCq(text: String?, type: String = "", index: Int = 0): String? {
        if (text == null) {
            return null
        }
        if (index < 0) {
            throw IndexOutOfBoundsException("$index")
        }

        var i = -1
        var ti = 0
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
    fun getCq(text: String?, index: Int = 0): String? = getCq(text = text, type = "", index = index)

    /**
     * 提取字符串中的全部CQ码字符串
     * @since 1.1-1.11
     */
    @JvmOverloads
    fun getCqs(text: String?, type: String = ""): Array<String>? {
        if (text == null) {
            return null
        }

        var ti = 0
        var e = 0
        val het = CQ_HEAD + type
        val ent = CQ_END
        // temp list
        val list = mutableListOf<String>()

        do {
            ti = text.indexOf(het, e)
            if (ti >= 0) {
                e = text.indexOf(ent, ti)
                if (e >= 0) {
                    list.add(text.substring(ti, e + 1))
                } else {
                    e = ti + 1
                }
            }
        } while (ti >= 0 && e >= 0)

        return list.toTypedArray()
    }

    /**
     * 获取文本中的CQ码的参数。
     * 如果文本为null、找不到对应索引的CQ码、找不到此key，返回null；如果找到了key但是无参数，返回空字符串
     *
     * 默认情况下获取第一个CQ码的参数
     * @since 1.1-1.11
     */
    fun getParam(text: String?, paramKey: String, index: Int = 0): String? = getParam(text = text, paramKey = paramKey, type = "", index = index)

    /**
     * 获取文本中的CQ码的参数。
     * 如果文本为null、找不到对应索引的CQ码、找不到此key，返回null；如果找到了key但是无参数，返回空字符串
     *
     * 默认情况下获取第一个CQ码的参数
     * @since 1.1-1.11
     */
    @JvmOverloads
    fun getParam(text: String?, paramKey: String, type: String = "", index: Int = 0): String? {
        if (text == null) {
            return null
        }

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
            if(startIndex > text.lastIndex || startIndex > pei){
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
     */
    @JvmOverloads
    fun getCqIter(text: String?, type: String = ""): Iterator<String>? {
        if (text == null) {
            return null
        }
        return CqIterator(text, type)
    }


    /**
     * 文本CQ码迭代器
     * @since 1.1-1.11
     */
    class CqIterator(private val text: String, private val type: String = "") : Iterator<String> {
        private var i = -1
        private var ti = 0
        private var e = 0
        private val het = CQ_HEAD + type
        private val ent = CQ_END
        private var next: String? = null
        private var get = true

        /**
         * Returns `true` if the iteration has more elements.
         */
        override fun hasNext(): Boolean {
            if (!get) {
                return next != null
            }
            get = false
            do {
                ti = text.indexOf(het, e)
                if (ti >= 0) {
                    e = text.indexOf(ent, ti)
                    if (e >= 0) {
                        i++
                        next = text.substring(ti, e + 1)
                    } else {
                        e = ti + 1
                    }
                }
            } while (next == null && (ti >= 0 && e >= 0))

            return next != null
        }

        /**
         * Returns the next element in the iteration.
         */
        override fun next(): String {
            val n = next
            next = null
            get = true
            return n!!
        }

    }

    /**
     * 以[getCqIter]方法为基础获取字符串中全部的Kqs对象
     * @since 1.1-1.11
     */
    @JvmOverloads
    fun getKqs(text: String?, type: String = ""): Array<KQCode>? {
        val iter = getCqIter(text, type)
        return if (iter != null) {
            val list = mutableListOf<KQCode>()
            iter.forEach { list.add(KQCode.of(it)) }
            list.toTypedArray()
        } else {
            null
        }
    }

    /**
     * 获取指定索引位的cq码，并封装类KQ对象
     */
    @JvmOverloads
    fun getKq(text: String?, type: String = "", index: Int = 0): KQCode? {
        val cq = getCq(text, type, index)
        return if (cq == null) {
            return null
        } else {
            KQCode.of(cq)
        }
    }

    /**
     * 获取指定索引位的cq码，并封装类KQ对象
     */
    fun getKq(text: String?, index: Int = 0): KQCode? = getKq(text = text, type = "", index = index)

    /**
     * 移除CQ码，可指定类型
     * 具体使用参考[remove] 和 [removeByType]
     * @since 1.2-1.12
     */
    private fun removeCode(type: String, text: String?, trim: Boolean = true, ignoreEmpty: Boolean = true, delimiter: CharSequence = ""): String? {
        when {
            text == null -> {
                return null
            }
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
                var sps = 0;
                var sub: String
                var next: Char

                if(text.length < head.length + end.length){
                    return text
                }

                do {
                    hi++
                    hi = text.indexOf(head, hi)
                    next = text[hi + head.length]
                    // 如果text存在内容，则判断：下一个不是逗号或者结尾
                    if(type.isNotEmpty() && (next != ',' && next.toString() != end)){
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
                                if(!ignoreEmpty || (ignoreEmpty && sub.isNotBlank())){
                                    if (trim) {
                                        sub = sub.trim()
                                    }
                                    if (sb.isNotEmpty()) {
                                        sb.append(delimiter)
                                    }
                                    sb.append(sub)
                                }
                                sps = ei
                            }else if(hi == 0){
                                sps = ei

                            }
                        }
                    }
                } while (hi >= 0 && ei > 0)

                // 没有头了
                if (sps != text.lastIndex) {
                    sub = text.substring(sps + 1)
                    if(!ignoreEmpty || (ignoreEmpty && sub.isNotBlank())){
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
//                 返回结果
//                return if (sb.isEmpty() && text.isNotEmpty()) {
//                    text
//                } else {
//                    sb.toString()
//                }
            }
        }
    }

    /**
     * 移除字符串中的所有的CQ码，返回字符串
     * 必须是完整的\[CQ...]
     * @param type CQ码的类型
     * @param text 文本正文
     * @param trim 是否对文本执行trim，默认为true
     * @param ignoreEmpty 如果字符为纯空白字符，是否忽略
     * @param delimiter 切割字符串
     */
    @JvmOverloads
    fun remove(text: String?, trim: Boolean = true, ignoreEmpty: Boolean = true, delimiter: CharSequence = ""): String? = removeCode("", text, trim, ignoreEmpty, delimiter)

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
    fun removeByType(type: String, text: String?, trim: Boolean = true, ignoreEmpty: Boolean = true, delimiter: CharSequence = "") = removeCode(type ?: "", text, trim, ignoreEmpty, delimiter)


}




