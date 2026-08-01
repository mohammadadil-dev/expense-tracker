# ZATCA Phase-2 E-Invoicing — Integration Spike & Architecture

_Prepared July 2026. Purpose: decide whether Baqaya can offer ZATCA-compliant e-invoicing without breaking its offline / no-backend model, and if so, scope, sequence, effort, and risk._

## Verdict

**GO — and it does not require a Baqaya backend.** The phone itself acts as the E-Invoice Generation Solution (EGS) unit: the private key lives in the Android Keystore, and XML generation, signing, the QR code, and the hash chain are all done on-device. The only network calls go **directly from the device to ZATCA's Fatoora platform** — which is a legal requirement, not data flowing to *our* servers — so the "nothing leaves the phone to Baqaya" promise survives intact. Scope the first release to **B2C simplified invoices (report-after-issue)**, which is both the easier path and the one that fits Baqaya's small-merchant / khata audience. Treat B2B clearance as a later phase.

## What ZATCA Phase-2 actually requires

Every compliant Phase-2 invoice is an **XML document (UBL 2.1)** carrying five mandatory security elements: a **UUID**, a **cryptographic stamp** applied with a ZATCA-issued CSID, an **X.509 / XAdES digital signature**, a **TLV-encoded QR code** with nine mandatory tags (seller name, VAT number, timestamp, invoice total, VAT total, stamp, public key, ECDSA signature), and a **SHA-256 hash chain** — each invoice embeds the hash of the previous one (the PIH), forming a tamper-evident chain.

There are two processing flows, and the distinction is the whole architecture decision:

- **B2B — Standard invoice → Clearance.** Must be sent to ZATCA and *approved in real time before* the invoice is given to the buyer. This is a synchronous, online-blocking call.
- **B2C — Simplified invoice → Reporting.** Generated, stamped and QR'd **on-device at point of sale**, given to the customer immediately, and **reported to ZATCA within 24 hours**. Asynchronous; tolerates being offline at the moment of sale.

Onboarding is a one-time (then annual) flow: the EGS generates a keypair and a CSR, submits it with an OTP from the Fatoora portal, receives a **Compliance CSID**, passes a **sandbox compliance test** (sample standard + simplified invoices), then requests the **Production CSID (PCSID)**, valid ~1 year and renewable.

## Why it fits Baqaya's model

Map each requirement to where it runs:

| Requirement | Where it runs | Needs network? |
|---|---|---|
| Keypair + CSR generation | On-device (Android Keystore) | No |
| CSID / PCSID issuance + annual renewal | Device → Fatoora API | Yes (one-time + yearly) |
| UBL 2.1 XML build | On-device | No |
| XAdES signature + cryptographic stamp | On-device (Keystore key) | No |
| TLV QR (9 tags) | On-device | No |
| SHA-256 PIH hash chain | On-device (persisted in Room) | No |
| B2C simplified **reporting** | Device → ZATCA, within 24h | Yes (async, batchable) |
| B2B standard **clearance** | Device → ZATCA, real-time | Yes (blocking) |

The private key never leaves the Keystore; invoice data goes only to ZATCA (legally mandated) and never to a Baqaya server — there is no Baqaya server. The offline promise and the zero-infrastructure cost model both hold. This is the key finding of the spike.

## Recommended architecture

Phone-as-EGS, single-tenant per install. Keypair generated and held in the Android Keystore (hardware-backed where available); the PCSID and cert stored in encrypted local storage. A `zatca` module does: XML assembly → canonicalization → XAdES signing → stamp → TLV QR → PIH linkage, then queues the invoice for reporting/clearance via a `WorkManager` job that retries on connectivity (the app already uses WorkManager for reminders/digests, so this is a known pattern). Reuse the official **ZATCA SDK / fatoora validation tooling** to self-check invoices against the spec before submission — this is the single biggest de-risker for a solo build.

Baqaya already stores part of the seller profile (`businessName`, `businessAddress`, `businessPhone`), so the data-model delta is contained: add a seller/VAT profile (VAT number, CR number, address components per ZATCA's data dictionary), an `invoices` table (buyer details, line items, totals, VAT breakdown, UUID, PIH, ICV counter, submission status), and secure storage for the cert/CSID. It slots cleanly onto the existing offline Room architecture.

## Scope & sequence

- **Phase A (first paid release): B2C simplified + reporting.** Onboarding/CSID flow, on-device XML + sign + stamp + QR + PIH, 24-hour reporting queue, human-readable invoice (PDF/A-3 optional). Serves the small-merchant/khata audience, and the async model tolerates spotty connectivity.
- **Phase B (later): B2B standard + clearance.** Real-time blocking clearance, buyer VAT capture, credit/debit notes. Only worth building once there's demand from VAT-registered B2B sellers.

## Effort & the one real risk

Realistically **6–10 focused weeks** for a robust Phase-A compliant generator plus sandbox certification — consistent with "not a weekend." The schedule risk is **not** the app code; it's **XAdES signing and the CSR extensions**. ZATCA's signature spec (signed properties, canonicalization, exact certificate OIDs) is where solo implementers lose weeks to opaque "invoice rejected" errors, and the compliance-CSID sandbox gate is pass/fail.

**Before committing a launch date, timebox a one-week sandbox spike** with a hard exit criterion: *generate one simplified invoice that passes ZATCA's compliance check in the sandbox and obtain a Compliance CSID.* If that lands in a week, the 6–10 week estimate holds. If XAdES fights back, we learn it cheaply instead of mid-quarter.

## Monetization tie-in

This is the flagship paid capability, and the buyer has a **legal deadline, not a preference** — Wave 24 (SAR 375k turnover) closed 30 June 2026 and Phase-2 now applies to every VAT-registered business, leaving a long tail of small merchants non-compliant or scrambling. Price it as its own **SME tier** above consumer "Baqaya Pro," with onboarding help. Compliance-driven demand converts far better than discretionary features.

## Decisions needed from Adil

Confirm the **B2C-first** scope (vs. insisting on B2B clearance for launch); approve the **one-week sandbox spike** before a committed timeline; and decide whether e-invoicing ships inside Baqaya or as a separate SME-branded app sharing the same codebase (recommendation: same codebase, gated behind the SME tier and shown only when a VAT profile is present).

---

### Sources

- [ZATCA — E-Invoicing Detailed Technical Guidelines (official)](https://zatca.gov.sa/en/E-Invoicing/Introduction/Guidelines/Documents/E-invoicing-Detailed-Technical-Guideline.pdf) · [ZATCA Security Features Implementation Standards](https://zatca.gov.sa/ar/E-Invoicing/SystemsDevelopers/Documents/20230519_ZATCA_Electronic_Invoice_Security_Features_Implementation_Standards_vF.pdf)
- [Phase 2 API integration guide (Jibrid)](https://www.jibrid.com/blog/zatca-phase2-api-integration-guide) · [Digital signature & CSID guide (Qeemah)](https://qeemahcloud.com/en/blog/zatca-digital-signature-csid-certificate-guide/)
- [Phase 2 onboarding steps (Wafeq)](https://www.wafeq.com/en-sa/tax-and-reporting/zatca-phase-2-integration:-5-steps-to-avoid-onboarding-errors) · [QR code TLV requirements (Wafeq)](https://www.wafeq.com/en-sa/tax-and-reporting/qr-code-requirements-for-e-invoices-zatca-saudi-arabia)
- [UBL XML / XAdES / hash-chain implementation walkthrough](https://dev.to/webkoding/implementing-zatca-phase-2-e-invoicing-in-woocommerce-ubl-xml-xades-signing-and-hash-chains-1682)
