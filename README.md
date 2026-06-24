# Expense Tracker

A native Android app for logging monthly expenses and viewing category breakdowns and 12-month trends. Built with Kotlin and Jetpack Compose. All data is stored on-device in a Room (SQLite) database — there is no backend, no account, and no recurring cost of any kind.

## Why this has zero server cost

- **Storage**: Room/SQLite, local to the device. No Firebase, no cloud database, no hosting bill.
- **Language**: Arabic and English bundled as static string resources (`values/strings.xml`, `values-ar/strings.xml`). No translation API calls.
- **Region/currency defaults**: detected once from the phone's own `Locale`/`Configuration` (`CurrencyLocaleMapper.kt`) — no IP geolocation service, no network call. The user can override the language and currency symbol manually in Settings at any time.
- **Charts**: drawn with Compose `Canvas` directly, no charting library/license.

The only unavoidable cost is the one-time $25 Google Play developer registration fee, paid once to Google when you create your Play Console account — that is not something this app or Claude can avoid, and it isn't a recurring cost.

## Project structure

```
ExpenseTrackerApp/
├── app/
│   ├── build.gradle.kts          # app-level Gradle config (applicationId, dependencies)
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/expensetracker/app/
│       │   ├── MainActivity.kt
│       │   ├── ExpenseApp.kt              # Application class — first-run currency detection, locale re-apply
│       │   ├── data/                      # Room entities, DAOs, database, repository, locale/currency mapping
│       │   ├── viewmodel/ExpenseViewModel.kt
│       │   ├── util/                      # DateUtils, Formatters, LocaleHelper, category display helper
│       │   └── ui/
│       │       ├── components/            # DonutChart, TrendBarChart, StatCard, AddEditExpenseSheet, CategoryManageSheet
│       │       ├── screens/                # DashboardScreen, SettingsScreen
│       │       ├── navigation/AppNav.kt
│       │       └── theme/                  # Color.kt, Theme.kt, Type.kt
│       └── res/
│           ├── values/strings.xml          # English strings
│           ├── values-ar/strings.xml       # Arabic strings
│           ├── mipmap-anydpi-v26/          # adaptive launcher icon (placeholder)
│           └── drawable/                   # launcher icon background/foreground
├── build.gradle.kts               # project-level Gradle config
├── settings.gradle.kts
└── gradle.properties
```

## Opening the project

1. Install [Android Studio](https://developer.android.com/studio) (the free Community/current edition is sufficient).
2. `File → Open`, select the `ExpenseTrackerApp` folder.
3. Let Gradle sync. **Note**: this project does not include the Gradle wrapper scripts (`gradlew`, `gradlew.bat`, `gradle-wrapper.jar`) — only `gradle/wrapper/gradle-wrapper.properties`. Android Studio detects this automatically and offers to regenerate the missing wrapper files on first sync ("Gradle wrapper is missing... Create one?" or it just does it silently via the bundled Gradle). If it doesn't prompt, open the Gradle tool window and click the elephant/sync icon, or run `gradle wrapper` once from a terminal with any local Gradle install.
4. Once sync completes, click Run (▶) with an emulator or a connected device (minSdk 26 / Android 8.0+).

## Before you publish — required changes

1. **`applicationId`**: currently `com.expensetracker.app` in `app/build.gradle.kts`. This is a placeholder. Change it to something unique that you control (e.g. reverse-domain of a domain you own, or `com.yourname.expensetracker`) — Play Console will reject a generic/squatted ID and you cannot change it after your first upload. (Kept as-is for now per the developer's choice — revisit this before the first upload, since it's permanent once submitted.)
2. **Launcher icon**: already replaced with a custom adaptive icon (`drawable/ic_launcher_foreground.xml` / `ic_launcher_background.xml`, plus a monochrome variant) — no further action needed unless you want different artwork.
3. **App name**: `app_name` in `strings.xml` / `values-ar/strings.xml` / `values-hi/strings.xml` — currently "Expense Tracker". Change if you want a different display name.
4. **Target/compile API level**: bumped to `compileSdk = 35` / `targetSdk = 35` in `app/build.gradle.kts` — Google Play has required new app submissions to target API 35 (Android 15) since Aug 31 2025; the project previously targeted 34 and would have been rejected on upload. If Android Studio's sync complains it doesn't recognize SDK 35 (rather than just failing to find the platform), update Android Studio / the Android Gradle Plugin too (`Help → Check for Updates`), and install "Android 15.0 (API 35)" via the SDK Manager if it's not already on your machine.
5. **Version bump**: `versionCode` / `versionName` in `app/build.gradle.kts` before each new release (currently `1` / `"1.0"`, correct for a first release).

## Building a release AAB (Android App Bundle)

Play Console requires an Android App Bundle, not a plain APK.

1. In Android Studio: `Build → Generate Signed Bundle / APK`.
2. Choose **Android App Bundle**.
3. Create a new keystore (`Create new...`) if you don't have one yet — store the `.jks` file and its passwords somewhere safe; you need the *same* keystore for every future update to this app.
4. Select the `release` build variant, finish the wizard. The signed `.aab` lands under `app/release/`.

## Publishing to the Play Store

1. Create a [Google Play Console](https://play.google.com/console) account if you don't already have one — one-time **$25** registration fee (paid directly to Google, not a Claude/Anthropic charge, and not recurring).
2. Create a new app, fill in the store listing. Ready-to-paste title/short description/full description text is in **`STORE_LISTING.md`** in this folder. You'll still need at least 2 phone screenshots and ideally a 1024×500 feature graphic — both have to come from an actual running build (emulator or device), which can't be produced without one.
3. Upload the signed `.aab` under **Internal testing** first (recommended, so you can install and check it yourself before anyone else can), then promote the same build to **Production** once you're satisfied.
4. **Data Safety form**: a full walkthrough with the exact answers for this app — including why the SMS auto-detect feature doesn't count as "collecting" data — is in **`DATA_SAFETY.md`**.
5. **Privacy policy**: Play Console requires a hosted privacy policy URL even for apps that collect no data. The text is already drafted in **`PRIVACY_POLICY.md`** — host it as-is on GitHub Pages, Google Sites, or any free static page host, and paste that URL into Play Console. (This is a template reflecting the app's actual behavior, not legal advice — have it reviewed if you want legal certainty.)
6. **Content rating questionnaire**: this is a simple finance/utility app with no objectionable content — answer accordingly.
7. Submit for review. Google's review typically takes from a few hours to a few days.

## Localization notes

- The app reads the phone's system language at first launch. If it's Arabic, the UI (including RTL layout — Compose handles this automatically via `LocalLayoutDirection`) and number/date formatting follow Arabic; otherwise it defaults to English.
- The user can force English or Arabic regardless of phone language from **Settings → Language**, independent of the OS setting.
- Currency symbol is guessed from the phone's region (e.g., a phone set to Saudi Arabia suggests `ر.س`, UAE suggests `د.إ`, US suggests `$`) but is always editable in **Settings → Currency**, either via the quick-pick chips or a free-text field for any symbol not listed.

## Resetting data

**Settings → Reset All Data** wipes every expense, category, and preference back to the bundled defaults — a two-step confirmation prevents accidental taps. There is no "undo" since nothing is backed up off-device by design (that's what keeps this free).
