package net.bxx2004.netzach.ui.components.display

import net.bxx2004.netzach.core.attributes.Attribute
import net.bxx2004.netzach.core.attributes.AttributeReader
import net.bxx2004.netzach.core.attributes.complex
import net.bxx2004.netzach.core.attributes.ref
import net.bxx2004.netzach.core.utils.client
import net.bxx2004.netzach.core.utils.nrl
import net.bxx2004.netzach.ui.callback.MouseClickCallback
import net.bxx2004.netzach.ui.components.IComponent
import net.bxx2004.netzach.ui.components.None
import net.bxx2004.netzach.ui.components.blit
import net.bxx2004.netzach.ui.components.container.SlottedComponent
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.inventory.AbstractContainerMenu

class Collapse : SlottedComponent() {
    var expanded = ref(false) // 默认展开状态
    var headerTexture = ref(nrl("textures/ui/collapse/header.png"))
    var expandIcon = ref(nrl("textures/ui/collapse/expand.png"))
    var collapseIcon = ref(nrl("textures/ui/collapse/collapse.png"))
    override val background: Attribute<ResourceLocation> = ref(nrl("textures/ui/collapse/content_bg.png"))
    var title = ref(Component.literal("Collapse Panel"))
    var headerHeight = ref(client().font.lineHeight + 12) // 增加标题栏高度
    var iconSize = ref(12)
    var contentPadding = ref(8) // 增加内容内边距
    var minWidth = ref(120) // 最小宽度

    // 动画相关
    private var animationProgress = ref(1f) // 默认完全展开
    private var lastRenderTime = System.currentTimeMillis()

    override fun hit(mx: Int, my: Int): IComponent {
        return if (expanded.getValue()) {
            hitInBox(mx, my)
        } else {
            if (isHeaderHovered(mx, my)) {
                getSlot("header")
            } else {
                this
            }
        }
    }

    override fun onOpen(handler: AbstractContainerMenu?) {
        super.onOpen(handler)
        updateLayout()
    }

    override fun default(id: String): IComponent {
        return when (id) {
            "header" -> createHeaderComponent()
            else -> None()
        }
    }

    private fun createHeaderComponent(): IComponent = object : IComponent() {
        init {
            callback<MouseClickCallback> {
                expanded.setValue(!expanded.getValue())
            }
        }

        override fun render(
            context: GuiGraphics,
            mouseX: Int,
            mouseY: Int,
            delta: Float,
            reader: AttributeReader
        ) {
            // 计算标题所需宽度
            val textWidth = client().font.width(title.getValue())
            val requiredWidth = (iconSize.getValue() + 8 + textWidth + 8).coerceAtLeast(minWidth.getValue())

            // 更新组件宽度
            if (this@Collapse.width.getValue() != requiredWidth) {
                this@Collapse.width.setValue(requiredWidth)
                updateLayout()
            }

            // 渲染标题栏背景（自动拉伸）
            context.blit(
                headerTexture.getValue(),
                reader.x, reader.y, reader.z,
                0f, 0f,
                requiredWidth, headerHeight.getValue(),
                requiredWidth, headerHeight.getValue()
            )

            // 渲染图标
            val icon = if (expanded.getValue()) collapseIcon.getValue() else expandIcon.getValue()
            val iconX = reader.x + 8 // 增加左边距
            val iconY = reader.y + (headerHeight.getValue() - iconSize.getValue()) / 2

            context.blit(
                icon,
                iconX, iconY, reader.z,
                0f, 0f,
                iconSize.getValue(), iconSize.getValue(),
                iconSize.getValue(), iconSize.getValue()
            )

            // 渲染完整标题文本（不截断）
            val textX = iconX + iconSize.getValue() + 8 // 增加图标与文本间距
            val textY = reader.y + (headerHeight.getValue() - client().font.lineHeight) / 2

            context.drawString(
                client().font,
                title.getValue(),
                textX, textY,
                0xFFFFFF,
                false
            )
        }
    }.apply {
        height.setValue(headerHeight.getValue())
    }

    override fun slots(): List<String> {
        return listOf("header", "content")
    }

    override fun render(context: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float, reader: AttributeReader) {
        // 更新动画
        updateAnimation()

        // 渲染标题栏
        getSlot("header").render(context, mouseX, mouseY, delta, reader.x, reader.y)

        // 渲染内容区域（确保始终正确渲染）
        val contentHeight = if (expanded.getValue()) {
            getSlot("content").height.getValue() + getSlot("content").absoluteY() - headerHeight.v
        } else {
            ((getSlot("content").height.getValue()+ getSlot("content").absoluteY() - headerHeight.v) * animationProgress.getValue()).toInt()
        }

        if (contentHeight > 0) {
            // 渲染内容背景（带圆角或渐变效果）
            context.blit(
                background.getValue(),
                reader.x,
                reader.y + headerHeight.getValue(),
                reader.z,
                0f, 0f,
                reader.width, contentHeight
            )
            context.enableScissor(
                reader.x,
                reader.y + headerHeight.getValue(),
                reader.x + reader.width,
                reader.y + headerHeight.getValue() + contentHeight
            )
            getSlot("content").render(
                context,
                mouseX,
                mouseY,
                delta,
                reader.ax,
                reader.ay
            )
            context.disableScissor()
        }

        // 更新组件总高度
        height.setValue(headerHeight.getValue() + contentHeight *2)
    }

    private fun updateAnimation() {
        val currentTime = System.currentTimeMillis()
        val deltaTime = (currentTime - lastRenderTime).coerceAtMost(50L)
        lastRenderTime = currentTime

        val target = if (expanded.getValue()) 1f else 0f
        val direction = if (animationProgress.getValue() < target) 1 else -1
        val step = deltaTime.toFloat() / 200f // 200ms动画时间

        animationProgress.setValue(
            (animationProgress.getValue() + direction * step).coerceIn(0f, 1f)
        )
    }

    private fun updateLayout() {
        // 更新标题栏布局
        getSlot("header").apply {
            width.setValue(this@Collapse.width.getValue())
            height.setValue(headerHeight.getValue())
        }

        // 更新内容区域布局（确保正确计算）
        getSlot("content").apply {
            x.setValue(contentPadding.getValue())
            y.setValue(headerHeight.getValue() + contentPadding.getValue())
            width.setValue(this@Collapse.width.getValue() - contentPadding.getValue() * 2)

            // 如果内容高度未设置，则自动计算
            if (height.getValue() <= 0) {
                height.setValue(calculateDefaultContentHeight())
            }
        }
    }

    private fun calculateDefaultContentHeight(): Int {
        // 这里可以添加更复杂的内容高度计算逻辑
        return 150 // 默认内容高度
    }

    private fun isHeaderHovered(mouseX: Int, mouseY: Int): Boolean {
        return mouseX >= x.getValue() &&
                mouseX <= x.getValue() + width.getValue() &&
                mouseY >= y.getValue() &&
                mouseY <= y.getValue() + headerHeight.getValue()
    }
}