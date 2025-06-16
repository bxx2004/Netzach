package net.bxx2004.netzach.ui.components.container

import net.bxx2004.netzach.core.attributes.ref
import net.bxx2004.netzach.core.attributes.AttributeReader
import net.bxx2004.netzach.ui.utils.Axis
import net.minecraft.client.gui.GuiGraphics


/**
 * @author 6hisea
 * @date  2025/5/2 17:30
 * @description: None
 */
open class FlexLayout : BaseLayout() {
    var type = ref(FlexType.LEFT)
    var axis = ref(Axis.HORIZONTAL)
    var gap = ref(1)

    fun fixed(reader: AttributeReader) {
        when (axis.getValue()) {
            // 水平布局
            Axis.HORIZONTAL -> {
                when (type.getValue()) {
                    FlexType.LEFT -> {
                        var cacheValue = 0
                        components.forEach {
                            it.x = ref(cacheValue)
                            it.y = ref(reader.y)
                            cacheValue += it.width.getValue() + gap.getValue()
                        }
                    }
                    FlexType.RIGHT -> {
                        var cacheValue = reader.width
                        components.reversed().forEach {
                            cacheValue -= it.width.getValue()
                            it.x = ref(cacheValue)
                            it.y = ref(reader.y)
                            cacheValue -= gap.getValue()
                        }
                    }
                    FlexType.CENTER -> {
                        val totalWidth = components.sumOf { it.width.getValue() } +
                                (components.size - 1) * gap.getValue()
                        var startX = (reader.width - totalWidth) / 2
                        components.forEach {
                            it.x = ref(startX)
                            it.y = ref(reader.y)
                            startX += it.width.getValue() + gap.getValue()
                        }
                    }
                    FlexType.AROUND -> {
                        if (components.isEmpty()) return
                        val spacing = reader.width.toFloat() / components.size
                        components.forEachIndexed { index, component ->
                            component.x = ref((index * spacing + (spacing - component.width.getValue()) / 2).toInt())
                            component.y = ref(reader.y)
                        }
                    }
                }
            }
            // 垂直布局
            Axis.VERTICAL -> {
                when (type.getValue()) {
                    FlexType.LEFT -> { // 顶部对齐
                        var cacheValue = 0
                        components.forEach {
                            it.x = ref(reader.x)
                            it.y = ref(cacheValue)
                            cacheValue += it.height.getValue() + gap.getValue()
                        }
                    }
                    FlexType.RIGHT -> { // 底部对齐
                        var cacheValue = reader.height
                        components.reversed().forEach {
                            cacheValue -= it.height.getValue()
                            it.x = ref(reader.x)
                            it.y = ref(cacheValue)
                            cacheValue -= gap.getValue()
                        }
                    }
                    FlexType.CENTER -> {
                        val totalHeight = components.sumOf { it.height.getValue() } +
                                (components.size - 1) * gap.getValue()
                        var startY = (reader.height - totalHeight) / 2
                        components.forEach {
                            it.x = ref(reader.x)
                            it.y = ref(startY)
                            startY += it.height.getValue() + gap.getValue()
                        }
                    }
                    FlexType.AROUND -> {
                        if (components.isEmpty()) return
                        val spacing = reader.height.toFloat() / components.size
                        components.forEachIndexed { index, component ->
                            component.x = ref(reader.x)
                            component.y = ref((index * spacing + (spacing - component.height.getValue()) / 2).toInt())
                        }
                    }
                }
            }
        }
    }

    override fun render(context: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float, reader: AttributeReader) {
        fixed(reader)
        super.render(context, mouseX, mouseY, delta, reader)
    }
    object FlexType{
        const val AROUND = "around"
        const val CENTER = "center"
        const val LEFT = "left"
        const val RIGHT = "right"
    }
}