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
}