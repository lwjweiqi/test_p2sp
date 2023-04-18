package com.mlytics.mlysdk.util

import java.net.URL

class M3u8Parser(val origin: String) {

    val originURL = URL(origin)

    var segmentTSs: MutableMap<String, Boolean> = mutableMapOf()
    var segmentURLs: MutableMap<String, Boolean> = mutableMapOf()
    var segmentIDs: MutableMap<String, Boolean> = mutableMapOf()

    fun parse(content: String): MutableMap<String, Boolean> {
        var segmentTSs = this.segmentTSs

        var nextIsTs = false

        val lines = content.split("\n")

        for (line in lines) {
            if (line.startsWith("#EXTINF:")) {
                nextIsTs = true
                continue
            }
            if (nextIsTs) {
                segmentTSs[line] = true
                nextIsTs = false
            }
        }
        this.segmentTSs = segmentTSs

        this.initSegmentURLs(originURL)
        this.initSegmentIDs()

        return segmentIDs
    }

    fun initSegmentURLs(base: URL) {
        this.segmentTSs.keys.forEach {
            val url = URLTool.absoluteURL(base, it).toString()
            this.segmentURLs[url] = true
        }
    }

    fun initSegmentIDs() {
        this.segmentURLs.keys.forEach {
            val id = HashTool.sha256base64url(it) ?: it
            this.segmentIDs[id] = true
        }
    }

}