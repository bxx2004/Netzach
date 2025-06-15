package net.bxx2004.netzach

import com.mojang.blaze3d.platform.InputConstants
import net.bxx2004.netzach.core.utils.nrl
import net.bxx2004.netzach.core.utils.rl
import net.bxx2004.netzach.ui.components.container.Layer
import net.bxx2004.netzach.ui.components.display.Texture
import net.minecraft.client.KeyMapping
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.jarjar.nio.util.Lazy
import net.neoforged.neoforge.client.event.ClientTickEvent
import org.lwjgl.glfw.GLFW

/**
 * @author 6hisea
 * @date  2025/6/11 19:40
 * @description: None
 */
@EventBusSubscriber(modid = Netzach.ID)
object Test {
    val EXAMPLE_MAPPING : Lazy<KeyMapping> = Lazy.of{
        KeyMapping("key.examplemod.example",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_L,
            "key.categories.misc")
    }
    @SubscribeEvent
    fun onClientTick(event: ClientTickEvent.Post) {
        while (EXAMPLE_MAPPING.get().consumeClick()) {

            Layer(rl("netzach","test")).apply {
                hideLayers.add(rl("minecraft","player_health"))
                addComponent(
                    Texture().apply {
                        texture.v = nrl("textures/test.gif")
                    }
                )
            }.add()
        }
    }
}