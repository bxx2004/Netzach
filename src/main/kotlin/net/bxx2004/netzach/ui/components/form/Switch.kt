package net.bxx2004.netzach.ui.components.form

import net.bxx2004.netzach.core.attributes.AttributeReader
import net.bxx2004.netzach.core.attributes.ref
import net.bxx2004.netzach.core.attributes.sequence.IntSequence
import net.bxx2004.netzach.core.utils.nrl
import net.bxx2004.netzach.ui.callback.StateChangedCallback
import net.bxx2004.netzach.ui.components.IComponent
import net.bxx2004.netzach.ui.components.None
import net.bxx2004.netzach.ui.components.blit
import net.bxx2004.netzach.ui.components.container.SlottedComponent
import net.minecraft.client.gui.GuiGraphics


class Switch : SlottedComponent() {

    var icon = ref(nrl("textures/ui/switch/icon.png"))
    var state = ref(false)

    var checked_background = ref(nrl("textures/ui/switch/checked_background.png"))
    var unchecked_background = ref(nrl("textures/ui/switch/unchecked_background.png"))

    var icon_x = ref(0)

    override fun render(
        context: GuiGraphics,
        mouseX: Int,
        mouseY: Int,
        delta: Float,
        reader: AttributeReader
    ) {
        val background = if (state.getValue()) checked_background else unchecked_background
        val iconWidth = (width.getValue() * 0.45).toInt()
        context.blit(background.getValue(),
            reader.ax,
            reader.ay,
            reader.z,
            0.0f,
            0.0f,
            reader.width,
            reader.height
        )
        context.blit(icon.getValue(),
            icon_x.getValue() + reader.x,
            reader.ay,
            reader.y,
            0.0f,
            0.0f,
            iconWidth,
            reader.height
        )
        if (state.getValue()){
            getSlot("checked").render(
                context,
                mouseX,
                mouseY,
                delta,
                reader.cx,
                reader.cy
            )
        }else{
            getSlot("unchecked").render(
                context,
                mouseX,
                mouseY,
                delta,
                reader.cx+iconWidth,
                reader.cy
            )
        }
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (button == 0){
            state.setValue(!state.getValue())
            emitter(
                StateChangedCallback(id.v,state.v)
            )
            val iconWidth = (width.getValue() * 0.45).toInt()
            icon_x = if (state.getValue()){
                IntSequence(0, width.getValue() - iconWidth, 1.0, false)
            }else{
                IntSequence(width.getValue()-iconWidth,0,-1.0,false)
            }
        }
        return super.mouseClicked(mouseX, mouseY, button)
    }
    override fun default(id: String): IComponent {
        val none = None()
        none.id.setValue("#$id")
        return none
    }

    override fun slots(): List<String> {
        return arrayListOf(
            "checked","unchecked"
        )
    }

}