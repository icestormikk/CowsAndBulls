package com.example

import com.example.functions.CowsAndBulls
import com.example.views.WelcomeView
import javafx.stage.Stage
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import tornadofx.*
import java.io.File

private const val STATISTICS_FILE_PATH = "statsData.json"

class TornadoApp: App(WelcomeView::class) {
    @OptIn(ExperimentalSerializationApi::class)
    override fun start(stage: Stage) {
        CowsAndBulls.initiateStatistics(
            Json.decodeFromStream(File(STATISTICS_FILE_PATH).inputStream())
        )
        super.start(stage)
    }

    fun main(args: Array<String>) {
        launch<TornadoApp>(args)
    }

    override fun stop() {
        File(STATISTICS_FILE_PATH).appendText(
            text = Json.encodeToString(CowsAndBulls.getStatistics()),
            charset = Charsets.UTF_8
        )
        super.stop()
    }
}
