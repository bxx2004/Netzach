package net.bxx2004.netzach.resources.data

import net.bxx2004.netzach.core.attributes.Attribute
import net.minecraft.resources.ResourceLocation

/**
 * @author 6hisea
 * @date  2025/6/13 16:52
 * @description: None
 */
interface DataProvider {
    fun addSource(level:Int, dataSource: DataSource)
    fun removeSource(resourceLocation: ResourceLocation)
    fun <T>getData(key:String,default:T): Attribute<T>
    fun <T>setData(key:String,data:T)
}