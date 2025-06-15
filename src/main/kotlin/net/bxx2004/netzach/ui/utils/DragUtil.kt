package net.bxx2004.netzach.ui.utils

import net.bxx2004.netzach.ui.utils.Direction.isInverted
import org.lwjgl.glfw.GLFW



/**
 * @author 6hisea
 * @date  2025/5/1 17:31
 * @description: None
 */
object DragUtil {
    fun isDecreasingKey(ch: Int, direction: String): Boolean {

        return if (direction.isInverted())
            (ch == GLFW.GLFW_KEY_RIGHT || ch == GLFW.GLFW_KEY_UP)
        else
            (ch == GLFW.GLFW_KEY_LEFT || ch == GLFW.GLFW_KEY_DOWN)
    }

    /**
     * Tests if the key should increase sliders with the specified direction.
     *
     * @param ch        the key code
     * @param direction the direction
     * @return true if the key should increase sliders with the direction, false otherwise
     * @since 2.0.0
     */
    fun isIncreasingKey(ch: Int, direction: String): Boolean {
        return if (direction.isInverted())
            (ch == GLFW.GLFW_KEY_LEFT || ch == GLFW.GLFW_KEY_DOWN)
        else
            (ch == GLFW.GLFW_KEY_RIGHT || ch == GLFW.GLFW_KEY_UP)
    }
}