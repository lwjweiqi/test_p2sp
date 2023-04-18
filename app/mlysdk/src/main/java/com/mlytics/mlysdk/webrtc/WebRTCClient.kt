package com.mlytics.mlysdk.webrtc

import android.content.Context
import com.google.protobuf.Message
import com.mlytics.mlysdk.peer.logString
import com.mlytics.mlysdk.util.AsyncValue
import com.mlytics.mlysdk.util.Logger
import com.mlytics.mlysdk.util.MessageQueue
import org.webrtc.DataChannel
import org.webrtc.DataChannel.Buffer
import org.webrtc.IceCandidate
import org.webrtc.MediaConstraints
import org.webrtc.MediaStream
import org.webrtc.PeerConnection
import org.webrtc.PeerConnectionFactory
import org.webrtc.RtpReceiver
import org.webrtc.SdpObserver
import org.webrtc.SessionDescription
import java.nio.ByteBuffer
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class WebRTCClient : PeerConnection.Observer, DataChannel.Observer {
    enum class ClientType {
        Offer, Answer
    }

    companion object {
        var DEFAULT_DATACHANNEL_NAME = "datachannel"
        private var factory: PeerConnectionFactory? = null

        fun createFactory(context: Context) {
            if (factory != null) {
                return
            }

            val option = PeerConnectionFactory.InitializationOptions.builder(context)
                .setEnableInternalTracer(true).createInitializationOptions()
            PeerConnectionFactory.initialize(option)

            val builder = PeerConnectionFactory.builder()
            factory = builder.createPeerConnectionFactory()
        }
    }

    var options = PeerConnection.RTCConfiguration(
        listOf(
            PeerConnection.IceServer.builder("stun:stun.l.google.com:19302").createIceServer(),
            PeerConnection.IceServer.builder("stun:stun.1und1.de:3478").createIceServer()
        )
    ).apply {
        sdpSemantics = PeerConnection.SdpSemantics.UNIFIED_PLAN
        continualGatheringPolicy = PeerConnection.ContinualGatheringPolicy.GATHER_ONCE
    }

    var connection: PeerConnection? = null
    var channel: DataChannel? = null
    var remoteChannel: DataChannel? = null

    var type: ClientType = ClientType.Offer
    lateinit var state: AsyncValue<PeerConnection.IceConnectionState>
    var onIce: (IceCandidate) -> Unit = { }
    var onClose: () -> Unit = {}
    var onReceive: (ByteArray) -> Unit = { }

    private fun connect() {
        Logger.debug("webrtc: connect")
        this.setupConnection()
        this.setupChannel()
        this.initSender()
    }

    // 己方
    suspend fun createOffer(): SessionDescription? {
        Logger.debug("webrtc: create offer")
        this.type = ClientType.Offer
        this.connect()

        return suspendCoroutine<SessionDescription?> {
            val co = CreateObserver { sdp ->
                if (sdp == null) {
                    it.resume(null)
                } else {
                    Logger.debug("webrtc: set local offer")
                    val so = SetterObserver { success ->
                        if (success) {
                            it.resume(sdp)
                        } else {
                            it.resume(null)
                        }
                    }
                    this.connection?.setLocalDescription(so, sdp)
                }
            }
            var constraints = MediaConstraints()
            this.connection?.createOffer(co, constraints)
        }
    }

    // 对方
    suspend fun commitOfferAndCreateAnswer(
        offer: SessionDescription
    ): SessionDescription? {
        this.type = ClientType.Answer
        this.connect()
        val success = suspendCoroutine {
            val so = SetterObserver { success ->
                it.resume(success)
            }
            this.connection?.setRemoteDescription(so, offer)
        }
        if (success) {
            return createAnswer()
        } else {
            return null
        }
    }

    // 对方
    private suspend fun createAnswer(): SessionDescription? {
        Logger.debug("webrtc: create answer")
        return suspendCoroutine {
            val co = CreateObserver { sdp ->
                if (sdp == null) {
                    it.resume(null)
                } else {
                    val so = SetterObserver { success ->
                        if (success) {
                            it.resume(sdp)
                        } else {
                            it.resume(null)
                        }
                    }
                    this.connection?.setLocalDescription(so, sdp)
                }
            }
            val constraints = MediaConstraints()
            this.connection?.createAnswer(co, constraints)
        }

    }

    // 己方
    suspend fun commitAnswer(answer: SessionDescription): Boolean {
        val connection = this.connection ?: return false
        return suspendCoroutine<Boolean> {
            val so = SetterObserver { success ->
                it.resume(success)
            }
            connection.setRemoteDescription(so, answer)
        }
    }

    fun addIceCandidate(ice: IceCandidate) {
        Logger.debug("webrtc: recv ice ${ice}")
        this.connect()
        this.connection?.addIceCandidate(ice)
    }

    fun addIceCandidate(sdp: String, sdpMLineIndex: Int, sdpMid: String?) {
        val ice = IceCandidate(sdpMid, sdpMLineIndex, sdp)
        this.addIceCandidate(ice)
    }

    fun send(msg: Message) {
        this.queue?.append(msg)
    }

    private fun send0(msg: Message): Boolean {
        val channel = this.remoteChannel ?: this.channel ?: return false
        val bytes = msg.toByteArray()
        val data = ByteBuffer.wrap(bytes)
        val buffer = Buffer(data, true)
        val success = channel.send(buffer)
        Logger.debug("webrtc: send $success ${msg.logString}")
        return success
    }

    fun close() {
        this.connection?.close()
        this.channel?.close()
        this.connection = null
        this.channel = null
        this.queue?.shutdown()
        this.queue = null
    }

    private fun setupConnection() {

        if (this.connection != null) {
            Logger.debug("webrtc: exists connection")
            return
        }
        if (factory == null) {
            Logger.debug("webrtc: factory null")
            return
        }
        Logger.debug("webrtc: setup connection")

        this.connection = factory!!.createPeerConnection(
            options, this
        )
    }

    fun setupChannel() {
        if (this.channel != null) {
            Logger.debug("webrtc: exists channel")
            return
        }
        Logger.debug("webrtc: setup channel")

        var config = DataChannel.Init()
        config.ordered = false
        config.id = 0
        config.negotiated = false

        this.channel = this.connection!!.createDataChannel(DEFAULT_DATACHANNEL_NAME, config)
        this.channel!!.registerObserver(this)

    }

    override fun onSignalingChange(p0: PeerConnection.SignalingState?) {
        Logger.debug("webrtc: onSignalingChange $p0")
    }

    override fun onIceConnectionChange(p0: PeerConnection.IceConnectionState?) {
        Logger.debug("webrtc: onIceConnectionChange $p0")
    }

    override fun onIceConnectionReceivingChange(p0: Boolean) {
        Logger.debug("webrtc: onIceConnectionReceivingChange $p0")
    }

    override fun onIceGatheringChange(p0: PeerConnection.IceGatheringState?) {
        Logger.debug("webrtc: onIceGatheringChange $p0")
    }

    override fun onIceCandidate(p0: IceCandidate?) {
        Logger.debug("webrtc: onIceCandidate $p0")
        p0?.apply(onIce)
    }

    override fun onIceCandidatesRemoved(p0: Array<out IceCandidate>?) {
        Logger.debug("webrtc: onIceCandidatesRemoved $p0")
    }

    override fun onAddStream(p0: MediaStream?) {
        Logger.debug("webrtc: onAddStream $p0")
    }

    override fun onRemoveStream(p0: MediaStream?) {
        Logger.debug("webrtc: onRemoveStream $p0")
    }

    override fun onDataChannel(p0: DataChannel?) {
        Logger.debug("webrtc: DataChannel $p0")
        this.remoteChannel = p0
        this.remoteChannel?.registerObserver(this)
    }

    override fun onRenegotiationNeeded() {
        Logger.debug("webrtc: onRenegotiationNeeded")
    }

    override fun onAddTrack(p0: RtpReceiver?, p1: Array<out MediaStream>?) {
        Logger.debug("webrtc: onAddTrack $p0")
    }

    override fun onBufferedAmountChange(p0: Long) {
        Logger.debug("webrtc: onBufferedAmountChange $p0")
    }

    override fun onStateChange() {
        val state = this.channel?.state()
        Logger.debug("webrtc: data channel state $state")
        when (state) {
            DataChannel.State.CONNECTING -> {

            }
            DataChannel.State.OPEN -> {

            }
            DataChannel.State.CLOSING -> {

            }
            DataChannel.State.CLOSED -> {
                this.onClose()
            }
            else -> Logger.debug("webrtc: ignore state")
        }
    }

    override fun onMessage(p0: Buffer?) {
        Logger.debug("webrtc: recv")
        try {
            val data = p0?.data ?: return
            var bytes = ByteArray(data.capacity())
            data.get(bytes)
            this.onReceive(bytes)
        } catch (e: Exception) {
            Logger.error("webrtc: recv", e)
        }

    }

    var queue: MessageQueue<Message>? = null
    private fun initSender() {
        queue = object : MessageQueue<Message>() {
            override suspend fun sendable(): Boolean {
                val channel = remoteChannel ?: channel ?: return false
                val state = channel.state()
                val amount = channel.bufferedAmount()
                return state == DataChannel.State.OPEN && amount == 0L
            }

            override suspend fun send(data: Message): Boolean {
                return send0(data)
            }

        }
    }

    class CreateObserver(val callback: (SessionDescription?) -> Unit) : SdpObserver {
        override fun onCreateSuccess(p0: SessionDescription?) {
            callback(p0)
        }

        override fun onSetSuccess() {
            Logger.debug("webrtc: onSetSuccess")
        }

        override fun onCreateFailure(p0: String?) {
            callback(null)
        }

        override fun onSetFailure(p0: String?) {
            Logger.debug("webrtc: onSetFailure")
        }
    }

    class SetterObserver(val callback: (Boolean) -> Unit) : SdpObserver {
        override fun onCreateSuccess(p0: SessionDescription?) {
            Logger.debug("webrtc: onCreateSuccess")
        }

        override fun onSetSuccess() {
            callback(true)
        }

        override fun onCreateFailure(p0: String?) {
            Logger.debug("webrtc: onCreateFailure")
        }

        override fun onSetFailure(p0: String?) {
            callback(false)
        }
    }
}

