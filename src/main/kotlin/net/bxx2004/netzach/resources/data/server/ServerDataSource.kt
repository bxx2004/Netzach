package net.bxx2004.netzach.resources.data.server

import net.bxx2004.netzach.core.attributes.Attribute
import net.bxx2004.netzach.core.attributes.mutable
import net.bxx2004.netzach.core.utils.rl
import net.bxx2004.netzach.network.NetzachNetwork
import net.bxx2004.netzach.resources.data.DataSource
import net.bxx2004.netzach.resources.data.DataSourcePacket
import net.bxx2004.netzach.resources.data.DataSourceType
import net.minecraft.resources.ResourceLocation
import net.neoforged.api.distmarker.Dist
import net.neoforged.api.distmarker.OnlyIn
import java.util.concurrent.ConcurrentHashMap

/**
 * @author 6hisea
 * @date  2025/6/13 17:42
 * @description: None
 */
@OnlyIn(Dist.CLIENT)
class ServerDataSource(regiseterId: ResourceLocation) : DataSource {
    override val id: ResourceLocation = regiseterId
    override val type: DataSourceType = DataSourceType.SERVER
    val responsePool = ConcurrentHashMap<String, Any?>()

    init {
        register()
    }

    override fun <T> get(key: String): Attribute<T> {
        NetzachNetwork.sendPacketToServer(DataSourcePacket.Request(id,key))
        return mutable { responsePool[key] as T }
    }

    override fun <T> update(key: String, value: T) {
        NetzachNetwork.sendPacketToServer(DataSourcePacket.Update(id,key,value))
    }
    private fun register() {
        allServerDataSource.add(this)
    }
    fun unregister() {
        allServerDataSource.remove(this)
    }
    companion object{
        val allServerDataSource = arrayListOf<ServerDataSource>()
    }
}