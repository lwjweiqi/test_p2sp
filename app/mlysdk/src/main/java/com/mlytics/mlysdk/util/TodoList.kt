package com.mlytics.mlysdk.util

import com.mlytics.mlysdk.kernel.core.model.service.ResourceRange
import java.io.InputStream
import java.util.concurrent.locks.ReentrantLock

class TodoList<T> {

    private var list = Queue<T>()
    private var condition = Condition()
    private var lock = ReentrantLock()
    suspend fun first(): T {
        while (list.isEmpty) {
            this.condition.done()
        }
        return list.first!!
    }

    fun add(item: T) {
        lock.lock()
        try {
            list.append(item)
        } finally {
            lock.unlock()
        }
        condition.pass()
    }

    fun removeFirst() {
        lock.lock()
        try {
            list.removeFirst()
        } finally {
            lock.unlock()
        }
    }

}
