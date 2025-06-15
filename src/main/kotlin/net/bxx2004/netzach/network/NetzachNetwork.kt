package net.bxx2004.netzach.network

import net.bxx2004.netzach.network.events.ReceiveClientPacketEvent
import net.bxx2004.netzach.network.events.ReceiveServerPacketEvent
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.level.ChunkPos
import net.neoforged.neoforge.common.NeoForge.EVENT_BUS
import net.neoforged.neoforge.network.PacketDistributor
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler
import net.neoforged.neoforge.network.handling.IPayloadContext
import net.neoforged.neoforge.network.registration.PayloadRegistrar


/**
 * @author 6hisea
 * @date  2025/6/11 16:38
 * @description: None
 */
object NetzachNetwork {
    fun registerPayloads(register: PayloadRegistrar){
        register.playBidirectional(NetzachPayload.TYPE, NetzachPayload.STREAM_CODEC,
            DirectionalPayloadHandler(
                this::clientHandle,
                this::serverHandle
            ))
    }
    private fun clientHandle(packet: NetzachPayload<*>, context: IPayloadContext){
        EVENT_BUS.post(
            ReceiveClientPacketEvent(packet.read(),context)
        )
    }
    private fun serverHandle(packet: NetzachPayload<*>, context: IPayloadContext){
        EVENT_BUS.post(
            ReceiveServerPacketEvent(packet.read(),context)
        )
    }
    fun sendPacketToPlayer(serverPlayer: ServerPlayer,packet:Packet){
        PacketDistributor.sendToPlayer(serverPlayer, NetzachPayload<Packet>().write(packet))
    }
    fun sendPacketToAllPlayers(packet:Packet){
        PacketDistributor.sendToAllPlayers(NetzachPayload<Packet>().write(packet))
    }
    fun sendPacketToServer(packet:Packet){
        PacketDistributor.sendToServer(NetzachPayload<Packet>().write(packet))
    }
    fun sendPacketToPlayersTrackingChunk(level: ServerLevel,chunk: ChunkPos, packet:Packet){
        PacketDistributor.sendToPlayersTrackingChunk(level,chunk,NetzachPayload<Packet>().write(packet))
    }
}