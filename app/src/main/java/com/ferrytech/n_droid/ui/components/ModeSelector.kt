package com.ferrytech.n_droid.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ferrytech.n_droid.data.model.ChatMode

@Composable
fun ModeSelector(
    currentMode: ChatMode,
    onModeChanged: (ChatMode) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        ModeButton(
            text = "🛠️ Project Generator",
            isSelected = currentMode == ChatMode.PROJECT_GENERATOR,
            onClick = { onModeChanged(ChatMode.PROJECT_GENERATOR) },
            modifier = Modifier.weight(1f)
        )

        ModeButton(
            text = "🐞 Bug Debugger",
            isSelected = currentMode == ChatMode.BUG_DEBUGGER,
            onClick = { onModeChanged(ChatMode.BUG_DEBUGGER) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun ModeButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            },
            contentColor = if (isSelected) {
                MaterialTheme.colorScheme.onPrimary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge.copy(
                fontSize = 13.sp
            )
        )
    }
}