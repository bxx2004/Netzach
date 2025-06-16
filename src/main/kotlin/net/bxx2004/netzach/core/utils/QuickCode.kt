package net.bxx2004.netzach.core.utils

/**
 * @author 6hisea
 * @date  2025/6/11 15:57
 * @description: None
 */


import net.bxx2004.netzach.Netzach
import net.minecraft.client.Minecraft
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.ItemStack
import net.neoforged.fml.ModList
import java.io.File
import java.util.concurrent.CompletableFuture


var mouseX = -100
var mouseY = -100
fun player() = Minecraft.getInstance().player
var lastScreenHandler: AbstractContainerMenu? = null
fun scaleFactor() = client().window.calculateScale(client().options.guiScale().get(),client().isEnforceUnicode)
fun windowSize() = arrayOf(Minecraft.getInstance().window.guiScaledWidth, Minecraft.getInstance().window.guiScaledHeight)
fun client() = Minecraft.getInstance()
fun isRightHand(itemStack: ItemStack):Boolean{
    return ItemStack.isSameItem(itemStack, player()!!.mainHandItem)
}
fun <T>future():CompletableFuture<T> = CompletableFuture<T>()
fun rl(a:String,b: String): ResourceLocation {
    return ResourceLocation.fromNamespaceAndPath(a, b)
}
fun rl(a:String): ResourceLocation {
    return ResourceLocation.parse(a)
}
fun nrl(a:String): ResourceLocation {
    return ResourceLocation.fromNamespaceAndPath(modId,a)
}
fun ResourceLocation.isEmpty(): Boolean{
    return path == "netzachempty"
}
fun emptyResourceLocation(): ResourceLocation {
    return ResourceLocation.fromNamespaceAndPath(modId,"netzachempty")
}
fun File.toSums() = length() + lastModified()
val modId = Netzach.ID
var modFile = ModList.get().getModFileById(modId).file
fun modData(child: String): File{
    val dir = ModList.get().getModFileById(modId).file.filePath.parent.resolve("client_data")
    val f =  File(dir.resolve(child).toUri())
    if (!f.parentFile.exists()){
        f.mkdirs()
    }
    if (!f.exists()){
        f.createNewFile()
    }
    return f
}
fun Any?.inferType(): Any? {
    if (this !is String) return this
    toIntOrNull()?.let { return it }
    toLongOrNull()?.let { return it }
    toDoubleOrNull()?.let { return it }
    toBooleanStrictOrNull()?.let { return it }
    return this
}