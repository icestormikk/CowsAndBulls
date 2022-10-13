package com.example.screens

import com.example.configuration.setEqualWidthForColumns
import com.example.domain.StatisticsNote
import com.example.functions.CowsAndBulls
import javafx.scene.Parent
import javafx.scene.control.TableView
import javafx.scene.layout.FlowPane
import tornadofx.*

class StatisticsScreen: Fragment("StatisticsScreen") {
    override val root: Parent by fxml()

    private val statsContainer: FlowPane by fxid()

    init {
        CowsAndBulls.getStatistics().also { statisticsNotes ->
            statsContainer.add(
                tableview(statisticsNotes.asObservable()) {
                    readonlyColumn("Время начала игры", StatisticsNote::startDateTimeAsString)
                    readonlyColumn("Режим игры", StatisticsNote::gamemodeTitle)
                    readonlyColumn("Продолжительность игры", StatisticsNote::durationOfGameAsString)
                    readonlyColumn("Кол-во перезапусков", StatisticsNote::restartsCount)
                }.apply {
                    prefWidthProperty().bind(statsContainer.widthProperty())
                    prefHeightProperty().bind(statsContainer.heightProperty())
                    setEqualWidthForColumns()
                }
            )
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun causeStatsTableUpdate() =
        (statsContainer.children[0] as TableView<StatisticsNote>).refresh()
}
