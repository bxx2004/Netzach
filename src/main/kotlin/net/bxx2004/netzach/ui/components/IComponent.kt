package net.bxx2004.netzach.ui.components

import net.bxx2004.netzach.core.attributes.ref
import net.bxx2004.netzach.core.utils.client
import net.bxx2004.netzach.core.utils.mouseX
import net.bxx2004.netzach.core.utils.mouseY
import net.bxx2004.netzach.core.utils.scaleFactor
import net.bxx2004.netzach.ui.ComponentsTag
import net.bxx2004.netzach.ui.UIAnimation
import net.bxx2004.netzach.ui.components.container.IContainer
import net.bxx2004.netzach.ui.style.ReadingContext
import net.bxx2004.netzach.ui.style.UIStyle
import net.bxx2004.netzach.core.attributes.AttributeReader
import net.bxx2004.netzach.core.attributes.InsDSL
import net.bxx2004.netzach.network.NetzachNetwork
import net.bxx2004.netzach.ui.callback.CharTypedCallback
import net.bxx2004.netzach.ui.callback.CloseCallback
import net.bxx2004.netzach.ui.callback.ComponentActionCallBack
import net.bxx2004.netzach.ui.callback.FocusCallback
import net.bxx2004.netzach.ui.callback.KeyPressCallback
import net.bxx2004.netzach.ui.callback.KeyReleaseCallback
import net.bxx2004.netzach.ui.callback.MouseClickCallback
import net.bxx2004.netzach.ui.callback.MouseDragCallback
import net.bxx2004.netzach.ui.callback.MouseMoveCallback
import net.bxx2004.netzach.ui.callback.MouseReleaseCallback
import net.bxx2004.netzach.ui.callback.MouseScrollCallback
import net.bxx2004.netzach.ui.callback.OpenCallback
import net.bxx2004.netzach.ui.callback.TickedCallback
import net.bxx2004.netzach.ui.packets.ComponentCallbackPacket
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.FormattedCharSequence
import net.minecraft.world.inventory.AbstractContainerMenu
import net.neoforged.api.distmarker.Dist
import net.neoforged.api.distmarker.OnlyIn
import org.joml.Quaternionf

abstract class IComponent {
    open var container: IContainer? = null

    //attribute
    var id = ref(this.toString().split("@")[1])
    var clazz = ref("")
    var x = ref(0)
    var y = ref(0)
    var z = ref(0)
    var width = ref(16)
    var height = ref(16)
    var transparency = ref(0.0)
    var visible = ref(true)
    var interactable = ref(true)
    var scale = ref(1.0f)
    var rotation_x = ref(0.0)
    var rotation_y = ref(0.0)
    var rotation_z = ref(0.0)
    var translate_x = ref(0.0)
    var translate_y = ref(0.0)
    var translate_z = ref(0.0)
    var drag = ref(false)
    var virtual_drag = ref(false)

    private val callbacks = hashMapOf<Class<ComponentActionCallBack>,ComponentActionCallBack.()-> Unit>()

    private val animations = arrayListOf<UIAnimation>()

    private val renderCalls = ArrayList<GuiGraphics.() -> Boolean>()


    fun root():IComponent{
        return if (container != null){
            if (container is IComponent){
                (container as IComponent).root()
            }else{
                container!! as IComponent
            }
        }else{
            this
        }
    }


    @OnlyIn(Dist.CLIENT)
    internal inline fun <reified T: ComponentActionCallBack>emitter(data: T){

        callbacks.filter { it.key == T::class.java }.forEach {
            it.value(data)
        }
        animations.forEach { it.runnable(data.callbackId,this) }
        NetzachNetwork.sendPacketToServer(ComponentCallbackPacket(
            currentUIID,data
        ))
    }

    @OnlyIn(Dist.CLIENT)
    internal inline fun <reified T: ComponentActionCallBack>callback(noinline callback:T.()-> Unit){
        callbacks[T::class.java as Class<ComponentActionCallBack>] = callback as ComponentActionCallBack.()-> Unit
    }

    fun isFocus(): Boolean{
        return container?.getFocus() == this
    }


    fun render(context: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float, containerX: Int, containerY: Int){
        useContext(context){
            render(this,mouseX, mouseY, delta, useReader(containerX,containerY))
        }
    }

    protected abstract fun render(context: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float, reader: AttributeReader)

    fun within(x:Int, y:Int): Boolean{
        return x>=this.absoluteX() && y>=absoluteY() && x< this.absoluteX()+this.width.v && y<this.absoluteY()+this.height.v
    }
    open fun onOpen(handler: AbstractContainerMenu?){
        //应用GUI缩放
        width.v = width.v * scaleFactor()
        height.v = height.v * scaleFactor()
        //缩放
        width.v = width.v * scale.getValue().toInt()
        height.v = height.v * scale.getValue().toInt()
        emitter(OpenCallback(id.v))
        if (this is IContainer){
            components.forEach {
                it.onOpen(handler)
            }
        }
    }
    open fun onClose(){
        emitter(CloseCallback(id.v))
        if (this is IContainer){
            components.forEach {
                it.onClose()
            }
        }
    }
    @OnlyIn(Dist.CLIENT)
    open fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        emitter(KeyPressCallback(id.v,keyCode.toString(),scanCode.toString(),modifiers))
        return true
    }
    @OnlyIn(Dist.CLIENT)
    open fun keyReleased(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        emitter(KeyReleaseCallback(id.v,keyCode.toString(),scanCode.toString(),modifiers))
        return true
    }
    open fun mouseScrolled(
        mouseX: Double,
        mouseY: Double,
        horizontalAmount: Double,
        verticalAmount: Double
    ): Boolean {
        emitter(MouseScrollCallback(id.v, mouseX, mouseY, horizontalAmount, verticalAmount))
        return true
    }
    open fun mouseReleased(mouseX: Double, mouseY: Double, button: Int): Boolean {
        emitter(MouseReleaseCallback(id.v, mouseX, mouseY, button))
        return interactable.getValue()
    }
    open fun mouseMoved(mouseX: Double, mouseY: Double) {
        emitter(MouseMoveCallback(id.v, mouseX, mouseY))
    }
    open fun mouseDragged(mouseX: Double, mouseY: Double, button: Int, deltaX: Double, deltaY: Double): Boolean {
        emitter(MouseDragCallback(id.v, mouseX, mouseY, button,deltaX, deltaY))
        return drag.getValue()
    }
    open fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        container?.setFocus(this)
        emitter(MouseClickCallback(id.v,mouseX, mouseY, button))
        return interactable.getValue()
    }
    open fun charTyped(chr: Char, modifiers: Int): Boolean{
        emitter(CharTypedCallback(id.v,chr,modifiers))
        return interactable.getValue()
    }
    open fun focused(): Boolean{
        emitter(FocusCallback(id.v))
        return true
    }
    private fun useReader(cx:Int,cy: Int):AttributeReader{
        return AttributeReader(x.getValue(),y.getValue(),z.getValue(),width.getValue(),height.getValue(),cx,cy)
    }
    private fun useContext(context: GuiGraphics,func:GuiGraphics.() -> Unit){
        emitter(TickedCallback(id.v))
        if (visible.getValue()){
            context.pose().pushPose()
            //平移
            context.pose().translate(translate_x.getValue(),translate_y.getValue(),translate_z.getValue())
            //旋转
            context.pose().mulPose(Quaternionf(rotation_x.getValue(),rotation_y.getValue(),rotation_z.getValue(),1.00))
            renderCalls.forEach{ it(context) }
            func(context)
            context.pose().popPose()
        }
    }
    fun addRenderCall(func: GuiGraphics.() -> Boolean = {true}){
        renderCalls.add(func)
    }
    fun addStyle(style: UIStyle,vararg parameters:Any){
        style.runnable.invoke(
            ReadingContext(parameters.toList()),
            this
        )
    }

    fun addAnimation(animation: UIAnimation){
        animations.add(animation)
    }

    fun isHover(): Boolean{
        return container?.hit(mouseX,mouseY) == this
    }
    fun absoluteX(): Int{
        return if (this.container != null && this.container is IComponent){
            (this.container as IComponent).absoluteX() + x.getValueCache()
        }else{
            x.getValueCache()
        }
    }
    fun absoluteY(): Int{
        return if (this.container != null&& this.container is IComponent){
            (this.container as IComponent).absoluteY() + y.getValueCache()
        }else{
            y.getValueCache()
        }
    }
    companion object{
        var currentUIID = ""
        @JvmStatic
        fun make(tag:String):IComponent{
            return ComponentsTag.of(tag).supplier.get()
        }
    }
}
fun GuiGraphics.blit(
    rl: ResourceLocation,
    x: Int,
    y: Int,
    z: Int,
    u: Float,
    v: Float,
    w: Int,
    h: Int
){
    blit(
        rl, x, y,z, u,v,w,h,w,h
    )
}
fun GuiGraphics.drawScaleText(text: Component,x: Int,y: Int,z: Int,height: Int,shadow: Boolean,color:Int = -1) {
    val fontHeight = client().font.lineHeight
    val scale = if ((height/fontHeight).toFloat() > fontHeight){
        (height/fontHeight).toFloat()
    }else{
        1
    }
    pose().pushPose()
    pose().scale(scale.toFloat(),scale.toFloat(),z.toFloat())
    drawString(client().font,text,x,y,color,shadow)
    pose().popPose()
}
fun GuiGraphics.drawScaleText(text: FormattedCharSequence,x: Int,y: Int,z: Int,height: Int,shadow: Boolean,color:Int = -1) {
    val fontHeight = client().font.lineHeight
    val scale = if ((height/fontHeight).toFloat() > fontHeight){
        (height/fontHeight).toFloat()
    }else{
        1
    }
    pose().pushPose()
    pose().scale(scale.toFloat(),scale.toFloat(),z.toFloat())
    drawString(client().font,text,x,y,color,shadow)
    pose().popPose()
}