package net.bxx2004.netzach.core.registry.helper

import net.minecraft.client.KeyMapping
import net.neoforged.neoforge.client.event.ClientTickEvent
import net.neoforged.neoforge.common.NeoForge.EVENT_BUS

/**
 * @author 6hisea
 * @date  2025/6/16 13:31
 * @description: None
 */
object KeyBindingHelper{
    fun KeyMapping.onPress(func:()->Unit){
        EVENT_BUS.addListener<ClientTickEvent.Post>{
            while (this.consumeClick()) {
                func()
            }
        }
    }
}