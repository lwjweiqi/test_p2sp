//Generated by the protocol buffer compiler. DO NOT EDIT!
// source: kernel/protocol/client/model/v1/message/tracker.proto

package kernel.protocol.client.model.v1;

@kotlin.jvm.JvmName("-initializetrackerConnectNodeResponse")
public inline fun trackerConnectNodeResponse(block: kernel.protocol.client.model.v1.TrackerConnectNodeResponseKt.Dsl.() -> kotlin.Unit): kernel.protocol.client.model.v1.Tracker.TrackerConnectNodeResponse =
  kernel.protocol.client.model.v1.TrackerConnectNodeResponseKt.Dsl._create(kernel.protocol.client.model.v1.Tracker.TrackerConnectNodeResponse.newBuilder()).apply { block() }._build()
public object TrackerConnectNodeResponseKt {
  @kotlin.OptIn(com.google.protobuf.kotlin.OnlyForUseByGeneratedProtoCode::class)
  @com.google.protobuf.kotlin.ProtoDslMarker
  public class Dsl private constructor(
    private val _builder: kernel.protocol.client.model.v1.Tracker.TrackerConnectNodeResponse.Builder
  ) {
    public companion object {
      @kotlin.jvm.JvmSynthetic
      @kotlin.PublishedApi
      internal fun _create(builder: kernel.protocol.client.model.v1.Tracker.TrackerConnectNodeResponse.Builder): Dsl = Dsl(builder)
    }

    @kotlin.jvm.JvmSynthetic
    @kotlin.PublishedApi
    internal fun _build(): kernel.protocol.client.model.v1.Tracker.TrackerConnectNodeResponse = _builder.build()

    /**
     * <code>string action = 1;</code>
     */
    public var action: kotlin.String
      @JvmName("getAction")
      get() = _builder.getAction()
      @JvmName("setAction")
      set(value) {
        _builder.setAction(value)
      }
    /**
     * <code>string action = 1;</code>
     */
    public fun clearAction() {
      _builder.clearAction()
    }

    /**
     * <code>.kernel.protocol.client.model.v1.ResponseHeader header = 2;</code>
     */
    public var header: kernel.protocol.client.model.v1.Base.ResponseHeader
      @JvmName("getHeader")
      get() = _builder.getHeader()
      @JvmName("setHeader")
      set(value) {
        _builder.setHeader(value)
      }
    /**
     * <code>.kernel.protocol.client.model.v1.ResponseHeader header = 2;</code>
     */
    public fun clearHeader() {
      _builder.clearHeader()
    }
    /**
     * <code>.kernel.protocol.client.model.v1.ResponseHeader header = 2;</code>
     * @return Whether the header field is set.
     */
    public fun hasHeader(): kotlin.Boolean {
      return _builder.hasHeader()
    }

    /**
     * <code>.kernel.protocol.client.model.v1.TrackerConnectNodeResponse.ResponseContent content = 3;</code>
     */
    public var content: kernel.protocol.client.model.v1.Tracker.TrackerConnectNodeResponse.ResponseContent
      @JvmName("getContent")
      get() = _builder.getContent()
      @JvmName("setContent")
      set(value) {
        _builder.setContent(value)
      }
    /**
     * <code>.kernel.protocol.client.model.v1.TrackerConnectNodeResponse.ResponseContent content = 3;</code>
     */
    public fun clearContent() {
      _builder.clearContent()
    }
    /**
     * <code>.kernel.protocol.client.model.v1.TrackerConnectNodeResponse.ResponseContent content = 3;</code>
     * @return Whether the content field is set.
     */
    public fun hasContent(): kotlin.Boolean {
      return _builder.hasContent()
    }
  }
  @kotlin.jvm.JvmName("-initializeresponseContent")
  public inline fun responseContent(block: kernel.protocol.client.model.v1.TrackerConnectNodeResponseKt.ResponseContentKt.Dsl.() -> kotlin.Unit): kernel.protocol.client.model.v1.Tracker.TrackerConnectNodeResponse.ResponseContent =
    kernel.protocol.client.model.v1.TrackerConnectNodeResponseKt.ResponseContentKt.Dsl._create(kernel.protocol.client.model.v1.Tracker.TrackerConnectNodeResponse.ResponseContent.newBuilder()).apply { block() }._build()
  public object ResponseContentKt {
    @kotlin.OptIn(com.google.protobuf.kotlin.OnlyForUseByGeneratedProtoCode::class)
    @com.google.protobuf.kotlin.ProtoDslMarker
    public class Dsl private constructor(
      private val _builder: kernel.protocol.client.model.v1.Tracker.TrackerConnectNodeResponse.ResponseContent.Builder
    ) {
      public companion object {
        @kotlin.jvm.JvmSynthetic
        @kotlin.PublishedApi
        internal fun _create(builder: kernel.protocol.client.model.v1.Tracker.TrackerConnectNodeResponse.ResponseContent.Builder): Dsl = Dsl(builder)
      }

      @kotlin.jvm.JvmSynthetic
      @kotlin.PublishedApi
      internal fun _build(): kernel.protocol.client.model.v1.Tracker.TrackerConnectNodeResponse.ResponseContent = _builder.build()

      /**
       * <code>string peer_id = 1;</code>
       */
      public var peerId: kotlin.String
        @JvmName("getPeerId")
        get() = _builder.getPeerId()
        @JvmName("setPeerId")
        set(value) {
          _builder.setPeerId(value)
        }
      /**
       * <code>string peer_id = 1;</code>
       */
      public fun clearPeerId() {
        _builder.clearPeerId()
      }

      /**
       * <code>string sdp = 2;</code>
       */
      public var sdp: kotlin.String
        @JvmName("getSdp")
        get() = _builder.getSdp()
        @JvmName("setSdp")
        set(value) {
          _builder.setSdp(value)
        }
      /**
       * <code>string sdp = 2;</code>
       */
      public fun clearSdp() {
        _builder.clearSdp()
      }

      /**
       * <code>string type = 3;</code>
       */
      public var type: kotlin.String
        @JvmName("getType")
        get() = _builder.getType()
        @JvmName("setType")
        set(value) {
          _builder.setType(value)
        }
      /**
       * <code>string type = 3;</code>
       */
      public fun clearType() {
        _builder.clearType()
      }
    }
  }
}
@kotlin.jvm.JvmSynthetic
public inline fun kernel.protocol.client.model.v1.Tracker.TrackerConnectNodeResponse.copy(block: kernel.protocol.client.model.v1.TrackerConnectNodeResponseKt.Dsl.() -> kotlin.Unit): kernel.protocol.client.model.v1.Tracker.TrackerConnectNodeResponse =
  kernel.protocol.client.model.v1.TrackerConnectNodeResponseKt.Dsl._create(this.toBuilder()).apply { block() }._build()

@kotlin.jvm.JvmSynthetic
public inline fun kernel.protocol.client.model.v1.Tracker.TrackerConnectNodeResponse.ResponseContent.copy(block: kernel.protocol.client.model.v1.TrackerConnectNodeResponseKt.ResponseContentKt.Dsl.() -> kotlin.Unit): kernel.protocol.client.model.v1.Tracker.TrackerConnectNodeResponse.ResponseContent =
  kernel.protocol.client.model.v1.TrackerConnectNodeResponseKt.ResponseContentKt.Dsl._create(this.toBuilder()).apply { block() }._build()

public val kernel.protocol.client.model.v1.Tracker.TrackerConnectNodeResponseOrBuilder.headerOrNull: kernel.protocol.client.model.v1.Base.ResponseHeader?
  get() = if (hasHeader()) getHeader() else null

public val kernel.protocol.client.model.v1.Tracker.TrackerConnectNodeResponseOrBuilder.contentOrNull: kernel.protocol.client.model.v1.Tracker.TrackerConnectNodeResponse.ResponseContent?
  get() = if (hasContent()) getContent() else null

