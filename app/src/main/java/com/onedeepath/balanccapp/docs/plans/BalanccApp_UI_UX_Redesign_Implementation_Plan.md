# BalanccApp — UI/UX Redesign Implementation Plan

## Objective

Implement the redesign described in `BalanccApp_UI_UX_Redesign_Spec.md` while preserving existing persistence, business rules, navigation, currency formatting, and transaction behavior.

## Scope and constraints

- The application UI is implemented entirely with Jetpack Compose; no XML layout migration is needed.
- Keep Room, DAO, repository contracts, use cases, balance domain models, and Hilt data wiring unchanged.
- Reuse the current ViewModels and their state flows. Any presentation-state adjustment must not alter business calculations or persistence behavior.
- Keep the existing navigation routes and the shared `YearMonthViewModel` flow intact.
- Preserve the persisted category enum values. The existing `Category` type contains Compose color data; do not expand that UI/domain coupling as part of the redesign.

## Current baseline

- Main UI routes: splash, dashboard, monthly detail, add transaction, and settings.
- Active theme entry point: `ui/theme/AppTheme.kt`. `ui/theme/BalanccAppTheme` exists but is not currently used.
- There is no central component package, spacing scale, shape scale, or semantic financial-color system.
- Current automated coverage is focused on DAO, repository, use cases, and ViewModels. No screen-level Compose tests currently exist.

## Implementation phases

### [x] Phase 0 — Baseline and regression protection

**Likely files**

- `app/build.gradle.kts` only if a missing test configuration blocks UI validation.
- New focused Compose tests under `app/src/androidTest/...` only when an emulator/device is available.

**Work**

1. Confirm a clean working tree before UI implementation.
2. Run the existing unit-test suite as a behavior baseline.
3. Record the current test coverage boundary: data/domain/ViewModel tests exist; Compose screen tests do not.
4. Define the manual regression paths to use throughout the redesign:
   - Select year and open monthly detail.
   - Add income and expense, including fast-add month selection.
   - Select date, category, and long description.
   - Delete a transaction.
   - Toggle dark mode and change application language.

**Risks**

- Existing tests do not exercise the full UI, navigation, or accessibility behavior.

**Validation**

- `./gradlew test`
- Manual verification of the listed paths before and after each UI phase.

**Baseline result — 2026-09-18**

- The working tree was clean before this planning document was created.
- `./gradlew test` completed 36 tests: 35 passed and 1 failed.
- Existing failure: `MainViewModelTest > when useCase emits empty balances then uiState updates months with zero values` at `MainViewModelTest.kt:129`.
- The failure predates the visual-redesign implementation. It is recorded as a baseline issue and is not changed in this phase.

### [x] Phase 1 — Design system and theme foundation

**Likely files**

- `ui/theme/Color.kt`
- `ui/theme/Theme.kt`
- `ui/theme/AppTheme.kt`
- `ui/theme/Type.kt`
- New files under `ui/designsystem/`

**New components/tokens**

- Light/dark semantic color tokens: background, surface, surface secondary, border, primary, income, expense, warning, and text hierarchy.
- Spacing scale: 4/8/12/16/20/24/32dp.
- Shape scale for controls, inputs, cards, sheets, and FABs.
- Material typography scale aligned with the redesign specification.

**Existing elements reused**

- Material 3 color schemes and the DataStore-backed dark-mode preference.

**Risks**

- Applying the configured typography through the active theme can change default Material text sizing across all screens.
- Hardcoded screen colors require gradual migration in later phases.

**Validation**

- Compile and run the existing tests.
- Review contrast, surface hierarchy, and default controls in both themes.

**Implementation result — 2026-09-18**

- Replaced the purple-led palette with the specified neutral surfaces, petrol primary, and restrained light/dark semantic financial colors.
- Added `FinancialColorScheme` and `MaterialTheme.financialColors` for income, expense, warning, supporting text, disabled text, and border tokens.
- Centralized the 4/8-based spacing scale, shape/radius scale, and subtle elevation tokens.
- Defined and applied the Material typography scale and shapes through the active `AppTheme` entry point.
- Kept `BalanccAppTheme` as a deprecated compatibility wrapper around `AppTheme`.
- `:app:compileDebugKotlin` passed.
- `./gradlew test` still reports the recorded baseline failure in `MainViewModelTest.kt:129` (35 passed, 1 failed); no new test failures were introduced.

### [x] Phase 2 — Shared UI components

**Likely files**

- New files under `ui/components/`
- `ui/screens/main/MainScreen.kt`
- `ui/screens/addbalance/AddBalanceScreen.kt`
- `ui/screens/detail/MonthsDetailScreen.kt`
- `ui/screens/settings/SettingsScreen.kt`

**New components**

- `BalanccTopBar`
- `BalanccFab`
- `PrimaryButton`
- `FinancialSegmentedControl`
- `SelectionField`
- `CategoryIcon`
- `SelectionDialog` or selector sheet
- `SectionHeader`
- `EmptyState`

**Existing elements reused**

- `SettingItem`, `YearFilterChip`, `YearPickerDialog`, `MonthItem`, and the current income/expense selector behavior.

**Risks**

- Callbacks, state ownership, and navigation must remain unchanged while the visual wrappers are extracted.

**Validation**

- Verify each selector, enabled/disabled button state, and navigation path.
- Check touch targets and content descriptions.

**Implementation result — 2026-09-18**

- Added shared Compose components: `BalanccTopBar`, `BalanccFab`, `PrimaryButton`, `FinancialSegmentedControl`, `SelectionField`, `CategoryIcon`, `SectionHeader`, `EmptyState`, and generic `SelectionDialog`.
- Replaced the duplicated income/expense tabs in the add and monthly-detail screens with the same `FinancialSegmentedControl` while preserving their existing state and callbacks.
- Replaced the duplicated add FAB implementations with `BalanccFab` and gave the action an accessible label.
- Migrated Settings to `BalanccTopBar` and `SectionHeader` without changing DataStore behavior or navigation.
- `./gradlew test` completed with the same recorded baseline result: 35 passed and `MainViewModelTest.kt:129` failed.

### [x] Phase 3 — Dashboard redesign

**Likely files**

- `ui/screens/main/MainScreen.kt`
- `ui/components/FinancialAmount.kt`
- `ui/components/FinancialSummaryCard.kt`
- `ui/components/YearSelector.kt`
- `res/values/strings.xml`
- `res/values-es/strings.xml`

**New/reused components**

- `BalanccTopBar`, `YearSelector`, `FinancialSummaryCard`, `FinancialAmount`, `BalanccFab`, and `EmptyState`.

**Risks**

- Do not change the existing year filter, the month list policy, or income/expense calculations.
- Ensure the FAB does not hide the final list item.

**Validation**

- Test years with and without movements and positive/negative balances.
- Open a month from the redesigned card and verify the selected month is preserved.

**Implementation result — 2026-09-18**

- Created reusable `FinancialAmount` composable for semantic currency styling (income green, expense red, sign support).
- Created `FinancialSummaryCard` replacing the old purple cards with clean surface styling, 20dp corners, 1dp subtle border, and distinct income/expense/balance hierarchy.
- Created `YearSelector` chip component powered by `SelectionDialog`.
- Replaced manual emoji empty state in `MainScreen` with the shared `EmptyState` component and added localized strings (`no_movements_in_year`).
- Configured `LazyColumn` bottom padding (`88.dp`) to prevent the `BalanccFab` from obscuring list items.
- `:app:compileDebugKotlin` passed.
- `./gradlew test` completed with the same recorded baseline result: 35 passed and `MainViewModelTest.kt:129` failed.

### [ ] Phase 4 — Add transaction and category selector redesign

**Likely files**

- `ui/screens/addbalance/AddBalanceScreen.kt`
- `res/values/strings.xml`
- `res/values-es/strings.xml`

**New/reused components**

- `BalanccTopBar` with back navigation, `FinancialSegmentedControl`, `FinancialAmount`, `CategorySelector`, `SelectionField`, and `PrimaryButton`.
- Reuse `AddBalanceViewModel`, the current calendar dependency, and amount formatting helpers.

**Risks**

- Keep fast-add month behavior, amount cleaning, date selection, and saving state intact.
- The calendar dialog must remain legible in both themes.

**Validation**

- Add one income and one expense, then confirm they persist into dashboard/detail.
- Test category selection, date selection, long notes, large amounts, and small-screen scrolling.

### [ ] Phase 5 — Monthly detail, chart, and transaction list redesign

**Likely files**

- `ui/screens/detail/MonthsDetailScreen.kt`
- Possibly `ui/screens/detail/model/MonthsDetailUiState.kt` only if required for presentation semantics.
- Possibly `ui/screens/detail/viewmodel/MonthsDetailViewModel.kt` only to remove UI-color presentation data; never to alter calculations.
- `core/Charts.kt` as a separate cleanup decision, since its chart implementation is currently unused.

**New/reused components**

- `FinancialSegmentedControl`, `FinancialAmount`, `FinancialDonutChart`, `TransactionCard`, `DateSectionHeader`, `BalanccFab`, and `EmptyState`.

**Risks**

- Preserve income/expense queries, grouping by day, chart data, and deletion behavior.
- The MPAndroidChart `AndroidView` must be checked in both themes.
- Correct the existing presentation defect where the expense list invokes `IncomeCard`; this must not alter deletion or data behavior.

**Validation**

- Test months containing only income, only expenses, both, and no transactions.
- Validate chart categories, large values, long descriptions, delete actions, and income/expense semantics.

### [ ] Phase 6 — Settings, accessibility, and final quality pass

**Likely files**

- `ui/screens/settings/SettingsScreen.kt`
- `ui/screens/splash/SplashScreen.kt`
- `res/values/strings.xml`
- `res/values-es/strings.xml`
- Shared components when final refinements are needed.

**New/reused components**

- `SectionHeader`, `SettingsRow`, `SelectionDialog`, and the shared top bar.

**Risks**

- Do not change DataStore persistence or locale application behavior.
- Settings must remain usable with font scaling and small screens.

**Validation**

- Toggle theme, restart, and confirm persistence.
- Change language and confirm locale application.
- Review accessibility labels, 48dp touch targets, light/dark contrast, small and large screens, and font scaling.
- Run `./gradlew test`, `./gradlew lint`, and the appropriate build task.

## Out of scope

- Room schema/migrations, DAO queries, repository interfaces, domain use cases, balance calculations, currency behavior, and navigation redesign.
- Refactoring `Category` to remove its Compose color dependency. That is a separate architecture task if desired later.
