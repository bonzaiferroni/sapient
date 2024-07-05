package sapient.app

import moe.tlaster.precompose.navigation.BackStackEntry
import moe.tlaster.precompose.navigation.path

object Scenes {
    val default = { questList.route }

    val questList = AppScene(
        name = "Quests",
        route = "/quests"
    ) { _, navigator ->
        QuestListScreen(navigator)
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
    Scenes.questList
)

