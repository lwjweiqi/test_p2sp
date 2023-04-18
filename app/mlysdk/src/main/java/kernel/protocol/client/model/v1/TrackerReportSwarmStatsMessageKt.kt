//Generated by the protocol buffer compiler. DO NOT EDIT!
// source: kernel/protocol/client/model/v1/message/tracker.proto

package kernel.protocol.client.model.v1;

@kotlin.jvm.JvmName("-initializetrackerReportSwarmStatsMessage")
public inline fun trackerReportSwarmStatsMessage(block: kernel.protocol.client.model.v1.TrackerReportSwarmStatsMessageKt.Dsl.() -> kotlin.Unit): kernel.protocol.client.model.v1.Tracker.TrackerReportSwarmStatsMessage =
  kernel.protocol.client.model.v1.TrackerReportSwarmStatsMessageKt.Dsl._create(kernel.protocol.client.model.v1.Tracker.TrackerReportSwarmStatsMessage.newBuilder()).apply { block() }._build()
public object TrackerReportSwarmStatsMessageKt {
  @kotlin.OptIn(com.google.protobuf.kotlin.OnlyForUseByGeneratedProtoCode::class)
  @com.google.protobuf.kotlin.ProtoDslMarker
  public class Dsl private constructor(
    private val _builder: kernel.protocol.client.model.v1.Tracker.TrackerReportSwarmStatsMessage.Builder
  ) {
    public companion object {
      @kotlin.jvm.JvmSynthetic
      @kotlin.PublishedApi
      internal fun _create(builder: kernel.protocol.client.model.v1.Tracker.TrackerReportSwarmStatsMessage.Builder): Dsl = Dsl(builder)
    }

    @kotlin.jvm.JvmSynthetic
    @kotlin.PublishedApi
    internal fun _build(): kernel.protocol.client.model.v1.Tracker.TrackerReportSwarmStatsMessage = _builder.build()

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
     * <code>.kernel.protocol.client.model.v1.TrackerReportSwarmStatsMessage.MessageContent content = 3;</code>
     */
    public var content: kernel.protocol.client.model.v1.Tracker.TrackerReportSwarmStatsMessage.MessageContent
      @JvmName("getContent")
      get() = _builder.getContent()
      @JvmName("setContent")
      set(value) {
        _builder.setContent(value)
      }
    /**
     * <code>.kernel.protocol.client.model.v1.TrackerReportSwarmStatsMessage.MessageContent content = 3;</code>
     */
    public fun clearContent() {
      _builder.clearContent()
    }
    /**
     * <code>.kernel.protocol.client.model.v1.TrackerReportSwarmStatsMessage.MessageContent content = 3;</code>
     * @return Whether the content field is set.
     */
    public fun hasContent(): kotlin.Boolean {
      return _builder.hasContent()
    }
  }
  @kotlin.jvm.JvmName("-initializemessageContent")
  public inline fun messageContent(block: kernel.protocol.client.model.v1.TrackerReportSwarmStatsMessageKt.MessageContentKt.Dsl.() -> kotlin.Unit): kernel.protocol.client.model.v1.Tracker.TrackerReportSwarmStatsMessage.MessageContent =
    kernel.protocol.client.model.v1.TrackerReportSwarmStatsMessageKt.MessageContentKt.Dsl._create(kernel.protocol.client.model.v1.Tracker.TrackerReportSwarmStatsMessage.MessageContent.newBuilder()).apply { block() }._build()
  public object MessageContentKt {
    @kotlin.OptIn(com.google.protobuf.kotlin.OnlyForUseByGeneratedProtoCode::class)
    @com.google.protobuf.kotlin.ProtoDslMarker
    public class Dsl private constructor(
      private val _builder: kernel.protocol.client.model.v1.Tracker.TrackerReportSwarmStatsMessage.MessageContent.Builder
    ) {
      public companion object {
        @kotlin.jvm.JvmSynthetic
        @kotlin.PublishedApi
        internal fun _create(builder: kernel.protocol.client.model.v1.Tracker.TrackerReportSwarmStatsMessage.MessageContent.Builder): Dsl = Dsl(builder)
      }

      @kotlin.jvm.JvmSynthetic
      @kotlin.PublishedApi
      internal fun _build(): kernel.protocol.client.model.v1.Tracker.TrackerReportSwarmStatsMessage.MessageContent = _builder.build()

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

      /**
       * <code>double swarm_score = 2;</code>
       */
      public var swarmScore: kotlin.Double
        @JvmName("getSwarmScore")
        get() = _builder.getSwarmScore()
        @JvmName("setSwarmScore")
        set(value) {
          _builder.setSwarmScore(value)
        }
      /**
       * <code>double swarm_score = 2;</code>
       */
      public fun clearSwarmScore() {
        _builder.clearSwarmScore()
      }
    }
  }
}
@kotlin.jvm.JvmSynthetic
public inline fun kernel.protocol.client.model.v1.Tracker.TrackerReportSwarmStatsMessage.copy(block: kernel.protocol.client.model.v1.TrackerReportSwarmStatsMessageKt.Dsl.() -> kotlin.Unit): kernel.protocol.client.model.v1.Tracker.TrackerReportSwarmStatsMessage =
  kernel.protocol.client.model.v1.TrackerReportSwarmStatsMessageKt.Dsl._create(this.toBuilder()).apply { block() }._build()

@kotlin.jvm.JvmSynthetic
public inline fun kernel.protocol.client.model.v1.Tracker.TrackerReportSwarmStatsMessage.MessageContent.copy(block: kernel.protocol.client.model.v1.TrackerReportSwarmStatsMessageKt.MessageContentKt.Dsl.() -> kotlin.Unit): kernel.protocol.client.model.v1.Tracker.TrackerReportSwarmStatsMessage.MessageContent =
  kernel.protocol.client.model.v1.TrackerReportSwarmStatsMessageKt.MessageContentKt.Dsl._create(this.toBuilder()).apply { block() }._build()

public val kernel.protocol.client.model.v1.Tracker.TrackerReportSwarmStatsMessageOrBuilder.headerOrNull: kernel.protocol.client.model.v1.Base.MessageHeader?
  get() = if (hasHeader()) getHeader() else null

public val kernel.protocol.client.model.v1.Tracker.TrackerReportSwarmStatsMessageOrBuilder.contentOrNull: kernel.protocol.client.model.v1.Tracker.TrackerReportSwarmStatsMessage.MessageContent?
  get() = if (hasContent()) getContent() else null

