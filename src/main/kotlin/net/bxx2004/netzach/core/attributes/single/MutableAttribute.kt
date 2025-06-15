package net.bxx2004.netzach.core.attributes.single

import net.bxx2004.netzach.core.attributes.Attribute

class MutableAttribute<T>(val func:()->T): Attribute<T>() {
    var cache: T? = null
    override fun getValue(): T {
        cache = func()
        return cache!!
    }

    override fun setValue(value: T) {
        cache = value
    }

    override fun getValueCache(): T {
        return if (cache == null){
            return getValue()
        }else{
            cache!!
        }
    }
}