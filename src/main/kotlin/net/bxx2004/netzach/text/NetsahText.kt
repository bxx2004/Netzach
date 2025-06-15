package net.bxx2004.netzach.text

import net.minecraft.network.chat.Component

/**
 * @author 6hisea
 * @date  2025/6/10 10:13
 * @description: None
 */
object NetsahText {
    fun String.colored(replaceSymbol: Char = '&'): Component{
        return Component.literal(
            replace(replaceSymbol.toString(),"§")
        )
    }
    fun Component.uncolored(): String{
        return this.string
    }
}