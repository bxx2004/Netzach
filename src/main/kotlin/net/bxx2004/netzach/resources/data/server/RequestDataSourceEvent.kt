package net.bxx2004.netzach.resources.data.server

import net.bxx2004.netzach.network.NetzachNetwork
import net.bxx2004.netzach.resources.data.DataSourcePacket
import net.minecraft.world.entity.player.Player
import net.neoforged.api.distmarker.Dist
import net.neoforged.api.distmarker.OnlyIn
import net.neoforged.bus.api.Event

/**
 * @author 6hisea
 * @date  2025/6/13 17:51
 * @description: None
 */
@OnlyIn(Dist.DEDICATED_SERVER)
class RequestDataSourceEvent(val packet: DataSourcePacket.Request,val player: Player) : Event() {
    fun response(data:Any?){
        NetzachNetwork.sendPacketToPlayer(player.server!!.playerList.getPlayer(player.uuid)!!,DataSourcePacket.Response(packet.id,packet.key,data))
    }
}