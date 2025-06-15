package net.bxx2004.netzach.ui.components.display

import net.bxx2004.netzach.core.attributes.ref
import net.bxx2004.netzach.core.utils.client
import net.bxx2004.netzach.ui.components.IComponent
import net.bxx2004.netzach.ui.components.drawScaleText
import net.bxx2004.netzach.core.attributes.AttributeReader
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component


class OrderedText : IComponent() {
    var shadow = ref(false)
    var text = ref(Component.literal(""))
    var texts = ref(listOf<Component>())
    var correct = ref(true)
    override fun render(
        context: GuiGraphics,
        mouseX: Int,
        mouseY: Int,
        delta: Float,
        reader: AttributeReader
    ) {
        val t = text.getValue()

        if (correct.getValue()){
            width.setValue(client().font.width(t))
            height.setValue(client().font.lineHeight)
        }
        if (texts.getValue().isNotEmpty()){
            var i = 0
            texts.getValue().forEach {
                context.drawScaleText(
                    it,
                    reader.ax,
                    reader.ay + i,
                    z.getValue(),
                    height.getValue(),
                    shadow.getValue()
                )
                i += client().font.lineHeight
            }
            return
        }
        context.drawScaleText(
            t,
            reader.ax,
            reader.ay,
            z.getValue(),
            height.getValue(),
            shadow.getValue()
        )
    }
}