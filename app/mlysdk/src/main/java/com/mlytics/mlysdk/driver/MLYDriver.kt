package com.mlytics.mlysdk.driver

import android.content.Context
import com.mlytics.mlysdk.kernel.core.infra.CDNDownloadReportListener
import com.mlytics.mlysdk.kernel.core.infra.metrics.CDNStateListener
import com.mlytics.mlysdk.kernel.core.servant.MCDNComponent
import com.mlytics.mlysdk.kernel.system.SystemBooter
import com.mlytics.mlysdk.kernel.system.SystemComponent
import com.mlytics.mlysdk.util.AsyncValue
import com.mlytics.mlysdk.util.Backoff
import com.mlytics.mlysdk.util.Logger
import com.mlytics.mlysdk.util.MessageCode
import com.mlytics.mlysdk.util.ValidationError
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

object MLYDriver {

    val version = "0.0.1"

    fun initialize(closure: (MLYDriverOptions) -> Unit) {
        var options = MLYDriverOptions()
        closure(options)
        try {
            DriverManager.initialize(options)
        } catch (e: Exception) {
            Logger.error("MLYDriver.initialize", e)
        }

    }

    fun activate() {
        DriverManager.activate()
    }

    fun deactivate() {
        DriverManager.deactivate()
    }

}

object DriverManager {

    lateinit var context: Context
    var job = SupervisorJob()
    var ioScope: CoroutineScope = CoroutineScope(Dispatchers.IO + job)
    var defaultScope = CoroutineScope(Dispatchers.Default + job)
    var ready = AsyncValue<Boolean>()
    var isActivated = false
    var isConfigured = false
    var isSupported = true
    var booter = SystemBooter()
    var backoff = Backoff()

    private fun initBooter() {
        booter.register(SystemComponent)
        booter.register(MCDNComponent, SystemComponent::class)
        booter.register(CDNStateListener)
        booter.register(CDNDownloadReportListener)
    }

    private fun config(options: MLYDriverOptions?) {
        DriverConfigurator(options).config()
        this.isConfigured = true
    }

    fun initialize(options: MLYDriverOptions) {
        this.config(options)
        this.initBooter()
        this.activate()
    }

    fun activate() {
        if (this.isConfigured) else {
            throw ValidationError(MessageCode.WSV001)
        }
        this.isActivated = true
        this.activateBooter()
    }

    fun activateBooter() {
        defaultScope.launch {
            while (this.isActive) {
                try {
                    booter.activate()
                    ready.deliver(true)
                    backoff.reset()
                    delay(5000)
                } catch (e: Exception) {
                    Logger.error("SystemBooter", e)
                    backoff.delay()
                }
            }
        }
    }

    fun deactivate() {
        if (!this.isActivated) {
            return
        }
        this.isActivated = false
//        this.job.cancel()
        this.ready.deliver(false)
    }

}
