package com.example.configuration

import javafx.scene.control.Alert
import javafx.scene.control.TableView
import javafx.util.Duration
import tornadofx.*
import java.util.*

private const val HOURS_IN_DAY = 24
private const val MINUTES_IN_HOUR = 60

fun <T> TableView<T>.setEqualWidthForColumns() {
    columns.forEach {
        it.prefWidthProperty().bind(
            this.prefWidthProperty().div(columns.size)
        )
    }
}

fun Duration.toBeautyString() = String.format(
    Locale.ENGLISH,
    "%02d:%02d:%02d",
    toHours().toInt() % HOURS_IN_DAY,
    toMinutes().toInt() % MINUTES_IN_HOUR,
    toSeconds().toInt() % MINUTES_IN_HOUR
)

fun callAlertWindow(headerText: String, content: String, title: String? = null) =
    alert(
        type = Alert.AlertType.ERROR,
        header = headerText,
        content = content,
        title = title ?: "The algorithms are not perfect"
    )