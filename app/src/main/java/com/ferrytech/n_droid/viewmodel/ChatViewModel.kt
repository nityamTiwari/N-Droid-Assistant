package com.ferrytech.n_droid.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ferrytech.n_droid.data.local.ChatSessionEntity
import com.ferrytech.n_droid.data.local.DatabaseProvider
import com.ferrytech.n_droid.data.local.toEntity
import com.ferrytech.n_droid.data.local.toMessage
import com.ferrytech.n_droid.data.model.ChatMode
import com.ferrytech.n_droid.data.model.Message
import com.ferrytech.n_droid.data.repository.GeminiRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

@OptIn(ExperimentalCoroutinesApi::class)
class ChatViewModel : ViewModel() {

    private val repository = GeminiRepository()
    private val dao = DatabaseProvider.database.chatDao()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _currentMode = MutableStateFlow(ChatMode.PROJECT_GENERATOR)
    val currentMode: StateFlow<ChatMode> = _currentMode.asStateFlow()

    private val _currentSessionId = MutableStateFlow(UUID.randomUUID().toString())
    val currentSessionId: StateFlow<String> = _currentSessionId.asStateFlow()

    val sessions: StateFlow<List<ChatSessionEntity>> = dao.getAllSessions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val messages: StateFlow<List<Message>> = combine(_currentSessionId, _currentMode) { sessionId, mode ->
        sessionId to mode.name
    }.flatMapLatest { (sessionId, modeName) ->
        dao.getMessages(sessionId, modeName).map { entities ->
            entities.map { it.toMessage() }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun createNewSession() {
        _currentSessionId.value = UUID.randomUUID().toString()
        _currentMode.value = ChatMode.PROJECT_GENERATOR
    }

    fun loadSession(sessionId: String) {
        _currentSessionId.value = sessionId
    }

    fun setMode(mode: ChatMode) {
        _currentMode.value = mode
    }

    fun sendMessage(userMessage: String, imageUri: android.net.Uri? = null, context: android.content.Context? = null) {
        if (userMessage.isBlank() && imageUri == null) return

        val cleanInput = userMessage.trim()
        val sessionId = _currentSessionId.value
        val mode = _currentMode.value

        viewModelScope.launch {
            // Create session if it doesn't exist
            val sessionExists = sessions.value.any { it.id == sessionId }
            if (!sessionExists) {
                val title = cleanInput.take(30).ifBlank { "New Chat" }
                dao.insertSession(ChatSessionEntity(id = sessionId, title = title))
            }

            // USER MESSAGE
            val userMsg = Message(
                id = "user_${System.currentTimeMillis()}",
                text = cleanInput,
                isUser = true,
                imageUri = imageUri?.toString()
            )
            dao.insertMessage(userMsg.toEntity(sessionId, mode.name))

            _isLoading.value = true

            // AI PLACEHOLDER
            val aiMsgId = "ai_${System.currentTimeMillis()}"
            val aiMsg = Message(
                id = aiMsgId,
                text = "",
                isUser = false
            )
            dao.insertMessage(aiMsg.toEntity(sessionId, mode.name))

            try {
                var fullResponse = ""
                var bitmap: android.graphics.Bitmap? = null
                if (imageUri != null && context != null) {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                        val source = android.graphics.ImageDecoder.createSource(context.contentResolver, imageUri)
                        bitmap = android.graphics.ImageDecoder.decodeBitmap(source)
                        bitmap = bitmap.copy(android.graphics.Bitmap.Config.ARGB_8888, true)
                    } else {
                        @Suppress("DEPRECATION")
                        bitmap = android.provider.MediaStore.Images.Media.getBitmap(context.contentResolver, imageUri)
                    }
                }

                repository.sendMessage(cleanInput, mode, bitmap)
                    .collect { chunk ->
                        fullResponse += chunk
                        val cleaned = removeEcho(fullResponse, cleanInput)
                        dao.updateMessageText(aiMsgId, cleaned)
                    }

            } catch (e: Exception) {
                dao.updateMessageText(aiMsgId, "Error: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun removeEcho(response: String, userInput: String): String {
        val input = userInput.trim()
        return when {
            response.startsWith(input) -> response.removePrefix(input).trim()
            response.contains(input) -> response.replace(input, "").trim()
            else -> response
        }
    }

    fun clearChat() {
        val sessionId = _currentSessionId.value
        val mode = _currentMode.value.name
        viewModelScope.launch {
            dao.clearMessagesForMode(sessionId, mode)
        }
    }
}