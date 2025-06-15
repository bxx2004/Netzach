package net.bxx2004.netzach.ui.utils

/**
 * @author 6hisea
 * @date  2025/5/1 17:29
 * @description: None
 */
object Direction {
    const val LEFT = "LEFT"
    const val RIGHT = "RIGHT"
    const val UP = "UP"
    const val DOWN = "DOWN"
    const val CENTER = "CENTER"
    const val LEFT_TO_RIGHT = "LEFT_TO_RIGHT"
    const val RIGHT_TO_LEFT ="RIGHT_TO_LEFT"
    const val TOP_TO_BOTTOM = "TOP_TO_BOTTOM"
    const val BOTTOM_TO_TOP = "BOTTOM_TO_TOP"
    fun String.isInverted():Boolean{
        return this == "DOWN" || this == "LEFT"
    }
}