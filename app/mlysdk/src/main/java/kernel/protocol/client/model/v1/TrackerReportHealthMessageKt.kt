//Generated by the protocol buffer compiler. DO NOT EDIT!
// source: kernel/protocol/client/model/v1/message/tracker.proto

package kernel.protocol.client.model.v1;

@kotlin.jvm.JvmName("-initializetrackerReportHealthMessage")
public inline fun trackerReportHealthMessage(block: kernel.protocol.client.model.v1.TrackerReportHealthMessageKt.Dsl.() -> kotlin.Unit): kernel.protocol.client.model.v1.Tracker.TrackerReportHealthMessage =
  kernel.protocol.client.model.v1.TrackerReportHealthMessageKt.Dsl._create(kernel.protocol.client.model.v1.Tracker.TrackerReportHealthMessage.newBuilder()).apply { block() }._build()
public object TrackerReportHealthMessageKt {
  @kotlin.OptIn(com.google.protobuf.kotlin.OnlyForUseByGeneratedProtoCode::class)
  @com.google.protobuf.kotlin.ProtoDslMarker
  public class Dsl private constructor(
    private val _builder: kernel.protocol.client.model.v1.Tracker.TrackerReportHealthMessage.Builder
  ) {
    public companion object {
      @kotlin.jvm.JvmSynthetic
      @kotlin.PublishedApi
      internal fun _create(builder: kernel.protocol.client.model.v1.Tracker.TrackerReportHealthMessage.Builder): Dsl = Dsl(builder)
    }

    @kotlin.jvm.JvmSynthetic
    @kotlin.PublishedApi
    internal fun _build(): kernel.protocol.client.model.v1.Tracker.TrackerReportHealthMessage = _builder.build()

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
  }
}
@kotlin.jvm.JvmSynthetic
public inline fun kernel.protocol.client.model.v1.Tracker.TrackerReportHealthMessage.copy(block: kernel.protocol.client.model.v1.TrackerReportHealthMessageKt.Dsl.() -> kotlin.Unit): kernel.protocol.client.model.v1.Tracker.TrackerReportHealthMessage =
  kernel.protocol.client.model.v1.TrackerReportHealthMessageKt.Dsl._create(this.toBuilder()).apply { block() }._build()

public val kernel.protocol.client.model.v1.Tracker.TrackerReportHealthMessageOrBuilder.headerOrNull: kernel.protocol.client.model.v1.Base.MessageHeader?
  get() = if (hasHeader()) getHeader() else null

