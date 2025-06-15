package net.bxx2004.netzach.ui.components.form

import net.bxx2004.netzach.core.attributes.Attribute
import net.bxx2004.netzach.core.attributes.AttributeReader
import net.bxx2004.netzach.core.attributes.ref
import net.bxx2004.netzach.core.utils.client
import net.bxx2004.netzach.core.utils.nrl
import net.bxx2004.netzach.ui.components.IComponent
import net.bxx2004.netzach.ui.components.blit
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.Slot

class CustomSlot: IComponent(), ItemSlot {
    override var background = ref(nrl("textures/ui/custom_inventory_itemslot/background.png"))
    override var hovered_background = ref(nrl("textures/ui/custom_inventory_itemslot/hovered_background.png"))
    override val slots = ArrayList<Slot>()
    override var lineSize: Attribute<Int> = ref(9)
    override fun onOpen(handler: AbstractContainerMenu?) {
        super.onOpen(handler)
        handler?.slots?.addAll(slots)
    }
    override fun render(
        context: GuiGraphics,
        mouseX: Int,
        mouseY: Int,
        delta: Float,
        reader: AttributeReader
    ) {

        var w = 0
        var h = 0
        for ((index, slot) in slots.withIndex()) {
            if (lineSize.v % (index + 1) == 0){
                h+=1
                w = 0
            }
            w += 1

            context.blit(background.getValue(),
                reader.ax + (w * 16),
                reader.ay + (h * 16),
                z.getValue(),
                0.0F,
                0.0F,
                width.getValue(),
                height.getValue()
            )
            if (mouseX >= reader.ax + (w * 16) && mouseY >= reader.ay + (h * 16) && mouseX <= reader.x + (w * 16) + 16 && mouseY <= reader.y + (h * 16) + 16) {
                context.blit(hovered_background.getValue(),
                    reader.ax + (w * 16),
                    reader.ay + (h * 16),
                    z.getValue(),
                    0.0F,
                    0.0F,
                    width.getValue(),
                    height.getValue()
                )
            }
            context.renderItem(slot.item,reader.ax + (w * 16),reader.ay + (h * 16))
            context.renderItemDecorations(client().font,slot.item,reader.ax + (w * 16),reader.ay +  + (h * 16))
        }
    }
}