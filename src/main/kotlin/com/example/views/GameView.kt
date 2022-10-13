package com.example.views

import com.example.configuration.Gamemode
import com.example.configuration.Gamemode.LEADER_PLAYER
import com.example.domain.HistoryNote
import com.example.functions.CowsAndBulls
import com.example.functions.CowsAndBulls.addToHistory
import com.example.functions.CowsAndBulls.fetchCompatibleNumber
import com.example.functions.CowsAndBulls.getActualSequence
import com.example.functions.CowsAndBulls.getHistorySnapshot
import com.example.configuration.setEqualWidthForColumns
import com.example.domain.StatisticsNote
import javafx.animation.KeyFrame
import javafx.animation.Timeline
import javafx.beans.property.SimpleStringProperty
import javafx.scene.Parent
import javafx.scene.control.Alert
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
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.properties.Delegates

private const val HOURS_IN_DAY = 24
private const val MINUTES_IN_HOUR = 60
var CHOSEN_SEQUENCE_LENGTH = 1
var CHOSEN_GAMEMODE = LEADER_PLAYER

class GameView: View("Game View") {
    override val root: Parent by fxml()

    companion object GameStats {
        private lateinit var startDateTime: LocalDateTime
        private lateinit var gamemode : Gamemode
        private var restartsCounter by Delegates.notNull<Int>()
    }

    private val historyTableViewContainer: AnchorPane by fxid()
    private val stopwatchLabel: Label by fxid()
    private val answerLabel: Label by fxid()
    private val noMoreAnswersCount: Label by fxid()
    private val lostedNumbersCount: Label by fxid()
    private val possibleAnswersCount: Label by fxid()
    private val iThinkLabel: Label by fxid()
    private val aboutTimeLabel: Label by fxid()
    private val cowsCount: Spinner<Int> by fxid()
    private val bullsCount: Spinner<Int> by fxid()
    private val possibleAnswers: TextArea by fxid()
    private val noMoreAnswers: TextArea by fxid()
    private val submitCountsButton: Button by fxid()
    private val restartButton: Button by fxid()
    private val exitButton: Button by fxid()

    private var timerAnimation = Timeline()
    private var duration = Duration.ZERO
    private val stringTimeProperty = SimpleStringProperty()
    private var actualAnswer: String = ""
    private var lastSequenceState = mutableSetOf<String>()
    private var noMoreAnswersSequence = mutableSetOf<String>()

    init {
        configureHistoryView()
        stopwatchLabel.textProperty().bind(stringTimeProperty)

        restartButton.apply {
            graphic = FontIcon("cil-loop-circular")
            onLeftClick {
                restart()
            }
        }
        exitButton.apply {
            graphic = FontIcon("cil-exit-to-app")
            onLeftClick {
                exit()
            }
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
        startDateTime = LocalDateTime.now()
        restartsCounter = 0
        gamemode = CHOSEN_GAMEMODE

        iThinkLabel.apply {
            text = "Я думаю, вы загадали число..."
            textFill = Color.BLACK
        }
        aboutTimeLabel.text = ""
        answerLabel.textFill = Color.web("#249beb")

        submitCountsButton.isDisable = false

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
    @Suppress("UNCHECKED_CAST")
    private fun exit() {
        CowsAndBulls.clearGame()
        listOf(lastSequenceState, noMoreAnswersSequence).forEach { it.clear() }
        (historyTableViewContainer.children[0] as TableView<HistoryNote>).refresh()
        timerAnimation.apply { stop(); duration = Duration.ZERO }
        runLater {
            replaceWith<HomeView>()
        }
    }
    private fun restart() {
        CowsAndBulls.initializeGame(CHOSEN_SEQUENCE_LENGTH)
        duration = Duration.ZERO
        listOf(lastSequenceState, noMoreAnswersSequence).forEach {
            it.clear()
        }
        configureInitialState()
    }
    private fun victory() {
        iThinkLabel.text = "Вы загадали число: $actualAnswer"
        aboutTimeLabel.text = "Ответ был получен за время: ${duration.toBeautyString()}"

        timerAnimation.stop()

        answerLabel.textFill = Color.LIMEGREEN
        submitCountsButton.isDisable = true
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
            noMoreAnswersSequence.addAll(it)
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
                    prefWidthProperty().bind(historyTableViewContainer.widthProperty())
                    prefHeightProperty().bind(historyTableViewContainer.heightProperty())
                    columns.forEach {
                        it.prefWidthProperty().bind(
                            this.prefWidthProperty().div(columns.size)
                        )
                    }
                }
            )
        }
    }

    private fun configureTimer() {
        timerAnimation.keyFrames.clear()

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
