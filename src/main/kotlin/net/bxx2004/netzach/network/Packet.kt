package net.bxx2004.netzach.network

import net.minecraft.resources.ResourceLocation

/**
 * @author 6hisea
 * @date  2025/6/11 17:13
 * @description: None
 */

abstract class Packet() {
    abstract val id: ResourceLocation
    val timestamp:Long = System.currentTimeMillis()
    inline fun <reified T>except(func:(packet:T) -> Unit):Packet{
        if (this is T){
            func(this)
        }
        return this
    }
    inline fun <reified T>exceptFilter(id: ResourceLocation, func:(packet:T) -> Unit):Packet{
        if (this is T && this.id == id){
            func(this)
        }
        return this
    }
}
fun <T: Packet>T.wrapper(): NetzachPayload<T>{
    return NetzachPayload<T>().write(this)
}