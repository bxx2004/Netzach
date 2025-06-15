package net.bxx2004.netzach.ui.components.container.panel

import net.bxx2004.netzach.core.attributes.AttributeReader
import net.bxx2004.netzach.core.attributes.ref
import net.bxx2004.netzach.ui.components.IComponent
import net.bxx2004.netzach.ui.components.None
import net.bxx2004.netzach.ui.components.container.IContainer
import net.bxx2004.netzach.ui.components.container.SlottedComponent
import net.bxx2004.netzach.ui.utils.Direction
import net.minecraft.client.gui.GuiGraphics

class MarqueePanel : SlottedComponent() {
    // 滚动属性
    var speed = ref(1.0f)
    var direction = ref(Direction.LEFT_TO_RIGHT)
    var padding = ref(4)

    // 状态
    private var scrollPosition = ref(0f)
    private var lastRenderTime = System.currentTimeMillis()
    private var contentWidth = 0
    private var contentHeight = 0

    override fun slots(): List<String> = listOf("content")

    override fun default(id: String): IComponent = when(id) {
        "content" -> None()
        else -> None()
    }

    override fun render(
        context: GuiGraphics,
        mouseX: Int,
        mouseY: Int,
        delta: Float,
        reader: AttributeReader
    ) {
        val currentTime = System.currentTimeMillis()
        val deltaTime = (currentTime - lastRenderTime).coerceAtMost(50L)
        lastRenderTime = currentTime

        // 更新滚动位置
        updateScrollPosition(deltaTime)

        // 启用裁剪
        context.enableScissor(
            reader.cx + padding.getValue(),
            reader.cy + padding.getValue(),
            reader.cx + width.getValue() - padding.getValue(),
            reader.cy + height.getValue() - padding.getValue()
        )

        // 计算内容位置
        val (contentX, contentY) = calculateContentPosition(reader.cx, reader.cy)

        // 绘制内容
        getSlot("content")?.render(
            context,
            mouseX - contentX,
            mouseY - contentY,
            delta,
            contentX,
            contentY
        )

        // 如果需要循环，绘制第二个内容实例
        if (shouldShowSecondInstance()) {
            val (secondX, secondY) = calculateSecondInstancePosition(contentX, contentY)
            getSlot("content")?.render(
                context,
                mouseX - secondX,
                mouseY - secondY,
                delta,
                secondX,
                secondY
            )
        }

        // 禁用裁剪
        context.disableScissor()
    }

    private fun calculateContentPosition(containerX: Int, containerY: Int): Pair<Int, Int> {
        return when (direction.getValue()) {
            Direction.LEFT_TO_RIGHT -> Pair(
                containerX + padding.getValue() - scrollPosition.getValue().toInt(),
                containerY + padding.getValue()
            )
            Direction.RIGHT_TO_LEFT -> Pair(
                containerX + width.getValue() - padding.getValue() - contentWidth + scrollPosition.getValue().toInt(),
                containerY + padding.getValue()
            )
            Direction.TOP_TO_BOTTOM -> Pair(
                containerX + padding.getValue(),
                containerY + padding.getValue() - scrollPosition.getValue().toInt()
            )
            else  -> Pair(
                containerX + padding.getValue(),
                containerY + height.getValue() - padding.getValue() - contentHeight + scrollPosition.getValue().toInt()
            )
        }
    }

    private fun shouldShowSecondInstance(): Boolean {
        return when (direction.getValue()) {
            Direction.LEFT_TO_RIGHT, Direction.RIGHT_TO_LEFT -> contentWidth > width.getValue() - 2 * padding.getValue()
            else -> contentHeight > height.getValue() - 2 * padding.getValue()
        }
    }

    private fun calculateSecondInstancePosition(contentX: Int, contentY: Int): Pair<Int, Int> {
        return when (direction.getValue()) {
            Direction.LEFT_TO_RIGHT -> Pair(contentX + contentWidth + padding.getValue() * 2, contentY)
            Direction.RIGHT_TO_LEFT -> Pair(contentX - contentWidth - padding.getValue() * 2, contentY)
            Direction.TOP_TO_BOTTOM -> Pair(contentX, contentY + height.getValue() + padding.getValue() * 2)
            else -> Pair(contentX, contentY - height.getValue() - padding.getValue() * 2)
        }
    }

    private fun updateScrollPosition(deltaTime: Long) {
        // 获取内容尺寸
        val content = getSlot("content") ?: return
        val currentWidth = content.width.getValue()
        val currentHeight = content.height.getValue()

        if (contentWidth != currentWidth || contentHeight != currentHeight) {
            contentWidth = currentWidth
            contentHeight = currentHeight
            scrollPosition.setValue(
                when (direction.getValue()) {
                    Direction.RIGHT_TO_LEFT -> (width.getValue() - 2 * padding.getValue()).toFloat()
                    Direction.BOTTOM_TO_TOP -> (height.getValue() - 2 * padding.getValue()).toFloat()
                    else -> 0f
                }
            )
        }

        scrollPosition.setValue(scrollPosition.getValue() + speed.getValue() * deltaTime / 16f)

        val maxScroll = when (direction.getValue()) {
            Direction.LEFT_TO_RIGHT, Direction.RIGHT_TO_LEFT -> contentWidth + padding.getValue() * 2
            else -> height.getValue().toFloat() + padding.getValue() * 2
        }

        if (scrollPosition.getValue() > maxScroll.toFloat()) {
            scrollPosition.setValue(scrollPosition.getValue() - maxScroll.toFloat())
        }
    }

    fun restart() {
        scrollPosition.setValue(
            when (direction.getValue()) {
                Direction.RIGHT_TO_LEFT -> (width.getValue() - 2 * padding.getValue()).toFloat()
                Direction.BOTTOM_TO_TOP -> (height.getValue() - 2 * padding.getValue()).toFloat()
                else -> 0f
            }
        )
    }

    fun pause() {
        speed.setValue(0f)
    }

    fun resume() {
        if (speed.getValue() == 0f) {
            speed.setValue(1f)
        }
    }

    override fun hit(mx: Int, my: Int): IComponent {
        if (isInContentArea(mx, my)) {
            val (contentX, contentY) = calculateContentPosition(x.getValue(), y.getValue())
            val relativeX = mx - contentX
            val relativeY = my - contentY

            if (relativeX in 0 until contentWidth && relativeY in 0 until contentHeight) {
                return if (getSlot("content") is IContainer){
                    (getSlot("content") as IContainer).hit(relativeX, relativeY)
                }else{null} ?: this
            }

            if (shouldShowSecondInstance()) {
                val (secondX, secondY) = calculateSecondInstancePosition(contentX, contentY)
                val secondRelativeX = mx - secondX
                val secondRelativeY = my - secondY

                if (secondRelativeX in 0 until contentWidth && secondRelativeY in 0 until contentHeight) {
                    return if (getSlot("content") is IContainer){
                        (getSlot("content") as IContainer).hit(secondRelativeX, secondRelativeY)
                    }else{null} ?: this
                }
            }
        }
        return this
    }

    private fun isInContentArea(mouseX: Int, mouseY: Int): Boolean {
        return mouseX >= x.getValue() + padding.getValue() &&
                mouseX <= x.getValue() + width.getValue() - padding.getValue() &&
                mouseY >= y.getValue() + padding.getValue() &&
                mouseY <= y.getValue() + height.getValue() - padding.getValue()
    }
}