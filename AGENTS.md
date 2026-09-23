# AGENTS.md — BalanccApp

Android personal-finance app (income/expense tracking by month and year).
Package: `com.onedeepath.balanccapp` · Single Gradle module `:app`.

Este repositorio usa **Spec-Driven Mobile Development (SDMD)**. Respeta el
workflow de etapas aprobadas explícitamente; no te lo saltes.

## Required reading

Antes de planificar, revisar o modificar código:

1. `docs/GENERIC_RULES.md` — reglas genéricas de trabajo.
2. `docs/CURRENT_ARCHITECTURE.md` — arquitectura real de BalanccApp.
3. `docs/MOBILE_GUIDELINES.md` — cuando la feature toque comportamiento mobile.
4. `docs/features/<feature>/SPEC.md`
5. `docs/features/<feature>/PLAN.md`
6. `docs/features/<feature>/TASKS.md`

Los templates (`docs/SPEC_TEMPLATE.md`, `docs/PLAN_TEMPLATE.md`) contienen sus
propias instrucciones en comentarios. Léalos completos antes de completarlos.

## Project

- App Android de finanzas personales: registro de ingresos/gastos por mes/año.
- Kotlin 2.0.21, AGP 8.10.1, Gradle 8.11.1, JVM target 11.
- Jetpack Compose (Material 3, BOM 2024.09.00), Navigation Compose.
- Hilt 2.51.1 vía `kapt`; Room 2.7.2 vía `kapt`; DataStore Preferences;
  MPAndroidChart; sheets-compose-dialogs (calendar); Gson.
- `compileSdk`/`targetSdk` 35, `minSdk` 26.
- Detalle completo: `docs/CURRENT_ARCHITECTURE.md`.

## Commands

Windows (`gradlew.bat`; en Unix `./gradlew`):

```bash
./gradlew.bat assembleDebug              # build debug APK
./gradlew.bat clean assembleDebug        # rebuild limpio (tras cambios kapt)
./gradlew.bat lintDebug                  # Android lint (sin ktlint/detekt)
./gradlew.bat testDebugUnitTest          # tests unitarios (app/src/test)
./gradlew.bat connectedDebugAndroidTest  # instrumentados (emulador/dispositivo)
```

Tests puntuales:

```bash
./gradlew.bat testDebugUnitTest --tests "com.onedeepath.balanccapp.domain.usecases.GetBalancesByYearUseCaseTest"
./gradlew.bat connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.onedeepath.balanccapp.data.dao.BalanceDaoTest
```

Verifica cambios con `lintDebug` + `testDebugUnitTest`; usa
`connectedDebugAndroidTest` al tocar DAOs/Room o UI Compose.

## Architecture

Límites reales (ver `docs/CURRENT_ARCHITECTURE.md`):

```
UI (Compose) → ViewModel → UseCase → Repository (interfaz, domain)
→ RepositoryImpl (data) → DAO (Room) / DataStore
```

- Presentation → Domain. Data → Domain.
- Domain **no** depende de Data, ni de Compose, ni de Room, ni de Android UI.
- La UI nunca accede directamente a DAOs ni a DataStore.
- Los ViewModels no contienen lógica visual; exponen `StateFlow` inmutable y
  reciben eventos de la UI.
- Capacidad nueva de repositorio: método en `domain/repository/`,
  implementación en `data/repository/*Impl`, binding con `@Binds` en `di/`.
- Use cases: `@Inject constructor` + `operator fun invoke`; validan input.
- No mover archivos ni reestructurar capas fuera del scope aprobado.

### Convenciones de código

- `kotlin.code.style=official`; 4 espacios; una clase por archivo; sin imports
  wildcard (uno legacy `org.junit.Assert.*` en un test: no copiarlo).
- Clases/objs `PascalCase`; funciones/propiedades `camelCase`; backing state
  `_uiState`; constantes `UPPER_SNAKE_CASE`.
- Screens `*Screen`, view models `*ViewModel`, estados `*UiState`; mappers
  como extensiones `toDomain()/toEntity()/to*Ui()`.
- Meses, días y años como `String` (`"January"`, `"2025"`): convención
  coherente con las queries de Room.
- Estado UI: `MutableStateFlow` privado + `uiState` público; mutar solo con
  `_uiState.update { it.copy(...) }`; `viewModelScope.launch` +
  `flowOn(ioDispatcher)` (@IoDispatcher) + `.catch {}` antes de
  `collectLatest`.
- UiState con `error: String?`, `isLoading`, flags one-shot; error limpiado con
  evento explícito (`onErrorShown()`); errores suspend con `try/catch` y flows
  con `.catch {}`.
- Strings nuevos en `values-es` **y** `values-en`; estilos vía
  `MaterialTheme.colorScheme`, `financialColors`, `BalanccSpacing`,
  `BalanccCornerRadius`; sin dp/colores hardcodeados en UI nueva.
- Rutas de navegación en `AppScreens` (sealed, kebab/snake_case) y destinos en
  `AppNavigation`.
- Testing: JUnit4 + MockK + kotlinx-coroutines-test + Turbine; nombres con
  backticks y Given/When/Then; ViewModels con `Dispatchers.setMain` en
  `@Before`/`resetMain()` en `@After` + `advanceUntilIdle()` (ver
  `app/src/test/.../utils/MainDispatcherRule.kt`). Test unitario por cada use
  case/ViewModel nuevo; test instrumentado Room (in-memory) para queries
  nuevas (modelo: `BalanceDaoTest.kt`).

### Gotchas

- `kapt` para Hilt + Room: tras añadir entidades/DAOs/módulos, puede requerir
  `./gradlew.bat clean assembleDebug`.
- No hardcodear secretos: `API_KEY` viene de `local.properties` vía
  `buildConfigField`; `local.properties` nunca se commitea.
- Sin ktlint/detekt: mantener estilo a mano y correr `lintDebug`.

## SDMD workflow

```
SPEC → aprobación explícita → PLAN → aprobación explícita
     → TASKS → autorización explícita de implementación
     → implementación → validación → evidencia
```

- Una etapa **nunca** avanza automáticamente.
- Completar un documento (SPEC/PLAN/TASKS) **no** significa que esté aprobado;
  la aprobación es explícita por la persona.
- Completar TASKS.md no autoriza a implementar: la autorización es explícita.
- No implementar hasta la autorización de la fase de implementación.
- Si durante la implementación surge una contradicción o un cambio de alcance,
  volver a SPEC/PLAN y pedir confirmación antes de seguir.
- Las features viven en `docs/features/<feature-name>/` con `SPEC.md`,
  `PLAN.md`, `TASKS.md`. `TASKS.md` se deriva del PLAN aprobado (no existe
  template de TASKS).

## Scope control

El agente no debe:

- inventar requisitos;
- añadir features no solicitadas;
- hacer refactors no relacionados;
- actualizar dependencias sin autorización;
- cambiar la arquitectura global durante una feature;
- mover archivos sin necesidad funcional;
- modificar comportamiento fuera del alcance aprobado.

## Validation

Antes de declarar una feature terminada:

- ejecutar los checks relevantes (`lintDebug`, `testDebugUnitTest`,
  `connectedDebugAndroidTest` cuando corresponda);
- reportar el estado real: PASS / FAIL / SKIPPED / BLOCKED por cada check;
- vincular la validación con cada criterio de aceptación de la SPEC.

Nunca declares una validación que no se haya ejecutado realmente.

## Security

Nunca incluir API keys, tokens, secretos ni credenciales en código,
documentación, fixtures o reportes. `local.properties` y valores como
`BuildConfig.API_KEY` se mantienen fuera de git.
