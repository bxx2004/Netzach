package net.bxx2004.netzach.ui.components.form

import net.bxx2004.netzach.core.attributes.Attribute
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.inventory.Slot


interface ItemSlot {
    var background: Attribute<ResourceLocation>
    var hovered_background: Attribute<ResourceLocation>
    var lineSize: Attribute<Int>
    val slots: List<Slot>
}