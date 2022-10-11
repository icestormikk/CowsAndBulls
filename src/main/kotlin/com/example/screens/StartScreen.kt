package com.example.screens

import com.example.configuration.MAX_SEQUENCE_LENGTH
import com.example.functions.CowsAndBulls
import com.example.views.CHOSEN_SEQUENCE_LENGTH
import com.example.views.GameView
import javafx.scene.Parent
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.Spinner
import javafx.scene.control.SpinnerValueFactory
import javafx.scene.layout.Border
import javafx.scene.layout.BorderPane
import javafx.scene.layout.BorderStroke
import javafx.scene.layout.BorderStrokeStyle
import javafx.scene.layout.BorderWidths
import javafx.scene.layout.CornerRadii
import javafx.scene.layout.FlowPane
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import org.kordamp.ikonli.javafx.FontIcon
import tornadofx.*

private const val DEFAULT_SEQUENCE_LENGTH = 4

class StartScreen: Fragment("StartScreen") {
    override val root: Parent by fxml()

    private val rulesScreen = find(RulesScreen::class)
    private val gameScreen = find(GameView::class)
    private val redirectToRulesLabel: Label by fxid()
    private val gamemodePanelComputer: FlowPane by fxid()
    private val gamemodePanelHuman: FlowPane by fxid()
    private val startButton: Button by fxid()
    private val sequenceUserLength: Spinner<Int> by fxid()

    init {
        redirectToRulesLabel.onLeftClick {
            (root.parent.parent as BorderPane).center.replaceChildren(rulesScreen.root)
        }
        configureGamemodeButtons()

        sequenceUserLength.valueFactory = SpinnerValueFactory.IntegerSpinnerValueFactory(
            /* min = */ 1, /* max = */ MAX_SEQUENCE_LENGTH, /* initialValue = */
            DEFAULT_SEQUENCE_LENGTH, /* amountToStepBy = */ 1
        )

        startButton.onLeftClick {
            CHOSEN_SEQUENCE_LENGTH = sequenceUserLength.value
            CowsAndBulls.initializeGame(CHOSEN_SEQUENCE_LENGTH)
            gameScreen.configureInitialState()

            root.parent.parent.replaceWith(gameScreen.root)
        }
    }

    private fun configureGamemodeButtons() {
        // Computer
        gamemodePanelComputer.children.add(1, FontIcon("cib-probot"))
        // Human
        gamemodePanelHuman.children.add(0, FontIcon("cil-people"))

        listOf(gamemodePanelComputer, gamemodePanelHuman).forEach { flowPane ->
            with(flowPane.parent as VBox) {
                onHover {
                    border = if (it) Border(
                        BorderStroke(
                            Color.DARKGREEN,
                            BorderStrokeStyle.SOLID,
                            CornerRadii(2.0),
                            BorderWidths(1.0)
                        )
                    ) else Border.EMPTY
                }
            }
        }
    }
}
