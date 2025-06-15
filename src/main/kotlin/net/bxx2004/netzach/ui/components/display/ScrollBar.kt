package net.bxx2004.netzach.ui.components.display

import net.bxx2004.netzach.core.attributes.ref
import net.bxx2004.netzach.core.utils.nrl
import net.bxx2004.netzach.ui.components.IComponent
import net.bxx2004.netzach.ui.components.blit
import net.bxx2004.netzach.core.attributes.AttributeReader
import net.bxx2004.netzach.ui.utils.Axis
import net.bxx2004.netzach.ui.utils.Direction
import net.bxx2004.netzach.ui.utils.DragUtil
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.world.inventory.AbstractContainerMenu
import kotlin.math.max


/**
 * @author 6hisea
 * @date  2025/5/1 17:06
 * @description: None
 */
class ScrollBar : IComponent() {
    var scrolling_speed = ref(4)

    var axis = ref(Axis.VERTICAL)
    var value = ref(0)
    var max_value = ref(100)
    var window = ref(16)

    var anchor = ref(-1)
    var anchorValue = ref(-1)
    var sliding = ref(false)
    var background = ref(nrl("textures/ui/scroll_bar/background_light.png"))
    var thumb = ref(nrl("textures/ui/scroll_bar/thumb_light.png"))
    var focus = ref(nrl("textures/ui/scroll_bar/hover_tab_background.png"))
    var thumb_hovered = ref(nrl("textures/ui/scroll_bar/thumb_hovered_light.png"))
    var thumb_pressed = ref(nrl("textures/ui/scroll_bar/thumb_pressed_light.png"))


    override fun render(
        context: GuiGraphics,
        mouseX: Int,
        mouseY: Int,
        delta: Float,
        reader: AttributeReader
    ) {
        val matrices = context.pose()

        context.blit(
            background.getValue(),
            reader.ax,
            reader.ay,
            reader.z,
            0.0F,
            0.0F,
            reader.width,
            reader.height
        )

        var thumbTexture = thumb.getValue()

        if (max_value.getValue() <= 0) return

        if (sliding.getValue()) {
            thumbTexture = thumb_pressed.getValue()
        } else if (within(mouseX, mouseY)) {
            thumbTexture = thumb_hovered.getValue()
        }

        matrices.pushPose()

        if (axis.getValue() === Axis.HORIZONTAL) {
            matrices.translate((reader.ax + 1 + getHandlePosition()).toFloat(), (reader.ay + 1).toFloat(), 0f)
            context.blit(
                thumbTexture,
                0,
                0,
                reader.z,
                0.0F,
                0.0F,
                getHandleSize(),
                reader.height - 2
            )

            if (isFocus()) {
                context.blit(
                    focus.getValue(),
                    0,
                    0,
                    reader.z,
                    0.0F,
                    0.0F,
                    getHandleSize(),
                    reader.height - 2
                )
            }
        } else {
            matrices.translate((reader.ax + 1).toFloat(), (reader.ay + 1 + getHandlePosition()).toFloat(), 0f)
            context.blit(
                thumbTexture,
                0,
                0,
                reader.z,
                0.0F,
                0.0F,
                reader.width - 2,
                getHandleSize()
            )

            if (isFocus()) {
                context.blit(
                    focus.getValue(),
                    0,
                    0,
                    reader.z,
                    0.0F,
                    0.0F,
                    reader.width - 2,
                    getHandleSize()
                )
            }
        }
        matrices.popPose()
    }



    /**
     * Gets the on-axis size of the scrollbar handle in gui pixels
     */
    fun getHandleSize(): Int {
        val percentage = if ((window.getValue() >= max_value.getValue())) 1f else window.getValue() / max_value.getValue().toFloat()
        val bar: Int = if ((axis.getValue() === Axis.HORIZONTAL)) width.getValue() - 2 else height.getValue() - 2
        var result = (percentage * bar).toInt()
        if (result < 6) result = 6
        return result
    }

    /**
     * Gets the number of pixels the scrollbar handle is able to move along its track from one end to the other.
     */
    fun getMovableDistance(): Int {
        val bar: Int = if ((axis.getValue() === Axis.HORIZONTAL)) width.getValue() - 2 else height.getValue() - 2
        return bar - getHandleSize()
    }

    fun pixelsToValues(pixels: Int): Int {
        val bar = getMovableDistance()
        val percent = pixels / bar.toFloat()
        return (percent * (max_value.getValue() - window.getValue())).toInt()
    }

    fun getHandlePosition(): Int {
        val percent = value.getValue() / max((max_value.getValue() - window.getValue()).toDouble(), 1.0)
        return (percent * getMovableDistance()).toInt()
    }

    /**
     * Gets the maximum scroll value achievable; this will typically be the maximum value minus the
     * window size
     */
    fun getMaxScrollValue(): Int {
        return max_value.getValue() - window.getValue()
    }

    protected fun adjustSlider(x: Int, y: Int) {
        var delta = 0
        delta = if (axis.getValue() === Axis.HORIZONTAL) {
            x - anchor.getValue()
        } else {
            y - anchor.getValue()
        }

        val valueDelta = pixelsToValues(delta)
        var valueNew = anchorValue.getValue() + valueDelta

        if (valueNew > getMaxScrollValue()) valueNew = getMaxScrollValue()
        if (valueNew < 0) valueNew = 0
        this.value = ref(valueNew)
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        container?.setFocus(this)

        if (axis.getValue() === Axis.HORIZONTAL) {
            anchor.setValue(mouseX.toInt())
            anchorValue.setValue(value.getValue())
        } else {
            anchor.setValue(mouseY.toInt())
            anchorValue.setValue(value.getValue())
        }
        sliding.setValue(true)
        return super.mouseClicked(mouseX, mouseY, button)
    }

    override fun onOpen(handler: AbstractContainerMenu?) {
        super.onOpen(handler)
        this.drag.setValue(true)
        this.virtual_drag.setValue(true)
    }
    override fun mouseDragged(mouseX: Double, mouseY: Double, button: Int, deltaX: Double, deltaY: Double): Boolean {
        adjustSlider(mouseX.toInt(), mouseY.toInt())
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)
    }

    override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int): Boolean {
        anchor = ref(-1)
        anchorValue = ref(-1)
        sliding = ref(false)
        return super.mouseReleased(mouseX, mouseY, button)
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        val direction = if (axis.getValue() === Axis.HORIZONTAL)
            Direction.RIGHT
        else
            Direction.DOWN

        if (DragUtil.isIncreasingKey(keyCode, direction)) {
            if (value.getValue() < getMaxScrollValue()) {
                value.setValue(value.getValue()+1)
            }
        } else if (DragUtil.isDecreasingKey(keyCode, direction)) {
            if (value.getValue() > 0) {
                value.setValue(value.getValue()-1)
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers)
    }

    override fun mouseScrolled(
        mouseX: Double,
        mouseY: Double,
        horizontalAmount: Double,
        verticalAmount: Double
    ): Boolean {
        var v = getValue() + (horizontalAmount - verticalAmount).toInt() * scrolling_speed.getValue()
        if (getMaxScrollValue() - v < scrolling_speed.getValue()) {
            v = getMaxScrollValue()
        }
        if (v < getMaxScrollValue() && v >= 0){
            setValue(v)
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)
    }

    fun getValue(): Int {
        return value.getValue()
    }

    fun setValue(value: Int): ScrollBar {
        this.value = ref(value)
        return this
    }
}