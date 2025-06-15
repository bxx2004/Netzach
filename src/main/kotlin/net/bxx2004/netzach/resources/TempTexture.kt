package net.bxx2004.netzach.resources

import net.minecraft.client.renderer.texture.AbstractTexture
import net.minecraft.server.packs.resources.ResourceManager

class TempTexture(val func:()-> Int) : AbstractTexture() {
    override fun load(manager: ResourceManager) {

    }

    override fun getId(): Int {
        return func()
    }

    override fun close() {

    }

    override fun bind() {

    }

    override fun releaseId() {

    }
}