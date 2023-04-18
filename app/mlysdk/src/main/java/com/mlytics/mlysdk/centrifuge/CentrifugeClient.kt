package com.mlytics.mlysdk.centrifuge

import com.google.protobuf.Message
import com.mlytics.mlysdk.peer.logString
import com.mlytics.mlysdk.util.Logger
import com.mlytics.mlysdk.util.MessageQueue
import io.github.centrifugal.centrifuge.Client
import io.github.centrifugal.centrifuge.ClientState
import io.github.centrifugal.centrifuge.ConnectedEvent
import io.github.centrifugal.centrifuge.ConnectingEvent
import io.github.centrifugal.centrifuge.ConnectionTokenGetter
import io.github.centrifugal.centrifuge.DisconnectedEvent
import io.github.centrifugal.centrifuge.ErrorEvent
import io.github.centrifugal.centrifuge.EventListener
import io.github.centrifugal.centrifuge.Options
import io.github.centrifugal.centrifuge.ServerPublicationEvent
import io.github.centrifugal.centrifuge.ServerSubscribedEvent
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class CentrifugeClient {

    lateinit var token: String
    lateinit var url: String
    lateinit var channel: String

    private var client: Client? = null
    private var listener = CentrifugeListener(this)
    var sendable: Boolean = false

    var timeout = 10000
    var activate = true

    var onReceive: (ByteArray) -> Unit = {}
//    var onClose: () -> Unit = {}
    var onError: (Throwable) -> Unit = {}
    var onSubscribed: (String) -> Unit = {}

    fun startup(token: String, url: String, channel: String) {
        this.token = token
        this.url = url
        this.channel = channel
        this.activate = true
        connect()
    }

    fun shutdown() {
        this.activate = false
        this.queue.shutdown()
        this.close()
    }

    fun connect() {
        val opts = Options()
        opts.token = token
        opts.maxReconnectDelay = timeout
        opts.maxServerPingDelay = timeout
        opts.timeout = timeout
        opts.tokenGetter = this.tokenGetter
        client = Client(url, opts, listener)
        Logger.debug("centrifuge: connect $channel $url $token")
        client?.connect()
    }

    fun close() {
        Logger.debug("centrifuge: close")
        try {
//            this.client?.close(timeout.toLong())
            this.client = null
        } catch (e: Exception) {
            Logger.error("centrifuge: close error", e)
        }
    }

    fun reconnect() {
//        this.close()
//        this.connect()
        this.client?.disconnect()
        this.client?.connect()
    }

    fun send(data: Message) {
        queue.append(data)
    }

    private var queue = object : MessageQueue<Message>() {
        override suspend fun sendable(): Boolean {
            return client?.state == ClientState.CONNECTED && sendable
        }

        override suspend fun send(proto: Message): Boolean {
            val client = client ?: return false
            try {
                return suspendCoroutine<Boolean> {
                    val data = proto.toByteArray()
                    sendable = false
                    client.publish(channel, data) { e, _ ->
                        if (e == null) {
                            Logger.debug("centrifuge: send success ${proto.logString}")
                            it.resume(true)
                        } else {
                            Logger.error("centrifuge: send failed ${proto.logString}", e)
                            sendable = true
                            onError(e)
                            it.resume(true)
                        }
                    }
                }
            } catch (e: Exception) {
                Logger.error("centrifuge: send error ${proto.logString}", e)
                sendable = true
                onError(e)
                return false
            }
        }
    }

    lateinit var tokenGetter: ConnectionTokenGetter
}

class CentrifugeListener(var client: CentrifugeClient) : EventListener() {

    override fun onConnecting(client: Client?, event: ConnectingEvent) {
        Logger.debug("centrifuge: onConnecting")
    }

    override fun onConnected(client: Client?, event: ConnectedEvent) {
        Logger.debug("centrifuge: onConnected")
    }

    override fun onDisconnected(client: Client?, event: DisconnectedEvent) {
        Logger.debug("centrifuge: onDisconnected")
    }

    override fun onError(client: Client?, event: ErrorEvent) {
        Logger.error("centrifuge: onError", event.error)
    }

    override fun onSubscribed(client: Client?, event: ServerSubscribedEvent) {
        this@CentrifugeListener.client.sendable = true
        this.client.onSubscribed(event.channel)
    }

    override fun onPublication(client: Client?, event: ServerPublicationEvent) {
        this@CentrifugeListener.client.sendable = true
        val data = event.data
        this.client.onReceive(data)
    }
}