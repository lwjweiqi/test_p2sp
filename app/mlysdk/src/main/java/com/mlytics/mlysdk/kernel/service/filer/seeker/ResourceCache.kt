package com.mlytics.mlysdk.kernel.service.filer.seeker

import com.mlytics.mlysdk.util.Cache
import com.mlytics.mlysdk.util.ResourceRequest

class ResourceCache {

    companion object {
        var cache = Cache<ResourceRequest>()
    }

    fun get(key: String): ResourceRequest? = cache.get(key)
    fun set(key:String, value: ResourceRequest, ttl: Long = 60000) = cache.set(key, value, ttl)

}