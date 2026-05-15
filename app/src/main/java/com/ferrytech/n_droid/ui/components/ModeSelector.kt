package com.ferrytech.n_droid.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ferrytech.n_droid.data.model.ChatMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModeSelector(
    currentMode: ChatMode,
    onModeChanged: (ChatMode) -> Unit
) {

    val modes = listOf(
        ChatMode.PROJECT_GENERATOR,
        ChatMode.BUG_DEBUGGER,
        ChatMode.UI_BUILDER
    )

    SingleChoiceSegmentedButtonRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {

        modes.forEachIndexed { index, mode ->

            SegmentedButton(
                selected = currentMode == mode,
                onClick = {
                    onModeChanged(mode)
                },

                icon = {},

                shape = SegmentedButtonDefaults.itemShape(
                    index = index,
                    count = modes.size
                ),
                colors = SegmentedButtonDefaults.colors(

                    activeContainerColor =
                        MaterialTheme.colorScheme.primary,

                    activeContentColor =
                        MaterialTheme.colorScheme.onPrimary,

                    inactiveContainerColor =
                        MaterialTheme.colorScheme.surfaceVariant,

                    inactiveContentColor =
                        MaterialTheme.colorScheme.onSurfaceVariant
                ),

                label = {

                    Text(
                        text = when (mode) {

                            ChatMode.PROJECT_GENERATOR ->
                                "🛠️ Generator"

                            ChatMode.BUG_DEBUGGER ->
                                "🐞 Bug Fix"

                            ChatMode.UI_BUILDER ->
                                "🖼️ UI"
                        }
                    )
                }
            )
        }
    }
}