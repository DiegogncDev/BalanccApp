# BalanccApp — Modern UI/UX Redesign Specification

## 1. Purpose

This document defines the visual and UX redesign of **BalanccApp**, a personal/business finance tracking application.

The objective is to replace the current dated visual style with a **modern, professional, calm and commercially credible interface** suitable for:

- Personal expense/income tracking.
- Small-business and entrepreneur use.
- A developer portfolio demonstrating production-oriented UI/UX skills.
- Future expansion with statistics, calculators and other financial tools.

The redesign must improve the visual quality **without changing the application's existing business logic or data behavior** unless a UI change explicitly requires it.

The application should feel like a serious financial/productivity product rather than a demo application.

---

# 2. Core Design Direction

## Design keywords

The visual language should communicate:

- Professional
- Modern
- Trustworthy
- Calm
- Minimal
- Financial
- Organized
- Premium but accessible
- Soft and polished

Avoid a design that feels:

- Neon
- Excessively colorful
- Childish
- Overly playful
- Visually noisy
- Full of gradients
- Excessively shadowed
- Overloaded with cards

The previous purple-heavy design should not be preserved as the dominant visual identity.

The redesign should use **neutral surfaces + one restrained primary accent + semantic financial colors**.

---

# 3. General UX Principles

## 3.1 Information hierarchy

Every screen must have a clear hierarchy:

1. Screen purpose/title
2. Most important financial information
3. Primary action
4. Supporting information
5. Secondary actions

Do not give every element the same visual weight.

Large typography should be reserved for:

- Current balance
- Important totals
- Screen titles when appropriate

Secondary information should use smaller typography and lower contrast.

---

## 3.2 Reduce cognitive load

Financial applications contain numbers, categories and dates. The UI must make them easy to scan.

Prefer:

- Short labels
- Consistent alignment
- Predictable spacing
- Grouped information
- Clear visual distinction between income and expenses

Avoid unnecessary borders and decorative elements.

---

## 3.3 One primary action per screen

Each screen should have an obvious primary action.

For example:

- Add screen → `Add`
- Monthly details → floating `+`
- Dashboard → add transaction / navigate to details

Secondary actions should visually recede.

---

# 4. Visual Foundation

## 4.1 Surfaces

Use a soft neutral background instead of pure white.

Suggested light theme:

```text
Background:        #F7F8FA
Surface:            #FFFFFF
Surface Secondary:  #F1F3F5
Border:             #E3E6EA
```

The exact values may be adjusted slightly during implementation to maintain good contrast.

Do not use pure white for every surface.

Use white primarily for elevated cards and important content containers.

---

# 5. Color System

## 5.1 Primary color

Use a restrained blue/petrol/navy-oriented primary color.

Suggested:

```text
Primary:            #244A5A
Primary Dark:       #193743
Primary Container:  #DCE9ED
```

The primary color should communicate stability and professionalism.

It should not be excessively saturated.

---

## 5.2 Income

Income should use a calm green.

```text
Income:             #2E8B57
Income Container:   #E3F2E9
```

Use income green for:

- Positive balance
- Income totals
- Income transaction amounts
- Income chart indicators

Do not make every income-related component bright green.

---

## 5.3 Expenses

Expenses should use a restrained red/coral.

```text
Expense:            #C95757
Expense Container:  #F9E5E5
```

Use expense red for:

- Expense totals
- Expense transaction amounts
- Expense chart indicators
- Destructive financial context where appropriate

Avoid highly saturated bright red.

---

## 5.4 Text colors

```text
Text Primary:       #182126
Text Secondary:     #66727A
Text Tertiary:      #8A949B
Text Disabled:      #AEB5BA
```

Primary text must have strong contrast.

Secondary text should be clearly readable but visually subordinate.

---

## 5.5 Semantic colors

Additional semantic colors can be used sparingly:

```text
Warning:            #B7791F
Warning Container:  #FFF3D6

Error:              #C95757
Error Container:    #F9E5E5

Success:            #2E8B57
Success Container:  #E3F2E9
```

---

# 6. Typography

Use a modern sans-serif typeface available on Android, preferably the application's existing Material typography system.

Typography should have strong hierarchy.

Suggested scale:

```text
Screen Title:       28–32sp, Bold
Large Amount:       32–40sp, Bold
Section Title:      22–24sp, Bold
Card Title:         17–19sp, SemiBold
Body:               15–16sp, Regular
Label:              13–14sp, Medium
Supporting Text:    12–14sp, Regular
```

Avoid excessive use of bold.

Numbers should be visually strong because financial values are the most important content.

---

# 7. Spacing System

Use a consistent 4dp/8dp-based spacing system.

Preferred values:

```text
4dp   — micro spacing
8dp   — icon/text spacing
12dp  — compact component spacing
16dp  — standard padding
20dp  — section spacing
24dp  — major spacing
32dp  — large section spacing
```

Avoid arbitrary spacing values unless required for a specific visual alignment.

Cards should generally use:

```text
Horizontal padding: 16–20dp
Vertical padding:   16–20dp
```

---

# 8. Shape System

Rounded corners are a major part of the new identity, but they must remain controlled.

Suggested:

```text
Small controls:        10–12dp
Inputs:                14–16dp
Cards:                 18–20dp
Large containers:      24dp
Bottom sheets/dialogs: 24–28dp
Floating action:       18–20dp
```

Do not make every element extremely rounded.

Avoid excessive "pill" components except for:

- Segmented controls
- Filters
- Compact status indicators

---

# 9. Elevation and Shadows

The design should use **subtle elevation**.

Prefer:

- Very soft shadows
- Low opacity
- Small blur
- Minimal vertical offset

Cards should not appear to float heavily above the background.

When a border is sufficient to separate a surface, prefer the border over a strong shadow.

---

# 10. Icons

Use one consistent icon family.

Prefer Material Icons / Material Symbols or another consistent Android icon set already used by the project.

Icons should:

- Have consistent visual weight.
- Use the same size within equivalent components.
- Be secondary to the associated label unless they communicate an action.
- Avoid using a different bright color for every category.

Suggested sizes:

```text
Small icon:      20dp
Standard icon:   24dp
Large icon:      28–32dp
```

---

# 11. Category Colors

BalanccApp currently supports categories such as:

- Investment
- Work
- Gift
- Grocery
- Entertainment
- Transport
- Utilities
- Rent
- Health
- Travel
- Food
- Education
- Pet
- Other

Do not assign highly saturated random colors to every category.

Instead, use a **soft category color system**.

Each category can have:

```text
Icon color
Soft container/background color
```

The category color should primarily appear around the icon or as a subtle supporting visual.

The transaction amount must still use the semantic financial color (income/expense), not the category color.

This creates consistency:

```text
Category → identifies WHAT the transaction is
Income/Expense color → identifies financial DIRECTION
```

---

# 12. Dashboard / Main Screen

The main screen should become the strongest demonstration of the new design.

## Header

Use a clean header containing:

- BalanccApp title/logo
- Settings action
- Year selector

Avoid oversized empty areas.

The year selector should look like a compact modern control rather than a large colored button.

Example structure:

```text
BalanccApp                         ⚙

[ Calendar  2026  ˅ ]
```

---

## Monthly cards

Monthly information should be presented as clean cards.

Each card should contain:

```text
January

Income                 Expenses
5,000                   20

──────────────────────────────

Balance                         +4,980
```

Important rules:

- Card background should be white or a very subtle tinted surface.
- Do not use the old large purple cards.
- Income and expense values should be visually distinct.
- Balance should be the strongest value inside the card.
- Keep alignment consistent between months.
- Cards should have 18–20dp corner radius.
- Use subtle border/elevation.

The user should be able to scan many months quickly.

---

# 13. Add Transaction Screen

This screen is one of the most important flows because entering a transaction is a core task.

## Header

Use:

```text
←   Add transaction
```

The title should be clear and compact.

---

## Income / Expense selector

Use a modern segmented control.

Example:

```text
┌─────────────────────────────────────┐
│  Income          │       Expense    │
└─────────────────────────────────────┘
```

The selected state should use the primary color or a restrained tinted container.

Do not use a huge solid purple block.

The selected/unselected states must have sufficient contrast.

---

## Amount

The amount should be the visual focus of the form.

Example:

```text
Amount

$ 0.00
```

Use large typography.

When empty:

- Use a muted text color.
- Do not make the placeholder look like an error.

When populated:

- Use primary text.
- Format the number consistently.

---

## Category selector

Use a clean elevated field/card.

Example:

```text
[ icon ]   Work                              ›
```

The category icon can use the category's soft accent color.

The field should feel tappable.

---

## Date selector

Use a compact date field:

```text
[ calendar ]    January 2026    ›
```

Avoid large blocks of empty space.

If a date picker is required, it should follow the same surface, radius and color system.

---

## Details

Use a proper outlined or filled text field.

Example:

```text
Details
┌─────────────────────────────────────┐
│ Add a note...                       │
│                                     │
└─────────────────────────────────────┘
```

Do not use a huge empty textarea unless the content requires it.

---

## Add button

The primary button should be visually clear.

Example:

```text
┌─────────────────────────────────────┐
│               Add                   │
└─────────────────────────────────────┘
```

Disabled state:

- Low-contrast neutral surface.
- Clearly communicates that required information is missing.

Enabled state:

- Primary color.
- Strong readable text.

The button should have a consistent height, approximately 52–56dp.

---

# 14. Category Menu

The current category dropdown should be redesigned.

Instead of a tall old-style menu with many colorful icons, use a modern menu or bottom sheet depending on the existing navigation architecture.

Each row:

```text
[ icon ]  Work
```

Recommended:

- 48–56dp row height.
- 16–20dp horizontal padding.
- Soft category icon container.
- Clear selected state.
- No unnecessary bright colors.

For many categories, a bottom sheet or dedicated selector can provide better usability than an extremely tall popup.

---

# 15. Monthly Details Screen

The monthly details screen should immediately communicate the month's financial result.

## Header summary

Example:

```text
January 2026

+4,980
```

The balance should be large.

Supporting values can be displayed below:

```text
Income       5,000
Expenses        20
```

The header should not consume the majority of the screen.

---

## Income / Expense selector

Use the same segmented-control component as the Add screen.

**Consistency is mandatory.**

Do not create a different tab style for this screen.

---

# 16. Donut Chart

The donut chart should remain because it communicates distribution effectively.

However:

- Use a restrained palette.
- Avoid overly saturated colors.
- Use a consistent chart background.
- Keep the center value highly readable.
- Maintain adequate whitespace around the chart.
- Do not allow the chart to dominate the entire screen.

Center:

```text
5,000
```

Use strong typography.

The chart should support the transaction list rather than compete with it.

---

# 17. Transaction List

Each transaction should use a clean horizontal card/list row.

Suggested structure:

```text
┌──────────────────────────────────────────────┐
│  [icon]   TRANSPORT                    5,000  │
│           Transport                          │
│                                              │
└──────────────────────────────────────────────┘
```

Better spacing and alignment are more important than decorative styling.

The amount should be aligned consistently on the trailing edge.

Delete should be secondary.

Do not make destructive actions visually dominant.

If possible, consider contextual swipe/delete interaction later, but do not implement it merely for the redesign unless appropriate.

---

# 18. Floating Action Button

The floating `+` action should remain available where it is useful.

Design:

- Rounded square rather than a perfect circle if it fits the visual system.
- Approximately 56dp.
- Primary color.
- White/appropriate high-contrast plus icon.
- Subtle elevation.

It should not obscure transaction content.

Maintain safe spacing from navigation/system bars.

---

# 19. Settings Screen

The Settings screen should use grouped settings sections.

Example:

```text
Settings

Appearance
────────────────────────────────
[ icon ]  Dark mode
          Change application theme          [switch]

Preferences
────────────────────────────────
[ icon ]  Language
          Select application language       Español ›
```

Use section headers with smaller typography.

Avoid unnecessary divider lines.

Settings rows should have enough touch area:

```text
Minimum recommended touch target: 48dp
```

---

# 20. Dark Theme

Dark mode should be designed as a first-class theme rather than simply inverting colors.

Suggested dark palette:

```text
Background:          #101417
Surface:             #171D21
Surface Secondary:   #20282D
Border:              #303A40

Text Primary:        #F1F4F5
Text Secondary:      #AEB8BD
Text Tertiary:       #7F8A90

Primary:             #8DB8C5

Income:              #63B982
Expense:             #E27676
```

Avoid pure black backgrounds unless technically necessary.

Avoid extremely bright neon accent colors.

The same hierarchy must remain recognizable in both themes.

---

# 21. Responsive Layout

The application must not be designed around one exact screenshot size.

Use responsive Compose/XML layout principles appropriate to the existing implementation.

Consider:

- Different Android screen sizes.
- Small phones.
- Large phones.
- System navigation bars.
- Display cutouts.
- Accessibility font scaling.

Content must not be clipped.

Scrollable content should remain usable on smaller devices.

---

# 22. Accessibility

The redesign must maintain accessibility.

Requirements:

- Minimum touch target around 48dp.
- Good text/background contrast.
- Do not communicate financial meaning using color alone.
- Icons should have meaningful content descriptions where appropriate.
- Important values should remain understandable without color.
- Respect system font scaling.
- Avoid extremely small supporting text.

Example:

Income should be distinguishable by:

```text
"Income"
+ amount
```

not only by green color.

---

# 23. Motion and Animation

Animations should be subtle.

Use animation for:

- Screen transitions where already supported.
- Selection changes.
- Expanding/collapsing content.
- FAB interactions.
- Chart appearance if appropriate.

Avoid:

- Excessive bouncing.
- Long animations.
- Decorative motion.
- Animations that delay basic actions.

Suggested duration:

```text
Short:   120–180ms
Normal:  200–300ms
Large:   300–400ms
```

Motion should communicate state changes rather than exist purely for decoration.

---

# 24. Components to Standardize

Create reusable UI components where the architecture permits.

Examples:

```text
BalanccAppTopBar
YearSelector
FinancialSegmentedControl
FinancialSummaryCard
TransactionCard
CategorySelector
CategoryIcon
AmountDisplay
PrimaryButton
SecondaryButton
SectionHeader
SettingsRow
EmptyState
```

The exact names can follow the project's existing conventions.

The goal is to avoid duplicating styling logic across screens.

---

# 25. Architecture / Implementation Constraints

The redesign is primarily a **UI/UX task**.

Do not unnecessarily modify:

- Database schema
- Room entities
- DAO behavior
- Repository contracts
- Use cases
- Domain models
- Business calculations
- Currency logic
- Existing navigation behavior

Do not rewrite ViewModels simply to change colors or spacing.

Reuse existing state and business logic.

If a UI component currently receives data through a ViewModel, preserve that flow.

---

# 26. Existing Architecture Compatibility

The project already follows a separation between UI and business/data layers.

The redesign should preserve the existing architecture, including the project's current:

- ViewModels
- Use cases
- Repository layer
- Room persistence
- Hilt dependency injection
- Coroutine dispatchers
- UI state models

UI changes should remain primarily inside the presentation/UI layer.

If an architectural problem is discovered while implementing the redesign, **do not silently refactor it**.

Instead:

1. Identify the issue.
2. Explain why it matters.
3. Suggest a separate refactor.
4. Keep the visual redesign isolated where possible.

---

# 27. Git / Development Workflow

Before making changes:

```bash
git status
```

Confirm the working tree is clean.

Create a dedicated redesign branch if appropriate:

```bash
git switch -c feature/ui-redesign
```

Make changes incrementally.

Recommended commits:

```text
feat(ui): add new design system
feat(ui): redesign dashboard
feat(ui): redesign transaction form
feat(ui): redesign monthly details
feat(ui): redesign settings
feat(ui): refine dark theme
```

Do not make one enormous commit containing unrelated architecture changes.

---

# 28. Agent Instructions

When an AI coding agent receives this document, follow these rules:

## Before coding

1. Inspect the complete existing UI structure.
2. Identify the screens corresponding to:
   - Main/dashboard
   - Add transaction
   - Category selector
   - Monthly details
   - Settings
3. Identify the existing theme/color definitions.
4. Identify reusable UI components.
5. Identify whether each screen is implemented using XML, Jetpack Compose, or a mixture.
6. Do not assume the architecture from this document; inspect the repository first.
7. Explain the proposed implementation plan before performing a large-scale refactor.

## During coding

- Preserve existing functionality.
- Prefer reusable components.
- Avoid duplicate colors and dimensions.
- Centralize design tokens.
- Keep financial semantics clear.
- Do not introduce unnecessary dependencies.
- Do not replace the project's architecture simply because another architecture is preferred.
- Do not remove existing features.
- Do not alter database/data behavior unless strictly necessary.

## After coding

Run the relevant checks available in the project:

```bash
./gradlew test
./gradlew lint
```

and/or the appropriate Android Studio build/test tasks.

Fix compilation errors and UI regressions.

Review:

- Light theme
- Dark theme
- Small screens
- Large screens
- Empty states
- Long transaction descriptions
- Large monetary values
- Disabled buttons
- Selected/unselected states

---

# 29. Definition of Done

The redesign is considered complete when:

- [ ] Main dashboard follows the new visual system.
- [ ] Add transaction screen follows the new visual system.
- [ ] Category selector follows the new visual system.
- [ ] Monthly details follows the new visual system.
- [ ] Settings follows the new visual system.
- [ ] Light theme is coherent.
- [ ] Dark theme is coherent.
- [ ] Income and expense semantics remain clear.
- [ ] Typography is consistent.
- [ ] Spacing is consistent.
- [ ] Corner radii are consistent.
- [ ] Cards and controls use a consistent elevation/border strategy.
- [ ] Buttons have clear enabled/disabled states.
- [ ] Touch targets are accessible.
- [ ] Existing business logic still works.
- [ ] Existing database behavior is unchanged.
- [ ] Existing navigation remains functional.
- [ ] The application compiles successfully.
- [ ] Relevant tests pass.
- [ ] No unnecessary dependencies were introduced.
- [ ] No unrelated refactoring was mixed into the redesign.

---

# 30. Final Design Principle

BalanccApp should look like a financial product that a real person could trust with their everyday financial information.

The visual design should not try to impress through excessive decoration.

It should impress through:

**clarity + hierarchy + consistency + restraint + polish.**

Every visual decision should answer one question:

> "Does this make the user's financial information easier to understand and the product feel more trustworthy?"

If the answer is no, simplify it.
