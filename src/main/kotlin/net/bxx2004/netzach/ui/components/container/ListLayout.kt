package net.bxx2004.netzach.ui.components.container

import net.bxx2004.netzach.core.attributes.ref
import net.bxx2004.netzach.ui.components.IComponent
import net.bxx2004.netzach.ui.components.display.Clipped
import net.bxx2004.netzach.ui.components.display.ScrollBar
import net.bxx2004.netzach.core.attributes.AttributeReader
import net.bxx2004.netzach.core.attributes.mutable
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.world.inventory.AbstractContainerMenu
import kotlin.math.ceil
import kotlin.math.max


/**
 * @author 6hisea
 * @date  2025/5/1 18:41
 * @description: None
 */
open class ListLayout : Clipped() {
    var gap = ref(4)
    var cell_height = ref(32)


    lateinit var scrollBar: ScrollBar

    override fun onOpen(handler: AbstractContainerMenu?) {
        scrollBar = (components.find { it.id.getValue() == "#scrollbar" }?:ScrollBar()) as ScrollBar
        cache = components.clone() as List<IComponent>
    }
    private var lastScroll = -1
    private var cacheY = 0
    private var cache = listOf<IComponent>()
    override fun render(
        context: GuiGraphics,
        mouseX: Int,
        mouseY: Int,
        delta: Float,
        reader: AttributeReader
    ) {
        if (scrollBar.getValue() != lastScroll) {
            fixed()
            lastScroll = scrollBar.getValue()
        }
        super.render(context, mouseX, mouseY, delta, reader)
    }
    fun fixed(){
        cacheY = 0
        components.clear()
        scrollBar.width.setValue(8)
        scrollBar.height.setValue(this.height.getValue())
        scrollBar.x.setValue(this.width.getValue() - scrollBar.width.getValue())
        scrollBar.y.setValue(0)
        scrollBar.drag.setValue(true)
        scrollBar.virtual_drag.setValue(true)

        scrollBar.window.setValue(
            max(
                ceil(height.getValue() / (cell_height.getValue().toDouble() + gap.getValue())).toInt(),
                1
            )
        )
        scrollBar.max_value.setValue(cache.size)

        val presentCells: Int = (cache.size - scrollBar.getValue()).coerceAtMost(scrollBar.window.getValue())
        cache.filter { it.id.getValue() != "scrollbar" }.subList(scrollBar.getValue(),scrollBar.getValue() + presentCells)
            .map {
                it.x = ref(0)
                it.y = ref(cacheY)
                it.width = mutable { width.getValue()-scrollBar.width.getValue() }
                it.height = mutable { cell_height.getValue() }
                cacheY = it.y.getValue() + gap.getValue() + cell_height.getValue()
                it
            }.forEach {
                addComponent(it)
            }
        addComponent(scrollBar)
    }

    override fun mouseScrolled(
        mouseX: Double,
        mouseY: Double,
        horizontalAmount: Double,
        verticalAmount: Double
    ): Boolean {
        scrollBar.mouseScrolled(0.00, 0.00, horizontalAmount, verticalAmount)
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)
    }
}