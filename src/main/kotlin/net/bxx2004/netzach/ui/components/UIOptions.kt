package net.bxx2004.netzach.ui.components

import net.minecraft.resources.ResourceLocation

class UIOptions {
    var closeByEsc = true
    var type = "screen"
    var title = "Hello World"
    var positioner = false
    var id = ""
    var hideLayers = ArrayList<ResourceLocation>()
    class UIOptionsBuilder(){
        private val options = UIOptions()
        fun closeByEsc(b:Boolean){
            options.closeByEsc = b
        }
        fun type(type:String){
            options.type = type
        }
        fun title(title:String){
            options.title = title
        }
        fun positioner(positioner:Boolean){
            options.positioner = positioner
        }
        fun hideLayers(hide_huds:List<ResourceLocation>){
            options.hideLayers.addAll(hide_huds)
        }
        fun build():UIOptions{
            return options
        }
    }
    companion object{
        fun build(func:UIOptionsBuilder.()->Unit):UIOptions{
            val builder = UIOptionsBuilder()
            func(builder)
            return builder.build()
        }
    }
}