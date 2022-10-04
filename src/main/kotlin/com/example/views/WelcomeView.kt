package com.example.views

import com.example.config.GAME_TITLE
import com.example.fragments.Header
import javafx.application.Platform
import javafx.scene.Cursor
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.ProgressBar
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.BorderPane
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.scene.text.FontWeight
import javafx.util.Duration
import org.kordamp.ikonli.javafx.FontIcon
import tornadofx.*
import tornadofx.FX.Companion.stylesheets
import java.net.InetSocketAddress
import java.nio.channels.SocketChannel

private const val ICON_PATH = "static/icons/application_icon.png"
private const val STYLESHEET_FILE_PATH = "static/css/style.css"

class WelcomeView: View(
    title = "$GAME_TITLE : Welcome"
) {
    override val root: BorderPane by fxml()

    private val header = find(Header::class)
    private val controlsVBox: VBox by fxid()
    private val imageContainer: ImageView by fxid()
    private val playImageContainer: Button by fxid()

    init {
        root.top.add(header)
        currentStage.let {
            it!!.icons.add(Image(ICON_PATH))
            it.isResizable = false
        }
        currentWindow.let {
            stylesheets.add(STYLESHEET_FILE_PATH)
        }

        imageContainer.image = Image(ICON_PATH)
        playImageContainer.apply {
            graphic.addChildIfPossible(FontIcon("cil-media-play"), 0)
            graphic.styleClass.add("play-font-icon")

            cursor = Cursor.HAND
        }
    }

    /**
     * Checks for an internet connection
     * @return Boolean
     */
    private fun isInternetConnectionAvailable(): Boolean =
        try {
            SocketChannel.open().connect(
                InetSocketAddress("google.com", 80)
            )
        } catch (_: Exception) {
            false
        }

    fun enter() {
        val loadingBar = ProgressBar(0.0)
        val statusLabel = Label("Checking Internet Connection...").also {
            it.textFill = Color.GRAY
            it.font = Font.font("Calibri", FontWeight.BOLD ,20.0)
        }

        controlsVBox.children.apply {
            clear()
            addAll(loadingBar, statusLabel)
        }

        Platform.runLater {
            when {
                isInternetConnectionAvailable() -> {
                    statusLabel.apply {
                        text = "Success"
                        textFill = Color.GREEN
                    }
                    loadingBar.progress = 1.0
                    runLater(Duration.millis(1000.0)) {
                        replaceWith<HomeView>()
                    }
                }
                else -> {
                    statusLabel.apply {
                        text = "No Internet Connection"
                        textFill = Color.RED
                    }
                    runLater(Duration(1000.0)) {
                        controlsVBox.children.apply {
                            clear()
                            add(playImageContainer)
                        }
                    }
                }
            }
        }
    }
}