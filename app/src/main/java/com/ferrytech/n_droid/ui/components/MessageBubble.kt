package com.ferrytech.n_droid.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ferrytech.n_droid.data.model.Message

@Composable
fun MessageBubble(message: Message) {
    val context = LocalContext.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start
    ) {
        if (message.isUser) {
            // User message - simple bubble
            UserMessageBubble(message)
        } else {
            // AI message - parse and render with code blocks
            AIMessageBubble(message, context)
        }
    }
}

@Composable
private fun UserMessageBubble(message: Message) {
    Box(
        modifier = Modifier
            .widthIn(max = 300.dp)
            .clip(
                RoundedCornerShape(
                    topStart = 20.dp,
                    topEnd = 20.dp,
                    bottomStart = 20.dp,
                    bottomEnd = 4.dp
                )
            )
            .background(MaterialTheme.colorScheme.primary)
            .padding(14.dp)
    ) {
        Text(
            text = message.text,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White
        )
    }
}

@Composable
private fun AIMessageBubble(message: Message, context: Context) {
    Column(
        modifier = Modifier
            .widthIn(max = 340.dp)
    ) {
        val parts = parseMessageWithCodeBlocks(message.text)

        parts.forEach { part ->
            when (part) {
                is MessagePart.Text -> {
                    if (part.content.isNotBlank()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .padding(12.dp)
                        ) {
                            Text(
                                text = part.content.trim(),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                is MessagePart.Code -> {
                    CodeBlock(
                        code = part.content,
                        language = part.language,
                        context = context
                    )
                }
            }
        }
    }
}

@Composable
private fun CodeBlock(
    code: String,
    language: String,
    context: Context
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1E1E1E)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // Header with language and copy button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF2D2D2D))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = language.ifEmpty { "code" },
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(0xFF9CDCFE),
                    fontWeight = FontWeight.Bold
                )

                IconButton(
                    onClick = {
                        copyToClipboard(context, code)
                        Toast.makeText(context, "Code copied!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy code",
                        tint = Color(0xFF9CDCFE),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Code content
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(12.dp)
            ) {
                Text(
                    text = code.trim(),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 13.sp,
                        lineHeight = 20.sp
                    ),
                    color = Color(0xFFD4D4D4)
                )
            }
        }
    }
}

private fun copyToClipboard(context: Context, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("code", text)
    clipboard.setPrimaryClip(clip)
}

// Data classes for parsing
private sealed class MessagePart {
    data class Text(val content: String) : MessagePart()
    data class Code(val content: String, val language: String) : MessagePart()
}

private fun parseMessageWithCodeBlocks(text: String): List<MessagePart> {
    val parts = mutableListOf<MessagePart>()
    val codeBlockRegex = "```(\\w*)\\n([\\s\\S]*?)```".toRegex()

    var lastIndex = 0
    codeBlockRegex.findAll(text).forEach { matchResult ->
        // Add text before code block
        if (matchResult.range.first > lastIndex) {
            val textContent = text.substring(lastIndex, matchResult.range.first)
            if (textContent.isNotBlank()) {
                parts.add(MessagePart.Text(textContent))
            }
        }

        // Add code block
        val language = matchResult.groupValues[1]
        val code = matchResult.groupValues[2]
        parts.add(MessagePart.Code(code, language))

        lastIndex = matchResult.range.last + 1
    }

    // Add remaining text after last code block
    if (lastIndex < text.length) {
        val textContent = text.substring(lastIndex)
        if (textContent.isNotBlank()) {
            parts.add(MessagePart.Text(textContent))
        }
    }

    // If no code blocks found, treat entire message as text
    if (parts.isEmpty() && text.isNotBlank()) {
        parts.add(MessagePart.Text(text))
    }

    return parts
}