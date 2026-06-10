# 🤖 N-Droid — AI Android Development Assistant

<p align="center">
  <img src="app/src/main/res/mipmap-xxxhdpi/ic_launcher.png" width="120" alt="N-Droid Logo"/>
</p>

<p align="center">
  <b>Your AI-powered pair programmer for Android development.</b><br/>
  Built with Jetpack Compose · Powered by Gemini AI
</p>

<p align="center">
  <img src="https://img.shields.io/badge/version-2.0-blueviolet?style=for-the-badge"/>
  <img src="https://img.shields.io/badge/platform-Android-3DDC84?style=for-the-badge&logo=android"/>
  <img src="https://img.shields.io/badge/Jetpack%20Compose-Material3-6200EE?style=for-the-badge"/>
  <img src="https://img.shields.io/badge/AI-Gemini-4285F4?style=for-the-badge&logo=google"/>
  <img src="https://img.shields.io/badge/language-Kotlin-7F52FF?style=for-the-badge&logo=kotlin"/>
</p>

---

## ✨ What is N-Droid?

**N-Droid** is an AI-powered Android development assistant app built entirely in **Jetpack Compose**. It acts as your always-available pair programmer — helping you generate projects, debug errors, and convert UI screenshots into real Compose code, all powered by **Google Gemini AI**.

Whether you're a beginner trying to build your first app or an experienced developer who wants faster prototyping, N-Droid has you covered.

---

## 🚀 Features — Version 2.0

### 🛠️ Three Intelligent Modes
Each chat session contains **three independent AI modes**, each storing their own conversation history:

| Mode | Description |
|---|---|
| 🛠️ **Project Generator** | Describe your app idea and get a complete, runnable Android project structure |
| 🐞 **Bug Debugger** | Paste your Logcat error or buggy code — get an instant diagnosis and fix |
| 🖼️ **UI Builder** | Upload a screenshot of any UI and receive clean, copy-ready Jetpack Compose code |

### 💾 Persistent Chat History (NEW in v2.0)
- Chat sessions are saved locally using **Room Database**
- Each session **remembers all three modes** independently
- Switch between Bug Debugger and Project Generator — your chat is **never lost**
- Sessions persist across app restarts

### 📋 Hamburger Menu / Session Sidebar (NEW in v2.0)
- Tap the **☰ menu icon** to open a slide-out drawer
- View all your past chat sessions
- Start a **New Chat** anytime
- Each session shows its title and creation date

### 💬 Beautiful Chat UI
-  full-width message bubbles
- Gemini responses render with **code block highlighting**
- One-tap **Copy button** on every code block (all 3 modes!)
- Full **text selection** support on all AI responses
- Smooth auto-scroll as responses stream in

### 📸 Image Upload (UI Builder)
- Attach screenshots directly from your gallery
- AI analyzes the UI and produces production-ready Compose code
- Preview and remove images before sending

### 🔗 Prompt Tune Integration
- Quick-access button to get better prompts at [prompttune.banter.life](https://prompttune.banter.life/)

### 🔐 Authentication
- Firebase Email/Password login & sign up
- Phone number (OTP) authentication
- User profile management

---

## 🏗️ Architecture & Tech Stack

```
N-Droid/
├── data/
│   ├── local/              # Room Database (Sessions + Messages)
│   │   ├── ChatDatabase.kt
│   │   ├── ChatDao.kt
│   │   ├── ChatSessionEntity.kt
│   │   └── MessageEntity.kt
│   ├── model/              # Domain Models (Message, ChatMode)
│   └── repository/         # GeminiRepository (AI prompt handling)
├── ui/
│   ├── screens/            # ChatScreen, HomeScreen, Auth Screens...
│   └── components/         # MessageBubble, ModeSelector...
├── viewmodel/              # ChatViewModel (state + Room integration)
└── navigation/             # NavController setup
```

### Libraries & Tools
| Library | Purpose |
|---|---|
| **Jetpack Compose + Material3** | Modern declarative UI |
| **Google Gemini AI SDK** | AI text & vision generation |
| **Room Database** | Local chat history persistence |
| **Firebase Auth** | User authentication |
| **Coil** | Async image loading |
| **Kotlin Coroutines + Flow** | Reactive data streams |
| **ViewModel + StateFlow** | MVVM architecture |

---

## 📦 Getting Started

### Prerequisites
- Android Studio Hedgehog or newer
- Android SDK 26+
- A **Google Gemini API Key** ([Get one here](https://aistudio.google.com/))
- A **Firebase project** configured ([Firebase Console](https://console.firebase.google.com/))

### Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/your-username/NDroid.git
   cd NDroid
   ```

2. **Add your Gemini API Key**
   Create/update `local.properties`:
   ```
   GEMINI_API_KEY=your_api_key_here
   ```

3. **Add Firebase config**
   Place your `google-services.json` in the `app/` directory.

4. **Run the app**
   ```bash
   ./gradlew assembleDebug
   ```
   Or just press **Run ▶** in Android Studio!

---

## 📸 Screenshots

> *Coming soon *

---

## 🗺️ Roadmap

- [ ] Full Markdown rendering in chat
- [ ] Syntax highlighting for code blocks
- [ ] Export chat sessions as text/PDF
- [ ] Dark mode theme options
- [ ] Per-session Gemini model selection

---

## 🤝 Contributing

Contributions, issues and feature requests are welcome!

1. Fork the repo
2. Create your feature branch: `git checkout -b feature/amazing-feature`
3. Commit your changes: `git commit -m 'Add amazing feature'`
4. Push to the branch: `git push origin feature/amazing-feature`
5. Open a Pull Request

---

## 👨‍💻 Author

**Nityam Tiwari**  
Android Developer · AI Enthusiast

---

## 📄 License

This project is licensed under the **MIT License** — see the [LICENSE](LICENSE) file for details.

---

<p align="center"> N-Droid v2.0</p>
