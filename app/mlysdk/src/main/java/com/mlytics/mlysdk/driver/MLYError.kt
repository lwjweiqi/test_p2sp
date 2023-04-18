package com.mlytics.mlysdk.driver

class MLYError(msg: String) : Exception(msg) {

    companion object {
        val WithoutConfigureError = MLYError("error: without configure")
        val ConditionDeinitError = MLYError("error: condition deinit")
        val ConditionTimeoutError = MLYError("error: condition timeout")
        val CentrifugeSendError = MLYError("error: centrifuge send")
        val ProtobufCasterError = MLYError("error: protobuf cast error")
        val DataChannelClosed = MLYError("error: data channel closed")
        val DataChannelNil = MLYError("error: data channel is nil")
        val DataChannelSendFail = MLYError("error: data channel send fail")
        val PeerEmptyError = MLYError("error: peer empty")
        val HTTPTimeoutError = MLYError("error: http timeout")
    }

}