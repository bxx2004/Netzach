package net.bxx2004.netzach.core.attributes

/**
 * @author 6hisea
 * @date  2025/5/1 17:14
 * @description: None
 */
data class AttributeReader(
    val x:Int,
    val y : Int,
    val z : Int,
    val width :Int,
    val height:Int,
    //容器属性
    val cx:Int,
    val cy:Int
) {
    //绝对位置
    val ax = x + cx
    val ay = y + cy
    //矩形位置
    val rectangleHeight = ay + height
    val rectangleWidth = ax + width
}