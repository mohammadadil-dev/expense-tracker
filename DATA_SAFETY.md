# Play Console "Data Safety" Form — Answer Key

This walks through Play Console's **App content → Data safety** questionnaire with the exact answers for this app, and why. It's based on the app's actual code: no network permission usage, no analytics/ad SDKs, all storage local (Room/SQLite), and SMS access only via Android's SMS User Consent API (which never transmits anything off-device).

The Play Console rule that matters most here: in this form, **"collect" specifically means transmitting data off the user's device** to a server. Processing something on-device and never sending it anywhere does not count as "collecting" it. ([Google's own guidance](https://support.google.com/googleplay/android-developer/answer/10787469))

## Step 1 — Data collection and security

**"Does your app collect or share any of the required user data types?"**
→ **No.**

This is accurate because nothing the app touches — expenses, categories, budgets, display name, or the text of any SMS you approve — ever leaves the device. There's no backend to send it to. Answering "No" here skips all the granular per-data-type questions; you'll see a one-line summary like "No data collected" on the finished form.

If Play Console ever nudges you to reconsider because the app requests SMS-related Play Services APIs: it's still "No" for collection, because the SMS User Consent API's whole design point is that the message text is handed to your app locally by the OS only after a per-message tap, and your app never relays it anywhere.

## Step 2 — Security practices

Since you answered "No" to collection, most of this section is skipped automatically. If Play Console still asks:

- **"Is all user data encrypted in transit?"** → Not applicable / no data is transmitted.
- **"Do you provide a way for users to request that their data be deleted?"** → **Yes** — note in the explanation field that users can delete everything instantly and permanently via **Settings → Reset All Data**, since there's no server-side copy to also delete.

## Step 3 — Privacy policy URL

Play Console requires this regardless of how you answered above. Host `PRIVACY_POLICY.md` (already drafted in this project) somewhere public — GitHub Pages and Google Sites both work and are free — and paste that URL here.

## Adjacent forms you'll also hit (not part of Data Safety, but in the same review flow)

- **Ads declaration**: answer **No** — the app has no ad SDK anywhere in its dependencies.
- **Advertising ID declaration** (required for apps targeting API 33+): answer **"No, my app does not use advertising ID."** Verified by checking `app/build.gradle.kts` (no ad/analytics SDK, no `play-services-ads-identifier`) and `AndroidManifest.xml` (no `com.google.android.gms.permission.AD_ID`). The only Google Play Services dependency, `play-services-auth`, is used solely for the SMS User Consent prompt and doesn't touch advertising ID.
- **Target audience and content**: this is a general-purpose finance utility, not designed for or directed at children — answer accordingly in the age-group questionnaire.
- **Content rating questionnaire**: answer **No** to all the violence/gambling/etc. prompts — it's a finance utility with no objectionable content. You'll come out at the lowest rating tier (e.g., "PEGI 3" / "Everyone").
- **Permissions declaration form (restricted permissions like SMS/Call Log)**: **this app does not need to file it.** That form only applies to apps that declare the dangerous `READ_SMS` / `RECEIVE_SMS` permissions in the manifest. This app's `AndroidManifest.xml` declares none — it uses the SMS User Consent API specifically because it's exempt from that permission and from this declaration requirement.

## Why this matters

Getting the SMS answer wrong in either direction has real consequences: under-disclosing risks a policy strike if a reviewer reads the code and sees SMS-related Play Services calls; over-disclosing (declaring "collects SMS data") would incorrectly trigger the restricted-permissions review process this app is specifically designed to avoid. The answer key above reflects what the code actually does, not just the most cautious guess.
