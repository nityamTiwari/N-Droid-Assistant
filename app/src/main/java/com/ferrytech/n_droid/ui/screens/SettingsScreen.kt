package com.ferrytech.n_droid.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ferrytech.n_droid.util.Constants

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit
) {
    var selectedModel by remember { mutableStateOf(Constants.MODEL_NAME) }
    var showModelDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // App Info Section
            SettingsSectionCard(
                title = "App Information",
                icon = Icons.Default.Info
            ) {
                SettingsItem(
                    label = "App Name",
                    value = "N-Droid Assistant"
                )
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                SettingsItem(
                    label = "Version",
                    value = "1.0.0"
                )
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                SettingsItem(
                    label = "Developer",
                    value = "Nityam Tiwari"
                )
            }

            // AI Model Section
            SettingsSectionCard(
                title = "AI Configuration",
                icon = Icons.Default.Psychology
            ) {
                SettingsItem(
                    label = "Current Model",
                    value = selectedModel
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { showModelDialog = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Change Model")
                }
            }

            // API Key Section
            SettingsSectionCard(
                title = "API Configuration",
                icon = Icons.Default.Key
            ) {
                SettingsItem(
                    label = "API Key Status",
                    value = if (Constants.GEMINI_API_KEY.isNotEmpty()) "✅ Configured" else "❌ Not Set"
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "API keys are stored securely in local.properties file.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // About Section
            SettingsSectionCard(
                title = "About",
                icon = Icons.Default.Info
            ) {
                Text(
                    text = "N-Droid is an AI-powered Android development assistant that helps you generate complete Android projects and debug errors using Google Gemini AI.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }

    // Model Selection Dialog
    if (showModelDialog) {
        ModelSelectionDialog(
            currentModel = selectedModel,
            onDismiss = { showModelDialog = false },
            onModelSelected = { model ->
                selectedModel = model
                showModelDialog = false
            }
        )
    }
}

@Composable
private fun SettingsSectionCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            content()
        }
    }
}

@Composable
private fun SettingsItem(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun ModelSelectionDialog(
    currentModel: String,
    onDismiss: () -> Unit,
    onModelSelected: (String) -> Unit
) {
    val models = listOf(
        "gemini-1.5-flash" to "Gemini 1.5 Flash (Fast & Efficient)",
        "gemini-1.5-flash-8b" to "Gemini 1.5 Flash 8B (Smaller & Faster)",
        "gemini-1.5-pro" to "Gemini 1 Pro (More Capable)",
        "gemini-2.0-flash-exp" to "Gemini 2.0 Flash (Experimental)"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select AI Model") },
        text = {
            Column {
                models.forEach { (modelId, modelName) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = currentModel == modelId,
                            onClick = { onModelSelected(modelId) }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = modelName,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}