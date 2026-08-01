# Baqaya — Product Strategy & Roadmap (India + Saudi Arabia)

_Prepared July 2026. Owner: Adil. Horizon: next two quarters._

## 1. The thesis in one paragraph

Baqaya is a mature, fully on-device personal-finance app (Kotlin/Compose, Room/SQLite, no backend, bilingual Arabic/English) with a broad, already-shipped feature set: expenses, budgets, income, khata, splits, debts, subscriptions, savings goals, family mode, and now Saudi-localized SMS auto-tracking, a jam'iya organiser, and a zakat calculator. Its structural edge is not any single feature — it is that **nothing leaves the phone**. That was a cost story ("zero infrastructure"); in 2026 it is also a **compliance-and-trust story**, and that is the strategy: stop competing feature-for-feature with cloud apps, and win the two markets where local money behaviour + data-residency anxiety are strongest — Saudi Arabia (on the back of Vision 2030) and India (on the back of the DPDP Rules) — with market-native modules layered on a privacy-by-design core.

## 2. Why "on-device" is now a moat, not just cheap

India notified the **Digital Personal Data Protection Rules, 2025** on 13 November 2025, with full compliance required by **13 May 2027**. Every personal-finance app that processes Indian residents' data is a *Data Fiduciary*, with penalties up to **₹250 crore** for security failures. Baqaya collects and stores nothing on a server, so it satisfies the hardest parts of DPDP by construction — while cloud competitors are spending the next 18 months and real money retrofitting consent, breach-notification, and data-residency machinery. The same "your financial data never leaves your device" message is a trust wedge in Saudi Arabia too. **This should become the headline of the store listing in both markets**, not a footnote in the privacy policy.

## 3. Market positioning

**Saudi Arabia — ride Vision 2030's Financial Sector Development Program.** The FSDP explicitly targets a stronger *savings culture*, *financial planning*, *financial inclusion*, and *SME financing*. Baqaya's roadmap maps onto that almost line-for-line: SMS auto-tracking (fits a cashless mada economy), jam'iya (savings culture — the KSA-native ROSCA), zakat (financial planning + religious obligation), and — the big one — ZATCA e-invoicing for the SME segment. Every one of these is a Vision-aligned reason for a Saudi user to choose a local-feeling app over a generic Western tracker.

**India — own the informal-ledger + UPI behaviour, defend on privacy.** Khata/udhaar and UPI are already native and built. India is crowded and hard to monetise, so the play here is retention and differentiation-on-privacy rather than heavy new investment: keep the ledger free and excellent, lead with DPDP-grade "on-device" positioning, and let the shared modules (zakat, budgeting, reports) carry monetisation.

## 4. Prioritised initiative backlog

Ordered by leverage ÷ effort. Status reflects work already done in this codebase.

1. **Saudi bank SMS localisation** — _Done._ Parser now reads Al Rajhi/SNB/Riyad/SABB/Alinma/stc pay/urpay/mada alerts in Arabic + English; India INR retained. Converts a silently-broken flagship feature into the top reason a Saudi user keeps the app.
2. **Jam'iya (ROSCA) organiser** — _Done._ Offline, no money movement (no SAMA licence), reuses the Splits/Khata engine. The KSA-native savings behaviour no competitor targets at the informal-cash layer.
3. **Zakat calculator** — _Logic + tests done; UI is the immediate next build._ Both markets, FSDP-aligned, an annual-obligation hook that pairs perfectly with a balances-aware app. Prefill assets from existing payment-account and savings-goal data; user supplies the current gold/silver price (keeps it offline and free).
4. **Locale-gating of India-only surface** — _Small, high polish._ Hide UPI/₹ affordances for KSA users so the app reads "made for us," not "ported." Low risk, pure conditional rendering.
5. **Privacy-first store repositioning + ASO, per market** — _Near-zero cost, high leverage._ Lead screenshots and copy with "100% on-device, your data never leaves your phone," localised keyword sets (Arabic: مصروفات، جمعية، زكاة، فاتورة; Hindi/Urdu: खाता، उधार, بجٹ). Ties directly to the DPDP moment in India.
6. **ZATCA Phase-2 e-invoicing tier (KSA SME)** — _Following quarter; the monetisation ceiling._ Wave 24 (SAR 375k turnover, 30 June 2026) was the final wave — Phase-2 integration now applies to **every VAT-registered business**, so a long tail of small merchants is legally required to issue compliant e-invoices and many are scrambling. Turning Baqaya's ledger into a compliant invoice generator (Arabic XML + cryptographic stamp + QR + Fatoora API, EGS onboarding + sandbox certification) is a real engineering effort, not a weekend — but it is a forced-demand, recurring-revenue product. This is the flagship paid feature.
7. **"Baqaya Pro" freemium monetisation** — _Following quarter, alongside #6._ See §5.
8. **Watch / defer:** open banking (SAMA moved it to a licensed activity in 2026 — *partner with a licensed provider, do not build*); Hijri-calendar & prayer-aware reminders (nice polish, low urgency).

## 5. Monetisation model

Keep the daily-use core free — the ledger, SMS tracking, budgets, jam'iya — because retention is the whole game at this stage. Charge for **compliance, reporting, and business** capability, where willingness-to-pay is real:

**Baqaya Pro (annual):** zakat report export, PDF/Excel statements, unlimited jam'iya circles and split groups, receipt-photo backup, and — for KSA — ZATCA-compliant invoicing.

Pricing guidance from 2026 benchmarks: freemium download-to-paid conversion clusters at ~2–8%, higher prices *pre-filter* for genuine intent (so don't underprice), and weekly plans dominate volume but churn hard. Recommendation: **annual-first** (roughly SAR 39–49/yr in KSA, ₹399–499/yr in India), with a monthly option, and no weekly plan. Price the ZATCA capability as its own higher SME tier once built — that buyer has a legal deadline, not a discretionary want.

## 6. Two-quarter execution sequence

**Q3 2026 (market fit + trust):** wire the Zakat UI with balance prefill → locale-gate India-only surface → ship the privacy-first store rework in both markets → instrument activation and SMS-grant funnels. Low-risk, compounding, all reinforce "made for this market."

**Q4 2026 (monetisation):** build ZATCA Phase-2 (EGS registration, XML/stamp/QR pipeline, Fatoora sandbox → production) → launch Baqaya Pro tiers → A/B the paywall placement. This is where revenue starts.

## 7. Risks & guardrails

- **Don't add a backend.** The privacy moat and the zero-cost model both die the moment data leaves the device. If a feature seems to need a server (e.g. ZATCA clearance calls), scope it as a direct device-to-ZATCA/API call or a user-triggered action, not a Baqaya-hosted data store.
- **Zakat correctness.** Nisab and hawl rules vary by authority; keep the metal price user-supplied (offline + neutral), show the basis (silver default, as the lower/more-cautious threshold), and label figures as guidance, not a fatwa.
- **ZATCA is the one heavy build** — certification is the schedule risk, not the code. Timebox a sandbox spike before committing a launch date.
- **Nav crowding** — resolved: bottom bar trimmed to five tabs (Dashboard, Khata, Splits, Jam'iya, More), with Debts/Subscriptions/Settings under More.

## 8. Metrics to watch

Activation (first expense logged within 24h of install), SMS-permission grant rate (KSA), jam'iya circles created per active user, zakat calculator completion, Pro conversion (target the 2–8% band, aim upper), and D30 retention split by market.

---

### Sources

- [Vision 2030 — Financial Sector Development Program](https://www.vision2030.gov.sa/en/explore/programs/financial-sector-development-program)
- [ZATCA Phase 2 Wave 24 — 30 June 2026 deadline guide (Origami)](https://origami.sa/en/blog/zatca-phase-2-wave-24-integration-guide/) · [Wave 24 overview (Sharayeh)](https://sharayeh.com/en/blog/zatca-phase-2-guide)
- [Digital Personal Data Protection Rules, 2025 (PIB)](https://static.pib.gov.in/WriteReadData/specificdocs/documents/2025/nov/doc20251117695301.pdf) · [DPDP for BFSI/fintech 2026](https://myitmanager.in/dpdp-act-fintech-nbfc-india/)
- [Nisab thresholds & zakat basis 2026 (MATW)](https://blog.matwproject.org/nisab-threshold-gold-vs-silver-2026/) · [Zakat calculation 2026 (Muslim Aid)](https://www.muslimaid.org/zakat-calculator/)
- [Subscription app pricing benchmarks 2026 (Airbridge)](https://www.airbridge.io/en/blog/subscription-app-pricing-by-category-2026-benchmark) · [Freemium conversion benchmarks (First Page Sage)](https://firstpagesage.com/seo-blog/saas-freemium-conversion-rates/)
