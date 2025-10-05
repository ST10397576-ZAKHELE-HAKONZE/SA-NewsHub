# 📰 SA NewsHub – Part 2 Prototype

**Your Local News, Anytime, Anywhere.**  
A mobile news application built for South Africans to stay informed about load shedding, weather alerts, job opportunities, and community updates — even in low-connectivity areas.

> 🔹 **Part 2 Submission** – OPSC6312 Portfolio of Evidence  
> 🔹 **Due Date**: 7 October 2025  
> 🔹 **Student**: Zakhele Hakonze (ST1039756)  
> 🔹 **Institution**: Rosebank College

---

## 🎥 Demo Video

Video link:

> 🔗 **[Click here to watch the full demo video ](https://youtu.be/xxx)**  
> Includes voice-over walkthrough of:  
> - User registration & login  
> - Settings customization (dark mode, region)  
> - Real-time news fetching from custom API  
> - Backend data in MongoDB Atlas & Render.com logs



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

## 🚀 Setup & Run

### Prerequisites
- Android Studio (Jellyfish or later)
- Physical Android device (min SDK 24 / Android 7.0)
- Internet connection

### Steps
1. Clone this repo:
2. Open in Android Studio
3. Sync Gradle dependencies
4. Run on an emulator
5. Test:
   - Register a new account
   - Log in
   - View news feed
   - Change settings
   
### GitHub Actions Badge & CI/CD Explanation
[![Build Status](https://github.com/ST10397576-ZAKHELE-HAKONZE/SA-NewsHub/actions/workflows/build.yml/badge.svg)](https://github.com/ST10397576-ZAKHELE-HAKONZE/SA-NewsHub/actions)

## 🧪 Continuous Integration (GitHub Actions)

This project uses GitHub Actions to ensure reliability:
- ✅ **Automated builds** on every push
- ✅ **Lint checks** for code quality
- ✅ **Compilation verification** across environments

Workflow file: `.github/workflows/build.yml`

## 🤖 AI Tool Usage Disclosure

This project utilized AI assistance for:
- Debugging Gradle build errors and runtime crashes (e.g., `INTERNET` permission, MongoDB IP whitelist)
- Generating Kotlin code snippets for Retrofit, SharedPreferences...
- Research Render.com deployment and MongoDB Atlas configuration
