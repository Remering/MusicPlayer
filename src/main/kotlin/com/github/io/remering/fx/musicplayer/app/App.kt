
import com.github.io.remering.fx.musicplayer.app.Styles
import com.github.io.remering.fx.musicplayer.view.MainView
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import tornadofx.*
import tornadofx.App


//class App: App(MainView::class, Styles::class)
class App: App(MainView::class, Styles::class)

class Language (name: String, static: Boolean) {
    val staticProperty = SimpleBooleanProperty()
    var static by staticProperty
    val nameProperty = SimpleStringProperty()
    var name by nameProperty
    init {
        this.name = name
        this.static = static
    }

}


class TestView: View(){
    override val root = tableview<Language> {
        column("name", Language::name){
            cellFormat {
                graphic = hbox {
                    label(it)
                    hbox {
                        button("Play")
                        button("DownLoad")

                        visibleWhen {
                            this@cellFormat.hoverProperty()
                        }
                    }
                }
            }
        }

        column("staic", Language::static)
        items.addAll(
            Language("Java", true),
            Language("C", true),
            Language("Kotlin", true),
            Language("Dart", false),
            Language("Javascript", false),
            Language("Python", false)
        )

    }

}