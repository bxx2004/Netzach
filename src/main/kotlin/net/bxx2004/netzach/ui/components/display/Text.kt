package net.bxx2004.netzach.ui.components.display

import net.bxx2004.netzach.core.attributes.ref
import net.bxx2004.netzach.core.utils.client
import net.bxx2004.netzach.ui.components.IComponent
import net.bxx2004.netzach.ui.components.drawScaleText
import net.bxx2004.netzach.core.attributes.AttributeReader
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component


class Text : IComponent(){
    var shadow = ref(false)
    var text = ref("N/A")
    var correct = ref(false)
    override fun render(
        context: GuiGraphics,
        mouseX: Int,
        mouseY: Int,
        delta: Float,
        reader: AttributeReader
    ) {
        val t = text.getValue()
        val wrapLine = client().font.split(Component.literal(t),width.getValue())
        if (correct.getValue() && wrapLine.size > 1){
            height.setValue(client().font.wordWrapHeight(t,width.getValue()))
        }
        if (correct.getValue() && wrapLine.size == 1){
            width.setValue(client().font.width(t))
            height.setValue(client().font.lineHeight)
        }
        var i = 0
        wrapLine.forEach {
            context.drawScaleText(
                it,
                reader.ax,
                reader.ay + i,
                reader.z,
                height.getValue(),
                shadow.getValue()
            )
            i += client().font.lineHeight
        }
    }
}