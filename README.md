# 💰 BalanccApp — Personal Finance & Expense Tracker

<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android-3DDC84?style=for-the-badge&logo=android&logoColor=white" alt="Platform: Android" />
  <img src="https://img.shields.io/badge/Kotlin-2.0.21-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white" alt="Kotlin: 2.0.21" />
  <img src="https://img.shields.io/badge/Jetpack_Compose-Material_3-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white" alt="Jetpack Compose: Material 3" />
  <img src="https://img.shields.io/badge/Architecture-Clean_%2B_MVVM-FF6F00?style=for-the-badge" alt="Architecture: Clean MVVM" />
  <img src="https://img.shields.io/badge/Min_SDK-26-brightgreen?style=for-the-badge" alt="Min SDK: 26" />
</p>

---

## 📌 Overview

**BalanccApp** is a modern, offline-first Android application designed for intuitive personal finance management. It empowers users to track their incomes and expenses with granular date filters (month and year), visual interactive charts, multi-currency support, and customizable themes.

Built adhering to **Clean Architecture** and **Modern Android Development (MAD)** best practices, the application leverages Jetpack Compose, Kotlin Coroutines & Flow, Hilt for dependency injection, and Room for local persistence.

---

## ✨ Key Features

- 📊 **Interactive Financial Dashboard**: Real-time balance calculations showing total income, total expenses, and net balance with visual breakdown charts.
- 📅 **Time-Based Filtering**: Filter financial records seamlessly by month and year.
- ➕ **Fast Transaction Logging**: Add income and expense entries with customizable categories, amounts, descriptions, and date pickers.
- 🔍 **Detailed Transaction History**: Inspect, browse, and organize historical financial data.
- 🎨 **Material 3 Design System**: Clean, accessible UI with seamless Dark & Light theme support.
- 🌐 **Internationalization & Currency**: Multi-language support (English / Spanish) and configurable currency preferences via DataStore.
- 🔒 **100% Offline & Private**: All data is securely stored locally on the device using Room Database.

---

## 🏛️ Architecture & Tech Stack

BalanccApp follows a strict unidirectional data flow (**Clean Architecture + MVVM**):

```
UI (Jetpack Compose) ──► ViewModel ──► UseCase (Domain) ──► Repository (Domain Interface)
                                                                 │
                                                    ┌────────────┴────────────┐
                                                    ▼                         ▼
                                          BalanceDatabase (Room)    DataStore Preferences
```

### 🛠️ Tech Stack & Libraries

| Category | Technologies / Libraries |
|---|---|
| **Language** | [Kotlin](https://kotlinlang.org/) (2.0.21) |
| **UI Framework** | [Jetpack Compose](https://developer.android.com/jetpack/compose) (BOM 2024.09.00), [Material 3](https://m3.material.io/) |
| **Navigation** | Navigation Compose |
| **Architecture** | MVVM + Clean Architecture, Unidirectional Data Flow (UDF) |
| **Asynchronous & Reactive** | Kotlin Coroutines, StateFlow / Flow |
| **Dependency Injection** | [Dagger Hilt](https://dagger.dev/hilt/) (2.51.1) |
| **Local Storage** | [Room Database](https://developer.android.com/training/data-storage/room) (2.7.2), Jetpack DataStore Preferences |
| **Visual Charts & Dialogs** | MPAndroidChart, Sheets-Compose-Dialogs (Calendar) |
| **Testing** | JUnit 4, MockK, Kotlinx Coroutines Test, Turbine, AndroidX UI Test |

---

## 📂 Project Structure

```
com.onedeepath.balanccapp
├── core/                # Core utilities, extensions, and dispatchers
├── data/                # Database (Room), entities, DAOs, DataStore, and repo implementations
│   ├── dao/             # Room DAOs (BalanceDao)
│   ├── datastore/       # DataStore preference repositories
│   ├── entity/          # Room entity models
│   └── repository/      # Repository implementations
├── di/                  # Hilt Dependency Injection modules
├── domain/              # Business logic (Pure Kotlin - independent of UI & Framework)
│   ├── model/           # Domain models
│   ├── repository/      # Repository contracts/interfaces
│   └── usecases/        # Single-responsibility business use cases
└── ui/                  # Presentation layer
    ├── components/      # Reusable Compose UI components
    ├── navigation/      # Navigation graphs and route definitions (AppNavigation)
    ├── screens/         # Feature screens (Splash, Main, AddBalance, Detail, Settings)
    └── theme/           # Material 3 colors, typography, shapes, and spacing tokens
```

---

## 🚀 Getting Started

### Prerequisites

- **Android Studio**: Ladybug / Koala or newer
- **JDK**: Version 11 or higher
- **Android SDK**: `minSdk 26`, `targetSdk 35`

### Installation & Build

1. **Clone the repository:**
   ```bash
   git clone https://github.com/DiegogncDev/BalanccApp.git
   cd BalanccApp
   ```

2. **Open the project in Android Studio** and let Gradle sync.

3. **Build the Debug APK:**
   - **Windows:**
     ```cmd
     gradlew.bat assembleDebug
     ```
   - **macOS / Linux:**
     ```bash
     ./gradlew assembleDebug
     ```

---

## 🧪 Testing & Verification

Run the test suite and code verification using the following commands:

```bash
# Run unit tests (Domain use cases, ViewModels, Repositories)
./gradlew testDebugUnitTest

# Run Android Lint checks
./gradlew lintDebug

# Run Room & UI instrumented tests (requires connected device / emulator)
./gradlew connectedDebugAndroidTest
```

---

## 👤 Author

**Diego (DiegogncDev)**
- GitHub: [@DiegogncDev](https://github.com/DiegogncDev)

---

## 📄 License

This project is developed for educational and portfolio purposes. Feel free to explore the code and contribute!
