package net.bxx2004.netzach.network.events

import net.bxx2004.netzach.network.Packet
import net.neoforged.bus.api.Event
import net.neoforged.neoforge.network.handling.IPayloadContext

/**
 * @author 6hisea
 * @date  2025/6/11 17:15
 * @description: None
 */
class ReceiveServerPacketEvent(
    val packet: Packet,
    val context: IPayloadContext
) : Event()