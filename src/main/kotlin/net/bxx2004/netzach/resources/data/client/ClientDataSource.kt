package net.bxx2004.netzach.resources.data.client

import com.alibaba.fastjson2.JSON
import com.alibaba.fastjson2.JSONObject
import net.bxx2004.netzach.core.attributes.Attribute
import net.bxx2004.netzach.core.attributes.ref
import net.bxx2004.netzach.resources.data.DataSource
import net.bxx2004.netzach.resources.data.DataSourceType
import net.minecraft.resources.ResourceLocation
import java.io.File
import java.io.FileReader

/**
 * @author 6hisea
 * @date  2025/6/13 17:58
 * @description: None
 */
class ClientDataSource(override val id: ResourceLocation, val file: File): DataSource {
    override val type: DataSourceType = DataSourceType.CLIENT

    val obj = parse()
    fun parse(): JSONObject {
        if (!file.exists()){
            file.createNewFile()
            file.writeText("{}")
        }
        return JSON.parseObject(FileReader(file))
    }
    override fun <T> get(key: String): Attribute<T> {
        return ref(obj.get(key) as T)
    }

    override fun <T> update(key: String, value: T) {
        obj[key] = value
        file.writeText(obj.toJSONString())
    }
}