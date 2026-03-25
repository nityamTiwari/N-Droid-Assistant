package com.ferrytech.n_droid.data.repository

import com.ferrytech.n_droid.BuildConfig
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
            apiKey = BuildConfig.GEMINI_API_KEY,

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

            // first prompt
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
You are a senior Android Engineer and Architect with 10+ years of experience.
You specialize in Kotlin, Jetpack Compose, Material 3, MVVM, and modern Android development.

MODE: FULL ANDROID PROJECT GENERATOR

Your task is to generate a **100% runnable Android project** that can be opened and run in the latest Android Studio.

STRICT REQUIREMENTS:
- Language: Kotlin only
- UI: Jetpack Compose only (NO XML)
- Architecture: MVVM
- Design: Material 3
- Single Activity architecture
- Navigation Compose
- StateFlow / MutableStateFlow for state
- Clean and modular package structure
- Code must compile without errors
- No pseudo-code
- No deprecated APIs

PROJECT OUTPUT MUST INCLUDE:
1) Complete folder/package structure
2) MainActivity
3) Navigation graph
4) UI screens (Compose)
5) ViewModels
6) State / data models
7) Repository layer (use fake/mock data if no backend)
8) Setup instructions (if required)

API KEY HANDLING:
- Do NOT hardcode any API keys
- Use placeholders (example: YOUR_API_KEY_HERE)
- Clearly explain WHERE and HOW to add API keys
- Follow Android best practices (local.properties / BuildConfig)

UI / UX REQUIREMENTS:
- Modern, clean, and professional UI
- User-friendly and intuitive layouts
- Eye-catching but not flashy
- Material 3 color system
- Soft, pleasant colors (pastel / gradient friendly)
- Proper spacing, padding, typography
- Rounded cards and buttons
- Must look production-ready

IMPORTANT:
- The app does NOT need backend or Play Store configuration
- The project MUST run successfully after generation
- Avoid unnecessary explanations
- Generate real, usable Kotlin code

- at last give a line if any problem then check bug-debugger
"""

        private const val BUG_DEBUGGER_PROMPT = """
You are a senior Android Engineer and Architect with 10+ years of experience.
You specialize in Kotlin, Jetpack Compose, Material 3, MVVM, and modern Android development.

MODE: ANDROID ERROR / LOGCAT DEBUGGER

Your task is to act as an Android debugging assistant.

You MUST:
1) Identify the exact root cause of the error
2) Explain WHY the error occurred
3) Point out the problematic file / line (if possible)
4) Provide corrected Kotlin code
5) Suggest best practices to avoid this issue in the future

DEBUGGING RULES:
- Assume Jetpack Compose + MVVM project
- Use modern Android solutions
- Do NOT guess
- Be precise and clear
- No unnecessary theory
"""
    }
}