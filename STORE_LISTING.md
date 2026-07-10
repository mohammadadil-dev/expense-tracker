# Play Store Listing Copy

Paste these directly into Play Console → your app → **Grow → Store presence → Main store listing**. Character counts are noted so you can confirm they fit Play Console's limits before saving.

## App name (max 30 characters)

```
Expense Tracker
```
(15 characters)

## Short description (max 80 characters)

```
Expenses, budgets, debts, splits & ledger — private, on-device, no account.
```
(76 characters)

## Full description (max 4000 characters)

```
Expense Tracker is an all-in-one money app — track spending, split bills with friends, manage a lending/borrowing ledger, and pay down debts — all without an account, and with every number staying on your phone.

WHAT YOU CAN DO
• Log expenses in seconds — by typing, scanning a receipt with your camera, or just saying "spent 50 on food"
• See a clear monthly breakdown, spending trends, and a financial health score at a glance
• Set a monthly budget and track how much you have left in real time
• Get plain-language spending insights — like when you're projected to go over budget, or which category jumped this month
• Auto-detect expenses from bank/payment SMS messages (optional, and only with your one-tap approval each time — the app never gets blanket access to your messages)
• Keep a Khata (ledger) of who owes you money and what you owe others, with one-tap WhatsApp payment reminders
• Track loans and debts with a clear payoff view
• Split group expenses with friends or family and settle up with one WhatsApp message
• Set savings goals and build a daily logging streak
• Export a polished PDF, CSV, or JSON report/backup of any month
• View your expenses by day, by category, as a grid, or on a calendar
• Lock the app with your fingerprint or face
• Add a home-screen widget for an at-a-glance balance
• Switch between English, Arabic, and Hindi, with full right-to-left support and an Arabic Hijri calendar

BUILT TO RESPECT YOUR PRIVACY
Expense Tracker has no servers and no user accounts. Every expense, budget, ledger entry, and setting is stored locally in a database on your device, and we — the developer — never receive, see, or store any of it. The app is supported by ads, which is how it stays free with no subscription — but your financial data is never shared with advertisers or anyone else.

DESIGNED FOR EVERYONE
Large, legible text, high-contrast screens, and simple navigation mean this app works just as well for a first-time budgeter as it does for someone tracking shared expenses across a family or a friend group — no finance background required.

Your currency and language are detected automatically from your phone, and can be changed any time in Settings.
```
(2,231 characters — under the 4,000 limit)

## Release notes — v1.0 (historical, max 500 characters)

This is what shipped in the first release. Kept here for reference — paste the **current version's**
notes (below) into Play Console for new submissions, not this one.

```
Introducing Expense Tracker — a simple, private way to track your spending.

• Log expenses and see monthly breakdowns at a glance
• Set a monthly budget and track what's left
• Get plain-language spending insights and a financial health score
• Auto-detect expenses from SMS (optional, one-tap approval each time)
• Export polished PDF reports
• Available in English, Arabic, and Hindi

No account, no cloud — everything stays on your device.
```
(437 characters)

## Release notes — v1.7.0 (max 500 characters)

Paste this into Play Console → your release (Internal testing or Production) → **Release notes**.
Update this section again for future versions rather than reusing v1.0's — the app has grown a lot
since the first release (Khata ledger, debts, splits, receipt scan, voice entry, goals and streaks,
biometric lock, widget all shipped after v1.0). Family Mode and Google Drive backup exist in the
codebase but their UI entry points are currently commented out / not shipped — don't advertise them
anywhere in the store listing until they're actually re-enabled for users.

```
Expense Tracker now does a lot more than expense tracking:

• Khata ledger for money you're owed or owe, with WhatsApp reminders
• Debt payoff tracking
• Split bills with friends — settle up on WhatsApp
• Receipt scanning and voice expense entry
• Savings goals and daily streaks
• Fingerprint/face app lock
• Home-screen widget

Still no account required, and your data still never leaves your device unless you choose to export it.
```
(423 characters)

## Notes

- The short description above already accounts for `&` and `—` rendering as single characters; re-check the live character counter in Play Console after pasting, since some fonts/locales can count slightly differently.
- Screenshots and a feature graphic still need to be captured from a real build (see the main README's "Publishing to the Play Store" section) — that requires running the app on a device or emulator, which isn't something that can be produced from here.
- **This file lags the actual live Play Store listing.** The real listing (title "Expense & Budget Tracker", developer "AG Tech") already has a richer description covering more languages than shown here, and — as of this writing — it still mentions "Google Drive cloud backup" as a feature. Since that feature's UI is currently disabled, that line should be removed from the *live* Play Console listing directly (not just this reference file) to avoid users looking for a feature they can't find.
