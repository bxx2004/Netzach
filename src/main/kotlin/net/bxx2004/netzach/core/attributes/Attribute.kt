package net.bxx2004.netzach.core.attributes

import net.bxx2004.netzach.core.attributes.single.MultipleAttribute
import net.bxx2004.netzach.core.attributes.single.MutableAttribute
import net.bxx2004.netzach.core.attributes.single.SimpleAttribute
import java.io.Serializable

abstract class Attribute<T>:Serializable{
    var v:T
        get() = getValue()
    set(value){
        setValue(value)
    }

    abstract fun getValueCache():T
    abstract fun getValue(): T
    abstract fun setValue(value: T)
    fun reverse():T{
        val v = getValue()
        return when(v){
            is Byte -> -v.toByte() as T
            is Short -> -v.toShort() as T
            is Int -> -v.toInt() as T
            is Long -> -v.toLong() as T
            is Float -> -v.toFloat() as T
            is Double -> -v.toDouble() as T
            is Boolean -> !v as T
            else -> return v
        }
    }
}


fun <T> ref(value: T): Attribute<T>{
    return SimpleAttribute(value)
}
fun <T> mutable(func:()->T): Attribute<T>{
    return MutableAttribute(func)
}
fun multiple(vararg attribute: Attribute<out Any>): Attribute<*>{
    val result =  MultipleAttribute()
    attribute.forEach { result.push(it) }
    return result
}
fun <T: Number>complex(vararg attribute: Attribute<T>): Attribute<T>{
    return MutableAttribute<T>{
        (attribute.map { it.v.toDouble() }.sum() as Number) as T
    }
}