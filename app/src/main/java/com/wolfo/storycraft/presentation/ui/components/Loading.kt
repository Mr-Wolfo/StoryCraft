package com.wolfo.storycraft.presentation.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wolfo.storycraft.R
import com.wolfo.storycraft.presentation.theme.StoryCraftTheme

@Composable
fun FullScreenLoading(
    modifier: Modifier = Modifier
) {
    AppBackground(modifier) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            AnimatedAppLogo()
        }
    }
}

@Preview
@Composable
private fun FullScreenLoadingPreview() {
    StoryCraftTheme {
        FullScreenLoading()
    }
}