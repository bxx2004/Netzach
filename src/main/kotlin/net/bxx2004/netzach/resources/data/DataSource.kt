package net.bxx2004.netzach.resources.data

import net.bxx2004.netzach.core.attributes.Attribute
import net.minecraft.resources.ResourceLocation

/**
 * @author 6hisea
 * @date  2025/6/13 16:53
 * @description: None
 */
interface DataSource {
    val id: ResourceLocation
    val type :DataSourceType
    fun <T>get(key :String): Attribute<T>
    fun <T>update(key :String,value:T)
    fun all():Map<String, Any>
}

enum class DataSourceType {
    CLIENT,
    SERVER,
    CUSTOM
}