//Generated by the protocol buffer compiler. DO NOT EDIT!
// source: kernel/protocol/client/model/v1/message/base.proto

package kernel.protocol.client.model.v1;

@kotlin.jvm.JvmName("-initializerequestPayload")
public inline fun requestPayload(block: kernel.protocol.client.model.v1.RequestPayloadKt.Dsl.() -> kotlin.Unit): kernel.protocol.client.model.v1.Base.RequestPayload =
  kernel.protocol.client.model.v1.RequestPayloadKt.Dsl._create(kernel.protocol.client.model.v1.Base.RequestPayload.newBuilder()).apply { block() }._build()
public object RequestPayloadKt {
  @kotlin.OptIn(com.google.protobuf.kotlin.OnlyForUseByGeneratedProtoCode::class)
  @com.google.protobuf.kotlin.ProtoDslMarker
  public class Dsl private constructor(
    private val _builder: kernel.protocol.client.model.v1.Base.RequestPayload.Builder
  ) {
    public companion object {
      @kotlin.jvm.JvmSynthetic
      @kotlin.PublishedApi
      internal fun _create(builder: kernel.protocol.client.model.v1.Base.RequestPayload.Builder): Dsl = Dsl(builder)
    }

    @kotlin.jvm.JvmSynthetic
    @kotlin.PublishedApi
    internal fun _build(): kernel.protocol.client.model.v1.Base.RequestPayload = _builder.build()

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
  }
}
@kotlin.jvm.JvmSynthetic
public inline fun kernel.protocol.client.model.v1.Base.RequestPayload.copy(block: kernel.protocol.client.model.v1.RequestPayloadKt.Dsl.() -> kotlin.Unit): kernel.protocol.client.model.v1.Base.RequestPayload =
  kernel.protocol.client.model.v1.RequestPayloadKt.Dsl._create(this.toBuilder()).apply { block() }._build()

public val kernel.protocol.client.model.v1.Base.RequestPayloadOrBuilder.headerOrNull: kernel.protocol.client.model.v1.Base.RequestHeader?
  get() = if (hasHeader()) getHeader() else null
