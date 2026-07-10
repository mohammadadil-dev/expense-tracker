# Privacy Policy for Expense Tracker

**Effective date: July 9, 2026**

This policy explains what Expense Tracker ("the app") does and does not do with your information. We wrote it to match exactly how the app behaves — there is no hidden data collection beyond what's described here.

## The short version

Expense Tracker has no servers and no user accounts. Every expense, category, budget, debt, ledger (Khata) entry, and setting you enter is stored only in a local database on your own device, and we — the developer — never receive, see, or store any of it. The app shows ads (see below), which is currently the only way any information leaves your device — described in full below.

## What the app stores, and where

All your data (expenses, income, categories, budgets, debts, ledger/Khata entries, split-group bills, goals, your display name, language and currency preferences) is saved in a local on-device database (Room/SQLite). It stays on your device unless you personally choose to move it — for example, by using the app's PDF/CSV export or local JSON backup file and sharing it yourself through Android's share sheet. That sharing step is initiated by you, goes through apps you select, and is governed by those apps' own privacy policies — we have no visibility into it.

If you uninstall the app, its local database is deleted with it. **Settings → Reset All Data** also lets you erase everything inside the app at any time, instantly and permanently.

## Advertising (AdMob)

The app shows banner and occasional full-screen ads served by Google AdMob, which is how the app stays free with no account or subscription required. AdMob may collect device identifiers (such as the Android Advertising ID), general app-interaction data, and IP-derived approximate location, and may use this data for ad personalization and measurement, per [Google's AdMob privacy practices](https://support.google.com/admob/answer/6128543). The app does not request device location permission and does not pass any of your financial data (expenses, budgets, Khata entries, etc.) to AdMob or any advertiser — the ad SDK operates independently of, and has no access to, the data described in "What the app stores" above. You can opt out of personalized advertising for your device in your Google Account's Ad Settings.

## Google Drive backup — not currently active

An optional Google Drive backup feature exists in the app's code but its UI is currently disabled/hidden pending Drive OAuth consent-screen setup — there is no way for a user to reach or trigger it in the current release, so no data is transmitted through it today. This policy will be updated with a full description before that entry point is re-enabled in a future release.

## SMS-based expense detection (optional)

If you turn on automatic expense detection from SMS in Settings, the app can detect bank/UPI/card transaction messages two ways — both optional, and both limited to your SMS app only:

1. **While the app is open** — Android's SMS User Consent API. This is a privacy-preserving system feature: when a new text message arrives, Android itself — not the app — briefly reads that one message and shows you a system prompt asking if you want to share it with the app. The app only receives the message text if you tap "Allow" on that prompt, message by message. The underlying Android permission this relies on does not even appear in the app's permission list.
2. **Even when the app is closed** — if you separately grant "Notification access" (a system settings screen, prompted from Settings → SMS Detection), the app reads notifications posted **only by your device's default SMS app** — no other app's notifications are read, ever. This is what lets detection keep working in the background; without granting this, detection only works while the app is open (method 1 above).

Either way, the message text is parsed entirely on your device to pull out a transaction amount and merchant name, pre-filling a new expense entry for you to review before saving. That text is not transmitted anywhere, not stored beyond what you choose to save as an expense, and not shared with us or anyone else. You can revoke notification access at any time from Android's Settings → Apps → Special app access → Notification access.

## Receipt scanning and voice entry (optional)

Receipt scanning uses ML Kit's on-device text recognition (camera permission) — the photo and extracted text are processed entirely on your device and never uploaded anywhere. Voice expense entry uses Android's on-device/system speech recognizer (microphone permission) the same way — your speech audio is handled by Android's speech recognition service the same as it would be for any other app using dictation, and the app itself never transmits it to us.

## What we don't do

- We don't operate any servers, and we never receive, see, or store your financial data ourselves.
- We don't use analytics or crash-reporting SDKs.
- We don't share or sell your financial data to third parties.
- We don't require or use an account, login, email, or phone number to use the app.
- We don't track you across other apps or websites.

## Children's privacy

Expense Tracker is a general-purpose finance utility, not directed at children, and we don't knowingly collect personal information from children. The app itself never receives your financial data regardless of age; the only data leaving the device is the ad-related data described above, handled by Google AdMob, which offers its own controls for treating users as children under COPPA/GDPR-K where applicable.

## Changes to this policy

If how the app handles data ever changes, this page will be updated and the "Effective date" above will change accordingly. Continued use of the app after an update means you accept the revised policy.

## Contact

Questions about this policy or the app's data handling can be sent to: **adilnitw0786@gmail.com**
