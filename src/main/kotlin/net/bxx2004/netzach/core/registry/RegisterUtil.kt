package net.bxx2004.netzach.core.registry

import net.minecraft.client.KeyMapping
import net.minecraft.core.Registry
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister
import org.apache.logging.log4j.LogManager
import thedarkcolour.kotlinforforge.neoforge.forge.MOD_BUS
import java.util.function.Supplier
import kotlin.collections.set
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties

object RegisterUtil {
    private val logger = LogManager.getLogger(RegisterUtil::class.java)

    private inline fun <reified T : Any> register(
        clazz: Class<*>,
        registryMap: MutableMap<String, DeferredRegister<T>>,
        registryKey: ResourceKey<Registry<T>>,
        registryType: KClass<T>
    ): Map<String, DeferredHolder<T, *>>? {
        val modId = getModId(clazz) ?: return null
        val kClass = clazz.kotlin

        try {
            val registry = registryMap.computeIfAbsent(modId) {
                DeferredRegister.create(registryKey, modId)
            }
            val registeredItems = mutableMapOf<String, DeferredHolder<T, *>>()

            kClass.findAnnotation<Name>()?.let { nameAnnotation ->
                if (registryType.java.isAssignableFrom(kClass.java)) {
                    val holder = registry.register(nameAnnotation.name, Supplier {
                        getSafeInstance(clazz, kClass) as T
                    })
                    registeredItems[nameAnnotation.name] = holder
                }
            }

            kClass.memberProperties.forEach { property ->
                try {
                    val name = property.findAnnotation<Name>()?.name ?: return@forEach
                    val field = clazz.getDeclaredField(property.name).apply { isAccessible = true }

                    if (registryType.java.isAssignableFrom(field.type)) {

                        val holder = registry.register(name, Supplier {
                            val instance = getSafeInstance(clazz, kClass)
                            field.get(instance) as T
                        })
                        registeredItems[name] = holder
                    }
                } catch (e: Exception) {
                    logger.error("Failed to register property ${property.name} in ${clazz.name}", e)
                }
            }

            if (registeredItems.isNotEmpty()) {
                registry.register(MOD_BUS)
                return registeredItems
            }
        } catch (e: Exception) {
            logger.error("Failed to register ${registryType.simpleName} for class ${clazz.name}", e)
        }

        return null
    }

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

    private fun getModId(clazz: Class<*>): String? {
        return clazz.kotlin.findAnnotation<RegisterContainer>()?.modId?.takeIf { it.isNotBlank() }
            ?: run {
                logger.error("Invalid or missing @RegisterContainer annotation in class ${clazz.name}")
                null
            }
    }

    private fun registerKey(clazz: Class<*>){
        clazz.kotlin.memberProperties.forEach { property ->
            try {
                val field = clazz.getDeclaredField(property.name).apply { isAccessible = true }

                if (field.type == KeyMapping::class.java) {
                    MOD_BUS.addListener<RegisterKeyMappingsEvent> {
                        it.register(field.get(getSafeInstance(clazz,clazz.kotlin)) as KeyMapping)
                    }
                }
            } catch (e: Exception) {
                logger.error("Failed to register property ${property.name} in ${clazz.name}", e)
            }
        }
    }

    fun registerItem(clazz: Class<*>): Map<String, DeferredHolder<Item, *>>? {
        return register(clazz, RegisterFactory.items, Registries.ITEM, Item::class)
    }

    fun registerBlock(clazz: Class<*>): Map<String, DeferredHolder<Block, *>>? {
        return register(clazz, RegisterFactory.blocks, Registries.BLOCK, Block::class)
    }

    fun registerKeyMapping(clazz: Class<*>): Map<String, DeferredHolder<Block, *>>? {
        return register(clazz, RegisterFactory.blocks, Registries.BLOCK, Block::class)
    }

    fun registerCreativeModeTab(clazz: Class<*>): Map<String, DeferredHolder<CreativeModeTab, *>>? {
        return register(clazz, RegisterFactory.creativeModeTabs, Registries.CREATIVE_MODE_TAB, CreativeModeTab::class)
    }
}