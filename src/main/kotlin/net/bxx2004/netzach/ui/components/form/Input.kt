package net.bxx2004.netzach.ui.components.form

import net.bxx2004.netzach.core.attributes.AttributeReader
import net.bxx2004.netzach.core.attributes.mutable
import net.bxx2004.netzach.core.attributes.ref
import net.bxx2004.netzach.core.utils.client
import net.bxx2004.netzach.core.utils.nrl
import net.bxx2004.netzach.ui.components.IComponent
import net.bxx2004.netzach.ui.components.blit
import net.bxx2004.netzach.ui.components.container.SlottedComponent
import net.bxx2004.netzach.ui.components.display.Text
import net.bxx2004.netzach.ui.components.drawScaleText
import net.bxx2004.netzach.ui.utils.Scissors
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import org.apache.commons.lang3.tuple.MutablePair
import org.lwjgl.glfw.GLFW

class Input : SlottedComponent() {
    override var background = ref(nrl("textures/ui/input/background.png"))
    var focus_texture = ref(nrl("textures/ui/input/focus_texture.png"))
    var hovered_texture = ref(nrl("textures/ui/input/hovered_texture.png"))
    var max_length = ref(100)
    var text = ref("")
    var cursor_index = ref(MutablePair<Int, Int>(0,0))


    override fun default(id: String): IComponent {
        val pt = Text()
        pt.id.setValue("#placeholder")
        pt.width = mutable {
            width.getValueCache()
        }
        pt.height = mutable {
            height.getValueCache()
        }
        pt.text = ref("请输入...")
        return pt
    }

    fun backspace(old: Char) {
        cursor_index.getValue().left = cursor_index.getValue().left-1
        cursor_index.getValue().right = cursor_index.getValue().right-getCharLength(old)
    }
    fun nextspace(old: Char) {
        cursor_index.getValue().left = cursor_index.getValue().left+1
        cursor_index.getValue().right = cursor_index.getValue().right+getCharLength(old)
    }

    fun getCharLength(i: Char): Int{
        return client().font.width(i.toString()) * (height.getValueCache()/client().font.lineHeight)
    }
    override fun charTyped(chr: Char, modifiers: Int): Boolean {
        if (text.getValue().length > max_length.getValue()){
            return false
        }
        if (text.getValue().isEmpty()){
            text.setValue(text.getValue() + chr.toString())
        }else{
            val array = ArrayList<Char>()
            array.addAll(text.getValue().toList())
            if (cursor_index.getValue().left >= array.size){
                array.add(chr)
            }else{
                array.add(cursor_index.getValue().left,chr)
            }
            text.setValue(array.joinToString(""))
        }
        nextspace(chr)
        return super.charTyped(chr, modifiers)
    }

    override fun slots(): List<String> {
        return arrayListOf("placeholder")
    }
    fun getCharIndex(mouseX: Int): MutablePair<Int, Int>{
        val postMouseX = mouseX - x.getValueCache() - (container as IComponent).x.getValueCache()
        var i = 0
        var length = 0
        text.getValue().forEachIndexed { index, value ->
            if (length >= postMouseX){
                return@forEachIndexed
            }
            i++
            length += client().font.width(value.toString()) * (height.getValueCache()/client().font.lineHeight)
        }
        return MutablePair(i,length)
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        val array = ArrayList<Char>()
        array.addAll(text.getValue().toList())
        if (array.isNotEmpty()){
            when(keyCode){
                GLFW.GLFW_KEY_BACKSPACE -> {
                    if (cursor_index.getValue().left != 0){
                        backspace(array.removeAt(cursor_index.getValue().left-1))
                    }
                    text.setValue(array.joinToString(""))
                }
                GLFW.GLFW_KEY_RIGHT -> {
                    if (cursor_index.getValue().left < array.size){
                        if (cursor_index.getValue().left == 0){
                            nextspace(array[cursor_index.getValue().left])
                        }else{
                            nextspace(array[cursor_index.getValue().left-1])
                        }
                    }
                }
                GLFW.GLFW_KEY_LEFT -> {
                    if (cursor_index.getValue().left>0){
                        backspace(array[cursor_index.getValue().left-1])
                    }
                }
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers)
    }
    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        cursor_index.setValue(getCharIndex(mouseX.toInt()))
        return super.mouseClicked(mouseX, mouseY, button)
    }
    override fun render(
        context: GuiGraphics,
        mouseX: Int,
        mouseY: Int,
        delta: Float,
        reader: AttributeReader
    ) {
        context.blit(background.getValue(),reader.ax,reader.ay,reader.z,0.0f,0.0f,width.getValue(),height.getValue())
        if (isHover()){
            context.blit(hovered_texture.getValue(),reader.ax,reader.ay,reader.z,0.0f,0.0f,width.getValue(),height.getValue())
        }
        if (isFocus()){
            context.blit(focus_texture.getValue(),reader.ax,reader.ay,reader.z,0.0f,0.0f,width.getValue(),height.getValue())
        }else{
            cursor_index.setValue(MutablePair(0,0))
        }
        if (text.getValue().isEmpty()){
            getSlot("placeholder").render(context,mouseX, mouseY, delta,reader.cx,reader.cy)
        }else{
            Scissors.push(reader.ax,
                reader.ay,
                width.getValueCache(),
                height.getValueCache()
            )
            val a = absoluteX()+cursor_index.getValue().right-width.getValueCache()
            if (a>0){
                context.pose().translate(-a.toDouble()-1,0.0,0.0)
            }
            context.drawScaleText(Component.literal(text.getValue()),
                reader.ax,
                reader.ay,reader.z,height.getValueCache(),false
            )
            if (isFocus()){
                context.fill(
                    cursor_index.getValueCache().right+absoluteX(),
                    absoluteY()+2,
                    cursor_index.getValueCache().right+absoluteX()+1,
                    absoluteY()+height.getValueCache()-2,
                    -1
                )
            }

            Scissors.pop()
        }
    }
}