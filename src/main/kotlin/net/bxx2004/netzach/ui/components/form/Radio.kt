package net.bxx2004.netzach.ui.components.form

import net.bxx2004.netzach.core.attributes.AttributeReader
import net.bxx2004.netzach.core.attributes.ref
import net.bxx2004.netzach.core.utils.nrl
import net.bxx2004.netzach.ui.components.IComponent
import net.bxx2004.netzach.ui.components.blit
import net.bxx2004.netzach.ui.components.container.SlottedComponent
import net.bxx2004.netzach.ui.components.display.Text
import net.bxx2004.netzach.ui.style.Styles
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component


class Radio : SlottedComponent() {
    var unchecked_texture = ref(nrl("textures/ui/radio/unchecked.png"))
    var checked_texture = ref(nrl("textures/ui/radio/checked.png"))
    var state = ref(false)
    override fun render(
        context: GuiGraphics,
        mouseX: Int,
        mouseY: Int,
        delta: Float,
        reader: AttributeReader
    ) {
        val drawTexture = if (state.getValue()) checked_texture else unchecked_texture
        context.blit(drawTexture.getValue(),
            reader.ax,
            reader.ay,
            reader.z,
            0.0F,
            0.0F,
            height.getValue(),
            height.getValue()
        )
        getSlot("text").render(context,mouseX, mouseY,delta,reader.rectangleWidth,reader.ay)
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (button == 0){
            if (!state.getValue()){
                state.setValue(true)
                container?.findByClass(clazz.getValue())?.forEach{
                    if (it != this && it is Radio){
                        it.state.setValue(false)
                    }
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button)
    }

    override fun default(id: String): IComponent {
        val a = Text()
        a.text.setValue(Component.literal("Radio"))
        a.id.setValue("#text")
        a.addStyle(Styles.ALIGNMENT,"none center")
        return a
    }

    override fun slots(): List<String> {
        return arrayListOf("text")
    }
}