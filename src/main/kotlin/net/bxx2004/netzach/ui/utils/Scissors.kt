package net.bxx2004.netzach.ui.utils

import net.bxx2004.netzach.ui.utils.Scissors.pop
import net.minecraft.client.Minecraft

import org.lwjgl.opengl.GL11
import java.lang.AutoCloseable
import java.util.*
import java.util.stream.Collectors

object Scissors {
    private val STACK = ArrayDeque<Frame>()

    /**
     * Pushes a new scissor frame onto the stack and refreshes the scissored area.
     *
     * @param x the frame's X coordinate
     * @param y the frame's Y coordinate
     * @param width the frame's width in pixels
     * @param height the frame's height in pixels
     * @return the pushed frame
     */
    fun push(x: Int, y: Int, width: Int, height: Int): Frame {
        val frame = Frame(x, y, width, height)
        STACK.push(frame)
        refreshScissors()

        return frame
    }

    /**
     * Pops the topmost scissor frame and refreshes the scissored area.
     *
     * @throws IllegalStateException if there are no scissor frames on the stack
     */
    fun pop() {
        check(!STACK.isEmpty()) { "No scissors on the stack!" }

        STACK.pop()
        refreshScissors()
    }

    fun refreshScissors() {
        val mc: Minecraft = Minecraft.getInstance()

        if (STACK.isEmpty()) {
            // Just use the full window framebuffer as a scissor
            GL11.glScissor(0, 0, mc.window.width, mc.window.height)
            return
        }

        var x = Int.Companion.MIN_VALUE
        var y = Int.Companion.MIN_VALUE
        var width = -1
        var height = -1

        for (frame in STACK) {
            if (x < frame.x) {
                x = frame.x
            }
            if (y < frame.y) {
                y = frame.y
            }
            if (width == -1 || x + width > frame.x + frame.width) {
                width = frame.width - (x - frame.x)
            }
            if (height == -1 || y + height > frame.y + frame.height) {
                height = frame.height - (y - frame.y)
            }
        }

        val windowHeight: Int = mc.window.width
        val scale: Double = mc.window.guiScale
        val scaledWidth = (width * scale).toInt()
        val scaledHeight = (height * scale).toInt()

        // Expression for Y coordinate adapted from vini2003's Spinnery (code snippet released under WTFPL)
        GL11.glScissor(
            (x * scale).toInt(),
            (windowHeight - (y * scale) - scaledHeight).toInt(),
            scaledWidth,
            scaledHeight
        )
    }

    /**
     * Internal method. Throws an [IllegalStateException] if the scissor stack is not empty.
     */
    fun checkStackIsEmpty() {
        check(STACK.isEmpty()) {
            "Unpopped scissor frames: " + STACK.stream().map<String?> { obj: Frame? -> obj.toString() }
                .collect(Collectors.joining(", "))
        }
    }

    /**
     * A single scissor frame in the stack.
     */
    class Frame(x: Int, y: Int, width: Int, height: Int) : AutoCloseable {
        val x: Int
        val y: Int
        val width: Int
        val height: Int

        init {
            require(width >= 0) { "Negative width for a stack frame" }
            require(height >= 0) { "Negative height for a stack frame" }

            this.x = x
            this.y = y
            this.width = width
            this.height = height
        }

        /**
         * Pops this frame from the stack.
         *
         * @throws IllegalStateException if:
         *  * this frame is not on the stack, or
         *  * this frame is not the topmost element on the stack
         *
         * @see pop
         */
        override fun close() {
            if (STACK.peekLast() != this) {
                check(!STACK.contains(this)) { this.toString() + " is not on top of the stack!" }
                throw IllegalStateException(this.toString() + " is not on the stack!")
            }

            pop()
        }

        override fun toString(): String {
            return "Frame{ at = (" + x + ", " + y + "), size = (" + width + ", " + height + ") }"
        }
    }
}