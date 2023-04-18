//Generated by the protocol buffer compiler. DO NOT EDIT!
// source: kernel/protocol/client/model/v1/message/tracker.proto

package kernel.protocol.client.model.v1;

@kotlin.jvm.JvmName("-initializetrackerConnectNodeRequest")
public inline fun trackerConnectNodeRequest(block: kernel.protocol.client.model.v1.TrackerConnectNodeRequestKt.Dsl.() -> kotlin.Unit): kernel.protocol.client.model.v1.Tracker.TrackerConnectNodeRequest =
  kernel.protocol.client.model.v1.TrackerConnectNodeRequestKt.Dsl._create(kernel.protocol.client.model.v1.Tracker.TrackerConnectNodeRequest.newBuilder()).apply { block() }._build()
public object TrackerConnectNodeRequestKt {
  @kotlin.OptIn(com.google.protobuf.kotlin.OnlyForUseByGeneratedProtoCode::class)
  @com.google.protobuf.kotlin.ProtoDslMarker
  public class Dsl private constructor(
    private val _builder: kernel.protocol.client.model.v1.Tracker.TrackerConnectNodeRequest.Builder
  ) {
    public companion object {
      @kotlin.jvm.JvmSynthetic
      @kotlin.PublishedApi
      internal fun _create(builder: kernel.protocol.client.model.v1.Tracker.TrackerConnectNodeRequest.Builder): Dsl = Dsl(builder)
    }

    @kotlin.jvm.JvmSynthetic
    @kotlin.PublishedApi
    internal fun _build(): kernel.protocol.client.model.v1.Tracker.TrackerConnectNodeRequest = _builder.build()

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
     * <code>.kernel.protocol.client.model.v1.RequestHeader header = 2;</code>
     */
    public var header: kernel.protocol.client.model.v1.Base.RequestHeader
      @JvmName("getHeader")
      get() = _builder.getHeader()
      @JvmName("setHeader")
      set(value) {
        _builder.setHeader(value)
      }
    /**
     * <code>.kernel.protocol.client.model.v1.RequestHeader header = 2;</code>
     */
    public fun clearHeader() {
      _builder.clearHeader()
    }
    /**
     * <code>.kernel.protocol.client.model.v1.RequestHeader header = 2;</code>
     * @return Whether the header field is set.
     */
    public fun hasHeader(): kotlin.Boolean {
      return _builder.hasHeader()
    }

    /**
     * <code>.kernel.protocol.client.model.v1.TrackerConnectNodeRequest.RequestContent content = 3;</code>
     */
    public var content: kernel.protocol.client.model.v1.Tracker.TrackerConnectNodeRequest.RequestContent
      @JvmName("getContent")
      get() = _builder.getContent()
      @JvmName("setContent")
      set(value) {
        _builder.setContent(value)
      }
    /**
     * <code>.kernel.protocol.client.model.v1.TrackerConnectNodeRequest.RequestContent content = 3;</code>
     */
    public fun clearContent() {
      _builder.clearContent()
    }
    /**
     * <code>.kernel.protocol.client.model.v1.TrackerConnectNodeRequest.RequestContent content = 3;</code>
     * @return Whether the content field is set.
     */
    public fun hasContent(): kotlin.Boolean {
      return _builder.hasContent()
    }
  }
  @kotlin.jvm.JvmName("-initializerequestContent")
  public inline fun requestContent(block: kernel.protocol.client.model.v1.TrackerConnectNodeRequestKt.RequestContentKt.Dsl.() -> kotlin.Unit): kernel.protocol.client.model.v1.Tracker.TrackerConnectNodeRequest.RequestContent =
    kernel.protocol.client.model.v1.TrackerConnectNodeRequestKt.RequestContentKt.Dsl._create(kernel.protocol.client.model.v1.Tracker.TrackerConnectNodeRequest.RequestContent.newBuilder()).apply { block() }._build()
  public object RequestContentKt {
    @kotlin.OptIn(com.google.protobuf.kotlin.OnlyForUseByGeneratedProtoCode::class)
    @com.google.protobuf.kotlin.ProtoDslMarker
    public class Dsl private constructor(
      private val _builder: kernel.protocol.client.model.v1.Tracker.TrackerConnectNodeRequest.RequestContent.Builder
    ) {
      public companion object {
        @kotlin.jvm.JvmSynthetic
        @kotlin.PublishedApi
        internal fun _create(builder: kernel.protocol.client.model.v1.Tracker.TrackerConnectNodeRequest.RequestContent.Builder): Dsl = Dsl(builder)
      }

      @kotlin.jvm.JvmSynthetic
      @kotlin.PublishedApi
      internal fun _build(): kernel.protocol.client.model.v1.Tracker.TrackerConnectNodeRequest.RequestContent = _builder.build()

      /**
       * <code>string sdp = 1;</code>
       */
      public var sdp: kotlin.String
        @JvmName("getSdp")
        get() = _builder.getSdp()
        @JvmName("setSdp")
        set(value) {
          _builder.setSdp(value)
        }
      /**
       * <code>string sdp = 1;</code>
       */
      public fun clearSdp() {
        _builder.clearSdp()
      }

      /**
       * <code>string type = 2;</code>
       */
      public var type: kotlin.String
        @JvmName("getType")
        get() = _builder.getType()
        @JvmName("setType")
        set(value) {
          _builder.setType(value)
        }
      /**
       * <code>string type = 2;</code>
       */
      public fun clearType() {
        _builder.clearType()
      }
    }
  }
}
@kotlin.jvm.JvmSynthetic
public inline fun kernel.protocol.client.model.v1.Tracker.TrackerConnectNodeRequest.copy(block: kernel.protocol.client.model.v1.TrackerConnectNodeRequestKt.Dsl.() -> kotlin.Unit): kernel.protocol.client.model.v1.Tracker.TrackerConnectNodeRequest =
  kernel.protocol.client.model.v1.TrackerConnectNodeRequestKt.Dsl._create(this.toBuilder()).apply { block() }._build()

@kotlin.jvm.JvmSynthetic
public inline fun kernel.protocol.client.model.v1.Tracker.TrackerConnectNodeRequest.RequestContent.copy(block: kernel.protocol.client.model.v1.TrackerConnectNodeRequestKt.RequestContentKt.Dsl.() -> kotlin.Unit): kernel.protocol.client.model.v1.Tracker.TrackerConnectNodeRequest.RequestContent =
  kernel.protocol.client.model.v1.TrackerConnectNodeRequestKt.RequestContentKt.Dsl._create(this.toBuilder()).apply { block() }._build()

public val kernel.protocol.client.model.v1.Tracker.TrackerConnectNodeRequestOrBuilder.headerOrNull: kernel.protocol.client.model.v1.Base.RequestHeader?
  get() = if (hasHeader()) getHeader() else null

public val kernel.protocol.client.model.v1.Tracker.TrackerConnectNodeRequestOrBuilder.contentOrNull: kernel.protocol.client.model.v1.Tracker.TrackerConnectNodeRequest.RequestContent?
  get() = if (hasContent()) getContent() else null

