package net.bxx2004.netzach.core.attributes.sequence

import net.bxx2004.netzach.core.attributes.Attribute
import java.io.Serializable

abstract class Sequence<T>(val repeatable: Boolean) : Attribute<T>(), Serializable {
    abstract fun isEnding(): Boolean
    abstract fun reset()
}