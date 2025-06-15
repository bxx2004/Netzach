package net.bxx2004.netzach.ui.components.form

import net.bxx2004.netzach.core.attributes.AttributeReader
import net.bxx2004.netzach.core.attributes.ref
import net.bxx2004.netzach.core.utils.client
import net.bxx2004.netzach.core.utils.nrl
import net.bxx2004.netzach.ui.callback.*
import net.bxx2004.netzach.ui.components.IComponent
import net.bxx2004.netzach.ui.utils.nineSlice
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import org.lwjgl.glfw.GLFW
import java.util.*
import kotlin.math.abs

class TextArea : IComponent() {
    // 文本属性
    var text = ref("")
    var placeholder = ref<Component?>(null)
    var textColor = ref(0xFFFFFFFF.toInt())
    var placeholderColor = ref(0xFF888888.toInt())

    // 外观属性 - 使用九宫格纹理
    var background = ref(nrl("textures/ui/text_area/background.png"))
    var borderWidth = ref(4) // 九宫格边框大小
    var padding = ref(4)
    var focusBorder = ref(nrl("textures/ui/text_area/focus_border.png"))

    // 文本布局属性
    var lineHeight = ref(12)
    var textWrap = ref(true)

    var isEditable = ref(true)
    var cursorPosition = ref(0)
    var selectionStart = ref(-1)
    var cursorBlink = ref(0)
    var scrollOffset = ref(0)

    private val cursorTimer = Timer()
    private var cursorX = 0
    private var cursorY = 0
    private var cursorLine = 0

    init {
        // 光标闪烁定时器
        cursorTimer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                cursorBlink.setValue((cursorBlink.getValue() + 1) % 20)
            }
        }, 0, 50)

        // 处理文本输入回调
        callback<CharTypedCallback> {
            if (isFocus()&& isEditable.getValue()) {
                if (hasSelection()) {
                    deleteSelection()
                }
                insertText(chr.toString())
                true
            } else {
                false
            }
        }

        // 处理键盘输入回调
        callback<KeyPressCallback> {
            if (!isFocus() || !isEditable.getValue()) return@callback

            when (keyCode.toInt()) {
                GLFW.GLFW_KEY_ENTER -> {
                    if (hasSelection()) deleteSelection()
                    insertText("\n")
                    emitter(EnterPressCallback(id.v))
                    true
                }
                GLFW.GLFW_KEY_BACKSPACE -> {
                    if (hasSelection()) {
                        deleteSelection()
                    } else if (cursorPosition.getValue() > 0) {
                        text.setValue(text.getValue().removeRange(cursorPosition.getValue() - 1, cursorPosition.getValue()))
                        cursorPosition.setValue(cursorPosition.getValue() - 1)
                        emitter(TextChangeCallback(id.v,text.v))
                    }
                    true
                }
                GLFW.GLFW_KEY_DELETE -> {
                    if (hasSelection()) {
                        deleteSelection()
                    } else if (cursorPosition.getValue() < text.getValue().length) {
                        text.setValue(text.getValue().removeRange(cursorPosition.getValue(), cursorPosition.getValue() + 1))
                        emitter(TextChangeCallback(id.v,text.v))
                    }
                    true
                }
                GLFW.GLFW_KEY_LEFT -> {
                    if (Screen.hasControlDown()) {
                        moveCursorToPrevWord()
                    } else {
                        cursorPosition.setValue((cursorPosition.getValue() - 1).coerceAtLeast(0))
                    }
                    if (!Screen.hasShiftDown()) {
                        selectionStart.setValue(-1)
                    }
                    true
                }
                GLFW.GLFW_KEY_RIGHT -> {
                    if (Screen.hasControlDown()) {
                        moveCursorToNextWord()
                    } else {
                        cursorPosition.setValue((cursorPosition.getValue() + 1).coerceAtMost(text.getValue().length))
                    }
                    if (!Screen.hasShiftDown()) {
                        selectionStart.setValue(-1)
                    }
                    true
                }
                GLFW.GLFW_KEY_UP -> {
                    moveCursorUp()
                    if (!Screen.hasShiftDown()) {
                        selectionStart.setValue(-1)
                    }
                    true
                }
                GLFW.GLFW_KEY_DOWN -> {
                    moveCursorDown()
                    if (!Screen.hasShiftDown()) {
                        selectionStart.setValue(-1)
                    }
                    true
                }
                GLFW.GLFW_KEY_HOME -> {
                    cursorPosition.setValue(getLineStart(cursorLine))
                    if (!Screen.hasShiftDown()) {
                        selectionStart.setValue(-1)
                    }
                    true
                }
                GLFW.GLFW_KEY_END -> {
                    cursorPosition.setValue(getLineEnd(cursorLine))
                    if (!Screen.hasShiftDown()) {
                        selectionStart.setValue(-1)
                    }
                    true
                }
                GLFW.GLFW_KEY_A -> {
                    if (Screen.hasControlDown()) {
                        selectionStart.setValue(0)
                        cursorPosition.setValue(text.getValue().length)
                        true
                    } else {
                        false
                    }
                }
                GLFW.GLFW_KEY_C -> {
                    if (Screen.hasControlDown() && hasSelection()) {
                        val (start, end) = getSelectionRange()
                        client().keyboardHandler.clipboard = text.getValue().substring(start, end)
                        true
                    } else {
                        false
                    }
                }
                GLFW.GLFW_KEY_V -> {
                    if (Screen.hasControlDown() && isEditable.getValue()) {
                        val clipboard = client().keyboardHandler.clipboard ?: return@callback
                        if (hasSelection()) deleteSelection()
                        insertText(clipboard)
                        true
                    } else {
                        false
                    }
                }
                GLFW.GLFW_KEY_X -> {
                    if (Screen.hasControlDown() && hasSelection() && isEditable.getValue()) {
                        val (start, end) = getSelectionRange()
                        client().keyboardHandler.clipboard = text.getValue().substring(start, end)
                        deleteSelection()
                        true
                    } else {
                        false
                    }
                }
                else -> false
            }
        }

        // 鼠标点击回调
        callback<MouseClickCallback> {
            if (button == 0) { // 左键点击
                updateCursorPosition(mouseX.toInt(), mouseY.toInt())
                selectionStart.setValue(cursorPosition.getValue())
                true
            } else {
                false
            }
        }

        // 鼠标拖动回调
        callback<MouseDragCallback> {
            if (button == 0) { // 左键拖动
                updateCursorPosition(mouseX.toInt(), mouseY.toInt())
                true
            } else {
                false
            }
        }

        // 鼠标滚轮回调
        callback<MouseScrollCallback> {
            scrollOffset.setValue((scrollOffset.getValue() + verticalAmount.toInt()).coerceIn(0, getMaxScroll()))
            true
        }
    }

    override fun render(context: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float, reader: AttributeReader) {
        val x = reader.x
        val y = reader.y
        val width = reader.width
        val height = reader.height

        // 使用九宫格绘制背景
        context.nineSlice(
            background.getValue(),
            x, y,
            width, height,
            borderWidth.getValue(),
            16, 16,
            reader.z
        )

        // 绘制边框（使用九宫格或简单边框）
        if (borderWidth.getValue() > 0) {
            if (isFocus()){
                val borderTexture = focusBorder.v

                context.nineSlice(
                    borderTexture,
                    x, y,
                    width, height,
                    borderWidth.getValue(),
                    16, 16,
                    reader.z + 1
                )
            }
        }

        // 计算可见行范围
        val visibleLines = (height - 2 * padding.getValue()) / lineHeight.getValue()
        val firstVisibleLine = scrollOffset.getValue()
        val lastVisibleLine = (firstVisibleLine + visibleLines).coerceAtMost(getLineCount())

        // 启用裁剪
        context.enableScissor(
            x + padding.getValue(), y + padding.getValue(),
            x + width - padding.getValue(), y + height - padding.getValue()
        )

        // 绘制文本或占位符
        if (text.getValue().isEmpty() && placeholder.getValue() != null) {
            // 绘制占位符文本
            context.drawString(
                client().font,
                placeholder.getValue(),
                x + padding.getValue(),
                y + padding.getValue() - scrollOffset.getValue() * lineHeight.getValue(),
                placeholderColor.getValue(),
                false
            )
        } else {
            // 绘制文本行
            val lines = getWrappedLines()
            val font = client().font
            for (i in firstVisibleLine until lastVisibleLine) {
                if (i >= lines.size) break

                context.drawString(
                    font,
                    lines[i],
                    x + padding.getValue(),
                    y + padding.getValue() + (i - firstVisibleLine) * lineHeight.getValue(),
                    textColor.getValue(),
                    false
                )
            }

            // 更新光标位置
            updateCursorVisualPosition()

            // 绘制光标
            if (isFocus() && cursorBlink.getValue() < 10) {
                context.fill(
                    x + padding.getValue() + cursorX,
                    y + padding.getValue() + cursorY - scrollOffset.getValue() * lineHeight.getValue(),
                    x + padding.getValue() + cursorX + 1,
                    y + padding.getValue() + cursorY + lineHeight.getValue() - scrollOffset.getValue() * lineHeight.getValue(),
                    0xFFFFFFFF.toInt()
                )
            }

            // 绘制选择区域
            if (hasSelection()) {
                drawSelection(context, x, y)
            }
        }

        // 禁用裁剪
        context.disableScissor()
    }

    private fun drawSelection(context: GuiGraphics, x: Int, y: Int) {
        val (start, end) = getSelectionRange()
        val font = client().font
        val lines = getWrappedLines()
        var currentPos = 0
        var drawnY = y + padding.getValue() - scrollOffset.getValue() * lineHeight.getValue()

        for (line in lines.indices) {
            val lineText = lines[line]
            val lineStart = currentPos
            val lineEnd = currentPos + lineText.length

            if (lineEnd < start || lineStart > end) {
                currentPos += lineText.length
                drawnY += lineHeight.getValue()
                continue
            }

            val selStart = (start - lineStart).coerceAtLeast(0)
            val selEnd = (end - lineStart).coerceAtMost(lineText.length)

            if (selStart < selEnd) {
                val beforeSel = lineText.substring(0, selStart)
                val selText = lineText.substring(selStart, selEnd)
                val selX = x + padding.getValue() + font.width(beforeSel)

                context.fill(
                    selX,
                    drawnY,
                    selX + font.width(selText),
                    drawnY + lineHeight.getValue(),
                    0x774477AA.toInt()
                )
            }

            currentPos += lineText.length
            drawnY += lineHeight.getValue()
        }
    }

    private fun updateCursorVisualPosition() {
        val lines = getWrappedLines()
        var currentPos = 0
        cursorLine = 0
        var found = false

        // 查找光标所在行
        for (line in lines.indices) {
            val lineText = lines[line]
            if (cursorPosition.getValue() >= currentPos &&
                cursorPosition.getValue() <= currentPos + lineText.length) {
                cursorLine = line
                val posInLine = cursorPosition.getValue() - currentPos
                cursorX = client().font.width(lineText.substring(0, posInLine))
                cursorY = line * lineHeight.getValue()
                found = true
                break
            }
            currentPos += lineText.length
        }

        // 如果光标在最后一行之后
        if (!found && lines.isNotEmpty()) {
            cursorLine = lines.size - 1
            val lastLine = lines.last()
            cursorX = client().font.width(lastLine)
            cursorY = cursorLine * lineHeight.getValue()
        }

        // 确保光标可见
        val visibleHeight = height.getValue() - 2 * padding.getValue()
        val cursorScreenY = cursorY - scrollOffset.getValue() * lineHeight.getValue()

        if (cursorScreenY < 0) {
            scrollOffset.setValue(scrollOffset.getValue() + cursorScreenY / lineHeight.getValue() - 1)
        } else if (cursorScreenY + lineHeight.getValue() > visibleHeight) {
            scrollOffset.setValue(scrollOffset.getValue() + (cursorScreenY + lineHeight.getValue() - visibleHeight) / lineHeight.getValue() + 1)
        }
    }

    private fun updateCursorPosition(mouseX: Int, mouseY: Int) {
        val relativeX = mouseX - x.getValue() - padding.getValue()
        val relativeY = mouseY - y.getValue() - padding.getValue()
        val clickedLine = (relativeY / lineHeight.getValue() + scrollOffset.getValue()).coerceIn(0, getLineCount() - 1)
        val lines = getWrappedLines()

        if (clickedLine >= lines.size) {
            cursorPosition.setValue(text.getValue().length)
            return
        }

        val lineText = lines[clickedLine]
        var charPos = 0
        var currentWidth = 0
        var bestPos = 0
        var bestDiff = Int.MAX_VALUE
        val font = client().font

        // 查找最接近点击位置的字符位置
        for (i in lineText.indices) {
            val charWidth = font.width(lineText[i].toString())
            val diff = abs(currentWidth - relativeX)
            if (diff < bestDiff) {
                bestDiff = diff
                bestPos = i
            }
            currentWidth += charWidth
        }

        // 检查是否点击在行末之后
        if (relativeX > currentWidth) {
            bestPos = lineText.length
        }

        // 计算实际文本位置
        var textPos = 0
        for (i in 0 until clickedLine) {
            textPos += lines[i].length
        }
        textPos += bestPos

        cursorPosition.setValue(textPos.coerceIn(0, text.getValue().length))
        cursorLine = clickedLine
    }

    private fun getWrappedLines(): List<String> {
        if (!textWrap.getValue()) {
            return text.getValue().lines().flatMap { line ->
                if (line.isEmpty()) listOf("") else line.split("\n")
            }
        }

        val maxWidth = width.getValue() - 2 * padding.getValue()
        val font = client().font
        val lines = mutableListOf<String>()

        for (originalLine in text.getValue().lines()) {
            if (originalLine.isEmpty()) {
                lines.add("")
                continue
            }

            var currentLineStart = 0
            var lastSpace = -1
            var currentWidth = 0

            for (i in originalLine.indices) {
                val char = originalLine[i]
                val charWidth = font.width(char.toString())

                if (char.isWhitespace()) {
                    lastSpace = i
                }

                if (currentWidth + charWidth > maxWidth && i > currentLineStart) {
                    // 如果有空格，优先在空格处换行
                    val breakPos = if (lastSpace > currentLineStart) lastSpace else i
                    lines.add(originalLine.substring(currentLineStart, breakPos))
                    currentLineStart = breakPos + if (lastSpace > currentLineStart) 1 else 0
                    currentWidth = font.width(originalLine.substring(currentLineStart, i + 1))
                    lastSpace = -1
                } else {
                    currentWidth += charWidth
                }
            }

            if (currentLineStart < originalLine.length) {
                lines.add(originalLine.substring(currentLineStart))
            }
        }

        return lines
    }

    private fun getLineCount(): Int {
        return getWrappedLines().size
    }

    private fun getMaxScroll(): Int {
        return (getLineCount() - (height.getValue() - 2 * padding.getValue()) / lineHeight.getValue()).coerceAtLeast(0)
    }

    private fun getLineStart(line: Int): Int {
        val lines = text.getValue().lines()
        if (line <= 0) return 0
        if (line >= lines.size) return text.getValue().length

        var pos = 0
        for (i in 0 until line) {
            pos += lines[i].length + 1 // +1 for newline
        }
        return pos
    }

    private fun getLineEnd(line: Int): Int {
        val lines = text.getValue().lines()
        if (line < 0) return 0
        if (line >= lines.size) return text.getValue().length

        var pos = 0
        for (i in 0..line) {
            pos += lines[i].length + if (i < line) 1 else 0 // +1 for newline except last line
        }
        return pos
    }

    private fun moveCursorUp() {
        val lines = getWrappedLines()
        if (cursorLine > 0) {
            cursorLine--
            val targetX = cursorX
            val targetLine = lines[cursorLine]

            var charPos = 0
            var currentWidth = 0
            var bestPos = 0
            var bestDiff = Int.MAX_VALUE

            for (i in targetLine.indices) {
                val charWidth = client().font.width(targetLine[i].toString())
                val diff = abs(currentWidth - targetX)
                if (diff < bestDiff) {
                    bestDiff = diff
                    bestPos = i
                }
                currentWidth += charWidth
            }

            // 更新光标位置
            cursorPosition.setValue(getCharPositionFromWrapped(cursorLine, bestPos))
            updateCursorVisualPosition()
        }
    }

    private fun moveCursorDown() {
        val lines = getWrappedLines()
        if (cursorLine < lines.size - 1) {
            cursorLine++
            val targetX = cursorX
            val targetLine = lines[cursorLine]

            var charPos = 0
            var currentWidth = 0
            var bestPos = 0
            var bestDiff = Int.MAX_VALUE

            for (i in targetLine.indices) {
                val charWidth = client().font.width(targetLine[i].toString())
                val diff = abs(currentWidth - targetX)
                if (diff < bestDiff) {
                    bestDiff = diff
                    bestPos = i
                }
                currentWidth += charWidth
            }

            // 更新光标位置
            cursorPosition.setValue(getCharPositionFromWrapped(cursorLine, bestPos))
            updateCursorVisualPosition()
        }
    }

    private fun getCharPositionFromWrapped(line: Int, charInLine: Int): Int {
        val unwrappedLines = text.getValue().lines()
        val wrappedLines = getWrappedLines()

        var unwrappedLine = 0
        var wrappedCount = 0
        var totalChars = 0

        while (unwrappedLine < unwrappedLines.size) {
            val lineText = unwrappedLines[unwrappedLine]
            val lineWrappedCount = (client().font.width(lineText) / (width.getValue() - 2 * padding.getValue())) + 1

            if (wrappedCount + lineWrappedCount > line) {
                // 找到对应的原始行
                val wrappedLineInGroup = line - wrappedCount
                val charsPerWrappedLine = lineText.length / lineWrappedCount

                val startChar = wrappedLineInGroup * charsPerWrappedLine
                val endChar = if (wrappedLineInGroup == lineWrappedCount - 1) {
                    lineText.length
                } else {
                    (wrappedLineInGroup + 1) * charsPerWrappedLine
                }

                val actualChar = (startChar + charInLine).coerceAtMost(endChar)
                return totalChars + actualChar + unwrappedLine // +unwrappedLine for newlines
            }

            wrappedCount += lineWrappedCount
            totalChars += lineText.length
            unwrappedLine++
        }

        return text.getValue().length
    }

    private fun moveCursorToPrevWord() {
        if (cursorPosition.getValue() <= 0) return

        val text = text.getValue()
        var pos = cursorPosition.getValue() - 1

        // 跳过空白字符
        while (pos > 0 && text[pos].isWhitespace()) {
            pos--
        }

        // 找到单词开头
        while (pos > 0 && !text[pos - 1].isWhitespace()) {
            pos--
        }

        cursorPosition.setValue(pos)
        updateCursorVisualPosition()
    }

    private fun moveCursorToNextWord() {
        if (cursorPosition.getValue() >= text.getValue().length) return

        val text = text.getValue()
        var pos = cursorPosition.getValue()
        val length = text.length

        // 跳过当前单词
        while (pos < length && !text[pos].isWhitespace()) {
            pos++
        }

        // 跳过空白字符
        while (pos < length && text[pos].isWhitespace()) {
            pos++
        }

        cursorPosition.setValue(pos)
        updateCursorVisualPosition()
    }

    private fun insertText(str: String) {
        val newText = StringBuilder(text.getValue())
        newText.insert(cursorPosition.getValue(), str)
        text.setValue(newText.toString())
        cursorPosition.setValue(cursorPosition.getValue() + str.length)
        emitter(TextChangeCallback(id.v,text.v))
        updateCursorVisualPosition()
    }

    private fun hasSelection(): Boolean {
        return selectionStart.getValue() != -1 && selectionStart.getValue() != cursorPosition.getValue()
    }

    private fun getSelectionRange(): Pair<Int, Int> {
        if (!hasSelection()) return Pair(cursorPosition.getValue(), cursorPosition.getValue())

        return if (selectionStart.getValue() < cursorPosition.getValue()) {
            Pair(selectionStart.getValue(), cursorPosition.getValue())
        } else {
            Pair(cursorPosition.getValue(), selectionStart.getValue())
        }
    }

    private fun deleteSelection() {
        val (start, end) = getSelectionRange()
        text.setValue(text.getValue().removeRange(start, end))
        cursorPosition.setValue(start)
        selectionStart.setValue(-1)
        emitter(TextChangeCallback(id.v,text.v))
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        val d = super.keyPressed(keyCode, scanCode, modifiers)
        if (keyCode == GLFW.GLFW_KEY_E){
            return !isFocus()
        }
        return d
    }
    override fun onClose() {
        super.onClose()
        cursorTimer.cancel()
    }
}