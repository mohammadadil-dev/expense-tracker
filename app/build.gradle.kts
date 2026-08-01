import java.io.FileInputStream
import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.kapt")
}

// Release signing credentials live in local.properties (gitignored, machine-local), never in
// this file — build.gradle.kts is tracked by git and previously had the keystore password
// committed in plaintext. Debug builds and non-release Gradle tasks don't need these values,
// so a missing/incomplete local.properties only breaks `assembleRelease`/`bundleRelease`,
// not day-to-day development.
val localProperties = Properties().apply {
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        load(FileInputStream(localPropertiesFile))
    }
}

android {
    namespace = "com.expensetracker.app"
    // From Aug 31 2026 Google Play requires app UPDATES to target Android 16 (API 36); staying
    // on 35 would block all future updates. compileSdk must be >= targetSdk, so both move to 36.
    // API 36 requires Android Gradle Plugin >= 8.9.1 and Gradle >= 8.11.1 (bumped in the root
    // build.gradle.kts and gradle-wrapper.properties). If Android Studio can't recognise
    // compileSdk 36, update Studio (Meerkat 2024.3.1+) and install the Android 16 SDK platform
    // via SDK Manager rather than lowering this.
    compileSdk = 36

    defaultConfig {
        // applicationId is the permanent, Play Store–unique package name — it does not need
        // to match `namespace` above (which only governs the generated R class package and
        // is what the app's own Kotlin source files are declared under). Keeping `namespace`
        // unchanged avoids renaming every package declaration in the codebase.
        applicationId = "com.agtech.expensetracker"
        minSdk = 26
        targetSdk = 36
        // v1.8.0 (versionCode 19) was set before the rebrand + dashboard/Splits work below,
        // and was never published either — live Play Store is still versionCode 17 (1.7.0).
        // Bumping straight to 20/1.9.0 to fold everything in at once: the app rename from
        // "Expense Tracker" to Baqaya (new launcher icon + app_name across all 14 locales),
        // a trimmed/collapsible Dashboard (Spending by Category, Recent Transactions, AI
        // Insights and the 12-month Trend chart are now collapsed-by-default sections instead
        // of always-stacked cards; the Debts tile was dropped since Debts already has its own
        // bottom-nav tab; Subscription Cost only shows once there's real spend to report), a
        // per-expense WhatsApp share button on Split expenses (replacing the old auto-popup
        // after every save), a fix for Split expense country-code phone numbers and several
        // raw-Saudi-Riyal-text rendering bugs, a scroll-reactive bottom nav bar + FAB
        // (hides on scroll-down, reappears on scroll-up, across all 5 main screens), and a fix
        // for the Splits list's "Settled" badge going stale after adding/editing an expense.
        // v2.0.0 — major release: Savings Circle (jam'iya), Zakat calculator, Saudi bank SMS
        // parsing, More-tab nav, Charity category, full 14-language localization, and the
        // Android 16 (API 36) target bump. versionCode jumps to 21 (live Play is still 17).
        versionCode = 21
        versionName = "2.0.0"

        vectorDrawables {
            useSupportLibrary = true
        }
    }

    signingConfigs {
        create("release") {
            val storeFilePath = localProperties.getProperty("RELEASE_STORE_FILE")
            storeFile     = storeFilePath?.let { file(it) }
            storePassword = localProperties.getProperty("RELEASE_STORE_PASSWORD")
            keyAlias      = localProperties.getProperty("RELEASE_KEY_ALIAS")
            keyPassword   = localProperties.getProperty("RELEASE_KEY_PASSWORD")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("release")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.10"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            // Google API client library bundles duplicate META-INF files that the
            // Android packager rejects. Excluding them avoids the duplicate-resource error.
            excludes += "META-INF/DEPENDENCIES"
            excludes += "META-INF/LICENSE"
            excludes += "META-INF/LICENSE.txt"
            excludes += "META-INF/license.txt"
            excludes += "META-INF/NOTICE"
            excludes += "META-INF/NOTICE.txt"
            excludes += "META-INF/notice.txt"
        }
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.biometric:biometric:1.2.0-alpha05")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.4")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.4")
    implementation("androidx.activity:activity-compose:1.9.1")
    // Explicit (not just relying on activity-compose's transitive dependency) so MainActivity's
    // `by viewModels()` delegate — used to share the ExpenseViewModel instance with the SMS
    // consent flow — is guaranteed to resolve.
    implementation("androidx.activity:activity-ktx:1.9.1")

    // Updated to 2025.01.00 — includes 16KB page-aligned graphics-path native library
    implementation(platform("androidx.compose:compose-bom:2025.01.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")

    implementation("androidx.navigation:navigation-compose:2.7.7")

    // Room (on-device SQLite — no server, no recurring cost)
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")

    // Google Mobile Ads (AdMob) — banner + interstitial
    // Replace test ad-unit IDs in AdComponents.kt with production IDs before publishing.
    // Verify the latest version at: https://developers.google.com/admob/android/rel-notes
    // 23.x adds 16KB memory page alignment required by Play Store for API 35 targets
    implementation("com.google.android.gms:play-services-ads:23.3.0")

    // SMS User Consent API (SmsRetrieverClient) — lets the app read the text of one incoming
    // SMS at a time, only after the user taps "Allow" on a system-drawn prompt. Needs no
    // READ_SMS/RECEIVE_SMS manifest permission, so it doesn't trip Play Store's restricted
    // permissions review the way full SMS access would for a non-default-SMS-app like this one.
    implementation("com.google.android.gms:play-services-auth:21.5.0")

    // WorkManager — schedules the daily "Did you log today?" reminder notification.
    // Survives app restarts and device reboots; battery-friendly vs AlarmManager.
    implementation("androidx.work:work-runtime-ktx:2.9.1")

    // Glance — Compose-based home screen widget toolkit.
    // 1.1.1 fixes CVE-2024-7254 (protobuf-java) — Play Console flagged 1.1.0 for this.
    implementation("androidx.glance:glance-appwidget:1.1.1")
    implementation("androidx.glance:glance-material3:1.1.1")

    // Play In-App Updates — forces users onto the latest version via a full-screen
    // immediate update flow that can't be dismissed (used to push users off crashing builds).
    implementation("com.google.android.play:app-update-ktx:2.1.0")

    // Play In-App Review — native star-rating dialog shown after the 5th logged expense,
    // without sending the user to the Play Store. See util/InAppReviewManager.kt.
    implementation("com.google.android.play:review-ktx:2.0.1")

    // ML Kit Text Recognition — offline OCR for receipt/bill scanning.
    // On-device model (bundled); no network call needed at scan time.
    implementation("com.google.mlkit:text-recognition:16.0.1")

    // ML Kit Barcode Scanning — offline QR decode, used to scan a party's UPI QR code
    // and auto-fill their VPA instead of asking the user to type/dictate it. On-device
    // model (bundled); no network call needed at scan time.
    implementation("com.google.mlkit:barcode-scanning:17.3.0")

    // ZXing — pure on-device QR code generation for Khata "Request via UPI" payment
    // requests (encode only). Actual scanning/decoding of QR codes is handled by
    // ML Kit Barcode Scanning above, not this library.
    implementation("com.google.zxing:core:3.5.3")

    // Coil — Compose async image loading, used to show Khata receipt-photo thumbnails and
    // the full-screen viewer from a local file path without blocking the UI thread decoding
    // full-resolution JPEGs. No network image loading is used anywhere in this app.
    implementation("io.coil-kt:coil-compose:2.6.0")

    // Google Drive API — used for cloud backup / restore.
    // SETUP REQUIRED before using: go to console.cloud.google.com, enable the Drive API,
    // and register an Android OAuth 2.0 client with package name com.agtech.expensetracker
    // and the SHA-1 from your release keystore. No google-services.json needed.
    implementation("com.google.api-client:google-api-client-android:2.2.0")
    implementation("com.google.apis:google-api-services-drive:v3-rev20220815-2.0.0")
    implementation("com.google.http-client:google-http-client-gson:1.43.3")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.12.01"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")

    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}
