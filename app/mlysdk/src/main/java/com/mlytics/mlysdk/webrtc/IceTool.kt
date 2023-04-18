package com.mlytics.mlysdk.webrtc

object IceTool {
    fun usernameFragment(candidate: String): String {
        var b = false
        candidate.splitToSequence(" ").any {
            if (b) {
                return it
            }
            if (it == "ufrag") {
                b = true
            }
            return@any false
        }

        return ""
    }
}