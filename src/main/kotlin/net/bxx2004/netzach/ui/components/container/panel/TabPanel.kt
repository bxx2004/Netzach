package net.bxx2004.netzach.ui.components.container.panel

import net.bxx2004.netzach.core.attributes.AttributeReader
import net.bxx2004.netzach.core.attributes.ref
import net.bxx2004.netzach.core.utils.client
import net.bxx2004.netzach.core.utils.nrl
import net.bxx2004.netzach.ui.callback.MouseClickCallback
import net.bxx2004.netzach.ui.components.IComponent
import net.bxx2004.netzach.ui.components.None
import net.bxx2004.netzach.ui.components.container.SlottedComponent
import net.bxx2004.netzach.ui.components.container.IContainer
import net.bxx2004.netzach.ui.utils.nineSlice
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.world.inventory.AbstractContainerMenu

class TabPanel : SlottedComponent() {
    // Tab properties
    var tabs = ref<List<TabData>>(emptyList())
    var activeTab = ref(0)
    var tabHeight = ref(20)
    var tabSpacing = ref(2)
    var tabPadding = ref(4)
    var tabBorderSize = ref(4) // Border size for nine-slice
    var tabBackground = ref(nrl("textures/ui/tab_panel/tab.png"))
    var tabActiveBackground = ref(nrl("textures/ui/tab_panel/active_tab_background.png"))
    var tabHoverBackground = ref(nrl("textures/ui/tab_panel/hover_tab_background.png"))

    // Panel properties
    override var background = ref(nrl("textures/ui/tab_panel/background.png"))
    var panelBorderSize = ref(4) // Border size for nine-slice
    var contentPadding = ref(4)
    var minTabWidth = ref(60)
    var maxTabWidth = ref(160)

    // Hover state
    private var hoveredTab = ref(-1)
    private var lastMouseX = -1
    private var lastMouseY = -1
    private var animationProgress = MutableList(0) { ref(0f) }
    private var lastRenderTime = System.currentTimeMillis()

    data class TabData(
        val title: Component,
        val content: IComponent
    )

    override fun hit(mx: Int, my: Int): IComponent {
        return if (isInTabArea(mx, my)) {
            getHoveredTab(mx, my)?.let { tabIndex ->
                getSlot("tab_$tabIndex")
            } ?: this
        } else {
            tabs.getValue().getOrNull(activeTab.getValue())?.let { activeTabData ->
                if (activeTabData.content is IContainer) {
                    activeTabData.content.hit(mx, my)
                } else {
                    hitInBox(mx,my)
                }
            } ?: this
        }
    }

    override fun onOpen(handler: AbstractContainerMenu?) {
        super.onOpen(handler)
        updateTabs()
    }

    override fun default(id: String): IComponent {
        return when {
            id.startsWith("tab_") -> createTabComponent(id.removePrefix("tab_").toInt())
            id == "content" -> None()
            else -> None()
        }
    }

    private fun createTabComponent(index: Int): IComponent = object : IComponent() {
        init {
            callback<MouseClickCallback>{
                if (index < tabs.getValue().size) {
                    activeTab.setValue(index)
                }
            }

            callback<MouseClickCallback>{
                lastMouseX = mouseX.toInt()
                lastMouseY = mouseY.toInt()
                val currentlyHovered = getHoveredTab(mouseX.toInt(), mouseY.toInt())
                if (currentlyHovered == index) {
                    if (hoveredTab.getValue() != index) {
                        hoveredTab.setValue(index)
                    }
                } else if (hoveredTab.getValue() == index) {
                    hoveredTab.setValue(-1)
                }
            }

        }

        override fun render(
            context: GuiGraphics,
            mouseX: Int,
            mouseY: Int,
            delta: Float,
            reader: AttributeReader
        ) {
            val tabData = tabs.getValue().getOrNull(index) ?: return
            val isActive = index == activeTab.getValue()
            val isHovered = index == hoveredTab.getValue() && !isActive

            // Calculate tab width
            val textWidth = client().font.width(tabData.title)
            val calculatedWidth = (textWidth + tabPadding.getValue() * 2)
                .coerceIn(minTabWidth.getValue(), maxTabWidth.getValue())

            if (width.getValue() != calculatedWidth) {
                width.setValue(calculatedWidth)
                updateTabs()
            }

            // Render tab background using nine-slice
            val background = when {
                isActive -> tabActiveBackground.getValue()
                else -> tabBackground.getValue()
            }

            // Nine-slice render for tab
            context.nineSlice(
                background,
                reader.x, reader.y,
                calculatedWidth, tabHeight.getValue(),
                tabBorderSize.getValue(),
                16, 16,
                reader.z
            )

            if (isHovered) {
                context.nineSlice(
                    tabHoverBackground.getValue(),
                    reader.x, reader.y,
                    calculatedWidth, tabHeight.getValue(),
                    tabBorderSize.getValue(),
                    16, 16,
                    reader.z
                )
            }

            // Render tab text
            val textX = reader.x + (calculatedWidth - textWidth) / 2
            val textY = reader.y + (tabHeight.getValue() - 8) / 2 // Centered vertically

            context.drawString(
                client().font,
                tabData.title,
                textX, textY,
                if (isActive) 0xFFFFFF else 0xDDDDDD,
                false
            )

            // Animation highlight
            if (animationProgress[index].getValue() > 0) {
                val progress = animationProgress[index].getValue()
                val highlightColor = (0x20 * progress).toInt() shl 24 or 0xFFFFFF
                context.fill(
                    reader.x, reader.y,
                    reader.x + calculatedWidth, reader.y + tabHeight.getValue(),
                    highlightColor
                )
            }
        }
    }.apply {
        height.setValue(tabHeight.getValue())
    }

    override fun slots(): List<String> {
        return listOf("content") + tabs.getValue().indices.map { "tab_$it" }
    }

    override fun render(context: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float, reader: AttributeReader) {
        // Update hover state
        if (mouseX != lastMouseX || mouseY != lastMouseY) {
            hoveredTab.setValue(getHoveredTab(mouseX, mouseY) ?: -1)
            lastMouseX = mouseX
            lastMouseY = mouseY
        }

        updateAnimations()

        // Render tabs
        tabs.getValue().indices.forEach { index ->
            getSlot("tab_$index")?.render(context, mouseX, mouseY, delta, reader.ax, reader.ay)
        }

        // Render content area
        val contentY = reader.y + tabHeight.getValue()
        val contentHeight = reader.height - tabHeight.getValue()

        if (contentHeight > 0) {
            // Render panel background using nine-slice
            context.nineSlice(
                background.getValue(),
                reader.x, contentY,
                reader.width, contentHeight,
                panelBorderSize.getValue(),
                16,16,
                reader.z
            )

            tabs.getValue().getOrNull(activeTab.getValue())?.let { activeTabData ->
                context.enableScissor(
                    reader.x, contentY,
                    reader.x + reader.width, contentY + contentHeight
                )

                activeTabData.content.render(
                    context,
                    mouseX, mouseY,
                    delta,
                    reader.ax + contentPadding.getValue(),
                    reader.ay + tabHeight.getValue() + contentPadding.getValue()
                )

                context.disableScissor()
            }
        }
    }

    private fun updateAnimations() {
        val currentTime = System.currentTimeMillis()
        val deltaTime = (currentTime - lastRenderTime).coerceAtMost(50L)
        lastRenderTime = currentTime

        if (animationProgress.size != tabs.getValue().size) {
            animationProgress = MutableList(tabs.getValue().size) {
                if (it < animationProgress.size) animationProgress[it] else ref(0f)
            }
        }

        tabs.getValue().indices.forEach { index ->
            val target = when {
                index == activeTab.getValue() -> 1f
                index == hoveredTab.getValue() -> 0.5f
                else -> 0f
            }

            val current = animationProgress[index].getValue()
            val direction = if (current < target) 1 else -1
            val step = deltaTime.toFloat() / 150f
            animationProgress[index].setValue((current + direction * step).coerceIn(0f, 1f))
        }
    }

    private fun updateTabs() {
        var xOffset = 0
        tabs.getValue().indices.forEach { index ->
            getSlot("tab_$index")?.apply {
                x.setValue(xOffset)
                y.setValue(0)
                width.setValue(width.getValue().coerceIn(minTabWidth.getValue(), maxTabWidth.getValue()))
                height.setValue(tabHeight.getValue())
            }
            xOffset += (getSlot("tab_$index")?.width?.getValue() ?: minTabWidth.getValue()) + tabSpacing.getValue()
        }

        tabs.getValue().forEachIndexed { index, tabData ->
            tabData.content.apply {
                x.setValue(contentPadding.getValue())
                y.setValue(0)
                width.setValue(this@TabPanel.width.getValue() - contentPadding.getValue() * 2)
                height.setValue(this@TabPanel.height.getValue() - tabHeight.getValue() - contentPadding.getValue() * 2)
            }
        }

        val totalTabsWidth = tabs.getValue().indices.sumOf {
            (getSlot("tab_$it")?.width?.getValue() ?: minTabWidth.getValue()) +
                    if (it < tabs.getValue().size - 1) tabSpacing.getValue() else 0
        }

        if (width.getValue() < totalTabsWidth) {
            width.setValue(totalTabsWidth)
        }
    }

    private fun isInTabArea(mouseX: Int, mouseY: Int): Boolean {
        return mouseX >= x.getValue() &&
                mouseX <= x.getValue() + width.getValue() &&
                mouseY >= y.getValue() &&
                mouseY <= y.getValue() + tabHeight.getValue()
    }

    private fun getHoveredTab(mouseX: Int, mouseY: Int): Int? {
        if (!isInTabArea(mouseX, mouseY)) return null

        var currentX = x.getValue()
        tabs.getValue().indices.forEach { index ->
            val tab = getSlot("tab_$index") ?: return@forEach
            val tabWidth = tab.width.getValue()

            if (mouseX >= currentX && mouseX <= currentX + tabWidth) {
                return index
            }
            currentX += tabWidth + tabSpacing.getValue()
        }
        return null
    }

    fun addTab(title: Component, content: IComponent) {
        tabs.setValue(tabs.getValue() + TabData(title, content))
        updateTabs()
    }

    fun removeTab(index: Int) {
        if (index in tabs.getValue().indices) {
            tabs.setValue(tabs.getValue().toMutableList().apply { removeAt(index) })
            if (activeTab.getValue() >= tabs.getValue().size) {
                activeTab.setValue(tabs.getValue().size - 1)
            }
            updateTabs()
        }
    }
}