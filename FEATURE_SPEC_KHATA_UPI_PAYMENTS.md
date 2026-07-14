# Feature Spec: Khata Payment Links + QR (UPI)

**Status:** Proposed, not yet built.
**Owner:** TBD.
**Depends on:** existing Khata module (`KhataScreen.kt`, `KhataDetailScreen.kt`, `KhataPartyEntity`), existing WhatsApp reminder flow.

## 1. Problem / opportunity

Khata currently tracks who owes what and can nudge people with a WhatsApp text reminder, but collecting the actual money is still manual — the customer has to separately open their own UPI app, find the shop owner's ID, and pay. That's friction on the one part of the app that's genuinely hard for generic expense-tracker clones to copy (Splitwise, Walnut, etc. don't have a shop-credit ledger at all). Closing that last gap — reminder to money-in-hand, in one tap — turns Khata from a bookkeeping tool into a small collections tool, which is a much stickier value proposition for the shopkeeper/vendor persona this feature already targets.

**India-only in v1.** UPI (`upi://pay` deep links, VPA addresses) only works with Indian bank accounts. This must be gated behind currency = INR (₹) so it doesn't show up for the global audience the rest of the app targets — same pattern already used for `CurrencyLocaleMapper.isSaudiRiyalSymbol()`.

## 2. What this is NOT

- **Not a payment processor integration.** The app never touches money, never calls a payment gateway API, never sees transaction confirmation. It only constructs a standard `upi://pay?...` URI and hands it to whatever UPI app the user has installed (GPay, PhonePe, Paytm, etc.) — identical in kind to how a printed shop QR code works today, or how the app already hands off to WhatsApp via `Intent.ACTION_VIEW`. This keeps it out of "financial transaction" territory entirely — no PCI scope, no RBI payment-aggregator licensing questions, no new Play Store financial-services review category.
- **Not mandatory.** Entirely optional — hidden unless currency is INR, and unless the user has entered a UPI ID.
- **Not bidirectional in v1.** Scoped to the common case: *collecting* money someone owes you. Paying *your own* dues to a party via their UPI ID is a natural v2 addition (see §7) but adds a second data field and a second flow for a less common case — cut from v1 to keep this shippable in one pass.

## 3. UX flow

### 3a. One-time setup (Settings)

New "Payment (UPI)" section in Settings, **visible only when detected/selected currency is ₹ (INR)**:
- Text field: "Your UPI ID" (e.g. `mohammad@okhdfcbank`), with a lightweight format check (must contain exactly one `@`, no spaces).
- Saved locally only — never transmitted anywhere, same as every other setting in this app.

### 3b. Requesting payment (Khata list + detail screens)

When **they owe the user** money (`direction == THEY_OWE`, outstanding balance ≥ ₹1) and the user has set their UPI ID:
- On the **Khata list screen** (`KhataScreen.kt`), each party row gets a compact "Request via UPI" affordance (icon button, to fit the existing row layout) alongside whatever action is already there.
- On the **detail screen** (`KhataDetailScreen.kt`), a full "Request via UPI" button appears next to the existing WhatsApp reminder button.
- Both open the same bottom sheet.
- Tapping it opens a bottom sheet showing:
  - A QR code (generated on-device, no network call) encoding the UPI deep link for the exact outstanding balance.
  - "Share payment link" — appends the same UPI deep link to the *existing* WhatsApp reminder message (reuses `reminderMsgOwe`/`reminderMsgCredit` construction in `KhataDetailScreen.kt` verbatim, just adds one more line).
  - Caption: "They can scan this in person, or tap the link you share to pay you directly."

When the customer opens the link (via WhatsApp on their own phone, or by scanning the QR on the shopkeeper's screen), their own UPI app opens pre-filled with the shopkeeper's VPA, the exact amount, and a note — they just confirm.

### 3c. Party-level UPI ID (optional, small addition)

`AddEditKhataPartySheet.kt` gets one more optional field, "Their UPI ID," shown only when currency is ₹ — not used by anything in v1, but capturing it now avoids a second data-entry pass if/when v2 (paying them) ships.

## 4. Data model changes

Current DB is at `version = 14` (see `AppDatabase.kt`), migrations run up to `MIGRATION_13_14`. This adds `MIGRATION_14_15`.

```kotlin
// KhataPartyEntity.kt — add one nullable column
@Entity(tableName = "khata_parties")
data class KhataPartyEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val phone: String = "",
    val direction: String,
    val upiId: String? = null,   // NEW — the party's own VPA, optional, INR-only feature
    val createdAt: Long = System.currentTimeMillis()
) { /* ... existing companion object unchanged ... */ }
```

```kotlin
// AppDatabase.kt
private val MIGRATION_14_15 = object : Migration(14, 15) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE khata_parties ADD COLUMN upiId TEXT DEFAULT NULL")
    }
}
// bump version = 15, add MIGRATION_14_15 to .addMigrations(...)
```

```kotlin
// SettingsRepository.kt — one new pref, same pattern as displayName/monthlySalary
var myUpiId: String
    get() = prefs.getString(KEY_MY_UPI_ID, "") ?: ""
    set(value) = prefs.edit().putString(KEY_MY_UPI_ID, value).apply()
// + private const val KEY_MY_UPI_ID = "my_upi_id"
```

## 5. New code

- **`util/UpiPaymentHelper.kt`** (new file) — pure functions, no Android framework dependency beyond `Uri`:
  - `buildUpiUri(payeeVpa: String, payeeName: String, amount: Double, note: String): Uri` → constructs `upi://pay?pa=...&pn=...&am=...&cu=INR&tn=...` with proper URL-encoding.
  - `generateQrBitmap(content: String, sizePx: Int): Bitmap` — wraps ZXing's `QRCodeWriter`, pure on-device generation.
  - `isValidVpa(input: String): Boolean` — basic format guard for the Settings text field.
- **New dependency:** `implementation("com.google.zxing:core:3.5.3")` — no Play Services, no network, no extra permissions. (`journeyapps:zxing-android-embedded` is an alternative if a ready-made scanning UI is ever wanted, but pure QR *generation* only needs `zxing:core`.)
- **New composable:** `RequestUpiPaymentSheet.kt` (bottom sheet) — QR image + "Share payment link" button + caption. No app-detection logic (see §10.3). Lives alongside the existing `SettleUpSheet.kt`/`AddKhataEntrySheet.kt` pattern in `ui/components`.
- **`KhataDetailScreen.kt`** — add the "Request via UPI" button and wire the new sheet in; append the UPI link to `reminderMsg` only when this flow is used (the existing plain WhatsApp reminder stays unchanged for parties without an INR/UPI setup).
- **`KhataScreen.kt`** — add the same "Request via UPI" affordance to each qualifying party row in the list, wired to the same bottom sheet.
- **`SettingsScreen.kt`** — new "Payment (UPI)" section, gated on `CurrencyLocaleMapper.isInrSymbol(currencySymbol)` (new one-line helper, same shape as the existing `isSaudiRiyalSymbol`).
- **`AddEditKhataPartySheet.kt`** — one optional field, same gating.

## 6. Strings / i18n

New keys (base + all 13 locales, following the pattern already established this session):
`upi_section_title`, `upi_id_label`, `upi_id_hint`, `upi_id_invalid`, `khata_request_via_upi`, `khata_upi_sheet_caption`, `khata_upi_share_link`, `khata_party_upi_hint`. Roughly 8 keys × 14 locales — smaller than the i18n pass already done in this session.

## 7. Explicitly out of scope for v1 (park for later)

- ~~Paying a party's own UPI ID (the "I owe them" direction)~~ — **shipped** as "Pay via UPI" (see §11).
- Reading payment confirmation/status back into the app (UPI has no standard callback for this without a payment gateway partner — out of reach for a client-only app, and deliberately so, given the no-transaction-processing stance in §2). **"Mark as Paid" (§11) is the deliberate manual substitute** — the app still never knows a payment actually succeeded, the user tells it.
- Any non-UPI rail (cards, wallets) — no reason to scope that until UPI is validated with real users.

## 8. Compliance notes

- No new manifest permissions — reuses the same `Intent.ACTION_VIEW` mechanism already used for the WhatsApp share.
- `myUpiId` and party `upiId` are stored locally only, same as phone numbers already stored for WhatsApp reminders — add one line to `PRIVACY_POLICY.md` / `DATA_SAFETY.md` when this ships (no new "collection" under Play's definition, since nothing leaves the device).
- Confirm this stays framed as "generate a link/QR for the user's own UPI app to handle" everywhere in copy — never "pay," "send," or "transfer" in the app's own UI, to avoid any appearance of the app itself moving money.

## 9. Effort estimate

Comparable in size to the reminder/SMS work already shipped this session (each of those was a single focused session). Rough breakdown:

| Task | Estimate |
|---|---|
| Room migration + entity change | 0.5 hr |
| `UpiPaymentHelper` (URI builder + QR gen) | 1 hr |
| Settings UI (UPI ID field, gating) | 0.75 hr |
| `AddEditKhataPartySheet` field | 0.5 hr |
| `RequestUpiPaymentSheet` + KhataDetailScreen wiring | 1.5–2 hr |
| KhataScreen list-row wiring | 0.5 hr |
| Strings × 14 locales | 0.75 hr |
| Manual test pass (GPay/PhonePe deep link, QR scan, no-UPI-app fallback) | 1 hr |
| Docs update (Privacy/Data Safety) | 0.25 hr |
| **Total** | **~6–7 hours / one focused session** |

## 10. Decisions (resolved)

1. **Yes** — "Request via UPI" appears on the Khata list screen (per-party row) as well as the detail screen, not just the detail screen.
2. **Minimum threshold: ₹1.** The UPI option is hidden when the outstanding balance is below ₹1.
3. **No copy-link fallback.** Simpler v1: no UPI-app-detection/fallback logic at all. The QR is generated and shown locally regardless (it's just an on-device image, needs no UPI app to display), and "Share payment link" always goes through WhatsApp exactly like the existing reminder flow. Whether the *recipient* has a UPI app is their own concern, same as it already is for the plain-text reminder today.

## 11. Shipped after v1: closing the loop

Built as a direct follow-up once real usage surfaced two gaps (see conversation history 2026-07-14):

- **The raw `upi://pay` link is dead text in WhatsApp.** WhatsApp only auto-linkifies `http(s)` URLs, so the shared link showed up as inert, ugly percent-encoded text with no functional benefit. Fixed by sharing the actual QR *image* (saved to cache, sent via `FileProvider` + `ACTION_SEND image/png`) instead of/alongside the link; the raw link was later dropped from the caption entirely since the image is the only part that actually works.
- **"Their UPI ID" field made no sense for THEY_OWE parties.** Request via UPI always pays the *app user's own* UPI ID — a party's UPI ID is never read by that flow. The editable field is now shown only for I_OWE parties; THEY_OWE parties instead show a read-only status line reflecting the user's own UPI ID readiness.
- **Pay via UPI** (I_OWE parties, party's own UPI ID set, INR, balance ≥ ₹1): opens a direct `ACTION_VIEW` on a `upi://pay` link built from the party's UPI ID. Unlike Request via UPI this is app-initiated, not shared as text, so the `upi://` scheme resolves straight to an installed UPI app with no WhatsApp-linkify problem. This is what makes the party-level `upiId` field (captured back in §4) actually useful.
- **Mark as Paid**: a general one-tap quick-settle action (confirm dialog, logs one PAYMENT entry for the full balance) available for *any* party regardless of direction or currency — not UPI-specific, since a debt can be settled in cash, bank transfer, etc. This is the deliberate manual stand-in for payment-confirmation tracking (§7) — the app still has no way to know a UPI payment actually went through; the user tells it.
- Deliberately **no auto-prompt** ("did you pay?") when returning to the app after opening a UPI app — consistent with the "no app-detection, keep it simple" stance in §10.3.
