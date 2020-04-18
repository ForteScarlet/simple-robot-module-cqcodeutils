package com.simplerobot.modules.utils

import java.lang.IndexOutOfBoundsException
import java.util.*

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
                if(e < 0){
                    list.add(text.substring(le+1, text.length))
                }else{
                    list.add(text.substring(e+1, text.length))
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
    fun getCq(text: String?, index: Int = 0): String? {
        if(text == null){
            return null
        }
        if(index < 0){
            throw IndexOutOfBoundsException("$index")
        }

        var i = -1
        var ti = 0
        var e = 0
        val het = CQ_HEAD
        val ent = CQ_END

        do {
            ti = text.indexOf(het, e)
            if(ti >= 0){
                e = text.indexOf(ent, ti)
                if(e >= 0){
                    i++
                }else{
                    e = ti+1
                }
            }
        }while( ti >= 0 && i < index )
        return if(i == index){
            text.substring(ti, e+1)
        }else{
            null
        }
    }

    /**
     * 提取字符串中的全部CQ码字符串
     * @since 1.1-1.11
     */
    fun getCqs(text: String?): Array<String>? {
        if(text == null){
            return null
        }

        var ti = 0
        var e = 0
        val het = CQ_HEAD
        val ent = CQ_END
        // temp list
        val list = mutableListOf<String>()

        do {
            ti = text.indexOf(het, e)
            if(ti >= 0){
                e = text.indexOf(ent, ti)
                if(e >= 0){
                    list.add(text.substring(ti, e+1))
                }else{
                    e = ti+1
                }
            }
        }while( ti >= 0 && e >= 0 )

        return list.toTypedArray()
    }

    /**
     * 获取文本中的CQ码的参数。
     * 如果文本为null、找不到对应索引的CQ码、找不到此key，返回null；如果找到了key但是无参数，返回空字符串
     *
     * 默认情况下获取第一个CQ码的参数
     * @since 1.1-1.11
     */
    @JvmOverloads
    fun getParam(text: String?, paramKey: String, index: Int = 0): String? {
        if(text == null){
            return null
        }

        val cqHead = CQ_HEAD
        val cqEnd = CQ_END
        val cqSpl = CQ_SPLIT

        var from = -1
        var end = -1
        var i = -1
        do {
            from = text.indexOf(cqHead, from + 1)
            if(from >= 0){
                // 寻找结尾
                end = text.indexOf(cqEnd, from)
                if(end >= 0){
                    i++
                }
            }
        }while (from >= 0 && i < index)

        // 索引对上了
        if(i == index){
            // 从from开始找参数
            val paramFind = ",$paramKey="
            val phi = text.indexOf(paramFind, from)
            if(phi < 0){
                return null
            }
            // 找到了之后，找下一个逗号，如果没有，就用最终结尾的位置
            var pei = text.indexOf(",", phi+paramFind.length)
            if(pei < 0 || pei > end){
                pei = end
            }
            return text.substring(phi + paramFind.length, pei)
        }else{
            return null
        }
    }

    /**
     * 获取文本字符串中CQ码字符串的迭代器
     * @since 1.1-1.11
     */
    fun getCqIter(text: String?): Iterator<String>? {
        if(text == null){
            return null
        }
        return CqIterator(text)
    }


    /**
     * 文本CQ码迭代器
     * @since 1.1-1.11
     */
    class CqIterator(private val text: String): Iterator<String> {
        private var i = -1
        private var ti = 0
        private var e = 0
        private val het = CQ_HEAD
        private val ent = CQ_END
        private var next: String? = null
        private var get = true

        /**
         * Returns `true` if the iteration has more elements.
         */
        override fun hasNext(): Boolean {
            if(!get){
                return next != null
            }
            get = false
            do {
                ti = text.indexOf(het, e)
                if(ti >= 0){
                    e = text.indexOf(ent, ti)
                    if(e >= 0){
                        i++
                        next = text.substring(ti, e+1)
                    }else{
                        e = ti+1
                    }
                }
            }while( next == null && (ti >= 0 && e >= 0) )

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
    fun getKqs(text: String?): Array<KQCode>? {
        val iter = getCqIter(text)
        return if(iter != null){
            val list = mutableListOf<KQCode>()
            iter.forEach { list.add(KQCode.of(it)) }
            list.toTypedArray()
        }else{
            null
        }
    }

    /**
     * 获取指定索引位的cq码，并封装类KQ对象
     */
    @JvmOverloads
    fun getKq(text: String?, index: Int = 0): KQCode? {
        val cq = getCq(text, index)
        return if(cq == null){
            return null
        }else{
            KQCode.of(cq)
        }
    }




}

//fun main() {
//    // CQCode length = 10000000, text length = 456666719
//    // getParams: 166ms
//    // getKq:     119ms
//    // getParam(getCq) 107ms
//    // getKqs:    内存溢出
//    val times = 10000000
//    val get = times / 2
//    val sb = StringBuilder(times * 40)
//    for (i in 0..times){
//        sb.append("[CQ:at$i,at=at${i}_at,qq=at${i}_qq]")
//    }
//    val text = sb.toString()
//
//    println("生成完毕，总长度：${text.length}")
//
//    val s = System.currentTimeMillis()
//
//    // getParam
////    val p = KQCodeUtils.getParam(text, "qq", get)
//    // getKqs(text)!![get]["qq"]
//    // getKq(text, get)!!["qq"]
////    val p = KQCodeUtils.getKq(text, get)!!["qq"]
//    // KQCodeUtils.getParam(KQCodeUtils.getCq(text, get), "qq") 107
//    val p = KQCodeUtils.getParam(KQCodeUtils.getCq(text, get), "qq")
//
//    println("qq = $p")
//
//    println("time: ${System.currentTimeMillis() - s} ms")
//}

fun main() {

    val kq = KQCode of "[CQ:at,qq=123456789]"

    println(kq)

}
