package com.ferrytech.n_droid.data.repository

import com.ferrytech.n_droid.data.model.ChatMode
import com.ferrytech.n_droid.util.Constants
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GeminiRepository {

    private fun getGenerativeModel(): GenerativeModel {
        return GenerativeModel(
            modelName = Constants.MODEL_NAME,
            apiKey = Constants.GEMINI_API_KEY,
            generationConfig = generationConfig {
                temperature = 0.7f
                topK = 40
                topP = 0.95f
                maxOutputTokens = 8192
            }
        )
    }

    fun sendMessage(userMessage: String, mode: ChatMode): Flow<String> = flow {

        val systemPrompt = when (mode) {
            ChatMode.PROJECT_GENERATOR -> PROJECT_GENERATOR_PROMPT
            ChatMode.BUG_DEBUGGER -> BUG_DEBUGGER_PROMPT
        }

        try {
            val generativeModel = getGenerativeModel()

            val chat = generativeModel.startChat(
                history = listOf(
                    content("user") { text(systemPrompt) },
                    content("model") { text("Understood. I will follow all rules strictly.") }
                )
            )

            // 🔥 WRAPPED USER INPUT (ANTI-ECHO FIX)
            val finalPrompt = """
User Request:
${userMessage.trim()}

IMPORTANT:
- Do NOT repeat the user request
- Do NOT include the user input in response
- Only return the final answer
""".trimIndent()

            val response = chat.sendMessageStream(finalPrompt)

            response.collect { chunk ->
                emit(chunk.text ?: "")
            }

        } catch (e: Exception) {
            emit("Error: ${e.message ?: "Something went wrong."}")
        }
    }

    companion object {

        private const val PROJECT_GENERATOR_PROMPT = """
You are a Staff-level Android Engineer (10+ years experience).

CRITICAL RULE:
- NEVER repeat, restate, or include the user's input in your response.
- If you do, the response is incorrect.

Expertise:
Kotlin, Jetpack Compose, Material 3, MVVM, Clean Architecture.

MODE: COMPLETE ANDROID PROJECT GENERATOR

STRICT RULES:
- Kotlin ONLY
- Jetpack Compose ONLY (NO XML)
- MVVM + Clean Architecture
- Single Activity
- Navigation Compose
- StateFlow ONLY
- Material 3 ONLY
- No deprecated APIs
- Code MUST compile

PROJECT STRUCTURE:
com.example.app
- data
- domain
- ui (screens, components, navigation, theme)
- viewmodel
- MainActivity.kt

REQUIRED:
- Full runnable project
- All files
- Proper imports
- No placeholders

OUTPUT:
- File-by-file
- Clean code blocks
"""

        private const val BUG_DEBUGGER_PROMPT = """
You are a Staff-level Android Engineer (10+ years experience).

CRITICAL RULE:
- NEVER repeat, restate, or include the user's input in your response.
- If you do, the response is incorrect.

MODE: ANDROID DEBUGGER

WHAT TO DO:
1. Root cause
2. Fix
3. Prevention

RULES:
- Be precise
- No unnecessary explanation
- Provide exact fix
"""
    }
}