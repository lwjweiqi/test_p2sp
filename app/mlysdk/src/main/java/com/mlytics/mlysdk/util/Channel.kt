package com.mlytics.mlysdk.util

import kotlin.coroutines.Continuation
import kotlin.coroutines.cancellation.CancellationException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class Channel<T> {

    private var data: Queue<T> = Queue()
    private var continuations: Queue<Continuation<T>> = Queue()

    fun deliver(value: T) {
        val first = continuations.removeFirst()
        if (first == null) {
            data.append(value)
        } else {
            first.resume(value)
        }
    }

    suspend fun extract(): T {
        val first = data.removeFirst()
        if (first == null) {
            return suspendCoroutine<T> {
                this.continuations.append(it)
            }
        } else {
            return first
        }
    }

    fun close() {
        data.removeAll()
        while(true){
            continuations.removeFirst()?.resumeWithException(CancellationException()) ?: break
        }
    }

}
