package net.bxx2004.netzach.ui.listeners

import net.bxx2004.netzach.Netzach
import net.bxx2004.netzach.core.utils.client
import net.bxx2004.netzach.ui.components.container.hideLayers
import net.bxx2004.netzach.ui.components.container.renderNetzachLayers
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent

/**
 * @author 6hisea
 * @date  2025/6/12 20:28
 * @description: None
 */
@EventBusSubscriber(value = [Dist.CLIENT],modid = Netzach.ID)
object LayerListener {
    @SubscribeEvent
    fun onHudRenderer(e: RenderGuiLayerEvent.Pre){
        if (client().hideLayers().contains(e.name)){
            e.isCanceled = true
        }
        client().renderNetzachLayers(e.guiGraphics)
    }
}