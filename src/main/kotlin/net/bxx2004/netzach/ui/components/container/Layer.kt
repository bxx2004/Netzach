package net.bxx2004.netzach.ui.components.container

import net.bxx2004.netzach.Netzach
import net.bxx2004.netzach.core.utils.client
import net.bxx2004.netzach.core.utils.mouseX
import net.bxx2004.netzach.core.utils.mouseY
import net.bxx2004.netzach.ui.components.IComponent
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.resources.ResourceLocation

/**
 * @author 6hisea
 * @date  2025/6/12 20:41
 * @description: None
 */
class Layer(val key: ResourceLocation) : BaseLayout() {
    val hideLayers = ArrayList<ResourceLocation>()
    fun add(){
        client().addLayer(this)
    }
    fun remove(){
        client().removeLayer(key)
    }
}
private val layers = arrayListOf<Layer>()
fun Minecraft.addLayer(container: Layer) {
    if (layers.map { it.key }.contains(container.key)) {
        Netzach.LOGGER.warn("Layer ${container.key} is already registered!")
        return
    }
    layers.add(container)
}
fun Minecraft.removeLayer(location: ResourceLocation) {
    layers.removeIf { it.key == location }
}
fun Minecraft.hideLayers():List<ResourceLocation> {
    val screen = client().screen
    if (screen != null && screen is UI) {
        return screen.options.hideLayers + layers.map { it.hideLayers }.flatten()
    }
    return layers.map { it.hideLayers }.flatten()
}
fun Minecraft.renderNetzachLayers(context: GuiGraphics) {
    layers.forEach {
        if (!hideLayers().contains(it.key)){
            (it as IComponent).render(context, mouseX, mouseY, client().timer.gameTimeDeltaTicks,0,0)
        }
    }
}