package com.simplerobot.modules.utils


/**
 * 以基于字符串截取为思想原理的kq迭代器父类
 */
internal abstract class BaseCqIterator<T>(protected val code: String): Iterator<T> {
    init {
        if (!code.startsWith(CQ_HEAD) || !code.endsWith(CQ_END)) {
            throw IllegalArgumentException("text \"$code\" is not a cq code text.")
        }
    }

    // 从索引0开始寻找
    /** 下一个开始的查询索引 */
    protected var index: Int = 0

    /** 下一个索引位 */
    protected abstract fun nextIndex(): Int


    /**
     * 判断是否还有下一个参数
     */
    override fun hasNext(): Boolean {
        // 如果有逗号，说明还有键值对
        return index >= 0 && nextIndex() > 0
    }

}

/**
 * 文本CQ码迭代器，从一串文本中迭代出其中的CQ码
 * @since 1.1-1.11
 */
internal class CqTextIterator(private val text: String, type: String = "") : Iterator<String> {
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
 * 一串儿CQ码字符串中的键迭代器
 * @since 1.8.0
 */
internal class CqParamKeyIterator(code: String): BaseCqIterator<String>(code) {


    override fun nextIndex(): Int {
        return code.indexOf(CQ_SPLIT, if (index == 0) 0 else index + 1)
    }

    /**
     * Returns the next element in the iteration.
     */
    override fun next(): String {
        if(!hasNext()) throw NoSuchElementException()

        // 下一个逗号所在处
        index = nextIndex()
        // 下一个kv切割符所在
        val nextKv = code.indexOf(CQ_KV, index)
        return code.substring(index+1, nextKv)
    }

}



/**
 * 一串儿CQ码字符串中的值迭代器
 * 得到的值会进行反转义。
 * @since 1.8.0
 */
internal class CqParamValueIterator(code: String): BaseCqIterator<String>(code) {


    override fun nextIndex(): Int {
        return code.indexOf(CQ_KV, if(index == 0) 0 else index + 1)
    }

    /**
     * 判断是否还有下一个参数
     */
    override fun hasNext(): Boolean {
        // 如果有逗号，说明还有键值对
        return index >= 0 && nextIndex() > 0
    }

    /**
     * Returns the next element in the iteration.
     */
    override fun next(): String {
        if(!hasNext()) throw NoSuchElementException()

        // 下一个逗号所在处
        index = nextIndex()
        // 下一个逗号或结尾符所在处
        var nextSplit = code.indexOf(CQ_SPLIT, index)
        if(nextSplit < 0){
            nextSplit = code.lastIndex
        }
        return CQDecoder.decodeParams(code.substring(index+1, nextSplit))!!
    }

}




/**
 * 一串儿CQ码字符串中的键值对迭代器
 * 得到的值会进行反转义。
 * @since 1.8.0
 */
internal class CqParamPairIterator(code: String): BaseCqIterator<Pair<String, String>>(code) {


    override fun nextIndex(): Int {
        return code.indexOf(CQ_SPLIT, if(index == 0) 0 else index + 1)
    }


    /**
     * Returns the next element in the iteration.
     */
    override fun next(): Pair<String, String> {
        if(!hasNext()) throw NoSuchElementException()

        // 下一个逗号所在处
        index = nextIndex()
        // 下下一个逗号或结尾符所在处
        var nextSplit = code.indexOf(CQ_SPLIT, index + 1)
        if(nextSplit < 0){
            nextSplit = code.lastIndex
        }
        val substr = code.substring(index + 1, nextSplit)
        val keyValue = substr.split(CQ_KV)
        return keyValue[0] to CQDecoder.decodeParams(keyValue[1])!!
    }

}


internal class CqParamEntryIterator(code: String): BaseCqIterator<Map.Entry<String, String>>(code) {

    override fun nextIndex(): Int {
        return code.indexOf(CQ_SPLIT, if(index == 0) 0 else index + 1)
    }

    /**
     * 判断是否还有下一个参数
     */
    override fun hasNext(): Boolean {
        // 如果有逗号，说明还有键值对
        return index >= 0 && nextIndex() > 0
    }

    /**
     * Returns the next element in the iteration.
     */
    override fun next(): Map.Entry<String, String> {
        if(!hasNext()) throw NoSuchElementException()

        // 下一个逗号所在处
        index = nextIndex()
        // 下下一个逗号或结尾符所在处
        var nextSplit = code.indexOf(CQ_SPLIT, index + 1)
        if(nextSplit < 0){
            nextSplit = code.lastIndex
        }
        val substr = code.substring(index + 1, nextSplit)
        val keyValue = substr.split(CQ_KV)
        return KqEntry(keyValue[0], CQDecoder.decodeParams(keyValue[1])!!)
    }

}


/**
 * 针对于[Map.Entry]的简易实现
 */
internal data class KqEntry(override val key: String, override val value: String) : Map.Entry<String, String>
