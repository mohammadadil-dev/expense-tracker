# Play Console "Data Safety" Form — Answer Key

**Updated 2026-07-10** to cover the new Notification Listener–based background SMS detection (see
"Notification Listener access" under Step 1) — this needs its own sensitive-permission disclosure
in Play Console, separate from the standard Data Safety questions below.

**This file was rewritten on 2026-07-09, then corrected again the same day.** The previous version
of this answer key told you to declare "no ads" and "no data collection" — that stopped being true
once AdMob (banner + interstitial) was added (currently in v1.7.0, `versionCode 17`). The first
rewrite also told you to declare Google Drive backup as collecting Financial info; that's been
walked back in this version because the Drive backup **UI is currently disabled** (see Step 1) —
declaring a feature users can't actually reach would over-disclose. Keep this file in sync with
`SettingsScreen.kt`'s current state, not just `build.gradle.kts`'s dependency list, since a
dependency being present doesn't mean the feature is reachable.

⚠️ **Not legal advice.** This is a careful reading of the code, not a substitute for review by
someone qualified to advise on Play Console policy compliance for your specific situation.

The Play Console rule that matters most here: in this form, **"collect" specifically means
transmitting data off the user's device**, to any destination — including the user's own Google
Drive or a third-party SDK's servers, not just "to the developer." ([Google's own guidance](https://support.google.com/googleplay/android-developer/answer/10787469))

## Step 1 — Data collection and security

**"Does your app collect or share any of the required user data types?"**
→ **Yes** — but for one reason only right now, not two:

1. **AdMob (banner + interstitial ads)** — the SDK is present (`play-services-ads:23.3.0` in
   `app/build.gradle.kts`, plus the `APPLICATION_ID` meta-data in `AndroidManifest.xml`) and is
   actively shown to users. Declare:
   - **Device or other IDs** → collected, shared with Google (AdMob), purpose: **Advertising or
     marketing**. This is what the Advertising ID (AD_ID) declaration below covers.
   - Data is **not** collected for App functionality/analytics by you directly — it's the ad SDK's
     own collection, which Play Console asks about separately as "third party" collection.
   - The app does **not** request `ACCESS_FINE_LOCATION`/`ACCESS_COARSE_LOCATION`, so you do not
     need to declare precise or approximate location as collected via ads.

2. **Google Drive backup — NOT currently live, do not declare.** The Drive backend code
   (`DriveBackupManager.kt`) and dependency are present in the project, but its entry point in
   **Settings → Backup** is commented out (`SettingsScreen.kt`: "Google Drive backup section
   hidden — re-enable in next release once OAuth consent screen is fully configured"). A user
   cannot reach this feature in the current build, so no financial data is actually transmitted
   through it — nothing to declare here *yet*. **Re-add this declaration (see prior version of
   this file, or ask to have it regenerated) the moment that Settings section is un-commented and
   shipped** — don't forget, since Google's static scan may eventually detect the dormant Drive
   API calls even before the UI is re-enabled, and an undeclared-but-present capability is exactly
   the kind of mismatch that triggers review scrutiny.

Everything else the app touches — expenses, categories, budgets, Khata/debt/split entries, display
name, or the text of any SMS you approve — never leaves the device unless the user explicitly
exports/shares it themselves via the PDF/CSV/JSON share sheet (which routes through apps the user
picks, not through us). The SMS User Consent flow itself is still **not** collection: Android hands
the message text to the app locally after a per-message tap, and the app never relays it anywhere.
Family Mode is also currently hidden/commented out in `SettingsScreen.kt` and irrelevant here
regardless, since it was always local-only with no data leaving the device.

### Notification Listener access (added for background SMS detection)

`TransactionNotificationListener.kt` uses `BIND_NOTIFICATION_LISTENER_SERVICE` to read
notifications from the device's default SMS app, so transaction detection keeps working when the
app is closed (see `SmsConsentManager.kt`'s doc comment for why the old foreground-only approach
needed this). Same as the SMS User Consent flow, this is **not** "collection" under Play's
definition — the notification text is read and parsed entirely on-device, filtered in code to the
default SMS package only, and nothing is transmitted anywhere.

However, this is a **separate, more sensitive disclosure than Data Safety**: Notification Listener
(`android.permission.BIND_NOTIFICATION_LISTENER_SERVICE`) is one of Android's "special app access"
permissions, in the same family of scrutiny as Accessibility Service — Play Console has historically
required a specific justification for apps requesting it, reviewed more strictly than an ordinary
runtime permission, precisely because it can technically read *any* notification on the device (this
app deliberately only reads the default SMS package's notifications, enforced in code, not by the
permission itself — the permission grant is all-or-nothing on Android's side). **Before submitting a
release with this feature**, check Play Console's current policy center for whatever the live
"sensitive permissions" / notification access disclosure requirement is at the time — this changes
more often than the standard Data Safety form, and specifics here may be stale by the time you read
this. Explain clearly, wherever Play Console asks: "reads notifications from the user's own default
SMS app only, to detect bank/UPI transaction messages, entirely on-device, off by default and opt-in."

## Step 2 — Security practices

- **"Is all user data encrypted in transit?"** → **Yes**, for the one active flow above (AdMob,
  HTTPS). Nothing else transmits while Drive backup stays disabled.
- **"Do you provide a way for users to request that their data be deleted?"** → **Yes** — users can
  delete everything instantly via **Settings → Reset All Data**.

## Step 3 — Privacy policy URL

Play Console requires this regardless of how you answered above. Host `PRIVACY_POLICY.md` (already
rewritten to match this version) somewhere public — GitHub Pages and Google Sites both work and are
free — and paste that URL here.

## Adjacent forms you'll also hit (not part of Data Safety, but in the same review flow)

- **Ads declaration**: answer **Yes** — the app shows ads via AdMob.
- **Advertising ID declaration** (required for apps targeting API 33+): answer **"Yes, my app uses
  advertising ID,"** purpose **Advertising or marketing**. The `play-services-ads` dependency pulls
  in the `com.google.android.gms.permission.AD_ID` permission automatically via manifest merger even
  though it's not written explicitly in `AndroidManifest.xml` — check the merged manifest
  (Build → Analyze APK, or the `app/build/intermediates/merged_manifest/` output) to confirm before
  submitting if you want to see it directly.
- **Target audience and content**: this is a general-purpose finance utility, not designed for or
  directed at children — answer accordingly in the age-group questionnaire. Note that since the app
  now shows ads, if you ever *do* mark it as child-directed, Play has extra requirements
  (family-safe ad SDKs, no AD_ID) that this app's current ad setup does not meet — keep the audience
  answer as "not directed at children" unless that setup changes too.
- **Content rating questionnaire**: answer **No** to all the violence/gambling/etc. prompts — it's a
  finance utility with no objectionable content. You'll come out at the lowest rating tier (e.g.,
  "PEGI 3" / "Everyone").
- **Permissions declaration form (restricted permissions like SMS/Call Log)**: **this app still does
  not need to file it.** That form only applies to apps that declare the dangerous `READ_SMS` /
  `RECEIVE_SMS` permissions in the manifest. This app's `AndroidManifest.xml` declares none — it
  uses the SMS User Consent API specifically because it's exempt from that permission and from this
  declaration requirement.

## Why this matters

Getting these answers wrong has real consequences in both directions: under-disclosing the ad SDK
risks a policy strike or suspension once Google's static analysis of the AAB detects
`play-services-ads` calls that don't match your declaration; over-disclosing a feature nobody can
reach (Drive backup, today) or the SMS flow (declaring "collects SMS data") would incorrectly
trigger extra review scrutiny this app doesn't need yet. Re-check this file against both
`app/build.gradle.kts` (what's *possible*) and `SettingsScreen.kt` (what's actually *reachable* by
a user) before every submission — a dependency alone doesn't mean a data flow is live.
