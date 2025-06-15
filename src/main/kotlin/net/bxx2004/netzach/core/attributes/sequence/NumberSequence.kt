package net.bxx2004.netzach.core.attributes.sequence

import java.io.Serializable

open class NumberSequence(val start: Number, val end: Number, val step: Number, repeatable: Boolean=false) : Sequence<Number>(repeatable),Serializable{
    var cacheValue = start.toDouble()
    override fun reset(){
        cacheValue = start.toDouble()
    }

    override fun getValueCache(): Number {
        return cacheValue
    }
    override fun getValue(): Number {
        cacheValue += step.toDouble()
        if (isEnding()){
            if (repeatable){
                reset()
            }
            return end
        }
        return cacheValue
    }

    override fun setValue(value: Number) {
        cacheValue = value.toDouble()
    }

    override fun isEnding():Boolean{
        return if (step.toDouble() < 0){
            cacheValue <= end.toDouble()
        }else{
            cacheValue >= end.toDouble()
        }
    }
}