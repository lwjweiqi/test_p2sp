package com.mlytics.mlysdk.peer

object ResourceStats {

    var resources: MutableMap<String, MutableMap<String, Long>> = mutableMapOf()
    //var correlations: MutableMap<String, String> = mutableMapOf()

    fun register(key: String): MutableMap<String, Long> {
        var result = resources[key]
        if (result == null) {
            result = mutableMapOf()
            resources[key] = result
        }

        return result

    }

    fun unregister(swarmID: String) {
        resources.remove(swarmID)
    }

    fun get(swarmID: String): MutableMap<String, Long>? {
        return resources[swarmID]
    }

}