package com.mlytics.mlysdk.vendor

import com.mlytics.mlysdk.util.Component
import com.mlytics.mlysdk.util.LogOptions
import com.mlytics.mlysdk.util.LogParams
import com.mlytics.mlysdk.util.Logger
import com.mlytics.mlysdk.util.LoggerHandler
import com.mlytics.mlysdk.util.LoggerLevel
import io.sentry.Sentry

object LoggerSentry : LoggerHandler {
    override var level = LoggerLevel.ERROR

    override fun log(
        level: LoggerLevel,
        message: String?,
        params: LogParams?,
        options: LogOptions?
    ) {
        if (level < this.level) {
            return
        }
        options?.error?.let {
            Sentry.captureException(it)
        } ?: message?.let {
            Sentry.captureMessage(it)
        }
    }
}

object SentryComponent: Component() {

    override suspend fun activate() {
        super.activate()
        Logger.loggers.add(LoggerSentry)
    }

    override suspend fun deactivate() {
        super.deactivate()
        Logger.loggers.remove(LoggerSentry)
    }
}


