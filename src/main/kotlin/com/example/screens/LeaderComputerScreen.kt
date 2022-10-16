package com.example.screens

import com.example.configuration.CHOSEN_SEQUENCE_LENGTH
import com.example.domain.HistoryNote
import com.example.functions.CowsAndBulls
import com.example.views.GameView
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.layout.FlowPane
import tornadofx.*

class LeaderComputerScreen: Fragment("LeaderComputerScreen") {
    override val root: FlowPane by fxml()
    private val gameView by inject<GameView>()

    private val playerGuess: TextField by fxid()
    private val submitPlayerAnswer: Button by fxid()
    private val errorLabel: Label by fxid()
    private val cowsAndBullsInfo: Label by fxid()
    private val additionalTitle: Label by fxid()

    private lateinit var realAnswer: String

    init {
        submitPlayerAnswer.onLeftClick {
            with (errorLabel) {
                if (playerGuess.hasValidInput()) {
                    with(CowsAndBulls.cowsAndBullsCounter(
                        playerGuess.text, realAnswer
                    )) {
                        CowsAndBulls.addToHistory(
                            HistoryNote(
                                guess = playerGuess.text,
                                cowsCount = first,
                                bullsCount = second
                            )
                        )
                        if (second == CHOSEN_SEQUENCE_LENGTH)
                            victory()
                        cowsAndBullsInfo.text = "$first коров(ы/a) и $second бык(ов/a)"
                    }
                    gameView.gameStatisticsFieldsUpdate()
                } else text = "Некорректный ввод"
            }
        }
    }

    fun configureInitialState() {
        additionalTitle.text = ""
        cowsAndBullsInfo.text = ""
        playerGuess.text = ""
        errorLabel.text = ""
        submitPlayerAnswer.isDisable = false

        realAnswer = CowsAndBulls.fetchRandomNumber()
    }

    private fun victory() {
        additionalTitle.text = "Вы угадали число!"
        submitPlayerAnswer.isDisable = true
    }
}

private fun TextField.hasValidInput(): Boolean {
    return try {
        val number = text.toInt()

        "$number".length == CHOSEN_SEQUENCE_LENGTH
                && number > 0
                && "$number".toCharArray().toSet().size == "$number".length
    } catch (_: NumberFormatException) {
        false
    }
}
