package com.ferrytech.n_droid.data.repository

import com.ferrytech.n_droid.data.model.ChatMode
import com.ferrytech.n_droid.util.Constants
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.generationConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GeminiRepository {

    private fun getGenerativeModel(): GenerativeModel {
        return GenerativeModel(
            modelName = Constants.MODEL_NAME,  //
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
            // Initialize chat with system prompt as the first message or history
            val chat = generativeModel.startChat(
                history = listOf(
                    com.google.ai.client.generativeai.type.content("user") { text(systemPrompt) },
                    com.google.ai.client.generativeai.type.content("model") { text("Understood. I will follow those instructions precisely.") }
                )
            )

            val response = chat.sendMessageStream(userMessage)
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

Expertise:
Kotlin, Jetpack Compose, Material 3, MVVM, Clean Architecture.

MODE: COMPLETE ANDROID PROJECT GENERATOR

Your task is to generate a FULLY RUNNABLE Android project.

════════════════════
STRICT RULES
════════════════════
- Kotlin ONLY
- Jetpack Compose ONLY (NO XML)
- MVVM + Clean Architecture
- Single Activity
- Navigation Compose
- StateFlow / MutableStateFlow ONLY
- Material 3 ONLY
- No deprecated APIs
- No pseudo-code
- Code MUST compile

════════════════════
PROJECT STRUCTURE
════════════════════
Use this structure:

com.example.app
- data (model, repository)
- domain (optional)
- ui
  - screens
  - components
  - navigation
  - theme
- viewmodel
- MainActivity.kt

════════════════════
REQUIRED FILES
════════════════════
You MUST generate:

1. MainActivity.kt
2. Navigation graph (NavHost)
3. Minimum 2 screens (Compose UI)
4. ViewModels (StateFlow based)
5. Repository (mock data allowed)
6. Data models
7. Reusable components
8. Material 3 theme

════════════════════
UI REQUIREMENTS
════════════════════
- Modern production-ready UI
- Clean layout with proper spacing
- Rounded shapes and cards
- Soft color palette
- Responsive design

════════════════════
API KEY RULES
════════════════════
- NEVER hardcode API keys
- Use: YOUR_API_KEY_HERE
- Show usage via:
  local.properties + BuildConfig

════════════════════
OUTPUT FORMAT (VERY IMPORTANT)
════════════════════
- File-by-file code
- Use this format:

// File: ui/screens/HomeScreen.kt

- Include ALL imports
- Do NOT skip files
- Do NOT summarize

════════════════════
FINAL GOAL
════════════════════
Project must:
✔ Compile without errors
✔ Run instantly after copy-paste
✔ Follow best practices

Generate now.
        ""${'"'}
        """

        private const val BUG_DEBUGGER_PROMPT = """
You are a Staff-level Android Engineer (10+ years experience).

MODE: ANDROID DEBUGGER

Your task is to analyze and fix Android errors.

════════════════════
WHAT YOU MUST DO
════════════════════
1. Identify the EXACT root cause
2. Explain WHY it happened (short & clear)
3. Pinpoint file / line (if possible)
4. Provide FIXED Kotlin code
5. Ensure fix does NOT break existing logic
6. Suggest best practices to prevent it

════════════════════
RULES
════════════════════
- Assume Jetpack Compose + MVVM
- Use modern Android practices
- No guessing
- No unnecessary theory
- Be precise and actionable

════════════════════
OUTPUT FORMAT
════════════════════
ROOT CAUSE:
<clear reason>

FIX:
<corrected code>

PREVENTION:
<best practices>

Analyze now.
        """
    }
}