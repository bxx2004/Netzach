package net.bxx2004.netzach.ui.components.form

import net.bxx2004.netzach.core.attributes.AttributeReader
import net.bxx2004.netzach.core.attributes.ref
import net.bxx2004.netzach.ui.callback.*
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import org.lwjgl.glfw.GLFW

class Input : TextArea() {
    // 覆盖父类的多行相关属性
    override var lineHeight = ref(16) // 固定行高
    override var textWrap = ref(false) // 禁用自动换行
    var maxLength = ref(Int.MAX_VALUE) // 最大输入长度

    init {
        // 初始化时设置一些单行输入特有的属性
        padding.setValue(6) // 调整内边距
    }

    override fun charTyped(chr: Char, modifiers: Int): Boolean {
        // 过滤换行符
        if (chr == '\n' || chr == '\r') return false

        // 检查长度限制
        if (text.getValue().length >= maxLength.getValue()) return false

        return super.charTyped(chr, modifiers)
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        // 拦截回车键
        if (keyCode == GLFW.GLFW_KEY_ENTER) {
            emitter(EnterPressCallback(id.v))
            return true
        }

        // 拦截上下箭头键
        if (keyCode == GLFW.GLFW_KEY_UP || keyCode == GLFW.GLFW_KEY_DOWN) {
            return true
        }

        return super.keyPressed(keyCode, scanCode, modifiers)
    }

    override fun getWrappedLines(): List<String> {
        // 单行文本框不需要换行，直接返回整个文本作为一行
        return listOf(text.getValue())
    }

    override fun render(context: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float, reader: AttributeReader) {
        // 保存原始值
        val originalText = text.getValue()

        // 如果正在编辑，临时移除换行符
        if (isFocus()) {
            text.setValue(originalText.replace("\n", ""))
        }

        super.render(context, mouseX, mouseY, delta, reader)

        // 恢复原始值
        if (isFocus()) {
            text.setValue(originalText)
        }
    }

    // 简化版的光标移动逻辑，移除多行相关功能
    override fun moveCursorUp() = Unit // 禁用上移
    override fun moveCursorDown() = Unit // 禁用下移

    override fun getLineStart(line: Int): Int = 0 // 总是返回文本开头
    override fun getLineEnd(line: Int): Int = text.getValue().length // 总是返回文本结尾

    // 添加一个便捷方法设置提示文本
    fun setPlaceholder(text: String): Input {
        placeholder.setValue(Component.literal(text))
        return this
    }

    // 添加一个便捷方法设置最大长度
    fun setMaxLength(length: Int): Input {
        maxLength.setValue(length)
        return this
    }
}