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

## Translated listings — Arabic and Hindi

Add these under Play Console → **Grow → Store presence → Main store listing → Manage translations**
(pick the language, paste title/short/full description). A translated listing is what lets the app
surface in Play Store search when someone searches in that language instead of English — this is
the single biggest lever available for the "can't find my app" problem, since English-only listings
rank poorly for non-English queries no matter how good the English copy is.

Terminology below matches what's already used in-app (`values-ar/strings.xml`,
`values-hi/strings.xml`) — e.g. Arabic "الحساب" and Hindi "खाता" for Khata, so a user who reads the
store listing sees the same words once they open the app. ⚠️ This was drafted by an AI, not a native
copywriter — good enough to publish and start getting real search traffic, but worth a native-speaker
read-through when you get the chance, since idiom and persuasive tone matter more for conversion than
literal correctness.

### Arabic (العربية)

**Title (max 30 characters)**
```
متابع المصروفات والميزانية
```

**Short description (max 80 characters)**
```
تتبع المصاريف والميزانية والديون والحساب (الخاتا) وتقسيم الفواتير — بدون إنترنت
```

**Full description**
```
متابع المصروفات هو تطبيق مالي شامل — تتبع مصاريفك، قسّم الفواتير مع الأصدقاء، أدر حساب (خاتا) لمن يدين لك ومن تدين له، وسدّد ديونك — كل ذلك دون حساب، وبياناتك تبقى على هاتفك فقط.

ما يمكنك فعله
• سجّل مصاريفك في ثوانٍ — بالكتابة، أو بتصوير الفاتورة بالكاميرا، أو حتى بقول "صرفت 50 على الطعام"
• شاهد ملخصًا شهريًا واضحًا، واتجاهات الإنفاق، ونقاط الصحة المالية
• حدّد ميزانية شهرية وتابع ما تبقى منها لحظة بلحظة
• احصل على رؤى مبسّطة حول إنفاقك — مثل توقع تجاوزك للميزانية
• اكتشاف تلقائي للمصاريف من رسائل البنك (اختياري، وبموافقتك في كل مرة)
• احتفظ بحساب (خاتا) لمن يدين لك ومن تدين له، مع تذكيرات فورية عبر واتساب
• تتبع القروض والديون بوضوح
• قسّم فواتير المجموعات مع الأصدقاء أو العائلة وسوِّ الحساب عبر واتساب
• حدد أهداف ادخار وحافظ على سلسلة تسجيل يومية
• صدّر تقرير PDF أو CSV لأي شهر
• اعرض مصاريفك حسب اليوم أو الفئة أو كشبكة أو في تقويم
• أقفل التطبيق ببصمة إصبعك أو وجهك
• أضف ودجت للشاشة الرئيسية لعرض رصيدك بسرعة
• يدعم اللغة العربية بالكامل، بما في ذلك الاتجاه من اليمين لليسار والتقويم الهجري

مبني ليحترم خصوصيتك
لا خوادم ولا حسابات مستخدمين. كل مصروف وميزانية وقيد في الحساب يُخزَّن محليًا على جهازك، ولا نصل إليه نحن مطلقًا. التطبيق مدعوم بالإعلانات، وهذا ما يبقيه مجانيًا دون اشتراك — لكن بياناتك المالية لا تُشارَك أبدًا مع المعلنين أو أي جهة أخرى.

مصمم للجميع
نصوص كبيرة وواضحة، وتباين عالٍ، وتنقل بسيط، يجعل هذا التطبيق مناسبًا لأول مرة تضع فيها ميزانية، كما يناسب من يريد تقارير شهرية مفصلة — دون الحاجة لخلفية مالية.

يتم تحديد عملتك ولغتك تلقائيًا من هاتفك، ويمكنك تغييرهما في أي وقت من الإعدادات.
```

### Hindi (हिंदी)

**Title (max 30 characters)**
```
खर्च और बजट ट्रैकर
```

**Short description (max 80 characters)**
```
खर्च, बजट, कर्ज, खाता और बिल विभाजन — पूरी तरह ऑफलाइन, बिना अकाउंट के
```

**Full description**
```
खर्च और बजट ट्रैकर एक ऑल-इन-वन मनी ऐप है — खर्च ट्रैक करें, दोस्तों के साथ बिल बांटें, खाता (उधार-जमा) मैनेज करें, और कर्ज चुकाएं — बिना किसी अकाउंट के, और आपका हर आंकड़ा सिर्फ आपके फोन में रहता है।

आप क्या कर सकते हैं
• सेकंडों में खर्च लॉग करें — टाइप करके, कैमरे से बिल स्कैन करके, या बस बोलकर "खाने पर 50 खर्च किए"
• महीने का साफ ब्रेकडाउन, खर्च का ट्रेंड, और फाइनेंशियल हेल्थ स्कोर देखें
• मासिक बजट सेट करें और रीयल-टाइम में बचा हुआ पैसा देखें
• आसान भाषा में खर्च से जुड़े इनसाइट्स पाएं — जैसे बजट से आगे निकलने का अनुमान
• बैंक/पेमेंट SMS से खर्च अपने आप पहचानें (वैकल्पिक, हर बार आपकी मंज़ूरी से)
• खाता रखें — कौन आपका पैसा देता है और आप किसका देते हैं, व्हाट्सएप रिमाइंडर के साथ
• कर्ज और लोन साफ-साफ ट्रैक करें
• दोस्तों या परिवार के साथ ग्रुप का बिल बांटें और व्हाट्सएप पर हिसाब चुकाएं
• बचत के लक्ष्य बनाएं और रोज़ाना लॉगिंग स्ट्रीक बनाए रखें
• किसी भी महीने की PDF या CSV रिपोर्ट एक्सपोर्ट करें
• अपने खर्च को दिन, श्रेणी, ग्रिड या कैलेंडर के हिसाब से देखें
• फिंगरप्रिंट या फेस से ऐप लॉक करें
• होम स्क्रीन विजेट से एक नज़र में बैलेंस देखें
• हिंदी सहित कई भारतीय भाषाओं में पूरी तरह उपलब्ध

आपकी प्राइवेसी का पूरा ध्यान
खर्च ट्रैकर का कोई सर्वर या यूज़र अकाउंट नहीं है। हर खर्च, बजट और खाता एंट्री सिर्फ आपके डिवाइस पर लोकल स्टोर होती है, और यह हम तक कभी नहीं पहुंचती। ऐप विज्ञापनों से चलता है, इसी वजह से यह बिना सब्सक्रिप्शन के मुफ्त है — लेकिन आपका फाइनेंशियल डेटा कभी किसी विज्ञापनदाता या किसी और के साथ शेयर नहीं होता।

सबके लिए बना
बड़ा और साफ टेक्स्ट, हाई-कॉन्ट्रास्ट स्क्रीन, और आसान नेविगेशन — यह ऐप पहली बार बजट बनाने वाले के लिए भी उतना ही आसान है जितना डिटेल्ड मंथली रिपोर्ट चाहने वाले के लिए। किसी फाइनेंस बैकग्राउंड की ज़रूरत नहीं।

आपकी करेंसी और भाषा आपके फोन से अपने आप पहचानी जाती है, और आप इसे कभी भी सेटिंग्स में बदल सकते हैं।
```
