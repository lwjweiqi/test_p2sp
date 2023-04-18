package com.mlytics.mlysdk.peer

object SwarmUsers {

    var joinedSwarms: MutableMap<String, MutableMap<String, Double>> = mutableMapOf()
    var correlations: MutableMap<String, String> = mutableMapOf()

    fun register(swarmID: String) {
        if (joinedSwarms.contains(swarmID)) {
            return
        }
        joinedSwarms[swarmID] = mutableMapOf()
    }

    fun unregister(swarmID: String) {
        joinedSwarms.remove(swarmID)?.forEach { t, u ->
            Peers.unregister(t)
        }
    }

    fun get(swarmID: String): MutableMap<String, Double>? {
        return joinedSwarms[swarmID]
    }

}