package com.mlytics.mlysdk.util

class AsyncValue<T> {
    var data: T? = null
    private var condition = Condition()

    fun deny() {
        condition.deny(AsyncValueError)
    }

    fun deliver(value: T) {
        this.data = value
        this.condition.pass()
    }

    suspend fun extract(): T? {
        if (this.data == null) {
            this.condition.done()
        }
        return this.data
    }
}

object AsyncValueError : Exception() {

}
