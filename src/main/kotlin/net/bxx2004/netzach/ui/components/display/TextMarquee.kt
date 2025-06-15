package net.bxx2004.netzach.ui.components.display

import net.bxx2004.netzach.core.attributes.AttributeReader
import net.bxx2004.netzach.core.attributes.ref
import net.bxx2004.netzach.core.utils.client
import net.bxx2004.netzach.ui.components.IComponent
import net.bxx2004.netzach.ui.utils.Direction
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component

@Deprecated("instead MarqueePanel")
class TextMarquee : IComponent() {
    // 文本属性
    var text = ref(Component.empty())
    var textColor = ref(0xFFFFFFFF.toInt())
    var speed = ref(1.0f) // 像素/帧
    var direction = ref(Direction.LEFT_TO_RIGHT)

    // 外观属性
    var background = ref(0x00000000) // 默认透明背景
    var padding = ref(0)

    // 状态
    private var scrollPosition = ref(0f)
    private var lastRenderTime = System.currentTimeMillis()
    private var textWidth = 0
    private var textHeight = 8 // 默认字体高度

    override fun render(context: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float, reader: AttributeReader) {
        val currentTime = System.currentTimeMillis()
        val deltaTime = (currentTime - lastRenderTime).coerceAtMost(50L)
        lastRenderTime = currentTime

        // 更新滚动位置
        updateScrollPosition(deltaTime)

        val x = reader.x
        val y = reader.y
        val width = reader.width
        val height = reader.height

        // 绘制背景
        context.fill(
            x, y,
            x + width, y + height,
            background.getValue()
        )

        // 启用裁剪
        context.enableScissor(
            x + padding.getValue(), y + padding.getValue(),
            x + width - padding.getValue(), y + height - padding.getValue()
        )

        // 计算文本位置
        val (textX, textY) = calculateTextPosition(x, y, width, height)

        // 绘制文本
        context.drawString(
            client().font,
            text.getValue(),
            textX,
            textY,
            textColor.getValue(),
            false
        )

        // 如果需要循环，绘制第二个文本实例
        if (shouldShowSecondInstance(width, height)) {
            val (secondTextX, secondTextY) = calculateSecondInstancePosition(textX, textY, width, height)
            context.drawString(
                client().font,
                text.getValue(),
                secondTextX,
                secondTextY,
                textColor.getValue(),
                false
            )
        }

        // 禁用裁剪
        context.disableScissor()
    }

    private fun calculateTextPosition(x: Int, y: Int, width: Int, height: Int): Pair<Int, Int> {
        val availableWidth = width - 2 * padding.getValue()
        val availableHeight = height - 2 * padding.getValue()

        return when (direction.getValue()) {
            Direction.LEFT_TO_RIGHT -> {
                // 从左侧开始向右滚动
                val textX = x + padding.getValue() + scrollPosition.getValue().toInt()
                val textY = y + padding.getValue() + (availableHeight - textHeight) / 2
                Pair(textX, textY)
            }
            Direction.RIGHT_TO_LEFT -> {
                // 从右侧开始向左滚动
                val textX = x + padding.getValue() + availableWidth - textWidth - scrollPosition.getValue().toInt()
                val textY = y + padding.getValue() + (availableHeight - textHeight) / 2
                Pair(textX, textY)
            }
            Direction.TOP_TO_BOTTOM -> {
                // 从顶部开始向下滚动
                val textX = x + padding.getValue() + (availableWidth - textWidth) / 2
                val textY = y + padding.getValue() + scrollPosition.getValue().toInt()
                Pair(textX, textY)
            }
            else -> {
                // 从底部开始向上滚动
                val textX = x + padding.getValue() + (availableWidth - textWidth) / 2
                val textY = y + padding.getValue() + availableHeight - textHeight - scrollPosition.getValue().toInt()
                Pair(textX, textY)
            }
        }
    }

    private fun shouldShowSecondInstance(width: Int, height: Int): Boolean {
        return when (direction.getValue()) {
            Direction.LEFT_TO_RIGHT, Direction.RIGHT_TO_LEFT -> textWidth > width - 2 * padding.getValue()
            else -> textHeight > height - 2 * padding.getValue()
        }
    }

    private fun calculateSecondInstancePosition(textX: Int, textY: Int, width: Int, height: Int): Pair<Int, Int> {
        return when (direction.getValue()) {
            Direction.LEFT_TO_RIGHT -> Pair(textX - (width - 2 * padding.getValue()), textY)
            Direction.RIGHT_TO_LEFT -> Pair(textX + (width - 2 * padding.getValue()), textY)
            Direction.TOP_TO_BOTTOM -> Pair(textX, textY - (height - 2 * padding.getValue()))
            else -> Pair(textX, textY + (height - 2 * padding.getValue()))
        }
    }

    private fun updateScrollPosition(deltaTime: Long) {
        // 计算文本尺寸（如果尚未计算或文本已更改）
        val font = client().font
        val currentTextWidth = font.width(text.getValue())
        textHeight = font.lineHeight

        if (textWidth != currentTextWidth) {
            textWidth = currentTextWidth
            scrollPosition.setValue(0f) // 重置滚动位置
        }

        // 更新滚动位置
        scrollPosition.setValue(scrollPosition.getValue() + speed.getValue() * deltaTime / 16f)

        // 重置位置以实现循环效果
        val maxScroll = when (direction.getValue()) {
            Direction.LEFT_TO_RIGHT, Direction.RIGHT_TO_LEFT -> width.getValue() - 2 * padding.getValue()
            else -> height.getValue() - 2 * padding.getValue()
        }

        if (scrollPosition.getValue() > maxScroll.toFloat()) {
            scrollPosition.setValue(scrollPosition.getValue() - maxScroll.toFloat())
        }
    }

    fun restart() {
        scrollPosition.setValue(0f)
    }

    fun pause() {
        speed.setValue(0f)
    }

    fun resume() {
        if (speed.getValue() == 0f) {
            speed.setValue(1f) // 恢复默认速度
        }
    }
}