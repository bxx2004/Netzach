package net.bxx2004.netzach.ui.packets

import net.bxx2004.netzach.core.utils.rl
import net.bxx2004.netzach.network.Packet
import net.bxx2004.netzach.ui.callback.ComponentActionCallBack
import net.minecraft.resources.ResourceLocation

/**
 * @author 6hisea
 * @date  2025/6/11 18:19
 * @description: None
 */
data class ComponentCallbackPacket<T:ComponentActionCallBack>(
    val uiId: String,
    val data: T
) : Packet() {
    override val id: ResourceLocation
        get() = rl("ui","component_action")
}