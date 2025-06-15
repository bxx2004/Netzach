package net.bxx2004.netzach.core.attributes.single

import net.bxx2004.netzach.core.attributes.Attribute

class SimpleAttribute<T>(value:T) : Attribute<T>(){
    var _value = value
    override fun getValue(): T {
        return _value
    }

    override fun setValue(value: T) {
        _value = value
    }

    override fun getValueCache(): T {
        return _value
    }
}