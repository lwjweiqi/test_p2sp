package com.mlytics.mlysdk.util

import android.net.Uri
import com.mlytics.mlysdk.kernel.core.model.service.Resource
import com.mlytics.mlysdk.kernel.core.model.service.ResourceRange

class BaseUri(var uri: String, var start: Int = 0, var length: Int = 0) {

    val range = range()
    val id = Resource.makeID(uri, range)
    val url = Uri.parse(uri)

    private fun range(): ResourceRange? {
        if (start == 0 && length == 0) {
            return null
        }
        return ResourceRange(start, length)
    }
}