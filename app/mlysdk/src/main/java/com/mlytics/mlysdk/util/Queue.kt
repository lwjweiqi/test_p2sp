package com.mlytics.mlysdk.util

//import Foundation
interface Deque<Element> {
    var first: Element?
    var last: Element?
    var size: Int
    var isEmpty: Boolean
    fun append(element: Element)
    fun removeFirst(): Element?
    fun removeAll()
}

class DoubleDequeItem<Element>(var value: Element) {
    var prev: DoubleDequeItem<Element>? = null
    var next: DoubleDequeItem<Element>? = null

}

class Queue<Element> : Deque<Element>, Iterable<Element> {

    override var size: Int = 0
    var firstItem: DoubleDequeItem<Element>? = null
    var lastItem: DoubleDequeItem<Element>? = null
    override var isEmpty: Boolean = true
        get() = this.firstItem == null
    override var first: Element? = null
        get() {
            return this.firstItem?.value
        }
    var isNotEmpty: Boolean = false
        get() = !isEmpty

    override var last: Element? = null
        get() {
            return this.lastItem?.value
        }

    constructor (array: MutableList<Element>? = null) {
        this.append(array)
    }

    fun append(array: List<Element>?) {
        array?.forEach {
            this.append(it)
        }
    }

    override fun append(element: Element) {
        val current = DoubleDequeItem<Element>(element)
        if (this.size == 0) {
            this.firstItem = current
            this.lastItem = current
            this.size = 1
            return
        }
        val last = this.lastItem!!
        last.next = current
        current.prev = last
        this.lastItem = current

        this.size += 1
    }

    override fun removeFirst(): Element? {
        if (this.firstItem == null) {
            return null
        }
        val first = this.firstItem!!
        val value = first.value
        if (this.size == 1) {
            this.lastItem = null
        }

        this.firstItem = first.next
        this.size -= 1
        return value
    }

    override fun removeAll() {
        this.firstItem = null
        this.lastItem = null
        this.size = 0
    }

    override operator fun iterator(): Iterator<Element> {
        return QueueIterator(this)
    }

    private inner class QueueIterator(val queue: Queue<Element>) : Iterator<Element> {

        var current: DoubleDequeItem<Element>? = queue.firstItem

        override fun hasNext(): Boolean {
            return current?.next != null
        }

        override fun next(): Element {
            current = current!!.next
            return current!!.value
        }
    }

}
