package com.mlytics.mlysdk.peer

import com.mlytics.mlysdk.driver.DriverManager
import com.mlytics.mlysdk.driver.integration.streaming.hls.MediaInfo
import com.mlytics.mlysdk.util.Component
import com.mlytics.mlysdk.util.Logger
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object PeerJob : Component() {

//    suspend fun connectUsers() {
//        Logger.debug("centrifuge: job connect users start")
//        for ((swarmID, users) in SwarmUsers.swarms) {
//            connectSwarmUsers(swarmID, users)
//        }
//        Logger.debug("centrifuge: job connect users finish")
//    }

    private fun obtainSwarmUsers() {
        SwarmUsers.joinedSwarms.keys.forEach {
            TrackerClient.requestObtainSwarmUsers(it)
        }
    }

    private fun reportSwarmStats() {
        SwarmUsers.joinedSwarms.keys.toList().forEach {
            val info = MediaInfo.mediaInfos[it] ?: return@forEach
            TrackerClient.reportSwarmStats(info.swarmID, info.genSwarmScore())
        }
    }

    private fun reportResourceStats() {
        MediaInfo.mediaInfos.values.toList().forEach {
            it.reportResourceStats()
        }
    }

    private fun reportHealth() {
        TrackerClient.reportHealth()
    }

    override suspend fun activate() {
        super.activate()
        this.initJob()
    }

    override suspend fun deactivate() {
        super.deactivate()
        inited = false
        jobs.forEach {
            it.cancelAndJoin()
        }
    }

    fun initJob() {
        if (inited) {
            return
        }
        inited = true
        job(20000) {
            obtainSwarmUsers()
            reportResourceStats()
            reportHealth()
            reportSwarmStats()
        }
    }

    var inited = false
    var jobs = mutableListOf<Job>()
    fun job(ms: Long, closure: suspend () -> Unit) {
        val job = DriverManager.ioScope.launch {
            while (isActivated) {
                try {
                    closure()
                } catch (e: Exception) {
                    Logger.error("centrifuge: job error", e)
                }
                delay(ms)
            }
        }
        jobs.add(job)
    }

}