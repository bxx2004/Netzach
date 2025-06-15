package net.bxx2004.netzach.core.attributes.sequence

import java.io.Serializable
import kotlin.Double

open class DoubleSequence(val start: Double, val end: Double, val step: Double, repeatable: Boolean=false) : Sequence<Double>(repeatable),Serializable{
    var cacheValue = start
    override fun reset(){
        cacheValue = start
    }

    override fun getValueCache(): Double {
        return cacheValue
    }
    override fun getValue(): Double {
        cacheValue += step
        if (isEnding()){
            if (repeatable){
                reset()
            }
            return end
        }
        return cacheValue
    }

    override fun setValue(value: Double) {
        cacheValue = value
    }

    override fun isEnding():Boolean{
        return if (step < 0){
            cacheValue <= end
        }else{
            cacheValue >= end
        }
    }
}