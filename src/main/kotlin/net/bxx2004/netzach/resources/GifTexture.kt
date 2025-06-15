package net.bxx2004.netzach.resources

import com.madgag.gif.fmsware.GifDecoder
import com.mojang.blaze3d.platform.NativeImage
import com.mojang.blaze3d.platform.TextureUtil
import net.minecraft.client.renderer.texture.AbstractTexture
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.resources.ResourceManager
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.Closeable
import java.io.InputStream
import javax.imageio.ImageIO

class GifTexture(val location: ResourceLocation) : AbstractTexture(), Closeable {
    private val glIDs = ArrayList<Int>()
    private val delays = ArrayList<Int>()
    private var index = 0
    private lateinit var stream: InputStream
    private var lastFrameTime = System.currentTimeMillis()

    override fun close() {
        glIDs.forEach { TextureUtil.releaseTextureId(it) }
        stream.close()
    }

    fun upload(glid: Int, image: NativeImage) {
        TextureUtil.prepareImage(glid, 0, image.width, image.height)
        image.upload(0, 0, 0, 0, 0, image.width, image.height, false, false, false, true)
    }

    override fun load(manager: ResourceManager) {
        stream = manager.open(location)
        val gd = GifDecoder()
        gd.read(stream)
        for (i in 0 until gd.frameCount) {
            val id = TextureUtil.generateTextureId()
            val img = NativeImage.read(bufferedImageToInputStream(gd.getFrame(i)))
            delays.add(gd.getDelay(i))
            glIDs.add(id)
            upload(id, img)
        }
        stream.close()
    }

    private fun bufferedImageToInputStream(image: BufferedImage): ByteArrayInputStream {
        val os = ByteArrayOutputStream()
        ImageIO.write(image, "png", os)
        val stream = ByteArrayInputStream(os.toByteArray())
        os.close()
        return stream
    }

    override fun getId(): Int {
        if (glIDs.isEmpty()) return -1

        val currentTime = System.currentTimeMillis()
        val delay = delays[index].coerceAtLeast(10) // Ensure minimum delay of 10ms

        if (currentTime - lastFrameTime >= delay) {
            index = (index + 1) % glIDs.size
            lastFrameTime = currentTime
        }

        return glIDs[index]
    }
}