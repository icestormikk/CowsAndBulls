package com.example

import com.example.views.WelcomeView
import tornadofx.*

class TornadoApp: App(WelcomeView::class) {
    fun main(args: Array<String>) {
        launch<TornadoApp>(args)
    }
}