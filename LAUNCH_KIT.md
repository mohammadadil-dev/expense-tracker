# Baqaya — Launch Kit (install-growth assets)

_Everything to ship this release for maximum installs. Pair with `STORE_LISTING_ASO.md` (title + descriptions + keywords) and `PRODUCT_STRATEGY.md` (positioning)._

Install rate = **impressions × listing conversion**. This kit targets both: release notes + screenshots + reels drive conversion and organic reach; reviews drive ranking. None of it costs money.

---

## 1) Release notes ("What's new") — paste into Play Console per language

Keep under Play's 500-character limit. Benefit-led, leads with the new stuff, ends on the privacy hook.

### English
```
New in Baqaya:
• Savings Circle (committee) — track who paid each round, whose turn to collect, and nudge them on WhatsApp
• Zakat calculator — work out what you owe, fully offline
• Auto-track expenses from bank SMS (India & Saudi banks)
• Cleaner dashboard, Riyal support, and more languages

Still 100% offline — your data never leaves your phone.
```

### Hindi (हिंदी)
```
बकाया में नया:
• कमेटी — हर दौर में किसने पैसे डाले, किसकी बारी, और WhatsApp पर याद दिलाएँ
• ज़कात कैलकुलेटर — पूरी तरह ऑफलाइन
• बैंक SMS से खर्च अपने आप ट्रैक (भारत + सऊदी बैंक)
• साफ़ डैशबोर्ड और ज़्यादा भाषाएँ

100% ऑफलाइन — आपका डेटा फ़ोन में ही रहता है।
```

### Arabic (العربية)
```
جديد في باقي:
• الجمعية — تابع من دفع في كل دورة ودور مَن للاستلام، وذكّرهم عبر واتساب
• حاسبة الزكاة — تعمل بدون إنترنت
• رصد المصاريف تلقائيًا من رسائل البنوك (السعودية والهندية)
• لوحة أوضح، دعم الريال، ولغات أكثر

يعمل بالكامل دون إنترنت — بياناتك لا تغادر هاتفك.
```

_Other languages: mirror the English structure. Not blocking — Play falls back to English per-locale, but a translated note lifts conversion for that language._

---

## 2) Review-prompt plan (ratings = ranking + trust)

**Already in place (good):** the native in-app review dialog fires **once per install, after the 5th expense** (`ExpenseViewModel.saveExpense` → `InAppReviewManager`). That's a solid trigger — enough usage to have an opinion, early enough to catch first-week enthusiasm. Just make sure it ships in this build.

**To maximise it:**
- Leave the 5-expense threshold; don't ask earlier (no opinion yet) or later (lost momentum).
- Keep the manual **"Rate us"** button in Settings as the repeat path (Google shows the native dialog only once per user, quota-limited).
- **Seed the first ~20–30 reviews by hand:** personally message your current ~50 users (WhatsApp/family/community) and ask them to rate — this is the single fastest ranking + conversion win at your scale. The store algorithm and new visitors both weight early ratings heavily.
- Don't fake reviews — Play detects and penalises. Real asks to real users only.

**In-context ask copy** (if you ever add a soft pre-prompt before the native dialog):
- EN: "Enjoying Baqaya? A quick rating helps other people find it 🙏"
- HI: "बकाया पसंद आया? एक छोटी रेटिंग से और लोग इसे ढूँढ पाएँगे 🙏"
- AR: "هل يعجبك باقي؟ تقييم سريع يساعد الآخرين على اكتشافه 🙏"

---

## 3) Screenshot script (first 3 decide the install)

Recapture from THIS build (new icon + dashboard). Add large in-image caption text — don't rely on the raw screen. Order:

1. **Hero — auto SMS.** Show the SMS-consent → expense prefill. Caption: *"Your bank SMS → an expense, automatically."*
2. **Privacy.** The no-account/on-device state. Caption: *"100% offline. Your data never leaves your phone."*
3. **Monthly breakdown.** Dashboard donut/trend. Caption: *"See exactly where your money goes."*
4. **Khata / Ledger.** Caption: *"Never forget who owes you."*
5. **Savings Circle (committee).** Rounds + turn order. Caption: *"Run your committee — who's paid, whose turn, reminders."*
6. **Zakat / languages.** Caption: *"Zakat calculator + 14 languages, all offline."*

**Localised captions**
- Hindi: 1) *"बैंक SMS → खर्च, अपने आप"* · 2) *"100% ऑफलाइन — डेटा आपके फ़ोन में"* · 3) *"पैसा कहाँ गया, साफ़ दिखे"* · 4) *"कौन आपका उधार रखता है, याद रहे"* · 5) *"अपनी कमेटी चलाएँ — किसने दिया, किसकी बारी"*
- Arabic: 1) *"رسالة البنك ← مصروف تلقائيًا"* · 2) *"يعمل دون إنترنت — بياناتك في هاتفك"* · 3) *"شاهد أين يذهب مالك"* · 4) *"حاسبة الزكاة، بدون إنترنت"* · 5) *"أدر جمعيتك — من دفع ودور من"*

---

## 4) Short-video / reel scripts (cheapest organic reach)

15–25s each, shot on-screen (screen recording + captions). Post to Instagram Reels, YouTube Shorts, TikTok. One clear hero per video.

### Reel A — Auto SMS + privacy (India, Hindi/English)
- **Hook (0–3s), on-screen:** "Stop typing every expense." (voice: "Har kharcha type karna band karo.")
- **Beat 1 (3–10s):** Show a bank SMS arriving → tap Allow → expense auto-fills. Caption: "Bank SMS → expense, automatically."
- **Beat 2 (10–17s):** Show the dashboard breakdown. Caption: "See where your money actually goes."
- **Beat 3 (17–22s):** Show 'no account, offline'. Caption: "100% private — data never leaves your phone."
- **CTA:** "Free on Play Store — search *Baqaya*." (pin link in bio/comment)

### Reel B — Committee / kitty (India, Hindi)
- **Hook:** "कमेटी का हिसाब कॉपी पर? अब नहीं।" ("Tracking your committee on paper? Not anymore.")
- **Beat 1:** Create a circle, add members, set turn order.
- **Beat 2:** Mark who's paid this round; show whose turn to collect.
- **Beat 3:** Tap Share → WhatsApp summary goes to the group.
- **CTA:** "Free, offline — Baqaya on Play Store."

### Reel C — Zakat (KSA + India Muslim audience, English/Arabic)
- **Hook:** "Calculate your Zakat in 30 seconds — offline." / "احسب زكاتك في 30 ثانية — بدون إنترنت."
- **Beat 1:** Enter gold/silver grams + price → value auto-calculates.
- **Beat 2:** Add cash/savings → Zakat due (2.5%) appears with the Nisab check.
- **Beat 3:** "No account, nothing leaves your phone."
- **CTA:** "Baqaya — free on Play Store."

**Distribution tips:** post in the app's languages (Hindi/Urdu reels for India, Arabic for KSA); drop the same clips in relevant communities (personal-finance subreddits, expat/OFW groups, Muslim-finance pages for Reel C); and cross-post a link from your **Saudi Salary Calculator** app's listing/updates — that's an owned audience you already have.

---

## Release checklist
- [ ] Build clean on API 36 (AGP 8.9.1 / Gradle 8.11.1); run `testDebugUnitTest`.
- [ ] Bump versionCode / versionName.
- [ ] Recapture icon + screenshots from this build; upload with captions above.
- [ ] Paste new title + descriptions (`STORE_LISTING_ASO.md`) + release notes (above); publish Hindi + Arabic listings.
- [ ] **Staged rollout** (e.g. 20% → watch crash-free rate → 100%).
- [ ] Day 1: personally ask your existing users for honest ratings.
- [ ] Post Reel A (+ B/C) and pin the Play link.
