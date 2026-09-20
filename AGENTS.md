# AGENTS.md — BalanccApp

Android personal-finance app (income/expense tracking by month and year).
Package: `com.onedeepath.balanccapp` · Single Gradle module `:app`.

## Tech stack

- Kotlin 2.0.21, AGP 8.10.1, Gradle 8.11.1 (wrapper), JVM target 11
- Jetpack Compose (Material 3, BOM 2024.09.00), Navigation Compose
- Hilt 2.51.1 (DI, via `kapt`), Room 2.7.2 (`kapt` compiler)
- DataStore Preferences (settings/theme/language), MPAndroidChart (JitPack),
  sheets-compose-dialogs (calendar), Gson
- coroutines/Flow for async; no RxJava
- compileSdk/targetSdk 35, minSdk 26

## Build / lint / test commands (Windows: use `gradlew.bat`)

```bash
./gradlew.bat assembleDebug              # Build debug APK
./gradlew.bat clean assembleDebug        # Clean build
./gradlew.bat lintDebug                  # Android lint (no ktlint/detekt configured)
./gradlew.bat testDebugUnitTest          # All unit tests (app/src/test)
./gradlew.bat connectedDebugAndroidTest  # Instrumented tests (needs emulator/device)
```

### Running a single test

```bash
# Single test class
./gradlew.bat testDebugUnitTest --tests "com.onedeepath.balanccapp.domain.usecases.GetBalancesByYearUseCaseTest"

# Single test method (backtick names contain spaces — keep the full quoted string)
./gradlew.bat testDebugUnitTest --tests "com.onedeepath.balanccapp.domain.usecases.GetBalancesByYearUseCaseTest.invoke with empty year returns empty list and does not interact with repository"

# Wildcard across packages
./gradlew.bat testDebugUnitTest --tests "com.onedeepath.balanccapp.ui.screens.main.*"

# Single instrumented test class
./gradlew.bat connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.onedeepath.balanccapp.data.dao.BalanceDaoTest
```

Verify changes with `lintDebug` + `testDebugUnitTest`; use `connectedDebugAndroidTest`
when touching DAOs/Room or Compose UI behavior.

## Architecture & package layout

MVVM + clean layering under `app/src/main/java/com/onedeepath/balanccapp`:

- `core/` — helpers (currency formatting, chart builders, converters)
- `data/`
  - `database/` — Room: `BalanceDatabase`, `dao/`, `entity/`
  - `datastore/` — `SettingsPreferences` (theme/language)
  - `model/` — DTO-level models (e.g. `BalanceByMonthEntity`)
  - `repository/` — repository implementations (`*Impl`)
- `domain/`
  - `model/` — domain models + entity↔domain mapper extension functions
  - `repository/` — repository interfaces
  - `usecases/` — one use case per file, `operator fun invoke`
- `di/` — Hilt modules (`@Module @InstallIn(SingletonComponent::class)`)
- `ui/` — Compose UI
  - `screens/<feature>/` — screen composable plus `viewmodel/`, `model/` (UiState),
    `mapper/` subpackages
  - `components/` — reusable composables (`BalanccTopBar`, `PrimaryButton`, …)
  - `theme/` — colors, type, `BalanccSpacing`, `BalanccCornerRadius`, `financialColors`
  - `navigation/` — `AppNavigation` + `AppScreens` sealed class (route strings)
  - `presentation/` — shared view models (`YearMonthViewModel`) and mapper/extensions

Tests mirror the main tree: unit tests in `app/src/test`, instrumented/Room tests in
`app/src/androidTest`.

## Code style

### General Kotlin

- `kotlin.code.style=official`; 4-space indent; keep files focused (one class per file).
- Imports: one per line, no wildcards in main code (one legacy `org.junit.Assert.*`
  exists in a test — don't copy it into new code).
- Naming: classes/objects `PascalCase`; functions/properties `camelCase`; private
  backing state `_uiState`; constants `UPPER_SNAKE_CASE` in `companion object`/module.
- Screens/features use `*Screen`, view models `*ViewModel`, states `*UiState`,
  mappers `toDomain()/toEntity()/to*Ui()` extension functions declared at file
  bottom/top level (see `BalanceModel.kt`, `BalanceEntity.kt`).
- Months, days and years are modeled as `String` throughout (e.g. `"January"`,
  `"2025"`) — follow this convention for consistency with Room queries.

### Architecture rules

- UI → ViewModel → use case → repository interface (domain) → repository impl (data)
  → DAO/datastore. Never call DAOs from ViewModels; never reference Android UI from
  `domain/` (it only depends on models/Flow).
- New repository capability: add method to `domain/repository/` interface, implement
  in `data/repository/*Impl`, bind with `@Binds` in `di/BalanceModule`-style module.
- Use cases are plain classes with `@Inject constructor` and `operator fun invoke`;
  validate input there (e.g. blank year → `flowOf(emptyList())`).

### Dependency injection (Hilt)

- ViewModels: `@HiltViewModel` + `@Inject constructor`; obtain in Compose with
  `hiltViewModel()`. Modules are `object`s in `di/`, installed in
  `SingletonComponent`; use `@Singleton` on provided singletons.
- Inject dispatchers via the `@IoDispatcher` qualifier (`di/DispatcherModule.kt`)
  instead of hardcoding `Dispatchers.IO` in ViewModels.

### Coroutines & Flow

- Expose UI state as immutable `StateFlow`: private `MutableStateFlow`, public
  `uiState: StateFlow<...> = _uiState.asStateFlow()`; mutate exclusively with
  `_uiState.update { it.copy(...) }`.
- Launch work in `viewModelScope.launch`; apply `.flowOn(ioDispatcher)` to cold
  flows from use cases and handle errors with `.catch { }` before `collectLatest`.
- Long-running calls (Room) are `suspend` or return `Flow`; keep that pattern.

### Error handling

- UiState carries `error: String?` (+ `isLoading`, one-shot flags like
  `saveSuccess`). On failure, set `error = throwable.message`/`e.message`;
  clear it via an explicit event (`onErrorShown()`). Use `try/catch` inside
  `viewModelScope.launch` for suspend calls, `.catch {}` for flows.
- Validate before acting and surface "Invalid data"-style errors through UiState,
  never crash or silently swallow.

### Compose conventions

- Stateless where possible: screens receive a `NavController` and a Hilt
  `ViewModel`; collect state with `collectAsState` / `collectAsStateWithLifecycle`;
  user actions flow up as `onXxxChange(...)`/`onXxxSelected(...)` callbacks.
- Reusable UI lives in `ui/components`; strings via `stringResource(R.string.*)`
  (app is localized `es`/`en` — add new strings to both); styling via
  `MaterialTheme.colorScheme`, `financialColors`, `BalanccSpacing`,
  `BalanccCornerRadius` — no hardcoded dp/color literals in new UI.
- Navigation routes go in `AppScreens` (sealed class, snake_case route strings);
  add the destination to `AppNavigation`. Shared `-screens` view models are scoped
  to the `MainScreen` back-stack entry (see `AppNavigation.kt`).

### Testing

- JUnit4 + MockK + kotlinx-coroutines-test + Turbine (`runTest`,
  `StandardTestDispatcher`, `flowOf`, `turbine.test {}`).
- Test names use backticks, Given/When/Then comments, e.g.
  `` `when useCase emits balances then uiState updates months and isLoading false` ``.
- Mock with `mockk()` or `@RelaxedMockK` + `MockKAnnotations.init(this)`; stub with
  `every { ... } returns ...` (suspend: `coEvery`, unit-returning: `just Runs`);
  assert interactions with `verify(exactly = n)` / `coVerify`.
- ViewModel tests: `Dispatchers.setMain(StandardTestDispatcher())` in `@Before`,
  `Dispatchers.resetMain()` in `@After`, drive coroutines with `advanceUntilIdle()`;
  see `MainViewModelTest.kt` and `utils/MainDispatcherRule.kt`.
- Add unit tests for every new use case/ViewModel; add Room DAO instrumented tests
  (in-memory database) for new queries — mirror `BalanceDaoTest.kt`.

## Gotchas

- kapt is used for Hilt + Room: after adding entities/DAOs/modules, a full rebuild
  may be needed (`./gradlew.bat clean assembleDebug`).
- Do not hardcode secrets/API keys in Gradle files (`buildConfigField`); use
  `local.properties` and never commit it.
- There is no formatter/linter plugin: keep style consistent by matching existing
  code and run `lintDebug` before finishing.
