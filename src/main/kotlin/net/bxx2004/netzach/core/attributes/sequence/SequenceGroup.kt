package net.bxx2004.netzach.core.attributes.sequence
import net.bxx2004.netzach.core.attributes.Attribute
import net.bxx2004.netzach.core.threads.submit
import java.io.Serializable
import java.util.concurrent.CopyOnWriteArrayList

class SequenceGroup<T: Sequence<T>>(val repeat:Boolean = false) : Attribute<T>(), Serializable{
    private var cache_time = -1
    private var cache_index = -1
    private val members = CopyOnWriteArrayList<Triple<String, Int, T>>()
    private lateinit var currentElement: Triple<String, Int, T>
    fun add(id:String,spacing:Int,member:T):Boolean{
        if (members.map { it.first }.contains(id)){
            return false
        }
        members.add(Triple(id, spacing, member))
        return true
    }
    fun remove(id:String):Boolean{
        if (!members.map { it.first }.contains(id)){
            return false
        }
        return members.removeIf { it.first == id }
    }

    override fun setValue(value: T) {
        currentElement.third.setValue(value)
    }

    override fun getValueCache(): T {
        return currentElement.third.getValueCache()
    }

    override fun getValue(): T {
        if (cache_time == -1){
            cache_index++
            if (cache_index >= members.size){
                if (repeat) {
                    members.forEach {
                        it.third.reset()
                    }
                    cache_index = 0
                }else{
                    return currentElement.third
                }
            }
            currentElement = members[cache_index]
            cache_time = 0
            submit(-1,50){task->
                cache_time++
                if (cache_time >= currentElement.second && currentElement.third.isEnding()){
                    cache_time = -1
                    task.cancel()
                }
            }
        }
        return currentElement.third
    }
}