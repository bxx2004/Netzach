package net.bxx2004.netzach.ui.style

import net.bxx2004.netzach.ui.components.IComponent

class UIStyle(val runnable:(context: ReadingContext, obj: IComponent) -> Unit) {}
fun registerStyle(runnable:(context: ReadingContext, obj: IComponent) -> Unit) : UIStyle{
    return UIStyle(runnable)
}