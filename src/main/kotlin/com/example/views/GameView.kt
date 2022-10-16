package com.example.views

import com.example.configuration.CHOSEN_GAMEMODE
import com.example.configuration.CHOSEN_SEQUENCE_LENGTH
import com.example.configuration.Gamemode
import com.example.configuration.Gamemode.LEADER_COMPUTER
import com.example.configuration.Gamemode.LEADER_PLAYER
import com.example.configuration.callAlertWindow
import com.example.domain.HistoryNote
import com.example.functions.CowsAndBulls
import com.example.functions.CowsAndBulls.getActualSequence
import com.example.functions.CowsAndBulls.getHistorySnapshot
import com.example.configuration.setEqualWidthForColumns
import com.example.configuration.toBeautyString
import com.example.domain.StatisticsNote
import com.example.screens.LeaderComputerScreen
import com.example.screens.LeaderPlayerScreen
import javafx.animation.KeyFrame
import javafx.animation.Timeline
import javafx.beans.property.SimpleStringProperty
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TableView
import javafx.scene.control.TextArea
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.BorderPane
import javafx.util.Duration
import org.kordamp.ikonli.javafx.FontIcon
import tornadofx.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.properties.Delegates

class GameView: View("Game View") {
    override val root: BorderPane by fxml()
    private val leaderComputerScreen = find(LeaderComputerScreen::class)
    private val leaderPlayerScreen = find(LeaderPlayerScreen::class)

    companion object GameStats {
        private lateinit var startDateTime: LocalDateTime
        private lateinit var gamemode : Gamemode
        private var restartsCounter by Delegates.notNull<Int>()
    }
    var gameDuration: Duration = Duration.ZERO

    private val historyTableViewContainer: AnchorPane by fxid()
    private val stopwatchLabel: Label by fxid()
    private val noMoreAnswersCount: Label by fxid()
    private val lostedNumbersCount: Label by fxid()
    private val possibleAnswersCount: Label by fxid()
    private val possibleAnswers: TextArea by fxid()
    private val noMoreAnswers: TextArea by fxid()
    private val restartButton: Button by fxid()
    private val exitButton: Button by fxid()

    private var timerAnimation = Timeline()
    private val stringTimeProperty = SimpleStringProperty()
    private var lastSequenceState = mutableSetOf<String>()
    private var noMoreAnswersSequence = mutableSetOf<String>()

    init {
        configureHistoryView()
        stopwatchLabel.textProperty().bind(stringTimeProperty)

        restartButton.apply {
            graphic = FontIcon("cil-loop-circular")
            onLeftClick { restart() }
        }
        exitButton.apply {
            graphic = FontIcon("cil-exit-to-app")
            onLeftClick { exit() }
        }
    }

    fun configureInitialState(isRestart: Boolean = false) {
        println(CHOSEN_GAMEMODE)
        when (CHOSEN_GAMEMODE) {
            LEADER_COMPUTER -> {
                root.center.replaceWith(leaderComputerScreen.root)
                leaderComputerScreen.configureInitialState()
            }
            LEADER_PLAYER -> {
                root.center.replaceWith(leaderPlayerScreen.root)
                leaderPlayerScreen.configureInitialState()
            }
        }
        startDateTime = LocalDateTime.now()
        if (!isRestart)
            restartsCounter = 0
        gamemode = CHOSEN_GAMEMODE

        configureTimer()
        gameStatisticsFieldsUpdate()
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
        println("RESTART")
        restartsCounter++

        CowsAndBulls.initializeGame(CHOSEN_SEQUENCE_LENGTH)
        duration = Duration.ZERO
        listOf(lastSequenceState, noMoreAnswersSequence).forEach {
            it.clear()
        }

        when (CHOSEN_GAMEMODE) {
            LEADER_PLAYER -> {
                leaderPlayerScreen.restart()
            }
            else -> {}
        }

        configureInitialState(isRestart = true)
    }
    private fun victory() {
        iThinkLabel.text = "Вы загадали число: $actualAnswer"
        aboutTimeLabel.text = "Ответ был получен за время: ${duration.toBeautyString()}"

        timerAnimation.stop()
        CowsAndBulls.addStatistics(
            StatisticsNote(
                startDateTime.format(DateTimeFormatter.ofPattern("dd MMM yyyy - HH:mm:ss")),
                gamemode.title,
                duration.toSeconds(),
                restartsCounter,
                duration.toBeautyString()
            )
        )
    }
    private fun error() {
        callAlertWindow(
            headerText = "Что-то пошло не так...",
            content = "Не удалось сделать предположение о следующем числе. Возможно, " +
                    "вы ввели информацию, расходящуюся с загаданным Вами числом.\n" +
                    "Проверьте историю ходов и нажмите кнопку \"Рестарт\"."
        )
        timerAnimation.stop()
    }

    @Suppress("UNCHECKED_CAST")
    private fun gameStatisticsFieldsUpdate() {
        (historyTableViewContainer.children[0] as TableView<HistoryNote>)
            .refresh()

        if (CHOSEN_GAMEMODE != LEADER_COMPUTER) {
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
                    setEqualWidthForColumns()
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
                    stringTimeProperty.set(toBeautyString())
                }
            })
        ).apply {
            cycleCount = Timeline.INDEFINITE
            play()
        }
    }
}
