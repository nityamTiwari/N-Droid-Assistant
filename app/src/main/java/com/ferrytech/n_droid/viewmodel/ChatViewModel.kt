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
    }

    fun sendMessage(userMessage: String) {
        if (userMessage.isBlank()) return

        // Add user message
        val userMsg = Message(text = userMessage, isUser = true)
        _messages.update { it + userMsg }
        _isLoading.value = true

        // Add placeholder for AI response
        val aiMsgId = System.currentTimeMillis().toString()
        val aiMsg = Message(id = aiMsgId, text = "", isUser = false)
        _messages.update { it + aiMsg }

        viewModelScope.launch {
            var fullResponse = ""
            repository.sendMessage(userMessage, _currentMode.value).collect { chunk ->
                fullResponse += chunk
                _messages.update { messages ->
                    messages.map { msg ->
                        if (msg.id == aiMsgId) {
                            msg.copy(text = fullResponse)
                        } else {
                            msg
                        }
                    }
                }
            }
            _isLoading.value = false
        }
    }

    fun clearChat() {
        _messages.value = emptyList()
    }
}