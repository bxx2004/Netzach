package net.bxx2004.netzach.ui.components.form

import net.bxx2004.netzach.core.attributes.AttributeReader
import net.bxx2004.netzach.core.attributes.mutable
import net.bxx2004.netzach.core.attributes.ref
import net.bxx2004.netzach.core.utils.nrl
import net.bxx2004.netzach.ui.components.IComponent
import net.bxx2004.netzach.ui.components.blit
import net.bxx2004.netzach.ui.components.container.SlottedComponent
import net.bxx2004.netzach.ui.components.display.Text
import net.bxx2004.netzach.ui.style.Styles
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Button
import net.minecraft.network.chat.Component
import net.minecraft.world.inventory.AbstractContainerMenu

class Button : SlottedComponent(){
    var enable_texture = ref(nrl("textures/ui/button/enable_texture.png"))
    var disable_texture = ref(nrl("textures/ui/button/disable_texture.png"))
    var hover_texture = ref(nrl("textures/ui/button/hover_texture.png"))
    var state = ref(true)

    override fun slots(): List<String> {
        return arrayListOf("content")
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (button == 0 && !state.getValue()){

            return false
        }
        return super.mouseClicked(mouseX, mouseY, button)
    }

    override fun default(id: String): IComponent {
        val t = Text()
        t.id.setValue("#content")
        t.container = this
        t.addStyle(Styles.ALIGNMENT,"center","center")
        t.text.setValue(Component.literal("点击"))
        t.correct.v = true

        return t
    }

    private fun center(){
        getSlot("content").x = mutable { (width.v - getSlot("content").width.v) / 2 }
        getSlot("content").y = mutable { (height.v - getSlot("content").height.v) / 2 }
    }
    override fun render(
        context: GuiGraphics,
        mouseX: Int,
        mouseY: Int,
        delta: Float,
        reader: AttributeReader
    ) {
        center()
        val texture = if (state.getValue()) if (isHover()) hover_texture else enable_texture else disable_texture
        context.blit(texture.getValue(),
            reader.ax,
            reader.ay,
            z.getValue(),
            0.0F,
            0.0F,
            width.getValue(),
            height.getValue()
        )
        getSlot("content").render(context,mouseX, mouseY,delta,reader.ax,reader.ay)
    }
}