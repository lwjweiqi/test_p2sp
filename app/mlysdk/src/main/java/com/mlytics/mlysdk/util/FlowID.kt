package com.mlytics.mlysdk.util

object FlowID {

    val TIME_PART_FORMAT = "YYYYMMDDHHmmss"
    val RANDOM_PART_LENGTH = 20

    fun make(): String {
        val timePart = TimeTool.makeNowFstring(TIME_PART_FORMAT)
        val randomPart = ByteTool.makeRandomBase36String(RANDOM_PART_LENGTH).lowercase()
        return "flow-$timePart-$randomPart"
    }

}