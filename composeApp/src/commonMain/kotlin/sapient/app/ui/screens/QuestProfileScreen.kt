package sapient.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Place
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import moe.tlaster.precompose.koin.koinViewModel
import moe.tlaster.precompose.navigation.Navigator
import org.koin.core.parameter.parametersOf
import sapient.app.Scenes
import sapient.app.ui.AppScaffold
import sapient.model.quest.Quest
import sapient.model.quest.QuestDto
import streetlight.app.chopui.ChopScaffold
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

    AppScaffold(
        title = "Quest Profile",
        navigator = navigator,
    ) {
        Column(
            horizontalAlignment = CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(BASE_PADDING),
            modifier = Modifier
                .fillMaxSize()
                .addBasePadding()
        ) {
            state.quest?.let {
                ParentSection(
                    quest = it,
                    movingQuest = state.movingQuest,
                    parent = state.parent,
                    siblings = state.siblings,
                    navigator = navigator,
                    startMove = model::startMove,
                    cancelMove = model::cancelMove,
                )
                QuestSection(
                    navigator = navigator,
                    quest = it,
                    movingQuest = state.movingQuest,
                    children = state.children,
                    onToggleComplete = model::onToggleComplete,
                    stepProgress = state.stepProgress,
                    startAddChild = model::startAddChild,
                    onToggleEdit = model::toggleEdit,
                    editQuest = state.editQuest,
                    updateTarget = model::updateTarget,
                    onDeleteProfile = model::deleteProfile,
                    onSaveProfile = model::saveProfile,
                    moveQuest = model::moveQuest
                )
            }
            AvailableSection(
                navigator = navigator,
                available = state.available,
                roots = state.roots,
                startAddParentless = model::startAddParentless,
            )
        }
    }
}

@Composable
fun AddStepDialog(
    newStepTarget: String?,
    onDismiss: () -> Unit,
    onConfirmation: () -> Unit,
    newStepTargetUpdate: (String) -> Unit
) {
    val focusRequester = remember { FocusRequester() }

    newStepTarget?.let {
        AlertDialog(
            title = {
                Text(text = "Enter a step")
            },
            text = {
                TextField(
                    value = newStepTarget,
                    onValueChange = newStepTargetUpdate,
                    label = { Text("Step") },
                    modifier = Modifier.focusRequester(focusRequester),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onNext = { onConfirmation() }),
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

        LaunchedEffect(newStepTarget) {
            focusRequester.requestFocus()
        }
    }
}

@Composable
fun ParentSection(
    quest: Quest,
    movingQuest: Quest?,
    parent: Quest?,
    siblings: List<QuestDto>,
    navigator: Navigator?,
    startMove: () -> Unit,
    cancelMove: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            parent?.let {
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
            }
            Spacer(modifier = Modifier.weight(1f))
            movingQuest?.let {
                IconButton(onClick = cancelMove) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Cancel")
                }
            } ?: run {
                IconButton(onClick = startMove) {
                    Icon(imageVector = Icons.Default.Place, contentDescription = "Move")
                }
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            siblings.forEach {
                Text(
                    text = if (it.quest.isCompleted) "●" else "○",
                    style = TextStyle(
                        color = if (it.quest.id == quest?.id)
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
    movingQuest: Quest?,
    children: List<QuestDto>,
    editQuest: Boolean,
    stepProgress: Float,
    onToggleComplete: (Quest, Boolean) -> Unit,
    startAddChild: () -> Unit,
    onToggleEdit: () -> Unit,
    updateTarget: (String) -> Unit,
    onDeleteProfile: (() -> Unit) -> Unit,
    onSaveProfile: () -> Unit,
    moveQuest: () -> Unit
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
                        trackColor = MaterialTheme.colorScheme.background,
                        modifier = Modifier.padding(4.dp)
                    )
                }
                if (editQuest) {
                    TextField(
                        value = quest.target,
                        onValueChange = updateTarget,
                        label = { Text("Quest") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { }),
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    Text(
                        text = quest.target,
                        style = TextStyle(
                            fontSize = 24.sp, // Adjust the font size as needed
                            fontWeight = FontWeight.Bold, // Make the text bold
                        )
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                if (editQuest) {
                    IconButton(onClick = onToggleEdit) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                    IconButton(onClick = { onDeleteProfile { Scenes.questProfile.go(navigator) }}) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete")
                    }
                    IconButton(onClick = onSaveProfile) {
                        Icon(imageVector = Icons.Default.Done, contentDescription = "Save")
                    }
                } else {
                    IconButton(onClick = onToggleEdit) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit")
                    }
                }
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
                Row {
                    IconButton(onClick = startAddChild) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
                    }
                    if (movingQuest != null && movingQuest.id != quest.id) {
                        IconButton(onClick = moveQuest) {
                            Icon(imageVector = Icons.Default.Place, contentDescription = "Move")
                        }
                    }
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
        Checkbox(
            checked = dto.quest.isCompleted,
            onCheckedChange = { updateCompleted(dto.quest, it) })
        Button(onClick = { Scenes.questProfile.go(navigator, dto.quest.id) }) {
            Text(text = dto.quest.target)
        }
    }
}

@Composable
fun AvailableSection(
    navigator: Navigator?,
    available: List<QuestDto>,
    roots: List<QuestDto>,
    startAddParentless: () -> Unit
) {
    Text("Available")
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(BASE_PADDING),
    ) {
        items(available) { dto ->
            Button(onClick = { Scenes.questProfile.go(navigator, dto.quest.id) }) {
                Text(dto.quest.target)
            }
        }
    }
    IconButton(onClick = startAddParentless) {
        Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
    }
    Text("Roots")
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(BASE_PADDING),
    ) {
        items(roots) {
            Button(onClick = { Scenes.questProfile.go(navigator, it.quest.id) }) {
                Text(it.quest.target)
            }
        }
    }
}
