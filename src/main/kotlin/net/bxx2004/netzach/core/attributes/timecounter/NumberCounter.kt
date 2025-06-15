package net.bxx2004.netzach.core.attributes.timecounter

import net.bxx2004.netzach.core.attributes.Attribute
import net.bxx2004.netzach.core.threads.submit

class NumberCounter<T: Number>(val start:T,val end:T,val step:T,val times: Int,val repeatable: Boolean) : Attribute<T>() {
    var cacheValue = start.toDouble()
    init {
        startCounter()
    }
    override fun getValue(): T {
        return cacheValue as T
    }
    override fun setValue(value: T) {
        cacheValue = value.toDouble()
    }
    fun startCounter(){
        submit(-1,times){
            cacheValue += step.toDouble()
            if (cacheValue>end.toDouble()){
                cacheValue = end.toDouble()
                if (repeatable){
                    cacheValue = start.toDouble()
                }
            }
        }
    }

    override fun getValueCache(): T {
        return cacheValue as T
    }
}