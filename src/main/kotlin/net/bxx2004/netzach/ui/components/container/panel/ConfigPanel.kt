package net.bxx2004.netzach.ui.components.container.panel

import net.bxx2004.netzach.core.attributes.mutable
import net.bxx2004.netzach.core.attributes.ref
import net.bxx2004.netzach.resources.data.DataSource
import net.bxx2004.netzach.ui.asHeight
import net.bxx2004.netzach.ui.asWidth
import net.bxx2004.netzach.ui.autoHeight
import net.bxx2004.netzach.ui.autoWidth
import net.bxx2004.netzach.ui.callback.CharTypedCallback
import net.bxx2004.netzach.ui.callback.KeyPressCallback
import net.bxx2004.netzach.ui.callback.MouseClickCallback
import net.bxx2004.netzach.ui.callback.MouseScrollCallback
import net.bxx2004.netzach.ui.center
import net.bxx2004.netzach.ui.components.IComponent
import net.bxx2004.netzach.ui.components.container.BaseLayout
import net.bxx2004.netzach.ui.components.container.ListLayout
import net.bxx2004.netzach.ui.components.container.RowLayout
import net.bxx2004.netzach.ui.components.display.Text
import net.bxx2004.netzach.ui.components.feedback.Popover
import net.bxx2004.netzach.ui.components.form.Button
import net.bxx2004.netzach.ui.full
import net.minecraft.network.chat.Component

class ConfigPanel<T : IComponent>(
    private val modId: String,
    source: DataSource,
    private val componentFactory: (key: String) -> T,
    private val saveHandler: (key: String, component: T) -> Unit
) : ListLayout() {

    class ItemGroup(
        title: Component,
        comment: Component,
        child: IComponent,
        saveAction: () -> Unit
    ) : BaseLayout() {
        var save = false
        init {
            addComponent(
                Popover().apply {
                    x = ref(0)
                    y = ref(0)
                    width = 0.2F.asWidth
                    height = ref(20)
                    slot<Popover>("trigger"){
                        Text().apply {
                            text.v = title
                        }
                    }
                    slot<Popover>("content"){
                        Text().apply { text.v = comment;width.v = 100;full() }
                    }
                }
            )
            addComponent(
                child.apply {
                    callback<KeyPressCallback> {
                        save = true
                    }
                    callback<CharTypedCallback> {
                        save = true
                    }
                    x = 0.2F.asWidth
                    y = ref(0)
                    width = 0.4F.asWidth
                    height = ref(20)
                }
            )

            addComponent(
                Button().apply {
                    x = 0.6F.asWidth
                    y = ref(0)
                    width = 0.2F.asWidth
                    height = ref(20)
                    callback<MouseClickCallback> {
                        save = false
                        saveAction()
                    }
                    state = mutable { save }
                    slot<Button>("content"){
                        val a =Text().apply {
                            text.v = Component.literal("Save")
                        }
                        a.full()
                        a
                    }
                }
            )
        }
    }

    init {
        autoWidth(0.8F)
        autoHeight(1.0F)
        center()

        source.all().forEach { (key, _) ->
            val editableComponent = componentFactory(key)
            addComponent(
                ItemGroup(
                    getLocalizedText(key, "title", "Untitled Setting"),
                    getLocalizedText(key, "comment", "No description available"),
                    editableComponent
                ) { saveHandler(key, editableComponent) }
            )
        }
    }

    private fun getLocalizedText(key: String, type: String, defaultValue: String): Component {
        val translationKey = "${modId}.config.$key.$type"
        val translated = Component.translatable(translationKey)
        return if (translated.string == translationKey) Component.literal(defaultValue) else translated
    }
}