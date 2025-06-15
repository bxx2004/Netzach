package net.bxx2004.netzach.resources.data

import net.bxx2004.netzach.network.Packet
import net.minecraft.resources.ResourceLocation

/**
 * @author 6hisea
 * @date  2025/6/13 17:30
 * @description: None
 */
class DataSourcePacket {
    class Request(override val id: ResourceLocation,val key: String) : Packet()
    class Update(override val id: ResourceLocation,val key: String,val data: Any?) : Packet()
    class Response(override val id: ResourceLocation,val key: String,val data: Any?) : Packet()
}