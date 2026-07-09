# Play Console "Data Safety" Form — Answer Key

**This file was rewritten on 2026-07-09.** The previous version of this answer key told you to declare
"no ads" and "no data collection." That stopped being true once AdMob (banner + interstitial) and
optional Google Drive backup were added (currently in v1.7.0, `versionCode 17`) — filing the old
answers now would be a **false declaration to Play Console**, which risks a policy strike or app
suspension if Google's automated scan of your APK/AAB (which does detect the AdMob SDK) disagrees
with what you declared. This version reflects the app's actual current dependencies and manifest.

⚠️ **Not legal advice.** This is a careful reading of the code, not a substitute for review by
someone qualified to advise on Play Console policy compliance for your specific situation.

The Play Console rule that matters most here: in this form, **"collect" specifically means
transmitting data off the user's device**, to any destination — including the user's own Google
Drive or a third-party SDK's servers, not just "to the developer." ([Google's own guidance](https://support.google.com/googleplay/android-developer/answer/10787469))

## Step 1 — Data collection and security

**"Does your app collect or share any of the required user data types?"**
→ **Yes.** Two independent things leave the device, and both need declaring:

1. **AdMob (banner + interstitial ads)** — the SDK is present (`play-services-ads:23.3.0` in
   `app/build.gradle.kts`, plus the `APPLICATION_ID` meta-data in `AndroidManifest.xml`). Declare:
   - **Device or other IDs** → collected, shared with Google (AdMob), purpose: **Advertising or
     marketing**. This is what the Advertising ID (AD_ID) declaration below covers.
   - Data is **not** collected for App functionality/analytics by you directly — it's the ad SDK's
     own collection, which Play Console asks about separately as "third party" collection.
   - The app does **not** request `ACCESS_FINE_LOCATION`/`ACCESS_COARSE_LOCATION`, so you do not
     need to declare precise or approximate location as collected via ads.
2. **Optional Google Drive backup** — only if the user turns it on in Settings. Declare:
   - **Financial info** (the backed-up expense/budget database) → collected, **not shared** with
     the developer (it goes to the user's own Drive, which the developer never accesses), purpose:
     **App functionality** (backup/restore), and mark it **optional** — the app is fully usable
     with backup off.
   - This data is encrypted in transit (HTTPS to Google's Drive API).

Everything else the app touches — expenses, categories, budgets, Khata/debt/split entries, display
name, or the text of any SMS you approve — still never leaves the device unless the user explicitly
exports/shares it themselves (PDF/CSV share sheet) or turns on Drive backup. The SMS User Consent
flow itself is still **not** collection: Android hands the message text to the app locally after a
per-message tap, and the app never relays it anywhere.

## Step 2 — Security practices

- **"Is all user data encrypted in transit?"** → **Yes**, for the two flows above (AdMob and Drive
  backup both use HTTPS). Nothing else transmits.
- **"Do you provide a way for users to request that their data be deleted?"** → **Yes** — users can
  delete everything instantly via **Settings → Reset All Data**, and can delete a Drive backup from
  their own Drive or from **Settings → Backup** at any time.

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
or Drive backup risks a policy strike or suspension once Google's static analysis of the AAB detects
`play-services-ads`/Drive API calls that don't match your declaration; over-disclosing the SMS flow
(declaring "collects SMS data") would incorrectly trigger the restricted-permissions review process
this app is specifically designed to avoid. Re-check this file against the actual dependency list in
`app/build.gradle.kts` before every submission — it's the single most reliable source of truth for
what needs declaring, more reliable than memory of what the app used to do in an earlier version.
