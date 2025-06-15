package net.bxx2004.netzach.resources.data.server

import net.bxx2004.netzach.network.events.ReceiveClientPacketEvent
import net.bxx2004.netzach.resources.data.DataSourcePacket
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.common.NeoForge.EVENT_BUS

/**
 * @author 6hisea
 * @date  2025/6/13 17:42
 * @description: None
 */
@EventBusSubscriber(value = [Dist.DEDICATED_SERVER])
object ServerDataSourceProcessor{
    @SubscribeEvent
    fun onReceive(e: ReceiveClientPacketEvent){
        e.packet.except<DataSourcePacket.Request> { data ->
            EVENT_BUS.post(RequestDataSourceEvent(data,e.context.player()))
        }
    }
}