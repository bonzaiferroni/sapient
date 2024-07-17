package sapient.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import moe.tlaster.precompose.koin.koinViewModel
import moe.tlaster.precompose.navigation.Navigator
import org.koin.core.parameter.parametersOf
import sapient.app.Scenes
import sapient.model.quest.Quest
import sapient.model.quest.QuestDto
import streetlight.app.chopui.Constants.BASE_PADDING
import streetlight.app.chopui.addBasePadding

@Composable
fun QuestProfileScreen(
    id: Int?,
    navigator: Navigator?
) {
    val model = koinViewModel(QuestProfileModel::class) { parametersOf(id) }
    val state by model.state

    AddStepDialog(
        newStepTarget = state.newQuest,
        onDismiss = model::cancelNewQuest,
        onConfirmation = model::addNewQuest,
        newStepTargetUpdate = model::updateNewQuest
    )

    Column(
        horizontalAlignment = CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(BASE_PADDING),
        modifier = Modifier
            .fillMaxSize()
            .addBasePadding()
    ) {
        state.parent?.let {
            ParentSection(
                quest = state.quest!!,
                parent = it,
                siblings = state.siblings,
                navigator = navigator
            )
        }
        state.quest?.let {
            QuestSection(
                navigator = navigator,
                quest = it,
                children = state.children,
                onToggleComplete = model::onToggleComplete,
                stepProgress = state.stepProgress,
                startAddChild = model::startAddChild,
            )
        }
        AvailableSection(
            navigator = navigator,
            steps = state.available,
            startAddParentless = model::startAddParentless
        )
    }
}

@Composable
fun AddStepDialog(
    newStepTarget: String?,
    onDismiss: () -> Unit,
    onConfirmation: () -> Unit,
    newStepTargetUpdate: (String) -> Unit
) {
    newStepTarget?.let {
        AlertDialog(
            title = {
                Text(text = "Enter a step")
            },
            text = {
                TextField(
                    value = newStepTarget,
                    onValueChange = newStepTargetUpdate,
                    label = { Text("Step") }
                )
            },
            onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(
                    onClick = onConfirmation
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = onDismiss
                ) {
                    Text("Dismiss")
                }
            }
        )
    }
}

@Composable
fun ParentSection(
    quest: Quest,
    parent: Quest,
    siblings: List<QuestDto>,
    navigator: Navigator?
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedButton(
            onClick = { Scenes.questProfile.go(navigator, parent.id) }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = parent.target)
                Icon(imageVector = Icons.Default.KeyboardArrowUp, contentDescription = "Up")
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            siblings.forEach {
                Text(
                    text = if (it.quest.isCompleted) "●" else "○",
                    style = TextStyle(
                        color = if (it.quest.id == quest.id)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurface
                    )
                )
            }
        }
    }
}

@Composable
fun QuestSection(
    navigator: Navigator?,
    quest: Quest,
    children: List<QuestDto>,
    stepProgress: Float,
    onToggleComplete: (Quest, Boolean) -> Unit,
    startAddChild: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(BASE_PADDING),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(BASE_PADDING),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (children.all { it.quest.isCompleted }) {
                    Checkbox(
                        checked = quest.isCompleted,
                        onCheckedChange = { onToggleComplete(quest, it) }
                    )
                } else {
                    CircularProgressIndicator(
                        progress = { stepProgress },
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.background
                    )
                }
                Text(
                    text = quest.target,
                    style = TextStyle(
                        fontSize = 24.sp, // Adjust the font size as needed
                        fontWeight = FontWeight.Bold, // Make the text bold
                    )
                )
            }
            Column(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Steps")
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(BASE_PADDING),
                ) {
                    items(children) { dto ->
                        StepRow(dto = dto, navigator = navigator, onToggleComplete)
                    }
                }
                IconButton(onClick = startAddChild) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
                }
            }
        }
    }
}

@Composable
fun StepRow(
    dto: QuestDto,
    navigator: Navigator?,
    updateCompleted: (Quest, Boolean) -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Checkbox(checked = dto.quest.isCompleted, onCheckedChange = {updateCompleted(dto.quest, it)})
        Button(onClick = { Scenes.questProfile.go(navigator, dto.quest.id) }) {
            Text(text = dto.quest.target)
        }
    }
}

@Composable
fun AvailableSection(
    navigator: Navigator?,
    steps: List<QuestDto>,
    startAddParentless: () -> Unit
) {
    Text("Available")
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(BASE_PADDING),
    ) {
        items(steps) { dto ->
            Button(onClick = { Scenes.questProfile.go(navigator, dto.quest.id) }) {
                Text(dto.quest.target)
            }
        }
    }
    IconButton(onClick = startAddParentless) {
        Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
    }
}
