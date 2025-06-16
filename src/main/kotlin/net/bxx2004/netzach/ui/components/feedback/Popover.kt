package net.bxx2004.netzach.ui.components.feedback

import net.bxx2004.netzach.core.attributes.ref
import net.bxx2004.netzach.ui.components.IComponent
import net.bxx2004.netzach.ui.components.None
import net.bxx2004.netzach.ui.components.container.SlottedComponent
import net.bxx2004.netzach.ui.components.display.Text
import net.bxx2004.netzach.core.attributes.AttributeReader
import net.bxx2004.netzach.ui.callback.MouseClickCallback
import net.bxx2004.netzach.ui.utils.TriggerType
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.world.inventory.AbstractContainerMenu

/**
 * @author 6hisea
 * @date  2025/5/2 16:51
 * @description: None
 */
class Popover : SlottedComponent(){
    var type = ref(TriggerType.HOVER)
    private var isClicked = false
    override fun default(id: String): IComponent {
        return when(id){
            "content" -> Text().apply { this.id.setValue("#content");this.text.setValue(Component.literal("I'm popover")) }
            "trigger" -> Text().apply { this.id.setValue("#trigger");this.text.setValue(Component.literal("You must provide #trigger")) }
            else -> None()
        }
    }

    override fun slots(): List<String> {
        return listOf("trigger","content")
    }

    override fun hit(mx: Int, my: Int): IComponent {
        return getSlot("trigger")
    }
    fun fixed(){
        getSlot("trigger").x = x
        getSlot("trigger").y = y
        getSlot("trigger").width = width
        getSlot("trigger").height = height
    }

    override fun onOpen(handler: AbstractContainerMenu?) {
        getSlot("trigger").callback<MouseClickCallback> {
            isClicked = !isClicked
        }
        super.onOpen(handler)
    }
    override fun render(context: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float, reader: AttributeReader) {
        fixed()
        getSlot("trigger").render(context, mouseX, mouseY, delta, reader.ax,reader.ay)
        when(type.getValue()){
            "hover" -> {
                if (within(mouseX,mouseY)){
                    getSlot("content").render(context, mouseX, mouseY, delta, mouseX,mouseY)
                }
            }
            "click" -> {
                if (isClicked && within(mouseX,mouseY)){
                    getSlot("content").render(context, mouseX, mouseY, delta, mouseX,mouseY)
                }
            }
            "focus"-> {
                if (isFocus() && within(mouseX,mouseY)){
                    getSlot("content").render(context, mouseX, mouseY, delta, mouseX,mouseY)
                }
            }
        }
    }
}