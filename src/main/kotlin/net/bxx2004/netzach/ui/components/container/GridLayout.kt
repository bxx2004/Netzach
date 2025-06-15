package net.bxx2004.netzach.ui.components.container

import net.bxx2004.netzach.core.attributes.ref
import net.bxx2004.netzach.core.utils.isEmpty
import net.bxx2004.netzach.ui.components.IComponent
import net.bxx2004.netzach.ui.components.None
import net.bxx2004.netzach.ui.components.blit
import net.bxx2004.netzach.core.attributes.AttributeReader
import net.minecraft.client.gui.GuiGraphics

/**
 * @author 6hisea
 * @date  2025/2/1 10:22
 * @description: None
 */
class GridLayout : SlottedComponent() {
    var column = ref(2)
    val forceWidth = ref(false)

    private fun perWidth():Int{
        return width.getValue() / column.getValue()
    }

    override fun default(id: String): IComponent {
        val none = None()
        none.id.setValue("#$id")
        return none
    }

    override fun slots(): List<String> {
        val r = ArrayList<String>()
        for (i in 1..column.getValue()) {
            r.add("$r")
        }
        return r
    }

    override fun render(
        context: GuiGraphics,
        mouseX: Int,
        mouseY: Int,
        delta: Float,
        reader: AttributeReader
    ) {
        if (!background.getValue().isEmpty()){
            context.blit(background.getValue(),reader.ax,reader.ay,reader.z,0.0f,0.0f,reader.width,reader.height)
        }
        slots().forEachIndexed { index, s ->
            val component = getSlot(s)
            component.container = this
            if (forceWidth.getValue()){
                component.width = ref(perWidth())
            }
            component.x = ref(perWidth() * index)
            component.y = ref(0)
            component.render(context,mouseX,mouseY,delta,reader.ax,reader.ay)
        }
    }
}