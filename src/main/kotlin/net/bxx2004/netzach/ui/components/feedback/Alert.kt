package net.bxx2004.netzach.ui.components.feedback

import net.bxx2004.netzach.core.attributes.ref
import net.bxx2004.netzach.core.threads.submit
import net.bxx2004.netzach.core.utils.nrl
import net.bxx2004.netzach.core.utils.windowSize
import net.bxx2004.netzach.ui.components.IComponent
import net.bxx2004.netzach.ui.components.blit
import net.bxx2004.netzach.ui.components.drawScaleText
import net.bxx2004.netzach.core.attributes.AttributeReader
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.util.FastColor
import net.minecraft.world.inventory.AbstractContainerMenu

/**
 * @author 6hisea
 * @date  2025/5/3 18:37
 * @description: None
 */
class Alert : IComponent(){
    var icon = ref(nrl("stextures/ui/alert/icon.png"))
    var background = ref(nrl("textures/ui/alert/background.png"))
    var message = ref("This is alert.")
    var duration = ref(5)
    var show = ref(false)
    var currentTime = -1
    override fun onOpen(handler: AbstractContainerMenu?) {
        super.onOpen(handler)
        width.setValue(windowSize()[0] / 4)
        height.setValue(16)
        x.setValue((windowSize()[0] - width.getValue()) / 2)
        y.setValue(5)
        z.setValue(99999999)
        interactable.setValue(false)

    }
    override fun render(context: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float, reader: AttributeReader) {
        if (!show.getValue()) return
        if (show.getValue() && currentTime == -1){
            currentTime = 0
            submit(0,1000){
                currentTime += 1
                if (currentTime >= duration.getValue()){
                    currentTime = -1
                    show.setValue(false)
                    it.cancel()
                }
            }
        }
        interactable.setValue(true)
        if (currentTime == -1) return
        val x = (windowSize()[0] - reader.width) / 2
        val y = 5
        context.blit(
            background.getValue(),
            x,
            y,
            reader.z,
            0F,0F,
            reader.width,
            reader.height,
        )
        context.blit(
            icon.getValue(),
            x,
            y,
            reader.z,
            0F,0F,
            reader.height,
            reader.height,
        )
        context.drawScaleText(
            Component.literal(message.getValue()),
            x + reader.height + 1,
            y,
            1,
            reader.height,
            false,
            -1
            )
    }
}