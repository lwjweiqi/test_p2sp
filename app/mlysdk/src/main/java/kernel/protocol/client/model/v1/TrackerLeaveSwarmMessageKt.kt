//Generated by the protocol buffer compiler. DO NOT EDIT!
// source: kernel/protocol/client/model/v1/message/tracker.proto

package kernel.protocol.client.model.v1;

@kotlin.jvm.JvmName("-initializetrackerLeaveSwarmMessage")
public inline fun trackerLeaveSwarmMessage(block: kernel.protocol.client.model.v1.TrackerLeaveSwarmMessageKt.Dsl.() -> kotlin.Unit): kernel.protocol.client.model.v1.Tracker.TrackerLeaveSwarmMessage =
  kernel.protocol.client.model.v1.TrackerLeaveSwarmMessageKt.Dsl._create(kernel.protocol.client.model.v1.Tracker.TrackerLeaveSwarmMessage.newBuilder()).apply { block() }._build()
public object TrackerLeaveSwarmMessageKt {
  @kotlin.OptIn(com.google.protobuf.kotlin.OnlyForUseByGeneratedProtoCode::class)
  @com.google.protobuf.kotlin.ProtoDslMarker
  public class Dsl private constructor(
    private val _builder: kernel.protocol.client.model.v1.Tracker.TrackerLeaveSwarmMessage.Builder
  ) {
    public companion object {
      @kotlin.jvm.JvmSynthetic
      @kotlin.PublishedApi
      internal fun _create(builder: kernel.protocol.client.model.v1.Tracker.TrackerLeaveSwarmMessage.Builder): Dsl = Dsl(builder)
    }

    @kotlin.jvm.JvmSynthetic
    @kotlin.PublishedApi
    internal fun _build(): kernel.protocol.client.model.v1.Tracker.TrackerLeaveSwarmMessage = _builder.build()

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
     * <code>.kernel.protocol.client.model.v1.TrackerLeaveSwarmMessage.MessageContent content = 3;</code>
     */
    public var content: kernel.protocol.client.model.v1.Tracker.TrackerLeaveSwarmMessage.MessageContent
      @JvmName("getContent")
      get() = _builder.getContent()
      @JvmName("setContent")
      set(value) {
        _builder.setContent(value)
      }
    /**
     * <code>.kernel.protocol.client.model.v1.TrackerLeaveSwarmMessage.MessageContent content = 3;</code>
     */
    public fun clearContent() {
      _builder.clearContent()
    }
    /**
     * <code>.kernel.protocol.client.model.v1.TrackerLeaveSwarmMessage.MessageContent content = 3;</code>
     * @return Whether the content field is set.
     */
    public fun hasContent(): kotlin.Boolean {
      return _builder.hasContent()
    }
  }
  @kotlin.jvm.JvmName("-initializemessageContent")
  public inline fun messageContent(block: kernel.protocol.client.model.v1.TrackerLeaveSwarmMessageKt.MessageContentKt.Dsl.() -> kotlin.Unit): kernel.protocol.client.model.v1.Tracker.TrackerLeaveSwarmMessage.MessageContent =
    kernel.protocol.client.model.v1.TrackerLeaveSwarmMessageKt.MessageContentKt.Dsl._create(kernel.protocol.client.model.v1.Tracker.TrackerLeaveSwarmMessage.MessageContent.newBuilder()).apply { block() }._build()
  public object MessageContentKt {
    @kotlin.OptIn(com.google.protobuf.kotlin.OnlyForUseByGeneratedProtoCode::class)
    @com.google.protobuf.kotlin.ProtoDslMarker
    public class Dsl private constructor(
      private val _builder: kernel.protocol.client.model.v1.Tracker.TrackerLeaveSwarmMessage.MessageContent.Builder
    ) {
      public companion object {
        @kotlin.jvm.JvmSynthetic
        @kotlin.PublishedApi
        internal fun _create(builder: kernel.protocol.client.model.v1.Tracker.TrackerLeaveSwarmMessage.MessageContent.Builder): Dsl = Dsl(builder)
      }

      @kotlin.jvm.JvmSynthetic
      @kotlin.PublishedApi
      internal fun _build(): kernel.protocol.client.model.v1.Tracker.TrackerLeaveSwarmMessage.MessageContent = _builder.build()

      /**
       * <code>string swarm_id = 1;</code>
       */
      public var swarmId: kotlin.String
        @JvmName("getSwarmId")
        get() = _builder.getSwarmId()
        @JvmName("setSwarmId")
        set(value) {
          _builder.setSwarmId(value)
        }
      /**
       * <code>string swarm_id = 1;</code>
       */
      public fun clearSwarmId() {
        _builder.clearSwarmId()
      }
    }
  }
}
@kotlin.jvm.JvmSynthetic
public inline fun kernel.protocol.client.model.v1.Tracker.TrackerLeaveSwarmMessage.copy(block: kernel.protocol.client.model.v1.TrackerLeaveSwarmMessageKt.Dsl.() -> kotlin.Unit): kernel.protocol.client.model.v1.Tracker.TrackerLeaveSwarmMessage =
  kernel.protocol.client.model.v1.TrackerLeaveSwarmMessageKt.Dsl._create(this.toBuilder()).apply { block() }._build()

@kotlin.jvm.JvmSynthetic
public inline fun kernel.protocol.client.model.v1.Tracker.TrackerLeaveSwarmMessage.MessageContent.copy(block: kernel.protocol.client.model.v1.TrackerLeaveSwarmMessageKt.MessageContentKt.Dsl.() -> kotlin.Unit): kernel.protocol.client.model.v1.Tracker.TrackerLeaveSwarmMessage.MessageContent =
  kernel.protocol.client.model.v1.TrackerLeaveSwarmMessageKt.MessageContentKt.Dsl._create(this.toBuilder()).apply { block() }._build()

public val kernel.protocol.client.model.v1.Tracker.TrackerLeaveSwarmMessageOrBuilder.headerOrNull: kernel.protocol.client.model.v1.Base.MessageHeader?
  get() = if (hasHeader()) getHeader() else null

public val kernel.protocol.client.model.v1.Tracker.TrackerLeaveSwarmMessageOrBuilder.contentOrNull: kernel.protocol.client.model.v1.Tracker.TrackerLeaveSwarmMessage.MessageContent?
  get() = if (hasContent()) getContent() else null
