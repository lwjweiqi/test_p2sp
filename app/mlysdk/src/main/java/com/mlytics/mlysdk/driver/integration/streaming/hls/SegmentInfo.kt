package com.mlytics.mlysdk.driver.integration.streaming.hls

import com.mlytics.mlysdk.util.M3u8Parser

class SegmentInfo(val swarmURI: String) {
    var parser = M3u8Parser(swarmURI)

    fun from(content: String) {
        parser.parse(content)
    }

}