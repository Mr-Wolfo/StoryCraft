package com.wolfo.storycraft.presentation.features.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.wolfo.storycraft.presentation.common.CustomScrollableColumn
import com.wolfo.storycraft.presentation.common.Error
import com.wolfo.storycraft.presentation.common.GlassCard
import com.wolfo.storycraft.presentation.common.Loading
import com.wolfo.storycraft.presentation.common.PremiumInfoChip
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = koinViewModel(),
    onLogout: () -> Unit,
    onEditProfile: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    when {
        uiState.isLoading -> Loading()
        uiState.error != null -> Error(uiState.error!!)
        else -> {
            Box(modifier = Modifier.fillMaxSize()) {
                // Параллакс-фон с аватаркой
                AsyncImage(
                    model = "https://sun9-3.userapi.com/impf/c630524/v630524916/10c52/ppv3IfW5_U4.jpg?size=640x400&quality=96&sign=1e96bd02712d03e3d66bc2343bb89f9e&c_uniq_tag=9rKScFdNWF-EbgOtU1YfowKoB8BnyvHD9T214LM-kCM&type=album", // uiState.user?.avatarUrl,
                    contentDescription = "Аватар",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .graphicsLayer {
                            translationY = scrollState.value * 0.5f
                        }
                )

                CustomScrollableColumn(scrollState = scrollState, Modifier.fillMaxWidth()) {
                    Spacer(modifier = Modifier.height(250.dp))

                    // Основная информация
                    GlassCard(Modifier.fillMaxWidth()) {
                        Column() {
                            Text(
                                text = uiState.user?.userName ?: "",
                                style = MaterialTheme.typography.headlineMedium
                            )

                            // Статистика (аналогично StoryDetails)
                            FlowRow(modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceAround) {
                                PremiumInfoChip(icon = Icons.Filled.Menu,
                                    text = "${12}",
                                    subText = "Кол-во историй") // Кол-во историй
                                PremiumInfoChip(
                                    icon = Icons.Filled.Star,
                                    text = "4.8",
                                    subText = "Рейтинг"
                                ) // Рейтинг
                            }
                        }
                    }

                    // Истории пользователя
                    /*LazyVerticalGrid(columns = GridCells.Adaptive(150.dp)) {
                        // --Необходимо добавить!!--
                        items(uiState.stories) { story ->
                            StoryCard(story, onClick = {  ...  })
                        }
                    }*/
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
    }
}