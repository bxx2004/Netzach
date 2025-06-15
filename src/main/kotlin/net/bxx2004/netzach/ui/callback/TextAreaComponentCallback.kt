package net.bxx2004.netzach.ui.callback

/**
 * @author 6hisea
 * @date  2025/6/13 21:58
 * @description: None
 */
data class TextChangeCallback(
    override val componentId: String,
    val text: String
) : ComponentActionCallBack("text-change")
data class EnterPressCallback(
    override val componentId: String
) : ComponentActionCallBack("enter-press")