package net.bxx2004.netzach.ui.components.display

import net.bxx2004.netzach.core.attributes.ref
import net.bxx2004.netzach.core.utils.nrl
import net.bxx2004.netzach.ui.components.IComponent
import net.bxx2004.netzach.ui.components.blit
import net.bxx2004.netzach.core.attributes.AttributeReader
import net.minecraft.client.gui.GuiGraphics


class Texture : IComponent() {
    var texture = ref(nrl("textures/ui/texture/texture.png"))
    var u = ref(0.0f)
    var v = ref(0.0f)
    var region_width = width
    var region_height = height
    override fun render(
        context: GuiGraphics,
        mouseX: Int,
        mouseY: Int,
        delta: Float,
        reader: AttributeReader
    ) {
        context.blit(
            texture.getValue(),
            reader.ax,
            reader.ay,
            reader.z,
            u.getValue(),
            v.getValue(),
            region_width.getValue(),
            region_height.getValue()
        )
    }
}