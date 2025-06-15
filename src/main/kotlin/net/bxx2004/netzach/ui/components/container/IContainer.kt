package net.bxx2004.netzach.ui.components.container

import net.bxx2004.netzach.core.attributes.Attribute
import net.bxx2004.netzach.ui.components.IComponent
import net.minecraft.resources.ResourceLocation

interface IContainer {
    val components: List<IComponent>
    fun addComponent(component: IComponent)
    fun removeComponent(id: String)
    fun getFocus(): IComponent?
    fun setFocus(component: IComponent?)
    fun hit(mx: Int,my: Int): IComponent
    fun findByID(id: String): IComponent?
    fun findByClass(className: String): List<IComponent>
    fun findByType(type: String): List<IComponent>
    val background: Attribute<ResourceLocation>
}

inline fun <T:IContainer> T.with(block: T.() -> IComponent):T{
    addComponent(block(this))
    return this
}
inline fun <T:IContainer> T.withs(block: T.() -> List<IComponent>):T{
    block(this).forEach { addComponent(it) }
    return this
}