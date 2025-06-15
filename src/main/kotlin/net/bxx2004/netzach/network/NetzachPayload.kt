package net.bxx2004.netzach.network

import com.alibaba.fastjson2.JSON
import com.alibaba.fastjson2.JSONObject
import com.alibaba.fastjson2.JSONReader
import com.alibaba.fastjson2.JSONWriter
import net.bxx2004.netzach.Netzach
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceLocation


/**
 * @author 6hisea
 * @date  2025/6/11 16:40
 * @description: None
 */
class NetzachPayload<T: Packet> : CustomPacketPayload{
    var data: ByteArray? = null
    fun write(d: T): NetzachPayload<T> {
        this.data = JSON.toJSONBytes(d, JSONWriter.Feature.WriteClassName)
        return this
    }
    fun read(): T{
        return JSONObject.parseObject(data!!.decodeToString(), Packet::class.java, JSONReader.Feature.SupportAutoType) as T
    }

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload?> {
        return TYPE
    }
    companion object{
        val TYPE = CustomPacketPayload.Type<NetzachPayload<*>>(
            ResourceLocation.fromNamespaceAndPath(
                Netzach.ID,
                "packet"
            )
        )
        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, NetzachPayload<*>> = StreamCodec.composite(
            ByteBufCodecs.BYTE_ARRAY,
            NetzachPayload<*>::data
        ){
            val packet = NetzachPayload<Packet>()
            packet.data = it
            packet
        }
    }
}
