package sapient.app

import moe.tlaster.precompose.navigation.BackStackEntry
import moe.tlaster.precompose.navigation.path
import sapient.app.ui.screens.QuestHistoryScreen
import sapient.app.ui.screens.QuestProfileScreen

object Scenes {
    val default = { questProfile.route }

    val questProfile = AppScene(
        name = "Quest Profile",
        route = "/quest/{id}?"
    ) { bse, navigator ->
        QuestProfileScreen(bse.getId(), navigator)
    }

    val questHistory = AppScene(
        name = "Quest History",
        route = "/quest/history"
    ) { _, navigator ->
        QuestHistoryScreen(navigator)
    }

    /*val debug = AppScene(
        name = "Debug",
        route = "/debug",
    ) { _, navigator ->
        DebugScreen(navigator)
    }

    val userEditor = AppScene(
        name = "Create User",
        route = "/user/{id}?"
    ) { bse, navigator ->
        UserEditorScreen(bse.getId(), navigator)
    }

    val locationList = AppScene(
        name = "Locations",
        route = "/locations"
    ) { _, navigator ->
        LocationListScreen(navigator)
    }

    val locationEditor = AppScene(
        name = "Create Location",
        route = "/location/{id}?"
    ) { bse, navigator ->
        LocationEditorScreen(bse.getId(), navigator)
    }*/
}

fun BackStackEntry.getId() = this.path<Int>("id")

val appScenes = listOf(
    Scenes.questProfile,
    Scenes.questHistory,
)

