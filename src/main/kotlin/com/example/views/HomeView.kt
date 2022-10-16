package com.example.views

import com.example.configuration.GAME_TITLE
import com.example.configuration.zoomOnHover
import com.example.screens.AuthorInfoScreen
import com.example.fragments.Header
import com.example.screens.RulesScreen
import com.example.screens.StartScreen
import com.example.screens.StatisticsScreen
import javafx.scene.control.Button
import javafx.scene.layout.BorderPane
import javafx.scene.layout.VBox
import org.kordamp.ikonli.javafx.FontIcon
import tornadofx.*

private const val START_GAME_LABEL = "Начать игру"
private const val RULES_LABEL = "Правила"
private const val STATISTICS_LABEL = "Статистика"
private const val ABOUT_AUTHOR_LABEL = "Об авторе"

class HomeView: View(
    title = GAME_TITLE
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
        configureMenu()
    }

    private fun configureMenu() {
        listOf(
            Triple(START_GAME_LABEL, FontIcon("cil-chevron-double-right"), startScreen),
            Triple(RULES_LABEL, FontIcon("cil-list-rich"), rulesScreen),
            Triple(STATISTICS_LABEL, FontIcon("cil-chart"), statisticsScreen),
            Triple(ABOUT_AUTHOR_LABEL, FontIcon("cil-address-book"), authorInfoScreen)
        ).forEach { (optionName, optionIcon, screen) ->
            menuButtonsContainer.children.add(
                Button(optionName).addClass("menuButton").apply {
                    useMaxWidth = true
                    graphic = optionIcon
                    zoomOnHover()
                    onLeftClick {
                        if (this.text == STATISTICS_LABEL)
                            statisticsScreen.causeStatsTableUpdate()
                        root.center.replaceChildren(screen.root)
                    }
                }
            )
        }
    }
}
