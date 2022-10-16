package com.example.screens

import com.example.configuration.CHOSEN_SEQUENCE_LENGTH
import com.example.configuration.toBeautyString
import com.example.configuration.zoomOnHover
import com.example.domain.HistoryNote
import com.example.functions.CowsAndBulls
import com.example.views.GameView
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.Spinner
import javafx.scene.control.SpinnerValueFactory
import javafx.scene.layout.FlowPane
import javafx.scene.paint.Color
import javafx.util.Duration
import tornadofx.*

class LeaderPlayerScreen: Fragment("LeaderPlayerScreen") {
    override val root: FlowPane by fxml()
    private val gameView by inject<GameView>()

    private val iThinkLabel: Label by fxid()
    private val aboutTimeLabel: Label by fxid()
    private val answerLabel: Label by fxid()
    private val cowsCount: Spinner<Int> by fxid()
    private val bullsCount: Spinner<Int> by fxid()
    private val submitCountsButton: Button by fxid()

    private var actualAnswer: String = ""

    init {
        submitCountsButton.onLeftClick {
            if (bullsCount.value != CHOSEN_SEQUENCE_LENGTH) {
                CowsAndBulls.addToHistory(
                    HistoryNote(
                        guess = actualAnswer,
                        cowsCount = cowsCount.value,
                        bullsCount = bullsCount.value
                    )
                )
                doOneMoreMove()
                gameView.gameStatisticsFieldsUpdate()
            }
            else victory(gameView.gameDuration)
        }
        submitCountsButton.zoomOnHover()
    }

    fun configureInitialState() {
        iThinkLabel.apply {
            text = "Я думаю, вы загадали число..."
            textFill = Color.BLACK
        }
        aboutTimeLabel.text = ""
        answerLabel.textFill = Color.web("#249beb")

        submitCountsButton.apply {
            isDisable = false
            text = "Отправить"
            addClass("submitButton").removeClass("cancelButton")
        }

        listOf(cowsCount, bullsCount).forEach {
            it.valueFactory = SpinnerValueFactory.IntegerSpinnerValueFactory(
                /* min = */ 0, /* max = */ CHOSEN_SEQUENCE_LENGTH, /* initialValue = */0,
                /* amountToStepBy = */ 1
            )
        }

        doOneMoreMove()
    }

    private fun doOneMoreMove() {
        try {
            actualAnswer = CowsAndBulls.fetchCompatibleNumber()
        } catch (_: NoSuchElementException) {
            error()
        }
        answerLabel.text = actualAnswer

        listOf(cowsCount, bullsCount).forEach {
            it.valueFactory.value = 0
        }
    }

    fun restart() {
        submitCountsButton.apply {
            text = "Отправить"
            addClass("submitButton").removeClass("cancelButton")
        }
    }
    private fun victory(gameDuration: Duration) {
        gameView.victory()
        iThinkLabel.text = "Вы загадали число: $actualAnswer"
        aboutTimeLabel.text = "Ответ был получен за время: ${gameDuration.toBeautyString()}"
        answerLabel.textFill = Color.LIMEGREEN
        submitCountsButton.isDisable = true
    }
    private fun error() {
        gameView.error()
        answerLabel.textFill = Color.RED
        iThinkLabel.textFill = Color.RED
        submitCountsButton.apply {
            isDisable = true
            text = "Требуется перезапуск игры"
            addClass("cancelButton").removeClass("submitButton")
        }
    }
}
