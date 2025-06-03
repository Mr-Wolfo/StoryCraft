package com.wolfo.storycraft.presentation.features.profile

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.wolfo.storycraft.R
import com.wolfo.storycraft.domain.model.story.StoryBaseInfo
import com.wolfo.storycraft.domain.model.user.User
import com.wolfo.storycraft.domain.model.user.UserSimple
import com.wolfo.storycraft.presentation.common.BackgroundImage
import com.wolfo.storycraft.presentation.common.CustomScrollableColumn
import com.wolfo.storycraft.presentation.common.ErrorBottomMessage
import com.wolfo.storycraft.presentation.common.GlassCard
import com.wolfo.storycraft.presentation.common.Loading
import com.wolfo.storycraft.presentation.common.LoadingBar
import com.wolfo.storycraft.presentation.common.PremiumInfoChip
import com.wolfo.storycraft.presentation.common.SuccessBottomMessage
import com.wolfo.storycraft.presentation.common.Utils
import com.wolfo.storycraft.presentation.features.story_list.AppStatusBarUiState
import com.wolfo.storycraft.presentation.features.story_list.PremiumBadge
import com.wolfo.storycraft.presentation.theme.extendedColors
import org.koin.androidx.compose.koinViewModel
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = koinViewModel(),
    onLogout: () -> Unit,
    onEditProfile: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val appStatusBarUiState by viewModel.appStatusBarUiState.collectAsState()
    val editMode by viewModel.editMode.collectAsState()

    val scrollState = rememberScrollState()
    var showDeleteConfirmation by remember { mutableStateOf<String?>(null) }

    // Для редактирования подписи
    var editingSignature by remember { mutableStateOf(false) }
    var newSignature by remember { mutableStateOf("") }

    // Для выбора нового аватара
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            viewModel.updateAvatar(it)
        }
    }

    when(val state = uiState) {
        is ProfileUiState.Idle -> { viewModel.loadProfile() }
        is ProfileUiState.Loading -> Loading()
        is ProfileUiState.Error -> Error(state.error)
        is ProfileUiState.Success -> {
            Profile(
                content = state.data,
                scrollState = scrollState,
                editMode = editMode,
                onToggleEditMode = { viewModel.toggleEditMode() },
                onAvatarClick = { if (editMode) launcher.launch("image/*") },
                onSignatureClick = {
                    if (editMode) {
                        newSignature = state.data.signature ?: ""
                        editingSignature = true
                    }
                },
                onLogout = onLogout,
                onDeleteStory = { storyId -> showDeleteConfirmation = storyId }
            )
        }
    }

    // Диалог подтверждения удаления истории
    showDeleteConfirmation?.let { storyId ->
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = null },
            title = { Text("Удаление истории") },
            text = { Text("Вы уверены, что хотите удалить эту историю?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteStory(storyId)
                        showDeleteConfirmation = null
                    }
                ) {
                    Text("Удалить", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmation = null }) {
                    Text("Отмена")
                }
            }
        )
    }

    // Диалог редактирования подписи
    if (editingSignature) {
        AlertDialog(
            onDismissRequest = { editingSignature = false },
            title = { Text("Редактировать подпись") },
            text = {
                TextField(
                    value = newSignature,
                    onValueChange = { newSignature = it },
                    label = { Text("Ваша подпись") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.updateSignature(newSignature)
                        editingSignature = false
                    }
                ) {
                    Text("Сохранить")
                }
            },
            dismissButton = {
                TextButton(onClick = { editingSignature = false }) {
                    Text("Отмена")
                }
            }
        )
    }

    when(val barState = appStatusBarUiState) {
        is AppStatusBarUiState.Idle -> { }
        is AppStatusBarUiState.Loading -> {
            Box(
                modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
                    .fillMaxSize().background(Color.Transparent),
                contentAlignment = Alignment.BottomCenter
            ) {
                LoadingBar(isVisible = true)
            }
        }
        is AppStatusBarUiState.Error -> {
            Box(
                modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars).padding(10.dp)
                    .fillMaxSize(),
                contentAlignment = Alignment.BottomCenter
            ) {
                ErrorBottomMessage(
                    message = barState.error.message ?: "Unknown error",
                    isVisible = true
                ) { }
            }
        }
        is AppStatusBarUiState.Success -> {
            Box(
                modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars).padding(10.dp)
                    .fillMaxSize(),
                contentAlignment = Alignment.BottomCenter
            ) {
                SuccessBottomMessage(
                    message = barState.message,
                    isVisible = true
                ) { }
            }
        }
    }
}

@Composable
fun Profile(
    content: User,
    scrollState: ScrollState,
    editMode: Boolean,
    onToggleEditMode: () -> Unit,
    onAvatarClick: () -> Unit,
    onSignatureClick: () -> Unit,
    onLogout: () -> Unit,
    onDeleteStory: (String) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        val backgroundColor = MaterialTheme.colorScheme.background

        Box(modifier = Modifier.fillMaxSize().background(backgroundColor)) {
            BackgroundImage(painter = painterResource(R.drawable.details_background))
        }

        CustomScrollableColumn(
            scrollState = scrollState,
            Modifier.fillMaxWidth()
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding()
                .padding(horizontal = 10.dp)
        ) {
            // Кнопка редактирования в правом верхнем углу
            Box(modifier = Modifier.fillMaxWidth()) {
                IconButton(
                    onClick = onToggleEditMode,
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Icon(
                        imageVector = if (editMode) Icons.Default.Done else Icons.Default.Edit,
                        contentDescription = "Редактировать",
                        tint = if (editMode) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .padding(30.dp)
                    .graphicsLayer {
                        translationY = scrollState.value * 0.5f
                    }
                    .clickable(
                        enabled = editMode,
                        onClick = onAvatarClick,
                        indication = if (editMode) ripple() else null,
                        interactionSource = remember { MutableInteractionSource() }
                    )
                    .border(
                        width = if (editMode) 2.dp else 0.dp,
                        color = if (editMode) MaterialTheme.colorScheme.primary else Color.Transparent,
                        shape = MaterialTheme.shapes.medium
                    )
            ) {
                Log.d("PROFILE", content.avatarUrl.toString())

                AsyncImage(
                    model = content.avatarUrl,
                    contentDescription = "Аватар",
                    contentScale = ContentScale.Crop,
                    error = painterResource(R.drawable.abstraction_profile),
                    modifier = Modifier.fillMaxSize()
                )

                if (editMode) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.4f))
                            .align(Alignment.CenterHorizontally),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Изменить аватар",
                            tint = Color.White,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }
            }

            GlassCard(
                modifier = Modifier.fillMaxWidth()
                    .clickable(
                        enabled = editMode,
                        onClick = onSignatureClick,
                        indication = if (editMode) ripple() else null,
                        interactionSource = remember { MutableInteractionSource() }
                    )
                    .border(
                        width = if (editMode) 2.dp else 0.dp,
                        color = if (editMode) MaterialTheme.colorScheme.primary else Color.Transparent,
                        shape = MaterialTheme.shapes.medium
                    )
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = content.username,
                        style = MaterialTheme.typography.headlineMedium,
                    )

                    HorizontalDivider(
                        modifier = Modifier.fillMaxSize(0.5f),
                        color = MaterialTheme.colorScheme.background
                    )

                    content.signature?.let {
                        Text(
                            text = it,
                            textAlign = TextAlign.Center
                        )
                    }
                    if (editMode) {
                        Text(
                            text = "Нажмите, чтобы изменить подпись",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            val dateTime = remember { Utils.toLocaleDateTime(content.createdAt) }
            val activeDays = Duration.between(dateTime, LocalDateTime.now()).toDays()

            GlassCard(Modifier.fillMaxWidth()) {
                Column {
                    FlowRow(
                        modifier = Modifier
                            .background(Color.Transparent)
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        PremiumInfoChip(
                            icon = Icons.Filled.Menu,
                            text = "${content.stories.size}",
                            subText = "Кол-во историй"
                        )
                        PremiumInfoChip(
                            icon = Icons.Filled.Star,
                            color = MaterialTheme.extendedColors.star,
                            text = "${content.overallRating}",
                            subText = "Рейтинг"
                        )
                        PremiumInfoChip(
                            icon = Icons.Filled.DateRange,
                            text = activeDays.toString(),
                            subText = "Дней в StoryCraft"
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            GlassCard(modifier = Modifier.heightIn(min = 0.dp, max = 350.dp)) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth().padding(5.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Ваши истории",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )

                    HorizontalDivider(
                        modifier = Modifier.fillMaxWidth(0.5f),
                        color = MaterialTheme.colorScheme.background
                    )

                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(150.dp),
                        contentPadding = PaddingValues(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(content.stories) { story ->
                            StoryByUserItem(
                                story = story,
                                onClick = {},
                                editMode = editMode,
                                onDelete = { onDeleteStory(story.id) }
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(80.dp))
        }

        // Кнопка выхода
        FloatingActionButton(
            onClick = onLogout,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp),
            containerColor = MaterialTheme.colorScheme.errorContainer
        ) {
            Icon(Icons.AutoMirrored.Filled.ExitToApp, "Выйти")
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun StoryByUserItem(
    story: StoryBaseInfo,
    onClick: () -> Unit,
    editMode: Boolean,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val borderColor = if (false) MaterialTheme.colorScheme.primary else Color.Transparent

    val dateTime = remember { Utils.toLocaleDateTime(story.publishedTime) }

    Box(modifier = modifier) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .clickable(onClick = onClick),
            shape = MaterialTheme.shapes.medium,
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            border = BorderStroke(
                width = if (false) 1.dp else 0.dp,
                color = borderColor
            )
        ) {

            Box(modifier = Modifier.fillMaxWidth()) {
                // Фоновое изображение
                AsyncImage(
                    model = story.coverImageUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                    placeholder = painterResource(R.drawable.ic_launcher_background),
                    error = painterResource(R.drawable.story_placeholder)
                )

                // Градиент для лучшей читаемости текста
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.7f)
                                ),
                                startY = 0.4f
                            )
                        )
                )

                // Контент
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        // Заголовок
                        Text(
                            text = story.title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            ),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Row(modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = MaterialTheme.extendedColors.star,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "%.1f".format(story.averageRating),
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = Color.White
                                )
                            )
                        }

                        val formattedDate = remember(dateTime) {
                            DateTimeFormatter
                                .ofPattern("dd.MM.yy", Locale.getDefault())
                                .format(dateTime)
                        }

                        // Дата
                        Text(
                            text = formattedDate,
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        )
                    }
                }

                // Иконка премиума
                if (false) { // is Premium?
                    PremiumBadge(modifier = Modifier.align(Alignment.TopEnd).size(35.dp))
                }
            }
        }

        if (editMode) {
            IconButton(
                onClick = onDelete,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .background(
                        color = MaterialTheme.colorScheme.errorContainer,
                        shape = CircleShape
                    )
                    .size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Удалить историю",
                    tint = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.size(12.dp)
                )
            }
        }
    }
}


@Preview
@Composable
fun ProfilePreview() {
    val scrollState = rememberScrollState()

    val content = User(
        id = "sdjhfjksdhf-dsfgdfg-fdgfg",
        username = "Wolfo",
        email = "test@gmail.com",
        signature = "This is MY Android App!",
        avatarUrl = "",
        createdAt = "2025-01-09T12:00:14.984Z",
        overallRating = 4.63f,
        stories = listOf(
            StoryBaseInfo(
                id = "dsgdfgdfg-fdgdf23r-dsf23",
                title = "My story",
                coverImageUrl = "",
                averageRating = 4.8f,
                publishedTime = "2025-05-07T12:21:56.288Z",
                description = null,
                viewCount = 0,
                author = UserSimple(
                    id = "sdjhfjksdhf-dsfgdfg-fdgfg",
                    username = "Wolfo",
                    avatarUrl = ""
                )
            ),
            StoryBaseInfo(
                id = "dsgdfgdfg-fdgdf23r-dsf23",
                title = "My story",
                coverImageUrl = "",
                averageRating = 4.8f,
                publishedTime = "2025-05-07T12:21:56.288Z",
                description = null,
                viewCount = 0,
                author = UserSimple(
                    id = "sdjhfjksdhf-dsfgdfg-fdgfg",
                    username = "Wolfo",
                    avatarUrl = ""
                )
            ),
            StoryBaseInfo(
                id = "dsgdfgdfg-fdgdf23r-dsf23",
                title = "My story",
                coverImageUrl = "",
                averageRating = 4.8f,
                publishedTime = "2025-05-07T12:21:56.288Z",
                description = null,
                viewCount = 0,
                author = UserSimple(
                    id = "sdjhfjksdhf-dsfgdfg-fdgfg",
                    username = "Wolfo",
                    avatarUrl = ""
                )
            ),StoryBaseInfo(
                id = "dsgdfgdfg-fdgdf23r-dsf23",
                title = "My story",
                coverImageUrl = "",
                averageRating = 4.8f,
                publishedTime = "2025-05-07T12:21:56.288Z",
                description = null,
                viewCount = 0,
                author = UserSimple(
                    id = "sdjhfjksdhf-dsfgdfg-fdgfg",
                    username = "Wolfo",
                    avatarUrl = ""
                )
            ),StoryBaseInfo(
                id = "dsgdfgdfg-fdgdf23r-dsf23",
                title = "My story",
                coverImageUrl = "",
                averageRating = 4.8f,
                publishedTime = "2025-05-07T12:21:56.288Z",
                description = null,
                viewCount = 0,
                author = UserSimple(
                    id = "sdjhfjksdhf-dsfgdfg-fdgfg",
                    username = "Wolfo",
                    avatarUrl = ""
                )
            ),StoryBaseInfo(
                id = "dsgdfgdfg-fdgdf23r-dsf23",
                title = "My story",
                coverImageUrl = "",
                averageRating = 4.8f,
                publishedTime = "2025-05-07T12:21:56.288Z",
                description = null,
                viewCount = 0,
                author = UserSimple(
                    id = "sdjhfjksdhf-dsfgdfg-fdgfg",
                    username = "Wolfo",
                    avatarUrl = ""
                )
            ),StoryBaseInfo(
                id = "dsgdfgdfg-fdgdf23r-dsf23",
                title = "My story",
                coverImageUrl = "",
                averageRating = 4.8f,
                publishedTime = "2025-05-07T12:21:56.288Z",
                description = null,
                viewCount = 0,
                author = UserSimple(
                    id = "sdjhfjksdhf-dsfgdfg-fdgfg",
                    username = "Wolfo",
                    avatarUrl = ""
                )
            ),StoryBaseInfo(
                id = "dsgdfgdfg-fdgdf23r-dsf23",
                title = "My story",
                coverImageUrl = "",
                averageRating = 4.8f,
                publishedTime = "2025-05-07T12:21:56.288Z",
                description = null,
                viewCount = 0,
                author = UserSimple(
                    id = "sdjhfjksdhf-dsfgdfg-fdgfg",
                    username = "Wolfo",
                    avatarUrl = ""
                )
            )
        )
    )
}

