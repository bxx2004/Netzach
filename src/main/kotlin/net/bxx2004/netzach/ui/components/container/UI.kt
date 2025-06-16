package net.bxx2004.netzach.ui.components.container

import net.bxx2004.netzach.core.attributes.Attribute
import net.bxx2004.netzach.core.attributes.ref
import net.bxx2004.netzach.core.utils.client
import net.bxx2004.netzach.core.utils.mouseX
import net.bxx2004.netzach.core.utils.mouseY
import net.bxx2004.netzach.core.utils.player
import net.bxx2004.netzach.resources.data.DataProvider
import net.bxx2004.netzach.resources.data.DataSource
import net.bxx2004.netzach.ui.components.IComponent
import net.bxx2004.netzach.ui.components.UIOptions
import net.bxx2004.netzach.ui.utils.Scissors
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.FastColor
import net.minecraft.world.inventory.AbstractContainerMenu
import org.lwjgl.opengl.GL11

open class UI(handler: AbstractContainerMenu = player()!!.containerMenu, val options: UIOptions) : AbstractContainerScreen<AbstractContainerMenu>(handler, player()!!.inventory, Component.literal(options.title)),
    IContainer, DataProvider {
        private var isHidePositioner = false


    val root = TemplateLayout(this)
    override val components: List<IComponent>
        get() = root.components
    override val background: Attribute<ResourceLocation> = root.background


    private val dataSourcePool = arrayListOf<DataSource>()
    override fun addSource(level: Int, dataSource: DataSource) {
        dataSourcePool.add(level, dataSource)
    }

    override fun removeSource(resourceLocation: ResourceLocation) {
        dataSourcePool.removeIf { it.id == resourceLocation }
    }

    override fun <T> getData(key: String, default: T): Attribute<T> {
        var result: Attribute<T> = ref(default)
        dataSourcePool.forEach {
            val data = it.get<T?>(key).v
            if (data != null) {
                result = it.get(key)
                return@forEach
            }
        }
        return result
    }

    override fun <T> setData(key: String, data: T) {
        dataSourcePool.forEach {
            it.update(key,data)
        }
    }

    override fun findByClass(className: String): List<IComponent> {
        return root.findByClass(className)
    }

    override fun findByType(type: String): List<IComponent> {
        return root.findByType(type)
    }
    override fun findByID(id: String): IComponent? {
        return root.findByID(id)
    }

    private fun drawInformation(hit: IComponent?,context: GuiGraphics) {
        context.drawString(
            client().font,
            "X: $mouseX",0,0, FastColor.ARGB32.color(-1,255,202,178),false
        )
        context.drawString(
            client().font,
            "Y: $mouseY",0,client().font.lineHeight, FastColor.ARGB32.color(-1,255,202,178).toInt(),false
        )
        context.drawString(
            client().font,
            "Hit: ${hit(mouseX, mouseY).id.v} | ${hit(mouseX, mouseY).javaClass.simpleName}",0,client().font.lineHeight*2, FastColor.ARGB32.color(-1,255,255,178).toInt(),false
        )
        context.drawString(
            client().font,
            "Focus: ${getFocus()?.id?.v} | ${getFocus()?.javaClass?.simpleName}",0,client().font.lineHeight*3, FastColor.ARGB32.color(-1,255,255,178).toInt(),false
        )
    }

    override fun render(context: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float) {
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        Scissors.refreshScissors()
        root.render(context, mouseX, mouseY, delta,0,0)
        context.pose().pushPose()
        if (!menu.carried.isEmpty) {
            context.renderItem(menu.carried, mouseX, mouseY);
            //context.drawItemInSlot(client()!!.textRenderer,handler.cursorStack,mouseX,mouseY)
        }
        if (options.positioner && !isHidePositioner){
            drawInformation(hit(mouseX,mouseY),context)
        }
        context.pose().popPose()
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        Scissors.checkStackIsEmpty();
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        return if (components
                .filter { it.within(mouseX.toInt(),mouseY.toInt()) }
                .map { if (it is IContainer) it.hit(mouseX.toInt(), mouseY.toInt()) else it }
                .filter { it.interactable.getValue() }
                .sortedBy { it.z.getValue() }.lastOrNull()?.let {
                    it.mouseClicked(mouseX, mouseY, button)
                } == true
        ){
            super.mouseClicked(mouseX, mouseY, button)
        }else{
            false
        }
    }

    override fun mouseDragged(mouseX: Double, mouseY: Double, button: Int, deltaX: Double, deltaY: Double): Boolean {
        val c = components
            .filter { it.within(mouseX.toInt(),mouseY.toInt()) }
            .map { if (it is IContainer) it.hit(mouseX.toInt(), mouseY.toInt()) else it }
            .filter { it.drag.getValue() && it.interactable.getValue() }
            .sortedBy { it.z.getValue() }.lastOrNull()
        return if (c?.let {
                if (!c.virtual_drag.getValue()){
                    it.x.setValue(it.x.getValueCache() + deltaX.toInt())
                    it.y.setValue(it.y.getValueCache() + deltaY.toInt())
                }
                it.mouseDragged(mouseX,mouseY,button,deltaX,deltaY)
            } == true
        ){
            if (!c.virtual_drag.getValue()){
                super.mouseDragged(mouseX, mouseY, button,deltaX,deltaY)
            }else{
                false
            }
        }else{
            false
        }
    }

    override fun mouseMoved(x: Double, y: Double) {
        mouseX = x.toInt()
        mouseY = y.toInt()
        components
            .filter { it.within(mouseX, mouseY) }
            .map { if (it is IContainer) it.hit(mouseX, mouseY) else it }
            .filter { it.visible.getValue() }.maxByOrNull { it.z.getValue() }?.let {
                it.mouseMoved(x, y)
            }
        super.mouseMoved(x, y)
    }

    override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int): Boolean {
        return if (components
                .filter { it.within(mouseX.toInt(), mouseY.toInt()) }
                .map { if (it is IContainer) it.hit(mouseX.toInt(), mouseY.toInt()) else it }
                .filter { it.interactable.getValue() }.maxByOrNull { it.z.getValue() }?.let {
                    it.mouseReleased(mouseX, mouseY, button)
                } == true
        ){
            super.mouseReleased(mouseX, mouseY, button)
        }else{
            false
        }
    }

    override fun mouseScrolled(
        mouseX: Double,
        mouseY: Double,
        horizontalAmount: Double,
        verticalAmount: Double
    ): Boolean {
        return if (components
                .filter { it.within(mouseX.toInt(), mouseY.toInt()) }
                .map { if (it is IContainer) it.hit(mouseX.toInt(), mouseY.toInt()) else it }
                .filter { it.interactable.getValue() }.maxByOrNull { it.z.getValue() }?.let {
                    it.mouseScrolled(mouseX,mouseY,horizontalAmount,verticalAmount)
                } == true
        ){
            super.mouseScrolled(mouseX, mouseY,horizontalAmount,verticalAmount)
        }else{
            false
        }
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        isHidePositioner = !isHidePositioner
        return if (root.getFocus()?.let {
                it.keyPressed(keyCode,scanCode, modifiers)
            } != false){
            super.keyPressed(keyCode,scanCode,modifiers)
        }else{
            false
        }
    }

    override fun keyReleased(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        return if (root.getFocus()?.let {
                it.keyReleased(keyCode,scanCode, modifiers)
            } != false){
            super.keyReleased(keyCode,scanCode,modifiers)
        }else{
            false
        }
    }

    override fun charTyped(chr: Char, modifiers: Int): Boolean {
        return if (root.getFocus()?.let {
                it.charTyped(chr, modifiers)
            } != false){
            super.charTyped(chr, modifiers)
        }else{
            false
        }
        return super.charTyped(chr, modifiers)
    }
    override fun addComponent(component: IComponent) {
        root.addComponent(component)
    }

    override fun removeComponent(id: String) {
        root.removeComponent(id)
    }

    override fun getFocus(): IComponent? {
        return root.getFocus()
    }
    override fun setFocus(component: IComponent?) {
        if (component == null){
            root.setFocus(null)
        }
        if (component?.focused() == true){
            root.setFocus(component)
        }
    }

    override fun hit(mx: Int, my: Int): IComponent {
        return root.hit(mx, my)
    }

    override fun renderBg(p0: GuiGraphics, p1: Float, p2: Int, p3: Int) {

    }
    override fun shouldCloseOnEsc(): Boolean {
        return options.closeByEsc
    }

    override fun onClose() {
        IComponent.currentUIID = ""
        root.onClose()
        super.onClose()
    }
    fun open(){
        try {
            IComponent.currentUIID = options.id
            client().setScreen(this)
            root.onOpen(menu)
            client().window.setTitle(options.title)
        }catch (e: Exception){e.printStackTrace()}

    }
}