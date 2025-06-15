package net.bxx2004.netzach.core.attributes.sequence

import java.io.Serializable
import kotlin.Int

open class IntSequence(val start: Int, val end: Int, val step: Double, repeatable: Boolean=false) : Sequence<Int>(repeatable),Serializable{
    var cacheValue = start.toDouble()
    override fun reset(){
        cacheValue = start.toDouble()
    }

    override fun getValueCache(): Int {
        return cacheValue.toInt()
    }
    override fun getValue(): Int {
        cacheValue += step
        if (isEnding()){
            if (repeatable){
                reset()
            }
            return end
        }
        return cacheValue.toInt()
    }

    override fun setValue(value: Int) {
        cacheValue = value.toDouble()
    }

    override fun isEnding():Boolean{
        return if (step < 0){
            cacheValue <= end
        }else{
            cacheValue >= end
        }
    }
}