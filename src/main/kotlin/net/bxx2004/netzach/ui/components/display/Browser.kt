package net.bxx2004.netzach.ui.components.display

import com.cinemamod.mcef.MCEF
import com.cinemamod.mcef.MCEFBrowser
import net.bxx2004.netzach.core.attributes.ref
import net.bxx2004.netzach.core.utils.client
import net.bxx2004.netzach.core.utils.modId
import net.bxx2004.netzach.core.utils.nrl
import net.bxx2004.netzach.resources.TempTexture
import net.bxx2004.netzach.ui.components.IComponent
import net.bxx2004.netzach.ui.components.blit
import net.bxx2004.netzach.core.attributes.AttributeReader
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.inventory.AbstractContainerMenu


/**
 * @author 6hisea
 * @date  2025/5/2 19:40
 * @description: None
 */
class Browser : IComponent() {
    private var browser:MCEFBrowser?=null

    var url = ref("https://www.baidu.com")

    override fun onOpen(handler: AbstractContainerMenu?) {
        super.onOpen(handler)
        if (browser == null) {
            val url = url.getValue()
            browser = MCEF.createBrowser(url, false)
            client().textureManager.register(refresh(), TempTexture { browser!!.renderer.textureID })
            resizeBrowser()
        }

    }

    private fun refresh(): ResourceLocation {
        val id =  browser!!.renderer.textureID
        return nrl("tempid${id}")
    }
    private fun mouseX(x: Double): Int {
        return ((x - absoluteX())).toInt()
    }

    private fun mouseY(y: Double): Int {
        return ((y -absoluteY())).toInt()
    }

    private fun scaleX(x: Double): Int {
        return ((x - absoluteX() * 2)).toInt()
    }

    private fun scaleY(y: Double): Int {
        return ((y - absoluteY() * 2)).toInt()
    }

    private fun resizeBrowser() {
        if (browser == null) return
        if (width.getValue() > 100 && height.getValue() > 100) {
            browser!!.resize(scaleX(width.getValueCache().toDouble()), scaleY(height.getValueCache().toDouble()))
        }
    }

    override fun onClose() {
        browser?.close()
        client().textureManager.release(refresh())
        super.onClose()
    }
    override fun render(context: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float, reader: AttributeReader) {
        resizeBrowser()
        if (browser != null){
            context.blit(
                refresh(),
                reader.ax,
                reader.ay,
                reader.z,
                0.0F,
                0.0F,
                reader.width,
                reader.height,
            )
        }

    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        browser?.sendMousePress(mouseX(mouseX), mouseY(mouseY), button);
        browser?.setFocus(true);
        return super.mouseClicked(mouseX, mouseY, button)
    }

    override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int): Boolean {
        browser?.sendMouseRelease(mouseX(mouseX), mouseY(mouseY), button)
        browser?.setFocus(true)
        return super.mouseReleased(mouseX, mouseY, button)
    }

    override fun mouseMoved(mouseX: Double, mouseY: Double) {
        browser?.sendMouseMove(mouseX(mouseX), mouseY(mouseY))
        super.mouseMoved(mouseX, mouseY)
    }

    override fun mouseScrolled(
        mouseX: Double,
        mouseY: Double,
        horizontalAmount: Double,
        verticalAmount: Double
    ): Boolean {
        browser?.sendMouseWheel(mouseX(mouseX), mouseY(mouseY), verticalAmount, 0);
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        browser?.sendKeyPress(keyCode, scanCode.toLong(), modifiers);
        browser?.setFocus(true);
        return super.keyPressed(keyCode, scanCode, modifiers)
    }

    override fun keyReleased(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        browser?.sendKeyRelease(keyCode, scanCode.toLong(), modifiers);
        browser?.setFocus(true);
        return super.keyReleased(keyCode, scanCode, modifiers)
    }

    override fun charTyped(chr: Char, modifiers: Int): Boolean {
        if (chr == 0.toChar()) return false
        browser!!.sendKeyTyped(chr, modifiers)
        browser!!.setFocus(true)
        return super.charTyped(chr, modifiers)
    }
}