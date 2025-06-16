package net.bxx2004.netzach.core.registry.helper

import net.bxx2004.netzach.core.registry.Category
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.memberProperties

/**
 * @author 6hisea
 * @date  2025/6/16 13:49
 * @description: None
 */
object CreativeModeTabBuilderHelper {
    private fun getSafeInstance(clazz: Class<*>, kClass: KClass<*>): Any {
        return try {
            kClass.objectInstance ?: clazz.getDeclaredField("INSTANCE").get(null)
        } catch (e: NoSuchFieldException) {
            try {
                clazz.newInstance()
            } catch (e: InstantiationException) {
                throw IllegalStateException("Failed to get instance for ${clazz.name}")
            }
        }
    }
    fun CreativeModeTab.Builder.displayItems(vararg clazz:Class<*>) {
        displayItems { a,b ->
            clazz.forEach { cls ->
                val kClass = cls.kotlin
                kClass.memberProperties.forEach { property ->
                    try {
                        val instance = getSafeInstance(cls, kClass)

                        if (!property.hasAnnotation<Category>()){
                            val field = cls.getDeclaredField(property.name).apply { isAccessible = true }
                            if (Item::class.java.isAssignableFrom(field.type)) {
                                b.accept(field.get(instance) as Item)
                            }
                        }

                        if (instance is Item){
                            b.accept(instance)
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

        }

    }
}