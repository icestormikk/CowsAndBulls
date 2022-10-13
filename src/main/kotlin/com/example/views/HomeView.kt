package com.example.views

import com.example.configuration.GAME_TITLE
import com.example.screens.AuthorInfoScreen
import com.example.fragments.Header
import com.example.screens.RulesScreen
import com.example.screens.StartScreen
import com.example.screens.StatisticsScreen
import javafx.geometry.Point2D
import javafx.scene.control.Button
import javafx.scene.layout.BorderPane
import javafx.scene.layout.VBox
import javafx.util.Duration
import org.kordamp.ikonli.javafx.FontIcon
import tornadofx.*

private const val ZOOM_X_COF = 1.02
private const val ZOOM_Y_COF = 1.05
private const val ANIMATION_DURATION = 100.0

class HomeView: View(
    title = "$GAME_TITLE : Home"
) {
    override val root: BorderPane by fxml()

    private val header = find(Header::class)
    private val startScreen = find(StartScreen::class)
    private val rulesScreen = find(RulesScreen::class)
    private val statisticsScreen = find(StatisticsScreen::class)
    private val authorInfoScreen = find(AuthorInfoScreen::class)
    private val menuButtonsContainer: VBox by fxid()

    init {
        root.top.add(header)
        currentStage.let {
            it!!.isResizable = true
        }

        configureMenu()
    }

    private fun configureMenu() {
        listOf(
            Triple("Start Game", FontIcon("cil-chevron-double-right"), startScreen),
            Triple("Rules", FontIcon("cil-list-rich"), rulesScreen),
            Triple("Statistics", FontIcon("cil-chart"), statisticsScreen),
            Triple("Author", FontIcon("cil-address-book"), authorInfoScreen)
        ).forEach { (optionName, optionIcon, screen) ->
            menuButtonsContainer.children.add(
                Button(optionName).addClass("menuButton").apply {
                    useMaxWidth = true
                    graphic = optionIcon
                    onHover {
                        scale(
                            time = Duration.millis(ANIMATION_DURATION),
                            scale = Point2D(if (it) ZOOM_X_COF else 1.0, if (it) ZOOM_Y_COF else 1.0),
                            reversed = false
                        ).play()
                    }
                    onLeftClick {
                        if (this.text == "Statistics")
                            statisticsScreen.causeStatsTableUpdate()
                        root.center.replaceChildren(screen.root)
                    }
                }
            )
        }
    }
}
