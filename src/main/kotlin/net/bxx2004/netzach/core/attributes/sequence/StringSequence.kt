package net.bxx2004.netzach.core.attributes.sequence

import net.bxx2004.netzach.core.threads.submit
import java.io.Serializable

class StringSequence(val step:Int,val content :List<String>,repeatable: Boolean = false) : Sequence<String>(repeatable), Serializable {
    var index = 0
    var ctime = -1
    override fun getValue(): String {
        if (ctime == -1){
            submit(-1,50){task->
                ctime++
                if (ctime >= step.toDouble()){
                    ctime = -1
                    index++
                    task.cancel()
                }
            }
        }
        if (isEnding()){
            if (repeatable){
                reset()
            }
            return content.last()
        }
        val r = content[index]
        return r
    }

    override fun getValueCache(): String {
        return content[index]
    }
    override fun setValue(value: String) {
        index = content.indexOf(value)
    }


    override fun isEnding(): Boolean {
        return index >= content.size
    }

    override fun reset() {
        index = 0
    }
}