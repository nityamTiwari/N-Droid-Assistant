package com.ferrytech.n_droid.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ferrytech.n_droid.data.model.ChatMode
import com.ferrytech.n_droid.data.model.Message
import com.ferrytech.n_droid.data.repository.GeminiRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {

    private val repository = GeminiRepository()

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _currentMode = MutableStateFlow(ChatMode.PROJECT_GENERATOR)
    val currentMode: StateFlow<ChatMode> = _currentMode.asStateFlow()

    fun setMode(mode: ChatMode) {
        _currentMode.value = mode
        clearChat()
    }

    fun sendMessage(userMessage: String, imageUri: android.net.Uri? = null, context: android.content.Context? = null) {
        if (userMessage.isBlank() && imageUri == null) return

        val cleanInput = userMessage.trim()

        // USER MESSAGE
        val userMsg = Message(
            id = "user_${System.currentTimeMillis()}",
            text = cleanInput,
            isUser = true,
            imageUri = imageUri?.toString()
        )
        _messages.update { it + userMsg }

        _isLoading.value = true

        // AI PLACEHOLDER
        val aiMsgId = "ai_${System.currentTimeMillis()}"
        val aiMsg = Message(
            id = aiMsgId,
            text = "",
            isUser = false
        )
        _messages.update { it + aiMsg }

        viewModelScope.launch {
            try {
                var fullResponse = ""

                var bitmap: android.graphics.Bitmap? = null
                if (imageUri != null && context != null) {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                        val source = android.graphics.ImageDecoder.createSource(context.contentResolver, imageUri)
                        bitmap = android.graphics.ImageDecoder.decodeBitmap(source)
                        // convert hardware bitmap to software to avoid genAI issues
                        bitmap = bitmap.copy(android.graphics.Bitmap.Config.ARGB_8888, true)
                    } else {
                        @Suppress("DEPRECATION")
                        bitmap = android.provider.MediaStore.Images.Media.getBitmap(context.contentResolver, imageUri)
                    }
                }

                repository.sendMessage(cleanInput, _currentMode.value, bitmap)
                    .collect { chunk ->

                        fullResponse += chunk

                        // 🔥 REMOVE ECHO HERE
                        val cleaned = removeEcho(fullResponse, cleanInput)

                        _messages.update { messages ->
                            messages.map { msg ->
                                if (msg.id == aiMsgId && !msg.isUser) {
                                    msg.copy(text = cleaned)
                                } else msg
                            }
                        }
                    }

            } catch (e: Exception) {
                _messages.update { messages ->
                    messages.map { msg ->
                        if (msg.id == aiMsgId) {
                            msg.copy(text = "Error: ${e.message}")
                        } else msg
                    }
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun removeEcho(response: String, userInput: String): String {
        val input = userInput.trim()

        return when {
            response.startsWith(input) -> {
                response.removePrefix(input).trim()
            }

            response.contains(input) -> {
                response.replace(input, "").trim()
            }

            else -> response
        }
    }

    fun clearChat() {
        _messages.value = emptyList()
    }
}