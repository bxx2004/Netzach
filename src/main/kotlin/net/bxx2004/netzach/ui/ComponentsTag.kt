package net.bxx2004.netzach.ui

import com.google.common.base.Supplier
import net.bxx2004.netzach.ui.components.IComponent
import net.bxx2004.netzach.ui.components.None

enum class ComponentsTag(val tag: Array<String>,val supplier: Supplier<IComponent>) {
    NONE(arrayOf("none"),{ None() });
    companion object{
        fun of(tag: String): ComponentsTag {
            return ComponentsTag.entries.find { it.tag.contains(tag) }?:NONE
        }
    }
}