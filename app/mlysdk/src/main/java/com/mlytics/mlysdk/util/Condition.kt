package com.mlytics.mlysdk.util

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.TimeoutException
import java.util.concurrent.locks.ReentrantLock
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class Condition {
    var continuations: Queue<Continuation<Unit>> = Queue()
    var items: Queue<ConditionItem> = Queue()
    var error: Exception? = null
    private val lock = ReentrantLock()

    private fun resume() {
        this.lock.lock()
        try {
            while (true) {
                items.removeFirst()?.resume() ?: break
            }
            while (true) {
                this.continuations.removeFirst()?.resume(Unit) ?: break
            }
        } finally {
            this.lock.unlock()
        }
    }

    suspend fun done() {
        return suspendCoroutine { continuation ->
            this.lock.lock()
            try {
                this.continuations.append(continuation)
            } finally {
                this.lock.unlock()
            }
        }
    }

    suspend fun await(timeout: Long) {
        return suspendCoroutine { continuation ->
            this.lock.lock()
            try {
                val job = GlobalScope.launch {
                    delay(timeout)
                    continuation.resumeWithException(TimeoutException("timeout=$timeout"))
                }
                this.items.append(ConditionItem(continuation, job, timeout))
            } finally {
                this.lock.unlock()
            }
        }
    }

    fun pass(error: Exception? = null) {
        this.error = error
        this.resume()
    }

    fun deny(error: Exception) {
        this.error = error
        this.resume()
    }

    fun reset() {
        this.error = null
        this.resume()
    }

}

class ConditionItem(var continuation: Continuation<Unit>, var job: Job, var timeout: Long = 0) {
    fun resume() {
        continuation.resume(Unit)
        job.cancel(CancellationException("Condition DONE"))
    }
}
