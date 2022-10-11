package com.example.views

import com.example.domain.HistoryNote
import com.example.functions.CowsAndBulls.getHistorySnapshot
import javafx.scene.Parent
import javafx.scene.canvas.Canvas
import javafx.scene.layout.AnchorPane
import tornadofx.*

class GameView: View("Game View") {
    override val root: Parent by fxml()

    private val changesMatrix: Canvas by fxid()
    private val historyTableViewContainer: AnchorPane by fxid()

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