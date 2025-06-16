package net.bxx2004.netzach.ui.listeners

import net.bxx2004.netzach.Netzach
import net.bxx2004.netzach.core.attributes.mutable
import net.bxx2004.netzach.core.attributes.ref
import net.bxx2004.netzach.core.utils.client
import net.bxx2004.netzach.core.utils.inferType
import net.bxx2004.netzach.core.utils.nrl
import net.bxx2004.netzach.resources.data.client.ClientDataSource
import net.bxx2004.netzach.ui.autoHeight
import net.bxx2004.netzach.ui.autoWidth
import net.bxx2004.netzach.ui.center
import net.bxx2004.netzach.ui.components.UIOptions
import net.bxx2004.netzach.ui.components.container.ListLayout
import net.bxx2004.netzach.ui.components.container.UI
import net.bxx2004.netzach.ui.components.container.hideLayers
import net.bxx2004.netzach.ui.components.container.panel.ConfigPanel
import net.bxx2004.netzach.ui.components.container.renderNetzachLayers
import net.bxx2004.netzach.ui.components.container.with
import net.bxx2004.netzach.ui.components.container.withs
import net.bxx2004.netzach.ui.components.display.Color
import net.bxx2004.netzach.ui.components.display.Text
import net.bxx2004.netzach.ui.components.form.Input
import net.bxx2004.netzach.ui.components.form.TextArea
import net.minecraft.network.chat.Component
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.client.event.ClientChatEvent
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent
import java.io.File

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
    val source = ClientDataSource(nrl("test"),File("C:\\Users\\12232\\Desktop\\分析报告\\test.json"))
    @SubscribeEvent
    fun test(e: ClientChatEvent){
        UI(
            options = UIOptions.build{
                positioner(true)
            }
        ).with {
            ConfigPanel("netzach",source,
                {key->
                    Input().apply {
                        id.v = key
                        text.v = source.get<Any>(key).v.toString()
                    }
                },
                {key, component ->
                    source.update(key,component.text.v.inferType())
                })
        }.open()
    }
}