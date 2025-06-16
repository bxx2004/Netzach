package net.bxx2004.netzach.ui.components.container

import net.bxx2004.netzach.core.attributes.AttributeReader
import net.bxx2004.netzach.core.attributes.mutable
import net.bxx2004.netzach.core.attributes.ref
import net.bxx2004.netzach.ui.components.IComponent
import net.bxx2004.netzach.ui.full
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.world.level.levelgen.Column
import kotlin.math.max
import kotlin.math.min

/**
 * @author 6hisea
 * @date  2025/6/15 19:09
 * @description: None
 */
open class RowLayout : BaseLayout() {
    var columns = ref(24)
    var gutter = ref(0)
    private var perWidth = mutable {
        (width.v - (gutter.v * (columns.v - 1))) / columns.v
    }

    /**
     * Adds a column component to this row
     * Only Column components are allowed as direct children
     */
    override fun addComponent(component: IComponent) {
        if (component !is ColumnLayout) return
        super.addComponent(component)
    }

    /**
     * Creates and adds a new column with the specified span
     * @param span Number of columns this component should span
     * @param builder Lambda to build the component content
     */
    fun col(span: Int = 24, builder: () -> IComponent) {
        val column = ColumnLayout().apply {
            this.span.v = span
            val a = builder()
            addComponent(a)
            a.full()
        }
        addComponent(column)
    }

    /**
     * Calculates and updates the layout of all child columns
     */
    private fun calculateLayout() {
        var currentX = x.v
        components.forEach { child ->
            if (child is ColumnLayout) {
                val columnSpan = max(1, min(child.span.v, columns.v))
                val width = columnSpan * perWidth.v + max(0, (columnSpan - 1) * gutter.v)

                        child.x.v = currentX
                        child.y.v = y.v
                        child.width.v = width
                        child.height.v = height.v

                        currentX += width + gutter.v
            }
        }
    }

    override fun render(context: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float, reader: AttributeReader) {
        calculateLayout()
        super.render(context, mouseX, mouseY, delta, reader)
    }
}
