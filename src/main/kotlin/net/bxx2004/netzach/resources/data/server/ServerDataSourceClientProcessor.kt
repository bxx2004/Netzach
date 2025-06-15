package net.bxx2004.netzach.resources.data.server

import net.bxx2004.netzach.network.events.ReceiveServerPacketEvent
import net.bxx2004.netzach.resources.data.DataSourcePacket
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber

/**
 * @author 6hisea
 * @date  2025/6/13 17:42
 * @description: None
 */
@EventBusSubscriber(value = [Dist.CLIENT])
object ServerDataSourceClientProcessor{
    @SubscribeEvent
    fun onReceive(e: ReceiveServerPacketEvent){
        e.packet.except<DataSourcePacket.Response> { data->
            ServerDataSource.allServerDataSource.find {
                it.id == data.id
            }?.responsePool[data.key] = data.data
        }
    }
}