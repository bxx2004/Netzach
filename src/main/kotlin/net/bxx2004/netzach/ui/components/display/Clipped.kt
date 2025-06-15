package net.bxx2004.netzach.ui.components.display

import net.bxx2004.netzach.ui.components.container.BaseLayout
import net.bxx2004.netzach.core.attributes.AttributeReader
import net.bxx2004.netzach.ui.utils.Scissors
import net.minecraft.client.gui.GuiGraphics


/**
 * @author 6hisea
 * @date  2025/5/1 18:43
 * @description: None
 */
abstract class Clipped : BaseLayout() {
    override fun render(
        context: GuiGraphics,
        mouseX: Int,
        mouseY: Int,
        delta: Float,
        reader: AttributeReader
    ) {
        Scissors.push(reader.ax, reader.ay, reader.width, reader.height)
        super.render(context, mouseX, mouseY, delta, reader)
        Scissors.pop()
    }
}