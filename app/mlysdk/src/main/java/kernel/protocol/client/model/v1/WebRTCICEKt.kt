//Generated by the protocol buffer compiler. DO NOT EDIT!
// source: kernel/protocol/client/model/v1/base/webrtc.proto

package kernel.protocol.client.model.v1;

@kotlin.jvm.JvmName("-initializewebRTCICE")
public inline fun webRTCICE(block: kernel.protocol.client.model.v1.WebRTCICEKt.Dsl.() -> kotlin.Unit): kernel.protocol.client.model.v1.Webrtc.WebRTCICE =
  kernel.protocol.client.model.v1.WebRTCICEKt.Dsl._create(kernel.protocol.client.model.v1.Webrtc.WebRTCICE.newBuilder()).apply { block() }._build()
public object WebRTCICEKt {
  @kotlin.OptIn(com.google.protobuf.kotlin.OnlyForUseByGeneratedProtoCode::class)
  @com.google.protobuf.kotlin.ProtoDslMarker
  public class Dsl private constructor(
    private val _builder: kernel.protocol.client.model.v1.Webrtc.WebRTCICE.Builder
  ) {
    public companion object {
      @kotlin.jvm.JvmSynthetic
      @kotlin.PublishedApi
      internal fun _create(builder: kernel.protocol.client.model.v1.Webrtc.WebRTCICE.Builder): Dsl = Dsl(builder)
    }

    @kotlin.jvm.JvmSynthetic
    @kotlin.PublishedApi
    internal fun _build(): kernel.protocol.client.model.v1.Webrtc.WebRTCICE = _builder.build()

    /**
     * <code>optional string candidate = 1;</code>
     */
    public var candidate: kotlin.String
      @JvmName("getCandidate")
      get() = _builder.getCandidate()
      @JvmName("setCandidate")
      set(value) {
        _builder.setCandidate(value)
      }
    /**
     * <code>optional string candidate = 1;</code>
     */
    public fun clearCandidate() {
      _builder.clearCandidate()
    }
    /**
     * <code>optional string candidate = 1;</code>
     * @return Whether the candidate field is set.
     */
    public fun hasCandidate(): kotlin.Boolean {
      return _builder.hasCandidate()
    }

    /**
     * <code>optional string sdpMid = 2;</code>
     */
    public var sdpMid: kotlin.String
      @JvmName("getSdpMid")
      get() = _builder.getSdpMid()
      @JvmName("setSdpMid")
      set(value) {
        _builder.setSdpMid(value)
      }
    /**
     * <code>optional string sdpMid = 2;</code>
     */
    public fun clearSdpMid() {
      _builder.clearSdpMid()
    }
    /**
     * <code>optional string sdpMid = 2;</code>
     * @return Whether the sdpMid field is set.
     */
    public fun hasSdpMid(): kotlin.Boolean {
      return _builder.hasSdpMid()
    }

    /**
     * <code>optional int64 sdpMLineIndex = 3;</code>
     */
    public var sdpMLineIndex: kotlin.Long
      @JvmName("getSdpMLineIndex")
      get() = _builder.getSdpMLineIndex()
      @JvmName("setSdpMLineIndex")
      set(value) {
        _builder.setSdpMLineIndex(value)
      }
    /**
     * <code>optional int64 sdpMLineIndex = 3;</code>
     */
    public fun clearSdpMLineIndex() {
      _builder.clearSdpMLineIndex()
    }
    /**
     * <code>optional int64 sdpMLineIndex = 3;</code>
     * @return Whether the sdpMLineIndex field is set.
     */
    public fun hasSdpMLineIndex(): kotlin.Boolean {
      return _builder.hasSdpMLineIndex()
    }

    /**
     * <code>optional string usernameFragment = 4;</code>
     */
    public var usernameFragment: kotlin.String
      @JvmName("getUsernameFragment")
      get() = _builder.getUsernameFragment()
      @JvmName("setUsernameFragment")
      set(value) {
        _builder.setUsernameFragment(value)
      }
    /**
     * <code>optional string usernameFragment = 4;</code>
     */
    public fun clearUsernameFragment() {
      _builder.clearUsernameFragment()
    }
    /**
     * <code>optional string usernameFragment = 4;</code>
     * @return Whether the usernameFragment field is set.
     */
    public fun hasUsernameFragment(): kotlin.Boolean {
      return _builder.hasUsernameFragment()
    }
  }
}
@kotlin.jvm.JvmSynthetic
public inline fun kernel.protocol.client.model.v1.Webrtc.WebRTCICE.copy(block: kernel.protocol.client.model.v1.WebRTCICEKt.Dsl.() -> kotlin.Unit): kernel.protocol.client.model.v1.Webrtc.WebRTCICE =
  kernel.protocol.client.model.v1.WebRTCICEKt.Dsl._create(this.toBuilder()).apply { block() }._build()

