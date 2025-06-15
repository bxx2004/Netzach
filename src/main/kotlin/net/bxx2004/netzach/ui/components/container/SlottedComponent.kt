package net.bxx2004.netzach.ui.components.container

import net.bxx2004.netzach.ui.components.IComponent
import net.minecraft.world.inventory.AbstractContainerMenu


abstract class SlottedComponent : BaseLayout(){
    override fun findByClass(className: String): List<IComponent> {
        return listOf(this)
    }

    override fun findByID(id: String): IComponent? {
        return this
    }

    override fun hit(mx: Int, my: Int): IComponent {
        return this
    }

    protected fun hitInBox(mx: Int, my: Int): IComponent {
        return components.filter { it.within(mx, my) }
            .map { if (it is IContainer) it.hit(mx, my) else it }
            .filter { it.interactable.getValue() }.maxByOrNull { it.z.getValue() } ?:this
    }


    override fun removeComponent(id: String) {

    }

    fun <T>slot(id:String,func:T.()-> IComponent){
        addComponent(func(this as T).apply { this.id.v = "#${id}" })
    }

    override fun onOpen(handler: AbstractContainerMenu?) {
        super.onOpen(handler)
        components.removeIf{
            !slots().contains(it.id.getValue().replace("#",""))
        }
    }
    abstract fun default(id: String): IComponent
    abstract fun slots(): List<String>
    fun getSlot(id: String): IComponent {
        if (components.find { it.id.getValue() == "#${id}" } == null){
            addComponent(default(id).apply { this.id.v = "#${id}" })
        }
        return components.find { it.id.getValue() == "#${id}" } as IComponent
    }
}