package net.bxx2004.netzach.ui.components.feedback

import net.bxx2004.netzach.core.attributes.ref
import net.bxx2004.netzach.core.utils.client
import net.bxx2004.netzach.core.utils.isEmpty
import net.bxx2004.netzach.core.utils.windowSize
import net.bxx2004.netzach.ui.components.IComponent
import net.bxx2004.netzach.ui.components.blit
import net.bxx2004.netzach.ui.components.container.SlottedComponent
import net.bxx2004.netzach.ui.components.display.Text
import net.bxx2004.netzach.ui.components.container.IContainer
import net.bxx2004.netzach.core.attributes.AttributeReader
import net.bxx2004.netzach.core.attributes.complex
import net.bxx2004.netzach.ui.autoHeight
import net.bxx2004.netzach.ui.autoWidth
import net.bxx2004.netzach.ui.callback.MouseClickCallback
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.world.inventory.AbstractContainerMenu
import java.awt.Color

/**
 * @author 6hisea
 * @date  2025/5/3 18:37
 * @description: None
 */
class Modal : SlottedComponent(){
    var show = ref(false)
    var showTitle = ref(true)
    init {
        autoWidth(0.4F)
        autoHeight(0.5F)
    }
    override fun onOpen(handler: AbstractContainerMenu?) {
        super.onOpen(handler)

        x.setValue((windowSize()[0] - width.getValue()) / 2)
        y.setValue((windowSize()[1] - height.getValue()) / 2)
        z.setValue(99999999)
        interactable.setValue(false)
        drag.setValue(true)
        fix()
    }

    override fun default(id: String): IComponent {
        if (id.contains("title")){
            return Text().apply {
                this.id.setValue("#title")
                text.setValue("我是一个模态框实例")
                width.v = client().font.width("我是一个模态框实例")
            }
        }
        if (id.contains("close")){
            return Text().apply {
                this.id.setValue("#close")
                text.setValue("[x]")
                width.v = 9
                callback<MouseClickCallback> {
                    show.v = false
                }
            }
        }
        return Text().apply { this.id.setValue("#content");text.v = "Empty.";width.setValue(
            client().font.width("Empty.")
        ) }
    }

    override fun hit(mx: Int, my: Int): IComponent {
        return components.filter { it.within(mx, my) }
            .map { if (it is IContainer) it.hit(mx, my) else it }
            .filter { it.interactable.getValue() }.maxByOrNull { it.z.getValue() } ?:this
    }
    override fun slots(): List<String> {

        return arrayListOf("content","close","title")
    }

    fun fix(){
        getSlot("content").y = complex(ref(client().font.lineHeight),getSlot("content").y)
    }

    override fun render(context: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float, reader: AttributeReader) {
        if (!show.v){
            interactable.setValue(false)
            return
        }
        interactable.setValue(true)

        if (!background.getValue().isEmpty()){
            context.blit(background.getValue(),reader.x,reader.y,reader.z,0.0f,0.0f,reader.width,reader.height)
        }else{
            context.fill(reader.x,reader.y,reader.x + reader.width,reader.y+reader.height, Color.DARK_GRAY.rgb)
        }
        getSlot("content").render(context, mouseX, mouseY, delta,reader.x,reader.y)

        if (showTitle.v){
            getSlot("title").render(context, mouseX, mouseY, delta,reader.x,reader.y)
        }

        getSlot("close").x.v = width.v - getSlot("close").width.v
        getSlot("close").y.v = 0
        getSlot("close").render(context, mouseX, mouseY, delta,reader.x,reader.y)
    }
}