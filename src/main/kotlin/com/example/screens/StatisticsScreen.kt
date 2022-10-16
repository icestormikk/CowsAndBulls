package com.example.screens

import com.example.configuration.Gamemode
import com.example.configuration.setEqualWidthForColumns
import com.example.domain.StatisticsNote
import com.example.functions.CowsAndBulls.getStatistics
import javafx.scene.Parent
import javafx.scene.control.Label
import javafx.scene.control.TableView
import javafx.scene.layout.FlowPane
import tornadofx.*
import java.util.*

class StatisticsScreen: Fragment("StatisticsScreen") {
    override val root: Parent by fxml()

    private val statsContainer: FlowPane by fxid()
    private val favoriteMode: Label by fxid()
    private val averageTime: Label by fxid()
    private val leaderComputerAverageTime: Label by fxid()
    private val leaderPlayerAverageTime: Label by fxid()

    init {
        getStatistics().also { statisticsNotes ->
            statsContainer.add(
                tableview(statisticsNotes.asObservable()) {
                    readonlyColumn("Время начала игры", StatisticsNote::startDateTimeAsString)
                    readonlyColumn("Режим игры", StatisticsNote::gamemodeTitle)
                    readonlyColumn("Длительность игры", StatisticsNote::durationAsBeautyString)
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
    fun causeStatsTableUpdate() {
        (statsContainer.children[0] as TableView<StatisticsNote>).refresh()
        with(getStatistics()) {
            if (isNotEmpty()) {
                favoriteMode.text = groupBy { it.gamemodeTitle }.maxBy { it.value.size }.key
                leaderComputerAverageTime.text = String.format(
                    Locale.ENGLISH,
                    "%6f сек.",
                    filter { it.gamemodeTitle == Gamemode.LEADER_COMPUTER.title }
                        .map { it.durationOfGameInSeconds }.average()
                )
                leaderPlayerAverageTime.text = String.format(
                    Locale.ENGLISH,
                    "%6f сек.",
                    filter { it.gamemodeTitle == Gamemode.LEADER_PLAYER.title }
                        .map { it.durationOfGameInSeconds }.average()
                )
            }
        }
    }
}
