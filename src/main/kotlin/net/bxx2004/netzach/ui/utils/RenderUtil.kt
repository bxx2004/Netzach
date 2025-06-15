package net.bxx2004.netzach.ui.utils

import net.bxx2004.netzach.core.utils.client
import net.bxx2004.netzach.ui.components.container.UI
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.resources.ResourceLocation

/**
 * @author 6hisea
 * @date  2025/6/12 19:26
 * @description: None
 */
fun GuiGraphics.nineSlice(
    texture: ResourceLocation,
    x: Int,
    y: Int,
    width: Int,
    height: Int,
    borderSize: Int,
    textureWidth: Int = width,
    textureHeight: Int = height,
    zIndex: Int = 0
) {
    // Ensure border size is reasonable
    val border = borderSize.coerceAtMost(textureWidth / 2).coerceAtMost(textureHeight / 2)
        .coerceAtMost(width / 2).coerceAtMost(height / 2)

    // Source texture coordinates (u, v)
    val u = listOf(0f, border.toFloat(), (textureWidth - border).toFloat(), textureWidth.toFloat())
    val v = listOf(0f, border.toFloat(), (textureHeight - border).toFloat(), textureHeight.toFloat())

    // Destination coordinates
    val xPos = listOf(x, x + border, x + width - border, x + width)
    val yPos = listOf(y, y + border, y + height - border, y + height)

    // Draw 9 slices
    for (i in 0..2) {
        for (j in 0..2) {
            val sliceWidth = xPos[i+1] - xPos[i]
            val sliceHeight = yPos[j+1] - yPos[j]
            val uWidth = u[i+1] - u[i]
            val vHeight = v[j+1] - v[j]

            // Skip empty regions
            if (sliceWidth <= 0 || sliceHeight <= 0 || uWidth <= 0 || vHeight <= 0) continue

            // The key difference is using blit with source and destination dimensions
            this.blit(
                texture,
                xPos[i], yPos[j], // Destination position
                sliceWidth, sliceHeight, // Destination size
                u[i], v[j], // Source position
                uWidth.toInt(), vHeight.toInt(), // Source size
                textureWidth, textureHeight
            )
        }
    }
}
val ui : UI
    get() = client().screen as UI