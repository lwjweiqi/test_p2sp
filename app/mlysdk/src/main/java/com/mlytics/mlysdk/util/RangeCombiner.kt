package com.mlytics.mlysdk.util

import com.mlytics.mlysdk.kernel.core.model.service.ResourceRange
import com.mlytics.mlysdk.kernel.core.model.service.ResourceRangeComparator
import java.lang.Integer.min
import java.util.concurrent.locks.ReentrantLock

class RangeCombiner {
    var data = CombinerBuffer()
    var ranges: MutableList<ResourceRange> = mutableListOf()
    var condition: Condition = Condition()

    //    var callback: (ByteArray, Int, Int) -> Unit =
//        { buffer: ByteArray, fromIndex: Int, toIndex: Int -> }
    private val lock = ReentrantLock()

    suspend fun more(sent: Int): Boolean {
        if (this.data.size > sent) {
            return true
        }
//        Logger.debug("ds-- combiner before more sent=$sent size=${this.data.size}")
        condition.done()
//        Logger.debug("ds-- combiner after more sent=$sent size=${this.data.size}")
        val size = this.data.size
        return size > sent
    }

    private fun appendBytes(data: ByteArray) {
        this.data.write(data)
//        Logger.debug("ds-- combiner pass size=${this.data.size()}")
        this.condition.pass()
//        this.callback(this.data.buffer(), this.data.size() - data.size, this.data.size())
    }

    fun appendRange(range: ResourceRange): Boolean {

        if (range.data.isEmpty()) {
            return false
        }

        val count1 = this.data.size
        lock.lock()
        try {
            val res = this.append(range)
            val count2 = this.data.size
            if (count2 > count1) {
//                Logger.debug("ds-- combiner pass size=${this.data.size()}")
                this.condition.pass()
            }
            return res
        } finally {
            lock.unlock()
        }
        return false
    }

    private fun checkRange(range: ResourceRange): ResourceRangeState {
        if (range.start == this.data.size) {
            return ResourceRangeState.Next
        } else if (range.start!! > this.data.size) {
            return ResourceRangeState.Future
        } else {
            if (range.end!! <= this.data.size) {
                return ResourceRangeState.Abandon
            }
            return ResourceRangeState.Partial
        }
    }

    private fun append(range: ResourceRange): Boolean {
        when (this.checkRange(range)) {
            ResourceRangeState.Abandon -> {
                return false
            }

            ResourceRangeState.Partial -> {
                this.appendPartial(range)
                return true
            }

            ResourceRangeState.Next -> {
                this.appendBytes(range.data)
                return true
            }

            ResourceRangeState.Future -> {
                this.ranges.add(range)
                this.ranges.sortWith(ResourceRangeComparator)
                return false
            }

        }
    }

    private fun append(data: ByteArray) {
        this.appendBytes(data)
        this.checkRanges()
    }

    private fun appendPartial(range: ResourceRange) {
        val start = this.data.size - range.start
        val bytes = range.data.copyOfRange(
            start, range.data.size
        )
        append(bytes)
    }

    private fun checkRanges() {
        if (this.ranges.isEmpty()) {
            return
        }
        val range = this.ranges.removeFirst()
        when (this.checkRange(range)) {
            ResourceRangeState.Abandon -> {
                this.ranges.removeFirst()
                this.checkRanges()
            }
            ResourceRangeState.Partial -> this.appendPartial(range)
            ResourceRangeState.Next -> this.append(range.data!!)
            ResourceRangeState.Future -> return
        }
    }

    fun reset() {
        this.condition.reset()
        this.data.reset()
        this.ranges.clear()
    }

}

enum class ResourceRangeState {
    Abandon, Partial, Next, Future
}

class CombinerBuffer {

    private var bufferSize = 8192
    var list = mutableListOf<ByteArray>()
    var size: Int = 0

    fun write(byteArray: ByteArray, offset: Int = 0, length: Int = byteArray.size) {
        var free = bufferSize - (size % bufferSize)
        if (free == bufferSize) {
            list.add(ByteArray(bufferSize))
        }
        val len = min(free, length)
        val last = list.last()
        val thisOffset = bufferSize - free
        byteArray.copyInto(last, thisOffset, offset, offset + len)
        size += len
        if (len == length) {
            return
        }
        write(byteArray, offset + len, length - len)
    }

    fun copyInto(byteArray: ByteArray, offset: Int, startIndex: Int, endIndex: Int) {
        if (endIndex > size || endIndex == 0 || startIndex == endIndex) {
            throw ArrayIndexOutOfBoundsException("size=$size offset=$offset startIndex=$startIndex endIndex=$endIndex")
        }
        val length = endIndex - startIndex
        val index = startIndex / bufferSize
        val thisOffset = startIndex % bufferSize
        val len = min(length, bufferSize - thisOffset)
        val thisBytes = this.list[index]
        thisBytes.copyInto(byteArray, offset, thisOffset, thisOffset + len)

        if (len == length) {
            return
        }
        copyInto(byteArray, offset + len, startIndex + len, endIndex)
    }
    fun toByteArray():ByteArray {
        val size = this.size
        var bytes = ByteArray(size)
        this.copyInto(bytes, 0, 0, size)
        return bytes
    }

    fun reset() {
        size = 0
        list.clear()
    }

}