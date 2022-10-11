package com.example.screens

import com.example.views.GameView
import javafx.scene.Parent
import javafx.scene.control.Button
import javafx.scene.control.Label
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


class StartScreen: Fragment("StartScreen") {
    override val root: Parent by fxml()

    private val rulesScreen = find(RulesScreen::class)
    private val gameScreen = find(GameView::class)
    private val redirectToRulesLabel: Label by fxid()
    private val gamemodePanelComputer: FlowPane by fxid()
    private val gamemodePanelHuman: FlowPane by fxid()
    private val startButton: Button by fxid()

    init {
        redirectToRulesLabel.onLeftClick {
            (root.parent.parent as BorderPane).center.replaceChildren(rulesScreen.root)
        }
        // Computer
        gamemodePanelComputer.children.add(1, FontIcon("cib-probot"))
        // Human
        gamemodePanelHuman.children.add(0, FontIcon("cil-people"))

        listOf(gamemodePanelComputer, gamemodePanelHuman).forEach { flowPane ->
            with(flowPane.parent as VBox) {
                onHover {
                    border = if (it) Border(BorderStroke(
                        Color.DARKGREEN,
                        BorderStrokeStyle.SOLID,
                        CornerRadii(2.0),
                        BorderWidths(1.0)
                    )) else Border.EMPTY
                }
            }
        }

        startButton.onLeftClick {
            root.parent.parent.replaceWith(gameScreen.root)
        }
    }
}
