package com.mlytics.mlysdk.kernel.system

import com.mlytics.mlysdk.driver.DriverManager
import com.mlytics.mlysdk.kernel.core.KernelSettings
import com.mlytics.mlysdk.kernel.core.api.base.ConfigRequester
import com.mlytics.mlysdk.kernel.core.api.base.HostRequester
import com.mlytics.mlysdk.kernel.core.api.base.TokenRequester
import com.mlytics.mlysdk.kernel.core.api.model.ConfigRequesterReadClientConfigOptions
import com.mlytics.mlysdk.kernel.core.api.model.TokenRequesterReadTokenOptions
import com.mlytics.mlysdk.kernel.core.api.model.TokenRequesterRenewTokenOptions
import com.mlytics.mlysdk.kernel.core.servant.CDN
import com.mlytics.mlysdk.kernel.core.servant.MCDNStatsHolder
import com.mlytics.mlysdk.peer.TrackerClient
import com.mlytics.mlysdk.util.DateTool
import com.mlytics.mlysdk.util.Flow
import com.mlytics.mlysdk.util.HashTool
import com.mlytics.mlysdk.util.Logger
import com.mlytics.mlysdk.util.MLYSDKError

class SystemInitializer : Flow() {
    override suspend fun process() {
        UpdateServerHostsHandler().process()
        UpdateClientAppConfigHandler().process()
//        UpdateClientTokenHandler().process()
    }
}

class UpdateServerHostsHandler : Flow() {
    override suspend fun process(): Unit {
        Logger.debug("UpdateServerHostsHandler() process")
        val resp = HostRequester().readHosts() ?: throw MLYSDKError.NetworkError

        KernelSettings.server.token.fqdn = resp.token
        KernelSettings.server.config.fqdn = resp.config
        KernelSettings.server.metering.fqdn = resp.metering
        KernelSettings.server.cdnScore.fqdn = resp.score
        KernelSettings.server.tracker.fqdns = resp.websocket
    }

}

open class UpdateClientTokenHandler : Flow() {
    fun intakeNonce(): Int {
        return DateTool.seconds().toInt()
    }

    fun intakeSignature(): String {
        val id = KernelSettings.client.id!!
        val key = KernelSettings.client.key!!
        val origin = KernelSettings.client.origin!!
        val nonce = intakeNonce().toString()
        val hashed1 = HashTool.sha256base16(nonce)!!
        val hashed2 = HashTool.sha256base16("${origin}${id}${hashed1}")!!
        val signature = HashTool.sha256base64url("${key}${hashed2}")!!
        return signature
    }

    override suspend fun process(): Unit {
        Logger.debug("UpdateClientTokenHandler() process")
        var options = TokenRequesterReadTokenOptions()
        options.clientID = KernelSettings.client.id
        options.origin = KernelSettings.client.origin
        options.nonce = intakeNonce()
        options.signature = intakeSignature()
        val resp = TokenRequester().readToken(options) ?: throw MLYSDKError.NetworkError

        val token = resp.data?.token ?: throw MLYSDKError.NetworkError

        KernelSettings.client.token = token
        KernelSettings.client.peerID = resp.data!!.peer_id

    }

}
class RenewClientTokenHandler: UpdateClientTokenHandler() {
    override suspend fun process(): Unit {
        Logger.debug("ReadTokenFlow() process")
        var options = TokenRequesterRenewTokenOptions()
        options.clientID = KernelSettings.client.id
        options.origin = KernelSettings.client.origin
        options.nonce = intakeNonce()
        options.signature = intakeSignature()
        options.token = KernelSettings.client.token
        val resp = TokenRequester().renewToken(options) ?: throw MLYSDKError.NetworkError

        val token = resp.data?.token ?: return
        KernelSettings.client.token = token
    }
}

class UpdateClientAppConfigHandler : Flow() {
    override suspend fun process(): Unit {
        Logger.debug("UpdateClientAppConfigHandler() process")
        var options = ConfigRequesterReadClientConfigOptions()
        options.clientID = KernelSettings.client.id
        val resp = ConfigRequester().readClientConfig(options) ?: throw MLYSDKError.NetworkError

        KernelSettings.system.mode = resp.mode ?: "none"

        KernelSettings.report.isEnabled = resp.enable_metering_report ?: false
        KernelSettings.report.sampleRate = resp.metering_report?.sample_rate ?: 1.0
        KernelSettings.stream.streamID = resp.stream_id ?: "null"

        resp.platform?.let {
            it.platforms?.forEach { platform ->
                if (platform.enable == true && platform.host?.isNotBlank() == true && platform.id != null) {
                    val score = platform.score ?: 1.0
                    val cdn = CDN()
                    cdn.id = platform.id
                    cdn.name = platform.name
                    cdn.domain = platform.host
                    cdn.isEnabled = platform.enable
                    cdn.businessScore = score
                    cdn.currentScore = score
                    cdn.currentScore = score
                    MCDNStatsHolder.cdns[cdn.id!!] = cdn
                }
            }
            KernelSettings.platforms = it
        }

        KernelSettings.client.key = resp.client_key
        KernelSettings.client.orgID = resp.org_id
        KernelSettings.muxEnvKey = resp.mux_env_key

        if (KernelSettings.system.isP2PAllowed) {
            DriverManager.booter.register(TrackerClient, SystemComponent::class)
        }

    }

}

