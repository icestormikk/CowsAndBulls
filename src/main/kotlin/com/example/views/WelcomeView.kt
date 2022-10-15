package com.example.views

import com.example.configuration.GAME_TITLE
import com.example.fragments.Header
import javafx.scene.Cursor
import javafx.scene.control.Button
import javafx.scene.image.Image
import javafx.scene.layout.BorderPane
import org.kordamp.ikonli.javafx.FontIcon
import tornadofx.*
import tornadofx.FX.Companion.stylesheets

private const val ICON_PATH = "static/icons/application_icon.png"
private const val STYLESHEET_FILE_PATH = "static/css/style.css"

class WelcomeView: View(
    title = "$GAME_TITLE : Welcome"
) {
    override val root: BorderPane by fxml()

    private val header = find(Header::class)
    private val playImageContainer: Button by fxid()

    init {
        root.top.add(header)
        currentStage?.let {
            it.icons.add(Image(ICON_PATH))
            it.isResizable = false
        }
        currentWindow.let {
            stylesheets.add(STYLESHEET_FILE_PATH)
        }

        playImageContainer.apply {
            graphic.addChildIfPossible(FontIcon("cil-media-play"), 0)
            graphic.styleClass.add("play-font-icon")

            cursor = Cursor.HAND
        }
    }

    fun enter() {
        replaceWith<HomeView>()
    }
}