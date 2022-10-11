package com.example.views

import com.example.domain.HistoryNote
import com.example.functions.CowsAndBulls.addToHistory
import com.example.functions.CowsAndBulls.fetchCompatibleNumber
import com.example.functions.CowsAndBulls.getActualSequence
import com.example.functions.CowsAndBulls.getHistorySnapshot
import javafx.animation.KeyFrame
import javafx.animation.Timeline
import javafx.beans.property.SimpleStringProperty
import javafx.scene.Parent
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.Spinner
import javafx.scene.control.SpinnerValueFactory
import javafx.scene.control.TableView
import javafx.scene.control.TextArea
import javafx.scene.layout.AnchorPane
import javafx.scene.paint.Color
import javafx.util.Duration
import org.kordamp.ikonli.javafx.FontIcon
import tornadofx.*
import java.util.Locale

private const val HOURS_IN_DAY = 24
private const val MINUTES_IN_HOUR = 60
var CHOSEN_SEQUENCE_LENGTH = 1

class GameView: View("Game View") {
    companion object GameConfiguration {
        private var timerAnimation = Timeline()

        private var duration = Duration.ZERO
        private val stringTimeProperty = SimpleStringProperty()
        private var actualAnswer: String = ""

        private var lastSequenceState = mutableSetOf<String>()
        private var noMoreAnswersSequence = mutableSetOf<String>()
    }

    override val root: Parent by fxml()

    private val historyTableViewContainer: AnchorPane by fxid()
    private val stopwatchLabel: Label by fxid()
    private val answerLabel: Label by fxid()
    private val cowsCount: Spinner<Int> by fxid()
    private val bullsCount: Spinner<Int> by fxid()
    private val possibleAnswers: TextArea by fxid()
    private val possibleAnswersCount: Label by fxid()
    private val noMoreAnswers: TextArea by fxid()
    private val noMoreAnswersCount: Label by fxid()
    private val lostedNumbersCount: Label by fxid()
    private val submitCountsButton: Button by fxid()
    private val restartButton: Button by fxid()
    private val exitButton: Button by fxid()

    init {
        configureHistoryView()
        stopwatchLabel.textProperty().bind(stringTimeProperty)

        restartButton.apply {
            graphic = FontIcon("cil-loop-circular")
        }
        exitButton.apply {
            graphic = FontIcon("cil-exit-to-app")
        }

        submitCountsButton.onLeftClick {
            if (bullsCount.value != CHOSEN_SEQUENCE_LENGTH) {
                addToHistory(HistoryNote(
                    guess = actualAnswer,
                    cowsCount = cowsCount.value,
                    bullsCount = bullsCount.value
                ))
                doOneMoreMove()
                gameStatisticsFieldsUpdate()
            }
            else victory()
        }
    }

    fun configureInitialState() {
        listOf(cowsCount, bullsCount).forEach {
            it.valueFactory = SpinnerValueFactory.IntegerSpinnerValueFactory(
                /* min = */ 0, /* max = */ CHOSEN_SEQUENCE_LENGTH, /* initialValue = */0,
                /* amountToStepBy = */ 1
            )
        }

        configureTimer()
        gameStatisticsFieldsUpdate()
        doOneMoreMove()
    }

    private fun victory() {
        information(
            header = "Число отгадано!",
            content = "Вы загадали число: $actualAnswer. Ответ был получен за время: " +
                    "${duration.toBeautyString()}c"
        )

        timerAnimation.stop()
        answerLabel.textFill = Color.LIMEGREEN
        listOf(restartButton, exitButton, submitCountsButton).forEach { it.isDisable = true }
    }

    private fun doOneMoreMove() {
        actualAnswer = fetchCompatibleNumber()
        answerLabel.text = actualAnswer

        listOf(cowsCount, bullsCount).forEach {
            it.valueFactory.value = 0
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun gameStatisticsFieldsUpdate() {
        (historyTableViewContainer.children[0] as TableView<HistoryNote>)
            .refresh()

        val newSequenceState = getActualSequence()
        lastSequenceState.minus(newSequenceState.toSet()).also {
            with(noMoreAnswersSequence) {
                this.addAll(it)
                sorted()
            }
            lostedNumbersCount.text = "${it.size}"
        }

        newSequenceState.apply {
            possibleAnswers.text = this.joinToString(separator = ", ")
            possibleAnswersCount.text = "${this.size}"
            noMoreAnswers.text = noMoreAnswersSequence.joinToString(separator = ", ")
            noMoreAnswersCount.text = "${noMoreAnswersSequence.size}"

            lastSequenceState = this.toMutableSet()
        }
    }

    private fun configureHistoryView() {
        getHistorySnapshot().also { historySnapshot ->
            historyTableViewContainer.add(
                tableview(historySnapshot.asObservable()) {
                    readonlyColumn("Guess", HistoryNote::guess)
                    readonlyColumn("Cows count", HistoryNote::cowsCount)
                    readonlyColumn("Bulls count", HistoryNote::bullsCount)
                }.apply {
                    useMaxSize = true
                }
            )
        }
    }

    private fun configureTimer() {
        timerAnimation = Timeline(
            KeyFrame(Duration.millis(1.0), {
                duration += (it.source as KeyFrame).time
                with(duration) {
                    stringTimeProperty.set(
                        toBeautyString()
                    )
                }
            })
        ).apply {
            cycleCount = Timeline.INDEFINITE
            play()
        }
    }

    private fun Duration.toBeautyString() = String.format(
        Locale.ENGLISH,
        "%02d:%02d:%02d",
        toHours().toInt() % HOURS_IN_DAY,
        toMinutes().toInt() % MINUTES_IN_HOUR,
        toSeconds().toInt() % MINUTES_IN_HOUR
    )
}