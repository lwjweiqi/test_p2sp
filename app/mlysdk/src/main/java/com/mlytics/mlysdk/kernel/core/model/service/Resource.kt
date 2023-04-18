package com.mlytics.mlysdk.kernel.core.model.service

import com.mlytics.mlysdk.util.HashTool
import com.mlytics.mlysdk.util.WatchTool

//import Foundation
class Resource {
    var uri: String
    var id: String
    var range: ResourceRange? = null
    var headers: MutableMap<String, String>

    var swarmID: String? = null
    var swarmURI: String? = null
    var sourceURI: String? = null

    var type: String? = null
    var total: Int? = null
    var content: ByteArray? = null
    var isComplete: Boolean = false

    var ctime: WatchTool = WatchTool()
    var mtime: WatchTool = WatchTool()

    var priority = 0
    var isShareable = false
        get() {
            return this.type?.endsWith("url", ignoreCase = true) ?: false
        }

    var size: Int = 0
        get() {
            return content?.size ?: -1
        }

    constructor (
        uri: String,
        range: ResourceRange? = null,
        headers: MutableMap<String, String> = mutableMapOf()
    ) {
        this.id = makeID(uri, range)
        this.uri = uri
        this.range = range
        this.headers = headers
    }

    companion object {
        fun makeID(uri: String, range: ResourceRange?): String {
            val r = range?.toString() ?: ""
            var s = uri + r
            return HashTool.sha256base64url(s) ?: s
        }
    }

}

class ResourceRange {
    var start: Int = -1
    var end: Int = -1
    var data: ByteArray = ByteArray(0)
    val exists: Boolean
        get() {
            return start > 0 || end > 0
        }
    val header: String?
        get() {
            return if (exists) {
                "bytes=${if (start > 0) start else ""}-${if (end > 0) end else ""}"
            }else{
                null
            }
        }

    val expendLength: Int?
        get() {
            val start = if (this.start > 0) this.start else 0
            val end = if (this.end > 0) this.end else 0
            if (end == 0){
                return null
            }
            return end - start
        }

    constructor() {

    }

    constructor(start: Int, length: Int) {
        this.start = start
        this.end = start + length
    }

    override fun toString(): String {
        if (
            start <= 0 && end <= 0
        ) {
            return ""
        }
        return "[${start}-${end}]"
    }

    fun from(range: String?): ResourceRange? {
        if (range == null) {
            return null
        }
        val rs = range.split("=", "-", limit = 3)
        if (rs.size != 3) {
            return null
        }
        start = rs[1].toInt()
        end = rs[2].toInt()
        return this
    }

}

object ResourceRangeComparator : Comparator<ResourceRange> {
    override fun compare(o1: ResourceRange?, o2: ResourceRange?): Int {
        if (o1 == null) {
            return -1
        }
        if (o2 == null) {
            return 1
        }
        if (o1.start == o2.start) {
            return o2.end - o1.end
        }
        return o2.start - o1.start
    }

}

class ResourceConcatOptions {
    var chunk: ByteArray? = null
    var range: ResourceRange? = null
}

class ResourceStat {
    var id: String? = null
    var completion: Int? = null
}
