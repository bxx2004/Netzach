package net.bxx2004.netzach.ui.components.display

import net.bxx2004.netzach.core.attributes.ref
import net.bxx2004.netzach.ui.components.IComponent
import net.bxx2004.netzach.core.attributes.AttributeReader
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.util.FastColor


class Color : IComponent(){
    var r = ref(225)
    var g = ref(0)
    var b = ref(0)
    override fun render(
        context: GuiGraphics,
        mouseX: Int,
        mouseY: Int,
        delta: Float,
        reader: AttributeReader
    ) {
        context.fill(
            reader.ax,
            reader.ay,
            reader.ax + width.getValue(),
            reader.ay + height.getValue(),
            FastColor.ARGB32.color((transparency.getValue() * 255).toInt(),r.getValue(),g.getValue(),b.getValue())
        )
    }
}