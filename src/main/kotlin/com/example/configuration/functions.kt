package com.example.configuration

import javafx.scene.control.TableView
import tornadofx.*

fun <T> TableView<T>.setEqualWidthForColumns() {
    columns.forEach {
        it.prefWidthProperty().bind(
            this.prefWidthProperty().div(columns.size)
        )
    }
}
