package net.bxx2004.netzach.ui

import net.bxx2004.netzach.core.attributes.Attribute
import net.bxx2004.netzach.core.attributes.mutable
import net.bxx2004.netzach.core.utils.windowSize
import net.bxx2004.netzach.ui.components.IComponent
import net.bxx2004.netzach.ui.utils.Direction

/**
 * @author 6hisea
 * @date  2025/6/12 10:24
 * @description: None
 */
private fun autoWidth(percentage: Float): Int{
    return (windowSize()[0] * percentage).toInt()
}
private fun autoHeight(percentage: Float): Int{
    return (windowSize()[1] * percentage).toInt()
}


val Float.asWidth : Attribute<Int>
    get() = mutable { autoWidth(this) }
val Float.asHeight : Attribute<Int>
    get() = mutable { autoHeight(this) }

fun IComponent.center(){
    x = mutable { (windowSize()[0] - width.v) /2 }
}
fun IComponent.middle(){
    y = mutable { (windowSize()[1] - height.v) /2 }
}

fun IComponent.full(){
    if (container == null) return
    if (container !is IComponent) return
    x = mutable { (container as IComponent).x.v }
    y = mutable { (container as IComponent).y.v }
    width = mutable { (container as IComponent).width.v }
    height = mutable { (container as IComponent).height.v }
}

fun IComponent.horizontal(direction: String,v:Float){
    when(direction){
        Direction.LEFT -> {
            x = mutable { (windowSize()[0] * v).toInt() }
        }
        Direction.RIGHT -> {
            x = mutable {
                val padding = (windowSize()[0] * v).toInt()
                windowSize()[0] - (padding + width.v)
            }
        }
    }
}
fun IComponent.vertical(direction: String,v:Float){
    when(direction){
        Direction.UP -> {
            y = mutable { (windowSize()[1] * v).toInt() }
        }
        Direction.DOWN -> {
            y = mutable {
                val padding = (windowSize()[1] * v).toInt()
                windowSize()[1] - (padding + height.v)
            }
        }
    }
}

fun IComponent.horizontal(direction: String,v: Int){
    when(direction){
        Direction.LEFT -> {
            x = mutable { v }
        }
        Direction.RIGHT -> {
            x = mutable {
                windowSize()[0] - (v + width.v)
            }
        }
    }
}
fun IComponent.vertical(direction: String,v:Int){
    when(direction){
        Direction.UP -> {
            y = mutable { v }
        }
        Direction.DOWN -> {
            y = mutable {
                val padding = v
                windowSize()[1] - (padding + height.v)
            }
        }
    }
}

// 放置在另一个组件的右侧
fun IComponent.rightOf(other: IComponent, margin: Float = 0f) {
    x = mutable {
        val otherRight = other.x.v + other.width.v
        (otherRight + (windowSize()[0] * margin).toInt()).toInt()
    }
}

// 放置在另一个组件的左侧
fun IComponent.leftOf(other: IComponent, margin: Float = 0f) {
    x = mutable {
        (other.x.v - width.v - (windowSize()[0] * margin).toInt()).toInt()
    }
}

// 放置在另一个组件的下方
fun IComponent.below(other: IComponent, margin: Float = 0f) {
    y = mutable {
        val otherBottom = other.y.v + other.height.v
        (otherBottom + (windowSize()[1] * margin).toInt()).toInt()
    }
}

// 放置在另一个组件的上方
fun IComponent.above(other: IComponent, margin: Float = 0f) {
    y = mutable {
        (other.y.v - height.v - (windowSize()[1] * margin).toInt()).toInt()
    }
}

fun IComponent.autoWidth(v:Float){
    width = v.asWidth
}
fun IComponent.autoHeight(v:Float){
    height = v.asHeight
}