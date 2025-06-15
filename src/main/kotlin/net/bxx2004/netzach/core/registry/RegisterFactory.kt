package net.bxx2004.netzach.core.registry

import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.neoforged.neoforge.registries.DeferredRegister

/**
 * @author 6hisea
 * @date  2025/6/14 17:54
 * @description: None
 */
object RegisterFactory {
    val items = hashMapOf<String, DeferredRegister<Item>>()
    val blocks = hashMapOf<String, DeferredRegister<Block>>()
    val creativeModeTabs = hashMapOf<String, DeferredRegister<CreativeModeTab>>()
}