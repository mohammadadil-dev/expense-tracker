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
    // Google Play has required new app submissions to target API 35 (Android 15) since
    // Aug 31 2025 — compileSdk must be >= targetSdk, so both move together. If Android
    // Studio's sync complains it doesn't recognize compileSdk 35, update Android Studio /
    // the Android Gradle Plugin first (Help → Check for Updates) rather than lowering this.
    compileSdk = 35

    defaultConfig {
        // applicationId is the permanent, Play Store–unique package name — it does not need
        // to match `namespace` above (which only governs the generated R class package and
        // is what the app's own Kotlin source files are declared under). Keeping `namespace`
        // unchanged avoids renaming every package declaration in the codebase.
        applicationId = "com.agtech.expensetracker"
        minSdk = 26
        targetSdk = 35
        versionCode = 17
        versionName = "1.7.0"

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
