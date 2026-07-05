package com.expensetracker.app.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileWriter

/**
 * Exports monthly expenses to a CSV file, saved to the app's private cache dir and
 * shared via FileProvider — no storage permission required.
 *
 * Format: Date, Category, Description, Amount
 * First row is a header. Values containing commas or quotes are RFC 4180 quoted.
 */
object CsvExporter {

    fun export(
        context: Context,
        monthLabel: String,
        colDate: String,
        colCategory: String,
        colDescription: String,
        colAmount: String,
        rows: List<ExportRow>
    ): Uri {
        val fileName = "expenses_${monthLabel.replace(" ", "_")}.csv"
        val file = File(context.cacheDir, fileName)

        FileWriter(file, false).use { writer ->
            // Header
            writer.appendLine(csvRow(colDate, colCategory, colDescription, colAmount))
            // Data rows
            rows.forEach { row ->
                writer.appendLine(csvRow(row.dateLabel, row.categoryLabel, row.description, row.amountLabel))
            }
        }

        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
    }

    fun shareOrSave(context: Context, uri: Uri, chooserTitle: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/csv"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, chooserTitle))
    }

    /** RFC 4180: wrap in double-quotes if value contains comma, quote, or newline. */
    private fun csvRow(vararg values: String): String =
        values.joinToString(",") { value ->
            if (value.contains(',') || value.contains('"') || value.contains('\n')) {
                "\"${value.replace("\"", "\"\"")}\""
            } else {
                value
            }
        }
}
