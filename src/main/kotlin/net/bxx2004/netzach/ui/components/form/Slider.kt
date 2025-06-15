package net.bxx2004.netzach.ui.components.form

import net.bxx2004.netzach.core.attributes.AttributeReader
import net.bxx2004.netzach.core.attributes.ref
import net.bxx2004.netzach.core.utils.nrl
import net.bxx2004.netzach.ui.components.IComponent
import net.bxx2004.netzach.ui.components.blit
import net.bxx2004.netzach.ui.utils.Axis
import net.bxx2004.netzach.ui.utils.Direction
import net.bxx2004.netzach.ui.utils.DragUtil
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.world.inventory.AbstractContainerMenu
import kotlin.math.roundToInt

/**
 * A slider component that allows selecting a value within a range
 */
class Slider : IComponent() {
    var axis = ref(Axis.HORIZONTAL) // Sliders are typically horizontal but can be vertical
    var value = ref(0.0f) // Current value (float for smoother sliding)
    var min_value = ref(0.0f)
    var max_value = ref(100.0f)
    var step = ref(1.0f) // Step size (0 for continuous)

    var anchor = ref(-1)
    var anchor_value = ref(0.0f)
    var sliding = ref(false)

    var background = ref(nrl("textures/ui/slider/background.png"))
    var thumb = ref(nrl("textures/ui/slider/thumb.png"))
    var focus = ref(nrl("textures/ui/slider/hover_tab_background.png"))
    var thumb_hovered = ref(nrl("textures/ui/slider/thumb_hovered.png"))
    var thumb_pressed = ref(nrl("textures/ui/slider/thumb_pressed.png"))
    var filled = ref(nrl("textures/ui/slider/filled.png")) // For showing filled portion

    override fun render(
        context: GuiGraphics,
        mouseX: Int,
        mouseY: Int,
        delta: Float,
        reader: AttributeReader
    ) {
        val matrices = context.pose()

        // Draw background
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

        // Draw filled portion (if applicable)
        if (axis.getValue() === Axis.HORIZONTAL) {
            val filledWidth = ((value.getValue() - min_value.getValue()) / (max_value.getValue() - min_value.getValue())) * (reader.width - 2)
            context.blit(
                filled.getValue(),
                reader.ax + 1,
                reader.ay + 1,
                reader.z,
                0.0F,
                0.0F,
                filledWidth.toInt(),
                reader.height - 2
            )
        } else {
            val filledHeight = ((value.getValue() - min_value.getValue()) / (max_value.getValue() - min_value.getValue())) * (reader.height - 2)
            context.blit(
                filled.getValue(),
                reader.ax + 1,
                (reader.ay + reader.height - 1 - filledHeight).toInt(),
                reader.z,
                0.0F,
                0.0F,
                reader.width - 2,
                filledHeight.toInt()
            )
        }

        var thumbTexture = thumb.getValue()

        if (sliding.getValue()) {
            thumbTexture = thumb_pressed.getValue()
        } else if (within(mouseX, mouseY)) {
            thumbTexture = thumb_hovered.getValue()
        }

        matrices.pushPose()

        if (axis.getValue() === Axis.HORIZONTAL) {
            val thumbPos = getThumbPosition(reader.width)
            matrices.translate((reader.ax + thumbPos).toFloat(), (reader.ay + 1).toFloat(), 0f)
            context.blit(
                thumbTexture,
                0,
                0,
                reader.z,
                0.0F,
                0.0F,
                getThumbSize(),
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
                    getThumbSize(),
                    reader.height - 2
                )
            }
        } else {
            val thumbPos = getThumbPosition(reader.height)
            matrices.translate((reader.ax + 1).toFloat(), (reader.ay + thumbPos).toFloat(), 0f)
            context.blit(
                thumbTexture,
                0,
                0,
                reader.z,
                0.0F,
                0.0F,
                reader.width - 2,
                getThumbSize()
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
                    getThumbSize()
                )
            }
        }
        matrices.popPose()
    }

    /**
     * Gets the size of the slider thumb
     */
    private fun getThumbSize(): Int {
        return if (axis.getValue() === Axis.HORIZONTAL) {
            height.getValue() - 2
        } else {
            width.getValue() - 2
        }
    }

    /**
     * Gets the position of the thumb along the slider track
     */
    private fun getThumbPosition(trackLength: Int): Int {
        val normalizedValue = (value.getValue() - min_value.getValue()) / (max_value.getValue() - min_value.getValue())
        val thumbSize = getThumbSize()
        val availableSpace = trackLength - thumbSize
        return (normalizedValue * availableSpace).toInt()
    }

    /**
     * Adjust slider value based on mouse position
     */
    private fun adjustSlider(x: Int, y: Int, trackLength: Int) {
        val pos = if (axis.getValue() === Axis.HORIZONTAL) x -absoluteX() else y - absoluteY()
        val thumbSize = getThumbSize()
        val availableSpace = trackLength - thumbSize

        val normalizedPos = (pos - thumbSize / 2).coerceIn(0, availableSpace).toFloat() / availableSpace
        var newValue = min_value.getValue() + normalizedPos * (max_value.getValue() - min_value.getValue())

        // Apply step if needed
        if (step.getValue() > 0) {
            newValue = (newValue / step.getValue()).roundToInt() * step.getValue()
        }

        value.setValue(newValue.coerceIn(min_value.getValue(), max_value.getValue()))
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        container?.setFocus(this)

        sliding.setValue(true)
        anchor.setValue(if (axis.getValue() === Axis.HORIZONTAL) mouseX.toInt() else mouseY.toInt())
        anchor_value.setValue(value.getValue())

        // Adjust value immediately on click (not just drag)
        val trackLength = if (axis.getValue() === Axis.HORIZONTAL) width.getValue() else height.getValue()
        adjustSlider(mouseX.toInt(), mouseY.toInt(), trackLength)

        return super.mouseClicked(mouseX, mouseY, button)
    }

    override fun mouseDragged(mouseX: Double, mouseY: Double, button: Int, deltaX: Double, deltaY: Double): Boolean {
        if (sliding.getValue()) {
            val trackLength = if (axis.getValue() === Axis.HORIZONTAL) width.getValue() else height.getValue()
            adjustSlider(mouseX.toInt(), mouseY.toInt(), trackLength)
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)
    }

    override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int): Boolean {
        sliding.setValue(false)
        return super.mouseReleased(mouseX, mouseY, button)
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        val direction = if (axis.getValue() === Axis.HORIZONTAL)
            Direction.RIGHT
        else
            Direction.DOWN

        var stepChange = step.getValue()
        if (stepChange == 0f) {
            // If no step, use 1/10th of the range
            stepChange = (max_value.getValue() - min_value.getValue()) / 10f
        }

        if (DragUtil.isIncreasingKey(keyCode, direction)) {
            setValue((value.getValue() + stepChange).coerceAtMost(max_value.getValue()))
        } else if (DragUtil.isDecreasingKey(keyCode, direction)) {
            setValue((value.getValue() - stepChange).coerceAtLeast(min_value.getValue()))
        }
        return super.keyPressed(keyCode, scanCode, modifiers)
    }

    fun getValue(): Float {
        return value.getValue()
    }

    fun setValue(newValue: Float): Slider {
        val steppedValue = if (step.getValue() > 0) {
            (newValue / step.getValue()).roundToInt() * step.getValue()
        } else {
            newValue
        }
        value.setValue(steppedValue.coerceIn(min_value.getValue(), max_value.getValue()))
        return this
    }

    override fun onOpen(handler: AbstractContainerMenu?) {
        super.onOpen(handler)
        this.drag.setValue(true)
        this.virtual_drag.setValue(true)
    }
}