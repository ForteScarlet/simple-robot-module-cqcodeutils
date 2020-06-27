package com.simplerobot.modules.utils

import com.simplerobot.modules.utils.KQCodeUtils.remove
import com.simplerobot.modules.utils.KQCodeUtils.removeByType
import java.lang.IllegalArgumentException

/**
 * mirai码工具类。
 * \[mirai:key=value]
 * \[GroupImage]: "[mirai:image:{01E9451B-70ED-EAE3-B37C-101F1EEBF5B5}.mirai]"
 * \[FriendImage]: "[mirai:image:/f8f1ab55-bf8e-4236-b55e-955848d7069f]"
 * \[PokeMessage]: "[mirai:poke:1,-1]"
 * 类型即参数名，且参数只有一个
 * 且似乎不存在转义符
 *
 * @since
 */
object MQCodeUtils {
    @JvmStatic
    val instance = this

    private const val MQ_HEAD = "[mirai:"
    private const val MQ_END = "]"
    private const val MQ_KV = ":"


    /**
     * 将字符串转化为mirai码。
     * 官方并不会解析mirai（至少现在没有），因此此方法意义不大
     * @param type 类型，也是参数名
     */
    fun toMq(type: String, param: String) = "$MQ_HEAD$type:$param$MQ_END"


    /**
     * 解析一串字符串，并转化为[MQCode]对象，如果能够转化的话。
     */
    fun toMqCode(str: String): MQCode {
        if (str.isBlank()) {
            throw IllegalArgumentException("blank string.")
        }

        // 获取前两个冒号
        val i1 = str.indexOf(':')
        val i2 = str.indexOf(':', i1 + 1)
        val end = str.indexOf(']')

        if (i1 < 0 || i2 < 0 || end < 0) {
            throw IllegalArgumentException("cannot parse $str to MqCode.")
        }

        val t = str.substring(i1 + 1, i2)
        val value = str.substring(i2 + 1, end)
        return MQCode(t, value)
    }

    /**
     * 将mirai码转化为[MQCode]对象
     */
    fun toMqCode(type: String, param: String) = MQCode(type, param)


    /**
     * 获取一段文字中某个索引位的mirai码字符串
     * 找不到会返回null
     * @param text 字符串
     * @param type mirai码的类型, 默认为任意类型
     * @param index 获取的索引位
     */
    @JvmOverloads
    fun getMq(text: String, type: String = "", index: Int = 0): String? {
        if (text.isBlank()) {
            return null
        }
        if (index < 0) {
            throw IndexOutOfBoundsException("$index")
        }

        var i = -1
        var ti: Int
        var e = 0
        val het = MQ_HEAD + type
        val ent = MQ_END

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
     * 获取一段文字中某个索引位的mirai码字符串
     * @param text 字符串
     * @param index 获取的索引位
     *
     * @see [getMq]
     */
    fun getMq(text: String, index: Int = 0): String? = getMq(text = text, type = "", index = index)


    /**
     * 将获取到的结果转化为[MQCode]对象
     * @param text 正文
     * @param type 类型
     * @param index 索引
     */
    @JvmOverloads
    fun getMqCode(text: String, type: String = "", index: Int = 0): MQCode? {
        val mq = getMq(text, type, index)
        return if (mq == null) {
            null
        } else {
            toMqCode(mq)
        }
    }


    /**
     * 将获取到的结果转化为[MQCode]对象
     * @param text 正文
     * @param type 类型
     * @param index 索引
     */
    fun getMqCode(text: String, index: Int = 0): MQCode? = getMqCode(text = text, type = "", index = index)

    /**
     * 获取一段文字中的所有的mirai码，可指定类型
     * 找不到会返回空数组，text为null会返回null
     * @param text 文本
     * @param type 查找的类型，默认所有
     */
    @JvmOverloads
    fun getMqs(text: String, type: String = ""): Array<String> {
        if (text.isBlank()) {
            return arrayOf()
        }

        var ti = 0
        var e = 0
        val het = MQ_HEAD + type
        val ent = MQ_END
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
     * 获取字符串中所有的Mirai码并转化为[MQCode]对象
     * 找不到会返回空数组，text为null会返回null
     * @param text 文本
     * @param type 查找的类型，默认所有
     */
    @JvmOverloads
    fun getMqCodes(text: String, type: String = ""): Array<MQCode> {
        if (text.isBlank()) {
            return arrayOf()
        }

        var ti: Int
        var e = 0
        val het = MQ_HEAD + type
        val ent = MQ_END
        // temp list
        val list = mutableListOf<MQCode>()
        do {
            ti = text.indexOf(het, e)
            if (ti >= 0) {
                e = text.indexOf(ent, ti)
                if (e >= 0) {
                    list.add(toMqCode(text.substring(ti, e + 1)))
                } else {
                    e = ti + 1
                }
            }
        } while (ti >= 0 && e >= 0)
        return list.toTypedArray()
    }


    /**
     * 将文本中的mirai码与普通文本相互分离
     * @param text 正文文本
     */
    fun split(text: String): Array<String> {
        // 准备list
        val list = mutableListOf<String>()

        val het = MQ_HEAD
        val ent = MQ_END

        // 查找最近一个[mirai:字符
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
                    list.add(text.substring(le + 1, h))
                }
                // 截取cq码
                list.add(text.substring(h, e + 1))
                // 重新查询
                text.indexOf(het, e)
            }
        }
        if (list.isEmpty()) {
            list.add(text)
        }
        if (e != text.length - 1) {
            if (e >= 0) {
                list.add(text.substring(e + 1, text.length))
            }
        }
        return list.toTypedArray()
    }


    /**
     * 移除mira码，可指定类型
     * 具体使用参考[remove] 和 [removeByType]
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
                val head =MQ_HEAD + type
                val end = MQ_END

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
                    // 如果text存在内容，则判断：下一个不是结尾
                    if(type.isNotEmpty() && (next.toString() != end)){
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
            }
        }
    }

    /**
     * 移除字符串中的所有的mirai码，返回字符串
     * @param text 文本正文
     * @param trim 是否对文本执行trim，默认为true
     * @param ignoreEmpty 如果字符为纯空白字符，是否忽略
     * @param delimiter 切割字符串
     */
    @JvmOverloads
    fun remove(text: String, trim: Boolean = true, ignoreEmpty: Boolean = true, delimiter: CharSequence = ""): String? = removeCode("", text, trim, ignoreEmpty, delimiter)

    /**
     * 移除某个类型的字符串中的所有的mirai码，返回字符串
     * @param type mirai码的类型
     * @param text 文本正文
     * @param trim 是否对文本执行trim，默认为true
     * @param ignoreEmpty 如果字符为纯空白字符，是否忽略
     * @param delimiter 切割字符串
     */
    @JvmOverloads
    fun removeByType(type: String, text: String?, trim: Boolean = true, ignoreEmpty: Boolean = true, delimiter: CharSequence = "") = removeCode(type, text, trim, ignoreEmpty, delimiter)


    /**
     * 获取文本字符串中CQ码字符串的迭代器
     * @since 1.1-1.11
     */
    @JvmOverloads
    fun getMqIter(text: String, type: String = ""): Iterator<String> = MqIterator(text, type)


    /**
     * 文本Mirai码迭代器
     * @since 1.1-1.11
     */
    internal class MqIterator(private val text: String, private val type: String = "") : Iterator<String> {
        private var i = -1
        private var ti = 0
        private var e = 0
        private val het = MQ_HEAD + type
        private val ent = MQ_END
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


    /** 将其中的mirai码字符串转化为CQ码字符串 */
    fun replaceToCq(text: String): String {
        return split(text).asSequence().map {
            if(it.trim().startsWith(MQ_HEAD))
                toMqCode(it).toKQCode().toString()
            else it
        }.joinToString("")
    }



}


/** MQ code 封装数据类。
 * 参数不可变的数据类。
 *
 */
data class MQCode(val type: String, val param: String): CharSequence {

    /**
     * string value
     */
    private val value: String
    get() = "[mirai:$type:$param]"


    override fun toString(): String = value

    /**
     * 转化为KQCode对象。
     */
    fun toKQCode() = KQCode(type, type to param)

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


