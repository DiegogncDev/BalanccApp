# Arquitectura actual de BalanccApp

Documento descriptivo del estado **real** del proyecto. No es una arquitectura
objetivo; es la que existe hoy y a la que deben ajustarse PLAN.md y TASKS.md de
cada feature.

## Stack

- Kotlin 2.0.21, AGP 8.10.1, Gradle 8.11.1, JVM target 11
- Jetpack Compose + Material 3 (BOM 2024.09.00), Navigation Compose
- Hilt 2.51.1 (DI vía `kapt`), Room 2.7.2 (`kapt` compiler)
- DataStore Preferences (tema/idioma), MPAndroidChart (JitPack),
  sheets-compose-dialogs (calendar), Gson
- Coroutines/Flow; sin RxJava
- `compileSdk`/`targetSdk` 35, `minSdk` 26
- Módulo único `:app`, namespace/applicationId `com.onedeepath.balanccapp`
- `BuildConfig.API_KEY` leído desde `local.properties` (no commiteado)

## Arquitectura y capas

MVVM + capas clean. Flujo de dependencias en una dirección:

```
UI (Compose) → ViewModel → UseCase → Repository (interfaz, domain)
→ RepositoryImpl (data) → DAO (Room) / DataStore
```

- **Presentation** (`ui/`): screens Compose, ViewModels (`@HiltViewModel`),
  `*UiState`, mappers `to*Ui()`. ViewModels compartidos entre pantallas viven en
  `ui/presentation/` (`YearMonthViewModel`) con scope al back-stack entry de
  `MainScreen` (ver `AppNavigation.kt`).
- **Domain** (`domain/`): modelos de dominio, interfaces de repositorio y use
  cases (`@Inject constructor` + `operator fun invoke`). Depende solo de
  modelos y `Flow`. **No** depende de Data, Compose, Room ni Android UI.
  Las validaciones de entrada viven en los use cases (p.ej. blank year →
  `flowOf(emptyList())`).
- **Data** (`data/`): `BalanceDatabase` (Room), `dao/` (`BalanceDao`),
  `entity/` (`BalanceEntity`), `datastore/SettingsPreferencesRepostory.kt`
  (nombre con typo, histórico), `model/` (DTOs como `BalanceByMonthEntity`),
  `mapper/`, `repository/BalanceRepositoryImpl`.
- **DI** (`di/`): módulos Hilt `object` en `SingletonComponent`:
  `BalanceModule.kt`, `RoomModule.kt`, `DateModule.kt`,
  `DispatcherModule.kt` (qualifier `@IoDispatcher`; los ViewModels no
  hardcodean `Dispatchers.IO`). `CurrencyModel.kt` también reside aquí.

## Room

- `BalanceDatabase`, `BalanceDao`, `BalanceEntity`.
- Meses, días y años modelados como `String` en toda la app (`"January"`,
  `"2025"`), convención alineada con las queries de Room.
- Operaciones suspend o `Flow`; sin bloqueos en hilo principal.

## Hilt

- `BalanceApp.kt` (`@HiltAndroidApp`), `MainActivity`, ViewModels con
  `@HiltViewModel` obtenidos en Compose con `hiltViewModel()`.
- Repositorios enlazados con `@Binds`; singletons con `@Singleton`.

## Navegación

- `ui/navigation/AppNavigation.kt` + `AppScreens.kt` (sealed class, rutas en
  snake_case). Pantallas: `splash`, `main`, `addbalance`, `detail`, `settings`.
- ViewModels compartidos (`YearMonthViewModel`) con scope al back-stack entry
  de `MainScreen`.

## Gestión de estado

- StateFlow inmutable: `_uiState` privado + `uiState: StateFlow` público;
  mutación solo con `_uiState.update { it.copy(...) }`.
- `viewModelScope.launch` + `flowOn(ioDispatcher)` + `.catch {}` antes de
  `collectLatest`.
- `UiState` con `error: String?`, `isLoading`, flags one-shot (`saveSuccess`);
  el error se limpia con evento explícito (`onErrorShown()`).

## Testing

- Unitarios (`app/src/test`): JUnit4 + MockK + kotlinx-coroutines-test +
  Turbine (`runTest`, `StandardTestDispatcher`, `advanceUntilIdle`).
  Tests de use cases, repository y ViewModels (ver
  `utils/MainDispatcherRule.kt`). Nombres con backticks y Given/When/Then.
- Instrumentados (`app/src/androidTest`): `BalanceDaoTest` con Room in-memory
  (requiere emulador/dispositivo) + `ExampleInstrumentedTest`.

## Comandos Gradle (Windows: `gradlew.bat`)

```bash
./gradlew.bat assembleDebug              # build debug
./gradlew.bat clean assembleDebug        # rebuild limpio (tras cambios kapt)
./gradlew.bat lintDebug                  # Android lint (sin ktlint/detekt)
./gradlew.bat testDebugUnitTest          # tests unitarios
./gradlew.bat connectedDebugAndroidTest  # tests instrumentados (emulador/dispositivo)
```

## Convenciones de código

- `kotlin.code.style=official`, 4 espacios; una clase por archivo; sin imports
  wildcard (existe uno legacy de `org.junit.Assert.*` en un test; no copiarlo).
- Naming: clases `PascalCase`; funciones/propiedades `camelCase`; backing state
  `_uiState`; constantes `UPPER_SNAKE_CASE`.
- Screens `*Screen`, view models `*ViewModel`, estados `*UiState`; mappers como
  extensiones top-level (`toDomain()/toEntity()/to*Ui()`).
- Meses/días/años como `String`.
- Strings localizados en `values-es` y `values-en`: cualquier string nuevo va en
  ambos. Estilos vía `MaterialTheme.colorScheme`, `financialColors`,
  `BalanccSpacing`, `BalanccCornerRadius` (sin dp/colores hardcodeados).

## Decisiones técnicas actuales y deuda conocida

- `kapt` (no KSP) para Hilt y Room; añadir entidades/DAOs/módulos puede
  requerir `clean assembleDebug`.
- Release build con `isMinifyEnabled = false` (sin R8/ProGuard en release).
- Typo histórico: `SettingsPreferencesRepostory.kt` (falta la "i").
- `versionCode = 1`, `versionName = "1.0"` sin política de versionado definida.
- Un import wildcard legacy (`org.junit.Assert.*`) en un test; no replicado.
- `API_KEY` en `BuildConfig` obliga a mantener `local.properties` fuera de git.
- Carpeta `app/src/main/java/com/onedeepath/balanccapp/docs/` (planes de UI/UX
  heredados) dentro del árbol de paquetes Java; ubicación anómala, fuera del
  alcance de esta migración documental.
- No hay ktlint/detekt: el estilo se mantiene manualmente + `lintDebug`.
