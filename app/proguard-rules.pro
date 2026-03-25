# proguard-rules.pro

# Keep BuildConfig
-keep class com.ferrytech.n_droid.BuildConfig { *; }

# Obfuscate API calls
-keepclassmembers class com.ferrytech.n_droid.data.repository.GeminiRepository {
    private ** apiKey;
}

# General Android rules
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Fragment
-keep public class * extends androidx.fragment.app.Fragment

# Keep Gemini AI SDK
-keep class com.google.ai.client.generativeai.** { *; }

# Keep Firebase
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }

# Kotlin
-keep class kotlin.** { *; }
-keep class kotlinx.** { *; }

# Compose
-keep class androidx.compose.** { *; }