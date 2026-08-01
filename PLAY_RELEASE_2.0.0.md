# Baqaya 2.0.0 — Play Store release kit

App: `com.agtech.expensetracker` · versionCode **21** · versionName **2.0.0**
Live on Play today: versionCode 17 (1.7.0) — so this update ships everything from 1.8/1.9/2.0 at once.
targetSdk/compileSdk **36** (satisfies Google's Aug 2026 API-36 requirement).

---

## 1. Release notes ("What's new")

> **Play limit: 500 characters per language.** The long version below is over that — use **Option A**
> as the actual release note and keep the long list for your own changelog / a blog post.

### Option A — recommended (English, 468 chars)

```
Baqaya 2.0 is our biggest update yet.

• Shared Bills — split apartment utility bills by how many days each person actually stayed
• Committee (savings circle) — track rounds, payouts and reminders
• Zakat calculator — enter gold/silver in grams, get your value automatically
• Saudi payments — save your IBAN or STC Pay number and share it in reminders
• Cleaner WhatsApp reminders
• Subscriptions revamp with monthly/3/6/12-month cycles
• Faster, tidier screens
```

### Option A — Arabic (for the ar-SA listing)

```
بقايا 2.0 — أكبر تحديث حتى الآن.

• الفواتير المشتركة — قسّم فواتير الشقة حسب أيام إقامة كل شخص
• الجمعية — تابع الأدوار والدفعات والتذكيرات
• حاسبة الزكاة — أدخل الذهب والفضة بالجرام واحصل على القيمة تلقائيًا
• مدفوعات السعودية — احفظ الآيبان أو رقم STC Pay وأرسله مع التذكير
• تذكيرات واتساب أوضح
• الاشتراكات: دورات شهرية و3 و6 و12 شهرًا
• شاشات أسرع وأنظف
```

### Full changelog (for your records — not for the Play field)

**New features**
- **Shared Bills** (Splits tab → "Shared bills"): create a group (apartment/floor/office), add flats or
  people, then add bills (Electricity, Water, Gas, Internet, Rent, Maintenance, Other). Cost is
  prorated by *people × days present*, absences entered as date ranges. Per-bill include/exclude for a
  vacant flat, paid-tracking, WhatsApp breakdown, and "log my share" into Utilities.
- **Committee / Jam'iya** (rotating savings circle): members, rounds, payout order, contribution
  tracking, completion state, edit-circle, WhatsApp sharing (group summary + per-member).
- **Zakat calculator**: gold/silver by grams × current price → auto value; nisab guidance; "what is
  Zakat" explainer.
- **Saudi payment methods**: save your **IBAN** (with automatic bank-name detection from the IBAN) and
  your **STC Pay** number (frozen 🇸🇦 +966 prefix). Either or both are attached to payment reminders.
- **Charity category** added, localized in all 14 languages.

**Improvements**
- WhatsApp reminders rewritten: amount on its own line, bold key values, labelled payment blocks,
  IBAN grouped in 4s for readability, and an install pitch with a tappable Play Store link.
- Subscriptions: service presets (Netflix, YouTube, Spotify, ChatGPT, Claude, Shahid, JioHotstar,
  Mobile recharge…) and real billing cycles (monthly / 3 / 6 / 12 months) honoured by the recurring
  engine; monthly total normalized across mixed cycles.
- Splits: add members to an existing group; duplicate member names rejected (case-insensitive).
- Ledger: whole screen scrolls as one list so the party list is never squeezed; compact + button.
- Riyal symbol rendered as the official glyph across the app.
- Business Profile is collapsible and only shown for Indian Rupee users.
- Bottom nav labels stay on one line.

**Fixes**
- **Currency conversion now rescales every stored amount** — budgets, salary/income, savings goals,
  debts and payments, ledger entries and credit limits, splits/shares/items, and committee
  contributions. Previously only expenses were converted, leaving other figures in the old currency.
- Various layout and validation fixes.

---

## 2. Pre-launch checklist (do these before rolling out)

- [ ] **Build + upgrade test.** This release moves the database from v17-era schema through to **v27**
      (several migrations). Install the *previous* Play build, then install this one over it and confirm
      old data survives. This is the single highest-risk item in the release.
- [ ] **AdMob IDs are production, not test.** Confirm the banner/interstitial unit IDs and the manifest
      `APPLICATION_ID` are your live AdMob values.
- [ ] Signed **AAB** built with your upload keystore (`./gradlew bundleRelease`).
- [ ] Test on a small screen (e.g. 5") — several screens were re-laid-out this release.
- [ ] Test in Arabic (RTL) and Hindi at least once.
- [ ] Staged rollout: **20% → 50% → 100%**, watching crash rate in Play Console → Quality → Vitals.

---

## 3. Play Console updates required

### a) Data safety form — **must be reviewed this release**
You show AdMob ads, so you are **not** "no data collected". Declare:

| Data type | Collected | Shared | Purpose | Notes |
|---|---|---|---|---|
| Device or other IDs (advertising ID) | Yes | Yes | Advertising / marketing | Via Google AdMob |
| App info & performance (crash logs) | Only if you use Firebase/Crashlytics | — | Analytics | Skip if not integrated |

Everything the user *types* — expenses, ledger, IBAN, STC number, salary — stays on device and is
**not** collected. Say that plainly in the listing; it's a genuine selling point.
Also confirm: data is **not** transferred off device, encryption in transit **not applicable** for user
content (nothing is sent), and there is **no** account deletion URL requirement (no accounts).

### b) Permissions to justify
- `RECORD_AUDIO` — voice entry for ledger/expenses.
- `CAMERA` — receipt photo capture.
- `POST_NOTIFICATIONS` — daily reminder + budget alerts.
- No `READ_SMS`: bank-SMS capture uses the **SMS User Consent API**, so no sensitive-permission
  declaration form is needed. (Keep it that way — READ_SMS would trigger a Play policy review.)

### c) Store listing — target countries
Set **Saudi Arabia** and **India** as priority countries, and add **localized listings** (see §4).

### d) Content rating, ads declaration
- Re-confirm "Contains ads" = **Yes**.
- Content rating questionnaire: unchanged (finance utility, no user-generated content sharing).

---

## 4. Getting found in search (this is what fixes 50 installs)

Your install problem is almost certainly **discoverability**, not the app. Three levers, in order of
impact:

### Lever 1 — Keywords in the right fields
Play indexes your **app title (30 chars)**, **short description (80 chars)**, and **full description
(4000 chars)**. Keyword-stuff nothing, but make sure the words people actually type appear naturally.

**Title options (30 char max):**
```
Baqaya: Expense Tracker
Baqaya — Expense & Khata
Baqaya: Expenses & Ledger
```
Recommended: `Baqaya: Expense Tracker` — "expense tracker" is the highest-volume search term in your
category and belongs in the title.

**Short description (80 char max):**
```
Track expenses, split bills & keep khata — offline, private, no account needed.
```

**Full description — must naturally include these phrases** (each is a real search people make):
expense tracker, expense manager, spending tracker, budget app, daily expense, khata book, udhar
bahi khata, credit ledger, split bills, bill splitter, shared expenses, savings circle / committee /
kitty, zakat calculator, IBAN, STC Pay, offline expense tracker, no internet, privacy.

### Lever 2 — Localized store listings (biggest under-used win for you)
The app supports 14 languages but if your Play listing is English-only, an Arabic or Hindi speaker
searching in their own language will **never find you**. Add localized listings for at least:
- **Arabic (ar)** — for Saudi Arabia. Keywords: مصروفات، حاسبة الزكاة، تقسيم الفاتورة، جمعية، دفتر الحساب، آيبان
- **Hindi (hi)** — for India. Keywords: खर्च, हिसाब किताब, खाता बही, उधार, बजट, कमेटी
- Urdu, Bengali, Tamil, Telugu if you have the appetite — you already have the in-app translations.

Each localized listing = its own title, short description, full description, and ideally screenshots.
This alone typically moves impressions substantially, because you start ranking in searches you were
previously invisible for.

### Lever 3 — Screenshots that sell in the first two
Most users decide on screenshots 1–2 without reading anything. Use captioned screenshots (a headline
baked into the image), in this order:
1. "Track every riyal / rupee — works offline" (dashboard)
2. "Split bills fairly by days stayed" (Shared Bills result) ← your unique feature, nobody else has this
3. "Keep khata / udhar with WhatsApp reminders"
4. "Committee & savings circles"
5. "Zakat calculator in seconds"
6. "14 languages · no account · private"

Also add: a **feature graphic** (1024×500), and if you can, a 30-second **promo video**.

### Other growth levers worth the effort
- **Ask for ratings.** You already prompt after the 5th expense. Rating count is a ranking factor —
  going from a handful of ratings to 50+ visibly moves conversion. Consider also prompting after a
  user successfully shares a reminder (a moment of delight).
- **Your WhatsApp reminders are your best growth channel.** Every reminder now carries a Play link,
  and it lands with someone who *already owes money* — high intent. This is organic, compounding
  distribution; make sure the pitch line stays in.
- **Lead with the differentiators.** "Shared bills split by days stayed", "Zakat calculator",
  "IBAN & STC Pay", "works fully offline", "no signup" — none of the big expense trackers do this
  combination for KSA/India. Say it in the first line of the description, not the fifth.
- Post in relevant communities (Saudi expat groups, r/india personal-finance style forums) — organic,
  free, and your Shared Bills feature is genuinely novel enough to be worth a post.

---

## 5. Suggested rollout order

1. Fix any build errors, run the **upgrade test** (§2).
2. Build the signed AAB, upload to **internal testing**, install over an old build, sanity-check.
3. Update **Data safety** + **store listing** + **screenshots** (§3, §4) *before* going live — the new
   listing is what converts the traffic the update brings.
4. Promote to production at **20%**, monitor Vitals for 48h, then 50%, then 100%.
5. Add the **Arabic** and **Hindi** localized listings within the first week.
