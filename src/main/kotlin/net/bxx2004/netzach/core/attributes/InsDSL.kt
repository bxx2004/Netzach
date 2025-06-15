package net.bxx2004.netzach.core.attributes

/**
 * @author 6hisea
 * @date  2025/6/13 18:31
 * @description: None
 */
interface InsDSL<T> {
    val dsl:T.()->Unit
}