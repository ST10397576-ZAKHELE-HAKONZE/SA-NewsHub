# 📰 SA NewsHub – Part 2 Prototype

[![Build Status](https://github.com/ST10397576-ZAKHELE-HAKONZE/SA-NewsHub/actions/workflows/build.yml/badge.svg)](https://github.com/ST10397576-ZAKHELE-HAKONZE/SA-NewsHub/actions)

**Your Local News, Anytime, Anywhere.**  
A mobile news application built for South Africans to stay informed about load shedding, weather alerts, job opportunities, and community updates — even in low-connectivity areas.

> 🔹 **Part 2 Submission** – OPSC6312 Portfolio of Evidence  
> 🔹 **Due Date**: 7 October 2025  
> 🔹 **Student**: Zakhele Hakonze (ST1039756)  
> 🔹 **Institution**: Rosebank College

---

## 🎥 Demo Video

[![Watch the Demo](https://img.youtube.com/vi/your-video-id/0.jpg)](https://youtu.be/Coa74ugiHNE)

> 🔗 **[Click here to watch the full demo video (unlisted)](https://youtu.be/Coa74ugiHNE)**  
> Includes voice-over walkthrough of:  
> - User registration & login  
> - Settings customization (dark mode, region)  
> - Real-time news fetching from custom API  
> - Backend data in MongoDB Atlas & Render.com logs

*(Replace `your-video-id` with your actual YouTube video ID)*

---

## ✅ Part 2 Features Implemented

This prototype fulfills **all mandatory Part 2 requirements** as per the POE brief:

| Feature | Status | Details |
|--------|--------|--------|
| **User Registration & Login** | ✅ | Email/password auth with **password encryption** via `bcrypt` on backend |
| **Custom REST API** | ✅ | Built with **Node.js + Express**, hosted on **Render.com**, connected to **MongoDB Atlas** |
| **Settings Screen** | ✅ | Users can toggle **dark mode** and select **region** (saved via `SharedPreferences`) |
| **News Feed** | ✅ | Displays real news from `/api/news` endpoint |
| **GitHub Actions** | ✅ | Automated build & test pipeline |
| **Google SSO** | ❌ | *Marked “POE only” – deferred to Final POE* |
| **Offline Mode / FCM / Multi-language** | ❌ | *POE-only features – not required for Part 2* |

---

## 🛠️ How It Works

### 🔐 Authentication Flow
1. **Register**:  
   - User enters email & password on `RegisterActivity`
   - App sends `POST /api/register` to backend
   - Backend **hashes password** using `bcrypt` and stores user in **MongoDB Atlas**

2. **Login**:  
   - User enters credentials on `LoginActivity`
   - App sends `POST /api/login`
   - Backend compares hashed password → returns success/failure
   - On success, navigates to `HomeActivity`

> 🔒 **No passwords are stored or transmitted in plain text.**

### 🌐 API Integration
- **Backend**: `https://sahub-api.onrender.com`
- **Endpoints**:
  - `GET /api/news` → Returns mock South African news (load shedding, weather, jobs)
  - `POST /api/register` → Creates new user
  - `POST /api/login` → Validates credentials
- **Tech Stack**: Node.js, Express, MongoDB Atlas, Render.com
- **Security**: MongoDB IP whitelist includes `0.0.0.0/0` for Render.com compatibility

### 📰 News Feed
- `HomeActivity` calls `ApiHelper.apiService.getNews()` on launch
- Uses **Retrofit + Coroutines** to fetch data off main thread
- Displays news titles in a clean vertical list
- Shows “Loading…” during fetch, handles errors gracefully

### ⚙️ Settings
- **Dark Mode**: Toggles `AppCompatDelegate` night mode
- **Region**: Dropdown with provinces (Gauteng, WC, KZN, etc.)
- Both settings persist via `SharedPreferences` and survive app restart

---

## 📂 Project Structure
```bash
SA-NewsHub/
├── app/
│ ├── src/main/java/com/st10397576/sanewshub/
│ │ ├── LoginActivity.kt # Email/password login
│ │ ├── RegisterActivity.kt # User registration
│ │ ├── HomeActivity.kt # News feed (API-connected)
│ │ ├── SettingsActivity.kt # Dark mode + region
│ │ ├── ApiService.kt # Retrofit interface
│ │ └── ApiHelper.kt # Singleton API client
│ └── res/
│ ├── layout/
│ │ ├── activity_login.xml
│ │ ├── activity_register.xml
│ │ ├── activity_settings.xml
│ │ └── ...
│ └── values/colors.xml # Includes purple_500
├── build.gradle # Includes Retrofit, Coroutines, KTX
└── README.md
```
---

## 🚀 Setup & Run

### Prerequisites
- Android Studio (Jellyfish or later)
- Physical Android device (min SDK 24 / Android 7.0)
- Internet connection

### Steps
1. Clone this repo:
   ```bash
   git clone https://github.com/ST10397576-ZAKHELE-HAKONZE/SA-NewsHub.git
   ```
   - Open in Android Studio
   - Sync Gradle dependencies
   - Run on a physical device
   - Test:
   - Register a new account
   - Log in
   - View news feed
   - Change settings
---
## ☁️ Backend Architecture
| **Component** | **Technology**                | **URL / Details**                                                |
| ------------- | ----------------------------- | ---------------------------------------------------------------- |
| **API**       | Node.js + Express             | [https://sahub-api.onrender.com](https://sahub-api.onrender.com) |
| **Database**  | MongoDB Atlas (Free Tier)     | Cloud-hosted cluster                                             |
| **Hosting**   | Render.com (Free Web Service) | Auto-deploys from GitHub                                         |

### Sample API Response (GET /api/news):
```bash
[
  {
    "id": 1,
    "title": "Load Shedding Stage 4 Announced",
    "body": "Eskom has declared Stage 4 load shedding starting at 18:00...",
    "category": "Energy",
    "timestamp": "2025-10-05T12:00:00Z",
    "source": "EskomSePush"
  }
]
```

## 🤖 AI Tool Usage Disclosure
This project utilized AI assistance for:

- Debugging Gradle build errors and runtime crashes
- Research on MongoDB IP whitelisting, Retrofit, SharedPreferences, RecyclerView and Render.com deployment
