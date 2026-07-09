# Privacy Policy for Expense Tracker

**Effective date: July 9, 2026**

This policy explains what Expense Tracker ("the app") does and does not do with your information. We wrote it to match exactly how the app behaves — there is no hidden data collection beyond what's described here.

## The short version

Expense Tracker has no servers and no user accounts. Every expense, category, budget, debt, ledger (Khata) entry, and setting you enter is stored only in a local database on your own device, and we — the developer — never receive, see, or store any of it. The app shows ads (see below) and offers an *optional* Google Drive backup feature (see below); both are the only ways any information ever leaves your device, and both are described in full here.

## What the app stores, and where

All your data (expenses, income, categories, budgets, debts, ledger/Khata entries, split-group bills, goals, your display name, language and currency preferences) is saved in a local on-device database (Room/SQLite). It stays on your device unless you personally choose to move it — for example, by using the app's PDF/CSV export and sharing that file yourself through Android's share sheet, or by turning on Google Drive backup (below). Sharing via the share sheet is initiated by you, goes through apps you select, and is governed by those apps' own privacy policies — we have no visibility into it.

If you uninstall the app, its local database is deleted with it. **Settings → Reset All Data** also lets you erase everything inside the app at any time, instantly and permanently.

## Advertising (AdMob)

The app shows banner and occasional full-screen ads served by Google AdMob, which is how the app stays free with no account or subscription required. AdMob may collect device identifiers (such as the Android Advertising ID), general app-interaction data, and IP-derived approximate location, and may use this data for ad personalization and measurement, per [Google's AdMob privacy practices](https://support.google.com/admob/answer/6128543). The app does not request device location permission and does not pass any of your financial data (expenses, budgets, Khata entries, etc.) to AdMob or any advertiser — the ad SDK operates independently of, and has no access to, the data described in "What the app stores" above. You can opt out of personalized advertising for your device in your Google Account's Ad Settings.

## Optional cloud backup (Google Drive)

If you turn on backup in **Settings → Backup**, the app uploads a copy of your local database to a folder in *your own* Google Drive account, using Google's Drive API after you sign in with your own Google account. This is entirely opt-in — the feature does nothing unless you turn it on — and the backup file goes only to storage you control; the developer never receives a copy. You can delete a backup at any time from your Google Drive, or from **Settings → Backup** in the app, and you can revoke the app's Drive access at any time from your Google Account's third-party access settings. Google's handling of the backup file once it's in your Drive is governed by [Google's own privacy policy](https://policies.google.com/privacy).

## SMS-based expense detection (optional)

If you turn on automatic expense detection from SMS in Settings, the app uses Android's SMS User Consent API. This is a privacy-preserving system feature: when a new text message arrives, Android itself — not the app — briefly reads that one message and shows you a system prompt asking if you want to share it with the app. The app only receives the message text if you tap "Allow" on that prompt, message by message. The app never gets blanket access to your inbox, never reads old messages, and the underlying Android permission this relies on does not even appear in the app's permission list.

The text of any message you approve is parsed entirely on your device to pull out a transaction amount and merchant name, pre-filling a new expense entry for you to review before saving. That text is not transmitted anywhere, not stored beyond what you choose to save as an expense, and not shared with us or anyone else.

## Receipt scanning and voice entry (optional)

Receipt scanning uses ML Kit's on-device text recognition (camera permission) — the photo and extracted text are processed entirely on your device and never uploaded anywhere. Voice expense entry uses Android's on-device/system speech recognizer (microphone permission) the same way — your speech audio is handled by Android's speech recognition service the same as it would be for any other app using dictation, and the app itself never transmits it to us.

## What we don't do

- We don't operate any servers, and we never receive, see, or store your financial data ourselves.
- We don't use analytics or crash-reporting SDKs.
- We don't share or sell your financial data to third parties.
- We don't require or use an account, login, email, or phone number to use the app (a Google sign-in is only requested if *you* turn on Drive backup).
- We don't track you across other apps or websites.

## Children's privacy

Expense Tracker is a general-purpose finance utility, not directed at children, and we don't knowingly collect personal information from children. The app itself never receives your financial data regardless of age; the only data leaving the device is the ad-related data described above (handled by Google AdMob, which offers its own controls for treating users as children under COPPA/GDPR-K where applicable) and, only if a user opts in, the Drive backup they explicitly control.

## Changes to this policy

If how the app handles data ever changes, this page will be updated and the "Effective date" above will change accordingly. Continued use of the app after an update means you accept the revised policy.

## Contact

Questions about this policy or the app's data handling can be sent to: **adilnitw0786@gmail.com**
