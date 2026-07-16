package com.expensetracker.app.util

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

/** One printable row of an exported report — already fully resolved to display strings.
 *
 * [accountLabel] is optional (null when the expense has no payment account tagged, or for
 * callers that don't pass it at all) and is only consumed by [CsvExporter] — [PdfExporter]'s
 * fixed 4-column page layout is left untouched by design, since reflowing its hand-tuned
 * column widths/clipping without a way to visually verify the render would risk a layout
 * regression in a report real users already rely on. */
data class ExportRow(
    val dateLabel: String,
    val categoryLabel: String,
    val description: String,
    val amountLabel: String,
    val accountLabel: String? = null
)

/**
 * Renders a simple monthly expense report straight to PDF using the platform's own
 * [android.graphics.pdf.PdfDocument] — no third-party library, no network call, no
 * recurring cost. The finished file is written to the app's private cache dir and handed
 * out via [androidx.core.content.FileProvider] so it can be saved/shared through the
 * system chooser without any storage permission.
 */
object PdfExporter {

    private const val PAGE_WIDTH = 595f  // A4 @ 72dpi
    private const val PAGE_HEIGHT = 842f
    private const val MARGIN = 36f
    private const val ROW_HEIGHT = 20f
    private const val COL_DATE_X = MARGIN
    private const val COL_CATEGORY_X = MARGIN + 80f
    private const val COL_DESCRIPTION_X = MARGIN + 210f
    private const val COL_AMOUNT_RIGHT_X = PAGE_WIDTH - MARGIN

    /**
     * Same traced outline as `res/drawable/ic_saudi_riyal.xml` (685x765 viewport), reproduced
     * here as a plain [Path] since this exporter draws straight to [Canvas] with no Compose/
     * vector-drawable inflation available. Built once and reused for every export.
     */
    private val riyalPath: Path by lazy {
        Path().apply {
            moveTo(685f, 624f)
            lineTo(427f, 679f)
            lineTo(411f, 722f)
            lineTo(404f, 765f)
            lineTo(661f, 711f)
            lineTo(677f, 669f)
            close()
            moveTo(483f, 40f)
            lineTo(403f, 109f)
            lineTo(403f, 353f)
            lineTo(324f, 371f)
            lineTo(322f, 0f)
            lineTo(244f, 64f)
            lineTo(242f, 387f)
            lineTo(64f, 426f)
            lineTo(40f, 512f)
            lineTo(241f, 470f)
            lineTo(242f, 574f)
            lineTo(23f, 621f)
            lineTo(0f, 707f)
            lineTo(260f, 644f)
            lineTo(321f, 554f)
            lineTo(323f, 452f)
            lineTo(403f, 437f)
            lineTo(403f, 600f)
            lineTo(662f, 545f)
            lineTo(685f, 459f)
            lineTo(483f, 500f)
            lineTo(483f, 419f)
            lineTo(661f, 381f)
            lineTo(685f, 298f)
            lineTo(483f, 335f)
            close()
        }
    }

    /** Draws the riyal glyph so its bottom rests on [baselineY], like a capital letter. */
    private fun drawRiyalIcon(c: Canvas, x: Float, baselineY: Float, iconHeight: Float, paint: Paint) {
        val scale = iconHeight / 765f
        val matrix = Matrix().apply {
            setScale(scale, scale)
            postTranslate(x, baselineY - iconHeight)
        }
        val transformed = Path(riyalPath).apply { transform(matrix) }
        c.drawPath(transformed, paint)
    }

    fun export(
        context: Context,
        appName: String,
        reportTitle: String,
        monthLabel: String,
        customerIdLabel: String,
        /** Optional address/phone line drawn right under [appName] when non-null/non-blank —
         *  lets a business profile (see SettingsRepository.businessName/Address/Phone) put its
         *  own contact details on the exported report. Null/blank = today's layout, unchanged. */
        businessContactLine: String? = null,
        colDate: String,
        colCategory: String,
        colDescription: String,
        colAmount: String,
        totalLabel: String,
        totalValue: String,
        footerText: String,
        emptyLabel: String,
        rows: List<ExportRow>
    ): Uri {
        val document = android.graphics.pdf.PdfDocument()

        val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#1F2430"); textSize = 18f; isFakeBoldText = true
        }
        val subtitlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#6B7280"); textSize = 11f
        }
        val headerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#4F46E5"); textSize = 10f; isFakeBoldText = true
        }
        val rowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#1F2430"); textSize = 10f
        }
        val totalPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#1F2430"); textSize = 13f; isFakeBoldText = true
        }
        val footerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#9CA3AF"); textSize = 9f
        }
        val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#E8E9ED"); strokeWidth = 1f
        }

        var pageNumber = 0
        var page: android.graphics.pdf.PdfDocument.Page
        var canvas: Canvas
        var y: Float

        fun drawRightAligned(c: Canvas, text: String, rightX: Float, baselineY: Float, paint: Paint) {
            val width = paint.measureText(text)
            c.drawText(text, rightX - width, baselineY, paint)
        }

        // Riyal amounts get the official symbol drawn in place of the "ر.س" text prefix,
        // sized off the paint's textSize so it scales with the row vs. total font sizes.
        fun drawAmountRightAligned(c: Canvas, amountLabel: String, rightX: Float, baselineY: Float, paint: Paint) {
            if (Formatters.isSaudiRiyalAmount(amountLabel)) {
                val numeric = Formatters.stripSaudiRiyalSymbol(amountLabel)
                val numWidth = paint.measureText(numeric)
                val iconHeight = paint.textSize * 0.72f
                val iconWidth = iconHeight * (685f / 765f)
                val gap = paint.textSize * 0.12f
                val left = rightX - (iconWidth + gap + numWidth)
                drawRiyalIcon(c, left, baselineY, iconHeight, paint)
                c.drawText(numeric, left + iconWidth + gap, baselineY, paint)
            } else {
                drawRightAligned(c, amountLabel, rightX, baselineY, paint)
            }
        }

        // Same as above but keeps a plain-text label (e.g. "Total: ") in front of the amount.
        fun drawLabeledAmountRightAligned(c: Canvas, prefix: String, amountLabel: String, rightX: Float, baselineY: Float, paint: Paint) {
            if (Formatters.isSaudiRiyalAmount(amountLabel)) {
                val numeric = Formatters.stripSaudiRiyalSymbol(amountLabel)
                val prefixWidth = paint.measureText(prefix)
                val numWidth = paint.measureText(numeric)
                val iconHeight = paint.textSize * 0.72f
                val iconWidth = iconHeight * (685f / 765f)
                val gap = paint.textSize * 0.12f
                val left = rightX - (prefixWidth + iconWidth + gap + numWidth)
                c.drawText(prefix, left, baselineY, paint)
                drawRiyalIcon(c, left + prefixWidth, baselineY, iconHeight, paint)
                c.drawText(numeric, left + prefixWidth + iconWidth + gap, baselineY, paint)
            } else {
                drawRightAligned(c, "$prefix$amountLabel", rightX, baselineY, paint)
            }
        }

        fun startPage(isFirst: Boolean): Pair<android.graphics.pdf.PdfDocument.Page, Float> {
            pageNumber++
            val p = document.startPage(
                android.graphics.pdf.PdfDocument.PageInfo.Builder(
                    PAGE_WIDTH.toInt(),
                    PAGE_HEIGHT.toInt(),
                    pageNumber
                ).create()
            )
            val c = p.canvas
            var cursorY = MARGIN
            if (isFirst) {
                cursorY += 16f
                c.drawText(appName, MARGIN, cursorY, titlePaint)
                if (!businessContactLine.isNullOrBlank()) {
                    cursorY += 15f
                    c.drawText(businessContactLine, MARGIN, cursorY, subtitlePaint)
                }
                cursorY += 18f
                c.drawText(reportTitle, MARGIN, cursorY, subtitlePaint)
                cursorY += 15f
                c.drawText(monthLabel, MARGIN, cursorY, subtitlePaint)
                cursorY += 15f
                c.drawText(customerIdLabel, MARGIN, cursorY, subtitlePaint)
                cursorY += 22f
            } else {
                cursorY += 4f
            }
            c.drawText(colDate, COL_DATE_X, cursorY, headerPaint)
            c.drawText(colCategory, COL_CATEGORY_X, cursorY, headerPaint)
            c.drawText(colDescription, COL_DESCRIPTION_X, cursorY, headerPaint)
            drawRightAligned(c, colAmount, COL_AMOUNT_RIGHT_X, cursorY, headerPaint)
            cursorY += 8f
            c.drawLine(MARGIN, cursorY, PAGE_WIDTH - MARGIN, cursorY, linePaint)
            cursorY += ROW_HEIGHT
            return p to cursorY
        }

        val first = startPage(isFirst = true)
        page = first.first
        canvas = page.canvas
        y = first.second

        // Clips [text] to [maxWidth] PDF-points using the given [paint]'s font metrics so each
        // column never overflows into the next — more reliable than character-count limits
        // because character widths vary by locale (e.g. Filipino month names are wider than
        // their English abbreviations, causing the date/category columns to collide).
        fun clipToWidth(text: String, maxWidth: Float, p: Paint): String {
            if (p.measureText(text) <= maxWidth) return text
            var result = text
            while (result.isNotEmpty() && p.measureText("$result…") > maxWidth) {
                result = result.dropLast(1)
            }
            return if (result.isEmpty()) text.take(1) else "$result…"
        }

        if (rows.isEmpty()) {
            canvas.drawText(emptyLabel, MARGIN, y, rowPaint)
            y += ROW_HEIGHT
        } else {
            for (row in rows) {
                if (y > PAGE_HEIGHT - 110f) {
                    document.finishPage(page)
                    val next = startPage(isFirst = false)
                    page = next.first
                    canvas = page.canvas
                    y = next.second
                }
                canvas.drawText(
                    clipToWidth(row.dateLabel, COL_CATEGORY_X - COL_DATE_X - 4f, rowPaint),
                    COL_DATE_X, y, rowPaint
                )
                canvas.drawText(
                    clipToWidth(row.categoryLabel, COL_DESCRIPTION_X - COL_CATEGORY_X - 4f, rowPaint),
                    COL_CATEGORY_X, y, rowPaint
                )
                canvas.drawText(
                    clipToWidth(row.description.ifBlank { "—" }, COL_AMOUNT_RIGHT_X - COL_DESCRIPTION_X - 80f, rowPaint),
                    COL_DESCRIPTION_X, y, rowPaint
                )
                drawAmountRightAligned(canvas, row.amountLabel, COL_AMOUNT_RIGHT_X, y, rowPaint)
                y += ROW_HEIGHT
            }
        }

        if (y > PAGE_HEIGHT - 90f) {
            document.finishPage(page)
            val next = startPage(isFirst = false)
            page = next.first
            canvas = page.canvas
            y = next.second
        }
        y += 6f
        canvas.drawLine(MARGIN, y, PAGE_WIDTH - MARGIN, y, linePaint)
        y += 20f
        drawLabeledAmountRightAligned(canvas, "$totalLabel: ", totalValue, COL_AMOUNT_RIGHT_X, y, totalPaint)

        canvas.drawText(footerText, MARGIN, PAGE_HEIGHT - 20f, footerPaint)
        document.finishPage(page)

        val exportDir = File(context.cacheDir, "exports").apply { mkdirs() }
        // Clear out any previously generated reports so the cache dir doesn't grow forever.
        exportDir.listFiles()?.forEach { it.delete() }
        val file = File(exportDir, "expense_report_${System.currentTimeMillis()}.pdf")
        FileOutputStream(file).use { document.writeTo(it) }
        document.close()

        return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    }

    /** Opens the system chooser so the user can save the report (Files/Drive/etc.) or share it. */
    fun shareOrSave(context: Context, uri: Uri, chooserTitle: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, chooserTitle))
    }
}
