package com.mlytics.mlysdk.util

import com.mlytics.mlysdk.driver.DriverManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

abstract class MessageQueue<T> {

    private var queue = Channel<T>()
    private var sender: Job = createJob()
    private var activate = true
    abstract suspend fun sendable(): Boolean
    abstract suspend fun send(data: T): Boolean

    fun shutdown() {
        activate = false
    }

    private fun createJob(): Job {
        return DriverManager.ioScope.launch {
            var data: T? = null
            while (activate && isActive) {
                if (sendable()) {
                    if (data == null) {
                        data = queue.extract()
                    }
                    if (data != null) {
                        if (send(data)) {
                            data = null
                        }else{
                            delay(500)
                        }
                    }
                }
            }
        }
    }

    fun append(data: T) {
        queue.deliver(data)
    }

}