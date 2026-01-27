package com.wolfo.storycraft.presentation.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.wolfo.storycraft.presentation.theme.StoryCraftTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoryCraftDialog(
    onDismissRequest: () -> Unit,
    title: String,
    text: String,
    onConfirm: () -> Unit,
    confirmButtonText: String = "Подтвердить",
    dismissButtonText: String = "Отмена",
    content: @Composable () -> Unit = {}
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        titleContentColor = MaterialTheme.colorScheme.onBackground,
        textContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        shape = MaterialTheme.shapes.large,
        title = {
            Text(text = title, style = MaterialTheme.typography.headlineSmall)
        },
        text = {
            Text(text = text, style = MaterialTheme.typography.bodyMedium)
            content
        },
        confirmButton = {
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
                Text(text = dismissButtonText, color = MaterialTheme.colorScheme.primary)
            }
        }
    )
}


@Preview
@Composable
private fun StoryCraftDialogPreview() {
    StoryCraftTheme {
        StoryCraftDialog(
            onDismissRequest = {},
            title = "Выход из аккаунта",
            text = "Вы уверены, что хотите выйти? Ваш прогресс будет сохранен.",
            onConfirm = {},
            content = { }
        )
    }
}