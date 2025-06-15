package net.bxx2004.netzach.ui.components.container

import net.bxx2004.netzach.core.attributes.Attribute
import net.bxx2004.netzach.core.attributes.ref
import net.bxx2004.netzach.core.utils.emptyResourceLocation
import net.bxx2004.netzach.core.utils.isEmpty

import net.bxx2004.netzach.ui.components.IComponent
import net.bxx2004.netzach.ui.components.blit
import net.bxx2004.netzach.core.attributes.AttributeReader
import net.bxx2004.netzach.ui.callback.UnloadCallback
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.resources.ResourceLocation

open class BaseLayout(): IComponent(), IContainer {
    override val components = ArrayList<IComponent>()
    override val background: Attribute<ResourceLocation> = ref(emptyResourceLocation())
    override var container: IContainer?=null
    override fun addComponent(component: IComponent) {
        component.container = this
        components.add(component)
    }
    override fun findByType(type: String): List<IComponent> {
        val result = ArrayList<IComponent>()
        components.forEach {
            if (it::class.java.toString().lowercase().contains(type)) {
                result.add(it)
            }
            if (it is IContainer){
                result.addAll(it.findByType(type))
            }
        }
        return result
    }
    override fun findByClass(className: String): List<IComponent> {
        val result = ArrayList<IComponent>()
        components.forEach {
            if (it.clazz.getValue().contains(className)) {
                result.add(it)
            }
            if (it is IContainer){
                result.addAll(it.findByClass(className))
            }
        }
        return result
    }

    override fun findByID(id: String): IComponent? {
        components.forEach {
            if (it.id.getValue() == id) {
                return it
            }
            if (it is IContainer){
                return it.findByID(id)
            }
        }
        return null
    }
    override fun hit(mx: Int, my: Int): IComponent {
        return components.filter { it.within(mx, my) }
            .map { if (it is IContainer) it.hit(mx, my) else it }
            .filter { it.interactable.getValue() }.maxByOrNull { it.z.getValue() } ?:this
    }
    override fun removeComponent(id: String) {
        emitter(UnloadCallback(id))
        components.removeIf{
            it.container = null
            it.id.getValue() == id
        }
    }

    override fun render(
        context: GuiGraphics,
        mouseX: Int,
        mouseY: Int,
        delta: Float,
        reader: AttributeReader
    ) {
        if (!background.getValue().isEmpty()){
            context.blit(background.getValue(),reader.ax,reader.ay,reader.z,0.0f,0.0f,reader.width,reader.height)
        }
        components.forEach {
            it.render(context,mouseX,mouseY,delta,reader.ax,reader.ay)
        }
    }

    override fun setFocus(component: IComponent?) {
        container?.setFocus(component)
    }

    override fun getFocus(): IComponent? {
        return container?.getFocus()
    }
}