//Generated by the protocol buffer compiler. DO NOT EDIT!
// source: kernel/protocol/client/model/v1/message/tracker.proto

package kernel.protocol.client.model.v1;

@kotlin.jvm.JvmName("-initializetrackerJoinSwarmRequest")
public inline fun trackerJoinSwarmRequest(block: kernel.protocol.client.model.v1.TrackerJoinSwarmRequestKt.Dsl.() -> kotlin.Unit): kernel.protocol.client.model.v1.Tracker.TrackerJoinSwarmRequest =
  kernel.protocol.client.model.v1.TrackerJoinSwarmRequestKt.Dsl._create(kernel.protocol.client.model.v1.Tracker.TrackerJoinSwarmRequest.newBuilder()).apply { block() }._build()
public object TrackerJoinSwarmRequestKt {
  @kotlin.OptIn(com.google.protobuf.kotlin.OnlyForUseByGeneratedProtoCode::class)
  @com.google.protobuf.kotlin.ProtoDslMarker
  public class Dsl private constructor(
    private val _builder: kernel.protocol.client.model.v1.Tracker.TrackerJoinSwarmRequest.Builder
  ) {
    public companion object {
      @kotlin.jvm.JvmSynthetic
      @kotlin.PublishedApi
      internal fun _create(builder: kernel.protocol.client.model.v1.Tracker.TrackerJoinSwarmRequest.Builder): Dsl = Dsl(builder)
    }

    @kotlin.jvm.JvmSynthetic
    @kotlin.PublishedApi
    internal fun _build(): kernel.protocol.client.model.v1.Tracker.TrackerJoinSwarmRequest = _builder.build()

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
     * <code>.kernel.protocol.client.model.v1.TrackerJoinSwarmRequest.RequestContent content = 3;</code>
     */
    public var content: kernel.protocol.client.model.v1.Tracker.TrackerJoinSwarmRequest.RequestContent
      @JvmName("getContent")
      get() = _builder.getContent()
      @JvmName("setContent")
      set(value) {
        _builder.setContent(value)
      }
    /**
     * <code>.kernel.protocol.client.model.v1.TrackerJoinSwarmRequest.RequestContent content = 3;</code>
     */
    public fun clearContent() {
      _builder.clearContent()
    }
    /**
     * <code>.kernel.protocol.client.model.v1.TrackerJoinSwarmRequest.RequestContent content = 3;</code>
     * @return Whether the content field is set.
     */
    public fun hasContent(): kotlin.Boolean {
      return _builder.hasContent()
    }
  }
  @kotlin.jvm.JvmName("-initializerequestContent")
  public inline fun requestContent(block: kernel.protocol.client.model.v1.TrackerJoinSwarmRequestKt.RequestContentKt.Dsl.() -> kotlin.Unit): kernel.protocol.client.model.v1.Tracker.TrackerJoinSwarmRequest.RequestContent =
    kernel.protocol.client.model.v1.TrackerJoinSwarmRequestKt.RequestContentKt.Dsl._create(kernel.protocol.client.model.v1.Tracker.TrackerJoinSwarmRequest.RequestContent.newBuilder()).apply { block() }._build()
  public object RequestContentKt {
    @kotlin.OptIn(com.google.protobuf.kotlin.OnlyForUseByGeneratedProtoCode::class)
    @com.google.protobuf.kotlin.ProtoDslMarker
    public class Dsl private constructor(
      private val _builder: kernel.protocol.client.model.v1.Tracker.TrackerJoinSwarmRequest.RequestContent.Builder
    ) {
      public companion object {
        @kotlin.jvm.JvmSynthetic
        @kotlin.PublishedApi
        internal fun _create(builder: kernel.protocol.client.model.v1.Tracker.TrackerJoinSwarmRequest.RequestContent.Builder): Dsl = Dsl(builder)
      }

      @kotlin.jvm.JvmSynthetic
      @kotlin.PublishedApi
      internal fun _build(): kernel.protocol.client.model.v1.Tracker.TrackerJoinSwarmRequest.RequestContent = _builder.build()

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
public inline fun kernel.protocol.client.model.v1.Tracker.TrackerJoinSwarmRequest.copy(block: kernel.protocol.client.model.v1.TrackerJoinSwarmRequestKt.Dsl.() -> kotlin.Unit): kernel.protocol.client.model.v1.Tracker.TrackerJoinSwarmRequest =
  kernel.protocol.client.model.v1.TrackerJoinSwarmRequestKt.Dsl._create(this.toBuilder()).apply { block() }._build()

@kotlin.jvm.JvmSynthetic
public inline fun kernel.protocol.client.model.v1.Tracker.TrackerJoinSwarmRequest.RequestContent.copy(block: kernel.protocol.client.model.v1.TrackerJoinSwarmRequestKt.RequestContentKt.Dsl.() -> kotlin.Unit): kernel.protocol.client.model.v1.Tracker.TrackerJoinSwarmRequest.RequestContent =
  kernel.protocol.client.model.v1.TrackerJoinSwarmRequestKt.RequestContentKt.Dsl._create(this.toBuilder()).apply { block() }._build()

public val kernel.protocol.client.model.v1.Tracker.TrackerJoinSwarmRequestOrBuilder.headerOrNull: kernel.protocol.client.model.v1.Base.RequestHeader?
  get() = if (hasHeader()) getHeader() else null

public val kernel.protocol.client.model.v1.Tracker.TrackerJoinSwarmRequestOrBuilder.contentOrNull: kernel.protocol.client.model.v1.Tracker.TrackerJoinSwarmRequest.RequestContent?
  get() = if (hasContent()) getContent() else null

