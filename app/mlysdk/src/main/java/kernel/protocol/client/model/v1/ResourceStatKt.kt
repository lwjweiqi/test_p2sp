//Generated by the protocol buffer compiler. DO NOT EDIT!
// source: kernel/protocol/client/model/v1/base/resource.proto

package kernel.protocol.client.model.v1;

@kotlin.jvm.JvmName("-initializeresourceStat")
public inline fun resourceStat(block: kernel.protocol.client.model.v1.ResourceStatKt.Dsl.() -> kotlin.Unit): kernel.protocol.client.model.v1.Resource.ResourceStat =
  kernel.protocol.client.model.v1.ResourceStatKt.Dsl._create(kernel.protocol.client.model.v1.Resource.ResourceStat.newBuilder()).apply { block() }._build()
public object ResourceStatKt {
  @kotlin.OptIn(com.google.protobuf.kotlin.OnlyForUseByGeneratedProtoCode::class)
  @com.google.protobuf.kotlin.ProtoDslMarker
  public class Dsl private constructor(
    private val _builder: kernel.protocol.client.model.v1.Resource.ResourceStat.Builder
  ) {
    public companion object {
      @kotlin.jvm.JvmSynthetic
      @kotlin.PublishedApi
      internal fun _create(builder: kernel.protocol.client.model.v1.Resource.ResourceStat.Builder): Dsl = Dsl(builder)
    }

    @kotlin.jvm.JvmSynthetic
    @kotlin.PublishedApi
    internal fun _build(): kernel.protocol.client.model.v1.Resource.ResourceStat = _builder.build()

    /**
     * <code>string id = 1;</code>
     */
    public var id: kotlin.String
      @JvmName("getId")
      get() = _builder.getId()
      @JvmName("setId")
      set(value) {
        _builder.setId(value)
      }
    /**
     * <code>string id = 1;</code>
     */
    public fun clearId() {
      _builder.clearId()
    }

    /**
     * <code>int64 completion = 2;</code>
     */
    public var completion: kotlin.Long
      @JvmName("getCompletion")
      get() = _builder.getCompletion()
      @JvmName("setCompletion")
      set(value) {
        _builder.setCompletion(value)
      }
    /**
     * <code>int64 completion = 2;</code>
     */
    public fun clearCompletion() {
      _builder.clearCompletion()
    }
  }
}
@kotlin.jvm.JvmSynthetic
public inline fun kernel.protocol.client.model.v1.Resource.ResourceStat.copy(block: kernel.protocol.client.model.v1.ResourceStatKt.Dsl.() -> kotlin.Unit): kernel.protocol.client.model.v1.Resource.ResourceStat =
  kernel.protocol.client.model.v1.ResourceStatKt.Dsl._create(this.toBuilder()).apply { block() }._build()

