package net.bxx2004.netzach.ui.components.display

import net.bxx2004.netzach.core.attributes.ref
import net.bxx2004.netzach.core.utils.nrl
import net.bxx2004.netzach.ui.components.IComponent
import net.bxx2004.netzach.ui.components.blit
import net.bxx2004.netzach.core.attributes.AttributeReader
import net.bxx2004.netzach.ui.utils.Direction
import net.minecraft.client.gui.GuiGraphics


/**
 * @author 6hisea
 * @date  2025/5/2 14:53
 * @description: None
 */
class Bar : IComponent(){
    var background = ref(nrl("textures/ui/bar/background.png"))
    var bar = ref(nrl("textures/ui/bar/bar.png"))
    var max_value = ref(100.0F)
    var insets = ref(arrayListOf(0,0,0,0))
    var direction = ref(Direction.RIGHT)
    var value = ref(0.0F)

    override fun render(
        context: GuiGraphics,
        mouseX: Int,
        mouseY: Int,
        delta: Float,
        reader: AttributeReader
    ) {
        val maxValue = max_value.getValue()
        val value = value.getValue()
        context.blit(background.getValue(),
            reader.ax,
            reader.ay,
            reader.z,
            0.0f,
            0.0f,
            reader.width,
            reader.height
        )
        var percent = value / maxValue.toFloat()
        if (percent < 0) percent = 0f
        if (percent > 1) percent = 1f
        var barMax: Int = reader.width
        if (direction.getValue() == Direction.DOWN || direction.getValue() == Direction.UP) {
            barMax = reader.height
        }
        percent = ((percent * barMax).toInt()) / barMax.toFloat()
        val barSize = (barMax * percent).toInt()
        if (barSize <= 0) return
        val inset = insets.getValue()
        when (direction.getValue()) {
            Direction.UP -> {
                val left = reader.ax
                var top: Int = reader.ay + reader.height
                top -= barSize
                context.blit(bar.getValue(),
                    left + inset[0],
                    top + inset[1],
                    reader.z,
                    0.0f,
                    0.0f,
                    reader.width + inset[2],
                    barSize+ inset[3]
                )
            }

            Direction.RIGHT -> {
                context.blit(bar.getValue(),
                    reader.ax + inset[0],
                    reader.ay + inset[1],
                    reader.z,
                    0.0f,
                    0.0f,
                    barSize + inset[2],
                    reader.height+ inset[3]
                )
            }

            Direction.DOWN -> {
                context.blit(bar.getValue(),
                    reader.ax+ inset[0],
                    reader.ay+ inset[1],
                    reader.z,
                    0.0f,
                    0.0f,
                    reader.width + inset[2],
                    barSize + inset[3]
                )
            }

            Direction.LEFT -> {
                var left: Int = reader.ax + reader.width
                val top = reader.ay
                left -= barSize
                context.blit(bar.getValue(),
                    left+ inset[0],
                    top+ inset[1],
                    reader.z,
                    0.0f,
                    0.0f,
                    barSize+ inset[2],
                    reader.height+ inset[3]
                )
            }
        }
    }
}