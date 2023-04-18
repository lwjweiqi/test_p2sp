//Generated by the protocol buffer compiler. DO NOT EDIT!
// source: kernel/protocol/client/model/v1/message/peer.proto

package kernel.protocol.client.model.v1;

@kotlin.jvm.JvmName("-initializepeerReportResourceStatMessage")
public inline fun peerReportResourceStatMessage(block: kernel.protocol.client.model.v1.PeerReportResourceStatMessageKt.Dsl.() -> kotlin.Unit): kernel.protocol.client.model.v1.Peer.PeerReportResourceStatMessage =
  kernel.protocol.client.model.v1.PeerReportResourceStatMessageKt.Dsl._create(kernel.protocol.client.model.v1.Peer.PeerReportResourceStatMessage.newBuilder()).apply { block() }._build()
public object PeerReportResourceStatMessageKt {
  @kotlin.OptIn(com.google.protobuf.kotlin.OnlyForUseByGeneratedProtoCode::class)
  @com.google.protobuf.kotlin.ProtoDslMarker
  public class Dsl private constructor(
    private val _builder: kernel.protocol.client.model.v1.Peer.PeerReportResourceStatMessage.Builder
  ) {
    public companion object {
      @kotlin.jvm.JvmSynthetic
      @kotlin.PublishedApi
      internal fun _create(builder: kernel.protocol.client.model.v1.Peer.PeerReportResourceStatMessage.Builder): Dsl = Dsl(builder)
    }

    @kotlin.jvm.JvmSynthetic
    @kotlin.PublishedApi
    internal fun _build(): kernel.protocol.client.model.v1.Peer.PeerReportResourceStatMessage = _builder.build()

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
     * <code>.kernel.protocol.client.model.v1.MessageHeader header = 2;</code>
     */
    public var header: kernel.protocol.client.model.v1.Base.MessageHeader
      @JvmName("getHeader")
      get() = _builder.getHeader()
      @JvmName("setHeader")
      set(value) {
        _builder.setHeader(value)
      }
    /**
     * <code>.kernel.protocol.client.model.v1.MessageHeader header = 2;</code>
     */
    public fun clearHeader() {
      _builder.clearHeader()
    }
    /**
     * <code>.kernel.protocol.client.model.v1.MessageHeader header = 2;</code>
     * @return Whether the header field is set.
     */
    public fun hasHeader(): kotlin.Boolean {
      return _builder.hasHeader()
    }

    /**
     * <code>.kernel.protocol.client.model.v1.PeerReportResourceStatMessage.MessageContent content = 3;</code>
     */
    public var content: kernel.protocol.client.model.v1.Peer.PeerReportResourceStatMessage.MessageContent
      @JvmName("getContent")
      get() = _builder.getContent()
      @JvmName("setContent")
      set(value) {
        _builder.setContent(value)
      }
    /**
     * <code>.kernel.protocol.client.model.v1.PeerReportResourceStatMessage.MessageContent content = 3;</code>
     */
    public fun clearContent() {
      _builder.clearContent()
    }
    /**
     * <code>.kernel.protocol.client.model.v1.PeerReportResourceStatMessage.MessageContent content = 3;</code>
     * @return Whether the content field is set.
     */
    public fun hasContent(): kotlin.Boolean {
      return _builder.hasContent()
    }
  }
  @kotlin.jvm.JvmName("-initializemessageContent")
  public inline fun messageContent(block: kernel.protocol.client.model.v1.PeerReportResourceStatMessageKt.MessageContentKt.Dsl.() -> kotlin.Unit): kernel.protocol.client.model.v1.Peer.PeerReportResourceStatMessage.MessageContent =
    kernel.protocol.client.model.v1.PeerReportResourceStatMessageKt.MessageContentKt.Dsl._create(kernel.protocol.client.model.v1.Peer.PeerReportResourceStatMessage.MessageContent.newBuilder()).apply { block() }._build()
  public object MessageContentKt {
    @kotlin.OptIn(com.google.protobuf.kotlin.OnlyForUseByGeneratedProtoCode::class)
    @com.google.protobuf.kotlin.ProtoDslMarker
    public class Dsl private constructor(
      private val _builder: kernel.protocol.client.model.v1.Peer.PeerReportResourceStatMessage.MessageContent.Builder
    ) {
      public companion object {
        @kotlin.jvm.JvmSynthetic
        @kotlin.PublishedApi
        internal fun _create(builder: kernel.protocol.client.model.v1.Peer.PeerReportResourceStatMessage.MessageContent.Builder): Dsl = Dsl(builder)
      }

      @kotlin.jvm.JvmSynthetic
      @kotlin.PublishedApi
      internal fun _build(): kernel.protocol.client.model.v1.Peer.PeerReportResourceStatMessage.MessageContent = _builder.build()

      /**
       * <code>.kernel.protocol.client.model.v1.ResourceStat stat = 1;</code>
       */
      public var stat: kernel.protocol.client.model.v1.Resource.ResourceStat
        @JvmName("getStat")
        get() = _builder.getStat()
        @JvmName("setStat")
        set(value) {
          _builder.setStat(value)
        }
      /**
       * <code>.kernel.protocol.client.model.v1.ResourceStat stat = 1;</code>
       */
      public fun clearStat() {
        _builder.clearStat()
      }
      /**
       * <code>.kernel.protocol.client.model.v1.ResourceStat stat = 1;</code>
       * @return Whether the stat field is set.
       */
      public fun hasStat(): kotlin.Boolean {
        return _builder.hasStat()
      }
    }
  }
}
@kotlin.jvm.JvmSynthetic
public inline fun kernel.protocol.client.model.v1.Peer.PeerReportResourceStatMessage.copy(block: kernel.protocol.client.model.v1.PeerReportResourceStatMessageKt.Dsl.() -> kotlin.Unit): kernel.protocol.client.model.v1.Peer.PeerReportResourceStatMessage =
  kernel.protocol.client.model.v1.PeerReportResourceStatMessageKt.Dsl._create(this.toBuilder()).apply { block() }._build()

@kotlin.jvm.JvmSynthetic
public inline fun kernel.protocol.client.model.v1.Peer.PeerReportResourceStatMessage.MessageContent.copy(block: kernel.protocol.client.model.v1.PeerReportResourceStatMessageKt.MessageContentKt.Dsl.() -> kotlin.Unit): kernel.protocol.client.model.v1.Peer.PeerReportResourceStatMessage.MessageContent =
  kernel.protocol.client.model.v1.PeerReportResourceStatMessageKt.MessageContentKt.Dsl._create(this.toBuilder()).apply { block() }._build()

public val kernel.protocol.client.model.v1.Peer.PeerReportResourceStatMessage.MessageContentOrBuilder.statOrNull: kernel.protocol.client.model.v1.Resource.ResourceStat?
  get() = if (hasStat()) getStat() else null

public val kernel.protocol.client.model.v1.Peer.PeerReportResourceStatMessageOrBuilder.headerOrNull: kernel.protocol.client.model.v1.Base.MessageHeader?
  get() = if (hasHeader()) getHeader() else null

public val kernel.protocol.client.model.v1.Peer.PeerReportResourceStatMessageOrBuilder.contentOrNull: kernel.protocol.client.model.v1.Peer.PeerReportResourceStatMessage.MessageContent?
  get() = if (hasContent()) getContent() else null
