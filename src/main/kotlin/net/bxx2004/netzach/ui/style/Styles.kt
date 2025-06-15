package net.bxx2004.netzach.ui.style

import net.bxx2004.netzach.core.attributes.mutable
import net.bxx2004.netzach.ui.components.IComponent

object Styles {
    val COLOR_BORDER = registerStyle{ context, obj ->
        val ton = context.number<Int>(1)
        val color = context.number<Int>(-1)
        val hover = context.string()
        obj.addRenderCall {
            val isRender = if (hover == "hover") {
                obj.isHover()
            } else {
                true
            }
            if (isRender) {
                fill(
                    obj.x.getValueCache() - ton,
                    obj.y.getValueCache() - ton,
                    obj.x.getValueCache() + obj.width.getValueCache() + ton,
                    obj.y.getValueCache(),
                    color
                )
                fill(
                    obj.x.getValueCache() - ton,
                    obj.y.getValueCache() + obj.height.getValueCache(),
                    obj.x.getValueCache() + obj.width.getValueCache() + ton,
                    obj.y.getValueCache() + obj.height.getValueCache() + ton,
                    color
                )
                fill(
                    obj.x.getValueCache() - ton,
                    obj.y.getValueCache(),
                    obj.x.getValueCache(),
                    obj.y.getValueCache() + obj.height.getValueCache(),
                    color
                )
                fill(
                    obj.x.getValueCache() + obj.width.getValueCache(),
                    obj.y.getValueCache(),
                    obj.x.getValueCache() + obj.width.getValueCache() + ton,
                    obj.y.getValueCache() + obj.height.getValueCache(),
                    color
                )
            }
            true
        }
    }
    val ALIGNMENT = registerStyle{ context, obj ->
        val horizontal = context.string("none")
        val vertical = context.string("none")
        when (horizontal) {
            "left" -> {
                obj.x = mutable {
                    (obj.container as IComponent).x.getValueCache()
                }
            }

            "right" -> {
                obj.x = mutable {
                    (obj.container as IComponent).width.getValueCache() - obj.width.getValueCache()
                }
            }

            "center" -> {
                obj.x = mutable {
                    ((obj.container as IComponent).width.getValueCache() - obj.width.getValueCache()) / 2
                }
            }

            "none" -> {

            }
        }
        when (vertical) {
            "top" -> {
                obj.y = mutable {
                    (obj.container as IComponent).y.getValueCache()
                }
            }

            "bottom" -> {
                obj.y = mutable {
                    (obj.container as IComponent).height.getValueCache() - obj.height.getValueCache()
                }
            }

            "center" -> {
                obj.y = mutable {
                    ((obj.container as IComponent).height.getValueCache() - obj.height.getValueCache()) / 2
                }
            }

            "none" -> {

            }
        }
    }
}