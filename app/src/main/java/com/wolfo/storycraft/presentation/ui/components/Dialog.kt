package com.wolfo.storycraft.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import com.wolfo.storycraft.presentation.theme.StoryCraftTheme
import com.wolfo.storycraft.presentation.theme.spacing

@Composable
fun AppDialog(
    title: String,
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
    text: String? = null,
    confirmButtonText: String = "Подтвердить",
    dismissButtonText: String = "Отмена",
    icon: ImageVector? = null,
    content: @Composable (() -> Unit)? = null
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismissRequest,
        containerColor = MaterialTheme.colorScheme.surface,
        icon = icon?.let {
            { Icon(it, contentDescription = null, tint = MaterialTheme.colorScheme.primary) }
        },
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
            ) {
                if (text != null) {
                    Text(
                        text = text,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                content?.invoke()
            }
        },
        confirmButton = {
            // Твоя кастомная кнопка
            StoryCraftButton(
                text = confirmButtonText,
                onClick = {
                    onConfirm()
                    onDismissRequest()
                }
            )
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(
                    text = dismissButtonText,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        shape = MaterialTheme.shapes.extraLarge
    )
}


@Preview
@Composable
private fun StoryCraftDialogPreview() {
    StoryCraftTheme {
        AppDialog(
            onDismissRequest = {},
            title = "Выход из аккаунта",
            text = "Вы уверены, что хотите выйти? Ваш прогресс будет сохранен.",
            onConfirm = {},
            content = { }
        )
    }
}