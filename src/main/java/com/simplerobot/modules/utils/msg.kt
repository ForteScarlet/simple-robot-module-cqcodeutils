package com.simplerobot.modules.utils

import java.util.*


/**
 * msgs，拼接消息链
 */
class Msgs
@JvmOverloads
constructor(private val delimiter: CharSequence = " ", private val delegate: () -> MutableList<CharSequence> = { LinkedList() }): MutableList<CharSequence> by delegate() {
    @JvmOverloads
    constructor(delimiter: CharSequence = " ", collection: Collection<CharSequence>): this(delimiter){ LinkedList(collection) }


    /** to string to show messages. delimiter is ' ' */
    override fun toString(): String = this.joinToString(delimiter)

    /** to list string to show messages.*/
    fun toListString(): String = this.joinToString(", ", "[", "]")

    /**
     * plus
     */
    operator fun plus(other: List<CharSequence?>): Msgs {
        other.forEach { this.add(it ?: "null") }
        return this
    }

    /**
     * plus
     */
    operator fun plus(other: CharSequence?): Msgs {
        this.add(other ?: "null")
        return this
    }
}