package net.bxx2004.netzach.core.attributes.single

import net.bxx2004.netzach.core.attributes.Attribute

class MultipleAttribute:Attribute<Any>() {
    val attributes = ArrayList<Attribute<out Any>>()
    fun push(attribute: Attribute<out Any>) {
        attributes.add(attribute)
    }
    fun remove(index: Int) {
        attributes.removeAt(index)
    }
    fun pop(): Attribute<out Any>? {
        return attributes.removeLast()
    }

    override fun getValueCache(): Any {
        val result = attributes.map { it.getValueCache() }
        return if (result.all { it is Number }){
            result.sumOf { it as Number;it.toDouble() }
        }else{
            result.joinToString("")
        }
    }

    override fun getValue(): Any {
        val result = attributes.map { it.getValue() }
        return if (result.all { it is Number }){
            result.sumOf { it as Number;it.toDouble() }
        }else{
            result.joinToString("")
        }
    }

    override fun setValue(value: Any) {

    }
}