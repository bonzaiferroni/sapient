package sapient.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import moe.tlaster.precompose.koin.koinViewModel
import moe.tlaster.precompose.navigation.Navigator
import org.koin.core.parameter.parametersOf
import sapient.app.Scenes
import sapient.model.quest.Quest
import streetlight.app.chopui.BoxScaffold
import streetlight.app.chopui.Constants.BASE_PADDING
import streetlight.app.chopui.addBasePadding

@Composable
fun QuestProfileScreen(
    id: Int?,
    navigator: Navigator?
) {
    val model = koinViewModel(QuestProfileModel::class) { parametersOf(id) }
    val state by model.state
    BoxScaffold(
        title = "quest",
        navigator = navigator,
        floatingAction = model::startChildTarget
    ) {
        Column(
            horizontalAlignment = CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(BASE_PADDING),
            modifier = Modifier
                .fillMaxSize()
                .addBasePadding()
        ) {
            state.parent?.let {
                ParentCard(it, state.siblings, navigator)
            }
            state.quest?.let {
                QuestSection(it, model::onToggleComplete)
            }
            LazyColumn {
                items(state.children) { quest ->
                    Button(onClick = { Scenes.questProfile.go(navigator, quest.id) }) {
                        Text(quest.target)
                    }
                }
            }
            state.newTarget?.let {
                Row {
                    TextField(it, onValueChange = model::childTargetUpdate)
                    Button(onClick = model::addChildTarget) {
                        Text("Add")
                    }
                }
            }
        }
    }
}

@Composable
fun ParentCard(
    parent: Quest,
    siblings: List<Quest>,
    navigator: Navigator?
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = parent.target,
                modifier = Modifier.padding(start = BASE_PADDING)
            )
            Row {
                siblings.forEach {
                    if (it.isCompleted) {
                        Text("●")
                    } else {
                        Text("○")
                    }
                }
            }
            Button(onClick = { Scenes.questProfile.go(navigator, parent.id) }) {
                Icon(imageVector = Icons.Default.KeyboardArrowUp, contentDescription = "Up")
            }
        }
    }
}

@Composable
fun QuestSection(
    quest: Quest,
    onToggleComplete: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = quest.target,
                modifier = Modifier.padding(start = BASE_PADDING),
                style = TextStyle(
                    fontSize = 24.sp, // Adjust the font size as needed
                    fontWeight = FontWeight.Bold, // Make the text bold
                )
            )
            Checkbox(checked = quest.isCompleted, onCheckedChange = onToggleComplete)
        }
    }
}