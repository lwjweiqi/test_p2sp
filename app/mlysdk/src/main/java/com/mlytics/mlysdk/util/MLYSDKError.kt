package com.mlytics.mlysdk.util

class MLYSDKError(message: String) : Exception() {
    companion object {
        val NetworkError = MLYSDKError("NetworkError")
        val TokenError = MLYSDKError("TokenError")
    }
}
