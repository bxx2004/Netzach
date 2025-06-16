package net.bxx2004.netzach.ui.components.display

import net.bxx2004.netzach.core.attributes.ref
import net.bxx2004.netzach.core.utils.client
import net.bxx2004.netzach.ui.components.IComponent
import net.bxx2004.netzach.ui.components.drawScaleText
import net.bxx2004.netzach.core.attributes.AttributeReader
import net.bxx2004.netzach.core.attributes.mutable
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.util.FormattedCharSequence


class Text : IComponent() {
    var shadow = ref(false)
    var text = ref<Component>(Component.literal("N/A"))
    var correct = ref(true)
    var center = ref(true)
    var color = ref(-1)

    override fun render(
        context: GuiGraphics,
        mouseX: Int,
        mouseY: Int,
        delta: Float,
        reader: AttributeReader
    ) {
        val t = text.getValue()
        // First split by newlines, then word-wrap each line
        val newlineSplit = t.string.split("\n")
        val wrapLines = mutableListOf<FormattedCharSequence>()

        newlineSplit.forEach { line ->
            val lineComponent = Component.literal(line)
            wrapLines.addAll(client().font.split(lineComponent, width.getValue()))
        }

        if (correct.getValue()) {
            if (wrapLines.size > 1) {
                height = mutable { wrapLines.size * client().font.lineHeight }
            } else {
                width = mutable { client().font.width(t) }
                height = mutable { client().font.lineHeight }
            }
        }

        var i = 0
        wrapLines.forEach { line ->
            if (center.v){
                context.drawCenteredString(
                    client().font,
                    line,
                    reader.ax,
                    reader.ay + i,
                    -1
                )
            }else{
                context.drawString(
                    client().font,
                    line,
                    reader.ax,
                    reader.ay + i,
                    color.v,
                    shadow.getValue()
                )
            }
            i += client().font.lineHeight
        }
    }
}