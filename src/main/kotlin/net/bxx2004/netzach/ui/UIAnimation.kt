package net.bxx2004.netzach.ui

import net.bxx2004.netzach.ui.components.IComponent

/**
 * @author 6hisea
 * @date  2025/6/11 18:48
 * @description: None
 */

class UIAnimation(val runnable:(type: String, obj: IComponent) -> Unit) {}
fun registerUIAnimation(runnable:(type: String, obj: IComponent) -> Unit) : UIAnimation{
    return UIAnimation(runnable)
}