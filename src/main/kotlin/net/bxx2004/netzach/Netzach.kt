package net.bxx2004.netzach

import net.bxx2004.netzach.Test.EXAMPLE_MAPPING
import net.bxx2004.netzach.core.registry.RegisterUtil
import net.bxx2004.netzach.network.NetzachNetwork
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.fml.common.Mod
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent
import net.neoforged.neoforge.network.registration.HandlerThread
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

@Mod(Netzach.ID)
@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
object Netzach {
    const val ID = "netzach"

    // the logger for our mod
    val LOGGER: Logger = LogManager.getLogger(ID)

    /*
    @SubscribeEvent
    fun registerBindings(event: RegisterKeyMappingsEvent) {
        event.register(EXAMPLE_MAPPING.get())
    }
     */

    @SubscribeEvent
    fun onCommonSetup(event: FMLCommonSetupEvent) {
        LOGGER.log(Level.INFO, "[Netzach] Initialized.")
    }

    @SubscribeEvent
    fun register(event: RegisterPayloadHandlersEvent) {
        val registrar = event.registrar("1").executesOn(HandlerThread.NETWORK);
        NetzachNetwork.registerPayloads(registrar)
    }
}
