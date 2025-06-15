package net.bxx2004.netzach.ui.callback

/**
 * @author 6hisea
 * @date  2025/6/12 22:02
 * @description: None
 */
data class MouseClickCallback(
    override val componentId: String,
    val mouseX: Double,
    val mouseY: Double,
    val button: Int
) : ComponentActionCallBack("mouse-click")

data class CharTypedCallback(
    override val componentId: String,
    val chr: Char,
    val modifiers: Int,
) : ComponentActionCallBack("char-typed")

data class FocusCallback(
    override val componentId: String
) : ComponentActionCallBack("focus")

data class TickedCallback(
    override val componentId: String
) : ComponentActionCallBack("tick")

data class MouseDragCallback(
    override val componentId: String,
    val mouseX: Double,
    val mouseY: Double,
    val button: Int,
    val deltaX:Double,
    val deltaY:Double
) : ComponentActionCallBack("mouse-drag")

data class MouseMoveCallback(
    override val componentId: String,
    val mouseX: Double,
    val mouseY: Double
) : ComponentActionCallBack("mouse-move")


data class MouseReleaseCallback(
    override val componentId: String,
    val mouseX: Double,
    val mouseY: Double,
    val button: Int
) : ComponentActionCallBack("mouse-release")

data class MouseScrollCallback(
    override val componentId: String,
    val mouseX: Double,
    val mouseY: Double,
    val horizontalAmount: Double,
    val verticalAmount:Double
) : ComponentActionCallBack("mouse-scroll")

data class KeyPressCallback(
    override val componentId: String,
    val keyCode: String,
    val scanCode: String,
    val modifiers: Int
) : ComponentActionCallBack("key-press")

data class KeyReleaseCallback(
    override val componentId: String,
    val keyCode: String,
    val scanCode: String,
    val modifiers: Int
) : ComponentActionCallBack("key-release")

data class OpenCallback(
    override val componentId: String
) : ComponentActionCallBack("open")

data class CloseCallback(
    override val componentId: String
) : ComponentActionCallBack("close")

data class LoadCallback(
    override val componentId: String
) : ComponentActionCallBack("load")

data class UnloadCallback(
    override val componentId: String
) : ComponentActionCallBack("unload")

data class StateChangedCallback(
    override val componentId: String,
    val state:Boolean
) : ComponentActionCallBack("state-changed")