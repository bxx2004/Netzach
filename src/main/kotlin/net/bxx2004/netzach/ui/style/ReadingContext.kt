package net.bxx2004.netzach.ui.style


class ReadingContext(private val value: List<Any>) {
    var index = -1


    fun boolean(d: Boolean = false): Boolean{
        index++
        if (!check()){
            return d
        }
        return value[index] as Boolean
    }
    fun string(d: String = ""):String{
        index++
        if (!check()){
            return d
        }
        return value[index].toString()
    }
    fun <T>number(d: Number): T{
        index++
        if (!check()){
            return d as T
        }
        return value[index] as T
    }
    fun check():Boolean{
        return index < value.size
    }
}