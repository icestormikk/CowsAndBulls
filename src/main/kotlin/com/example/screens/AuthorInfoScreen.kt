package com.example.screens

import javafx.event.EventHandler
import javafx.scene.Parent
import javafx.scene.control.Hyperlink
import javafx.scene.image.ImageView
import javafx.scene.layout.FlowPane
import org.kordamp.ikonli.javafx.FontIcon
import tornadofx.*

private const val ACCOUNT_GITHUB_LINK = "https://github.com/icestormikk"
private const val PROJECT_CODE_ON_GITHUB = "https://github.com/icestormikk/CowsAndBulls"
private const val KOTLIN_LOGO_LINK = "static/icons/kotlin-logo.png"
private const val KOTLIN_LOGO_HEIGHT = 40.0
private const val KOTLIN_LOGO_WIDTH = 200.0
private const val TORNADO_FX_LOGO_LINK = "static/icons/tornado-fx-logo.png"
private const val TORNADO_LOGO_HEIGHT = 110.0
private const val TORNADO_LOGO_WIDTH = 190.0

class AuthorInfoScreen: Fragment("AuthorInfoScreen") {
    override val root: Parent by fxml()

    private val githubAccount: FlowPane by fxid()
    private val accountLink: Hyperlink by fxid()
    private val repositoryBlock: FlowPane by fxid()
    private val repositoryLink: Hyperlink by fxid()
    private val logosContainer: FlowPane by fxid()

    init {
        logosContainer.children.addAll(
            ImageView(KOTLIN_LOGO_LINK).apply {
                fitHeight = KOTLIN_LOGO_HEIGHT; fitWidth = KOTLIN_LOGO_WIDTH
            },
            ImageView(TORNADO_FX_LOGO_LINK).apply {
                fitHeight = TORNADO_LOGO_HEIGHT; fitWidth = TORNADO_LOGO_WIDTH
            }
        )

        with (githubAccount) {
            children.add(0, FontIcon("cib-github"))
            addClass("author-info-links")
        }
        accountLink.onAction = EventHandler {
            hostServices.showDocument(ACCOUNT_GITHUB_LINK)
        }

        with (repositoryBlock) {
            children.add(0, FontIcon("cib-github"))
            addClass("author-info-links")
        }
        repositoryLink.onAction = EventHandler {
            hostServices.showDocument(PROJECT_CODE_ON_GITHUB)
        }
    }
}
