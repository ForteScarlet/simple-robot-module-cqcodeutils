package com.simplerobot.modules.utils

import java.lang.IllegalArgumentException
import java.util.*


/**
 * CQ码封装类Kt
 * @since 1.0-1.11
 */
class KQCode
private constructor(private val params: MutableMap<String, String>, var type: String) :
        CharSequence,
        MutableMap<String, String> by params {
    constructor(type: String): this(mutableMapOf(), type)
    constructor(type: String, params: MutableMap<String, String>): this(params.toMutableMap(), type)
    constructor(type: String, vararg params: Pair<String, String>): this(mutableMapOf(*params), type)
    constructor(type: String, vararg params: Map.Entry<String, Any>): this(mutableMapOf(*params.map { it.key to it.value.toString() }.toTypedArray()), type)
    constructor(type: String, vararg params: String): this(mutableMapOf(*params.map {
        val split = it.split(Regex("="), 2)
        split[0] to split[1]
    }.toTypedArray()), type)

    /**
     * Returns the length of this character sequence.
     */
    override val length: Int
        get() = toString().length

    /**
     * 获取转义后的字符串
     */
    fun getNoDecode(key: String) = CQEncoder.encodeParams(this[key])

    /**
     * Returns the character at the specified [index] in this character sequence.
     * @throws [IndexOutOfBoundsException] if the [index] is out of bounds of this character sequence.
     * Note that the [String] implementation of this interface in Kotlin/JS has unspecified behavior
     * if the [index] is out of its bounds.
     */
    override fun get(index: Int): Char = toString()[index]

    /**
     * Returns a new character sequence that is a subsequence of this character sequence,
     * starting at the specified [startIndex] and ending right before the specified [endIndex].
     *
     * @param startIndex the start index (inclusive).
     * @param endIndex the end index (exclusive).
     */
    override fun subSequence(startIndex: Int, endIndex: Int): CharSequence = toString().subSequence(startIndex, endIndex)

    /** toString */
    override fun toString(): String = KQCodeUtils.toCq(type, this)


    /** 从KQCode转到CQCode */
    fun toCQCode(): com.forte.qqrobot.beans.cqcode.CQCode
            = com.forte.qqrobot.beans.cqcode.CQCode.of(type, mutableMapOf(*params.entries.map { it.key to CQEncoder.encodeParams(it.value) }.toTypedArray()))


    companion object {


        /**
         * 从cq码字符串转到KQCode
         * @since 1.1-1.11
         * infix since 1.2-1.11
         */
        @JvmStatic
        infix fun of(text: String): KQCode {
            var tempText = text.trim()
            // 不是[CQ:开头，或者不是]结尾都不行
            if(!tempText.startsWith("[CQ:") || !tempText.endsWith("]")){
                throw IllegalArgumentException("not starts with '[CQ:' or not ends with ']'")
            }
            // 是[CQ:开头，]结尾，切割并转化
            tempText = tempText.substring(4, tempText.lastIndex)

            val split = tempText.split(Regex(" *, *"))

            val type = split[0]

            return if(split.size > 1){
                KQCode(type, *split.subList(1, split.size).toTypedArray())
            }else{
                KQCode(type)
            }

        }

        /**
         * 从CQCode转到KQCode
         * @since 1.0-1.11
         * infix since 1.2-1.11
         */
        @JvmStatic
        infix fun of(cqCode: com.forte.qqrobot.beans.cqcode.CQCode): KQCode = KQCode(cqCode.cqCodeTypesName, cqCode)

    }

    /**
     * Params
     * @since 1.0-1.11
     */
    open class Params{
        private val plist: MutableList<Pair<String, String>> = mutableListOf()
        var param: Pair<String, String>
            get() = plist.last()
            set(value) { plist.add(value) }


        /** 添加全部 */
        open fun addTo(kqCode: KQCode): KQCode{
            kqCode.putAll(plist)
            return kqCode
        }

        override fun toString(): String = plist.toString()
    }

    /**
     * Builder
     * @since 1.0-1.11
     */
    class Builder {
        var type: String = ""
        val _params = Params()
        var param: Pair<String, String>
            get() = _params.param
            set(value) { _params.param = value }
        /** 添加全部 */
        fun build(): KQCode {
            val kqCode = KQCode(type)
            _params.addTo(kqCode)
            return kqCode
        }

        override fun toString(): String = "$type:$_params"


    }

}


/**
 * DSL构建KQCode， 例如
 * ```
kqCode("at") {
    param = "key1" to "1"
    param = "key2" to "2"
    param = "key3" to "3"
    param = "key4" to "4"
}
 * ```
 * @since 1.0-1.11
 */
fun kqCode(type: String, block: KQCode.Params.() -> Unit): KQCode {
    val kqCode = KQCode(type)
    return KQCode.Params().apply(block).addTo(kqCode)
}

/**
 * DSL构建KQCode的参数列表
 * @since 1.0-1.11
 */
fun kqCode(block: KQCode.Builder.() -> Unit) = KQCode.Builder().apply(block).build()

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
fun KQCode.Builder.params(block: KQCode.Params.() -> Unit) {
    this._params.apply(block)
}


