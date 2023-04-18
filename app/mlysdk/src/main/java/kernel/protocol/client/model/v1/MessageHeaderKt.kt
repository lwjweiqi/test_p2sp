//Generated by the protocol buffer compiler. DO NOT EDIT!
// source: kernel/protocol/client/model/v1/message/base.proto

package kernel.protocol.client.model.v1;

@kotlin.jvm.JvmName("-initializemessageHeader")
public inline fun messageHeader(block: kernel.protocol.client.model.v1.MessageHeaderKt.Dsl.() -> kotlin.Unit): kernel.protocol.client.model.v1.Base.MessageHeader =
  kernel.protocol.client.model.v1.MessageHeaderKt.Dsl._create(kernel.protocol.client.model.v1.Base.MessageHeader.newBuilder()).apply { block() }._build()
public object MessageHeaderKt {
  @kotlin.OptIn(com.google.protobuf.kotlin.OnlyForUseByGeneratedProtoCode::class)
  @com.google.protobuf.kotlin.ProtoDslMarker
  public class Dsl private constructor(
    private val _builder: kernel.protocol.client.model.v1.Base.MessageHeader.Builder
  ) {
    public companion object {
      @kotlin.jvm.JvmSynthetic
      @kotlin.PublishedApi
      internal fun _create(builder: kernel.protocol.client.model.v1.Base.MessageHeader.Builder): Dsl = Dsl(builder)
    }

    @kotlin.jvm.JvmSynthetic
    @kotlin.PublishedApi
    internal fun _build(): kernel.protocol.client.model.v1.Base.MessageHeader = _builder.build()

    /**
     * <code>optional string correlation_id = 1;</code>
     */
    public var correlationId: kotlin.String
      @JvmName("getCorrelationId")
      get() = _builder.getCorrelationId()
      @JvmName("setCorrelationId")
      set(value) {
        _builder.setCorrelationId(value)
      }
    /**
     * <code>optional string correlation_id = 1;</code>
     */
    public fun clearCorrelationId() {
      _builder.clearCorrelationId()
    }
    /**
     * <code>optional string correlation_id = 1;</code>
     * @return Whether the correlationId field is set.
     */
    public fun hasCorrelationId(): kotlin.Boolean {
      return _builder.hasCorrelationId()
    }

    /**
     * <code>optional string source_peer_id = 6;</code>
     */
    public var sourcePeerId: kotlin.String
      @JvmName("getSourcePeerId")
      get() = _builder.getSourcePeerId()
      @JvmName("setSourcePeerId")
      set(value) {
        _builder.setSourcePeerId(value)
      }
    /**
     * <code>optional string source_peer_id = 6;</code>
     */
    public fun clearSourcePeerId() {
      _builder.clearSourcePeerId()
    }
    /**
     * <code>optional string source_peer_id = 6;</code>
     * @return Whether the sourcePeerId field is set.
     */
    public fun hasSourcePeerId(): kotlin.Boolean {
      return _builder.hasSourcePeerId()
    }
  }
}
@kotlin.jvm.JvmSynthetic
public inline fun kernel.protocol.client.model.v1.Base.MessageHeader.copy(block: kernel.protocol.client.model.v1.MessageHeaderKt.Dsl.() -> kotlin.Unit): kernel.protocol.client.model.v1.Base.MessageHeader =
  kernel.protocol.client.model.v1.MessageHeaderKt.Dsl._create(this.toBuilder()).apply { block() }._build()

