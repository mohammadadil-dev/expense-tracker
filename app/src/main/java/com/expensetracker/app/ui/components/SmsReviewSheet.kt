package com.expensetracker.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.expensetracker.app.R
import com.expensetracker.app.data.CategoryEntity
import com.expensetracker.app.data.PendingSmsExpense
import com.expensetracker.app.ui.theme.CardWhite
import com.expensetracker.app.ui.theme.TextSecondary
import com.expensetracker.app.util.DateUtils
import com.expensetracker.app.util.Formatters
import com.expensetracker.app.util.categoryDisplayName
import java.util.Locale

/**
 * Review queue for transactions the SMS parser picked up. Nothing here has been saved as a
 * real expense yet — each row is either turned into one via [onAccept] (which opens
 * [AddEditExpenseSheet] pre-filled so the user can fix anything before it's saved) or thrown
 * away via [onDismissItem].
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmsReviewSheet(
    items: List<PendingSmsExpense>,
    categories: List<CategoryEntity>,
    currencySymbol: String,
    locale: Locale,
    onDismiss: () -> Unit,
    onAccept: (PendingSmsExpense) -> Unit,
    onDismissItem: (PendingSmsExpense) -> Unit
) {
    val categoryById = remember(categories) { categories.associateBy { it.id } }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp)
        ) {
            Text(
                text = stringResource(R.string.sms_review_title, items.size),
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.sms_review_subtitle),
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
            Spacer(Modifier.height(16.dp))

            items.forEach { item ->
                val category = item.suggestedCategoryId?.let { id -> categoryById[id] }
                val categoryLabel = category?.let { categoryDisplayName(it) }
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardWhite),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.description,
                                    style = MaterialTheme.typography.bodyLarge,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = DateUtils.formatExpenseDate(item.date, locale) +
                                        (categoryLabel?.let { " · $it" } ?: ""),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextSecondary
                                )
                            }
                            Spacer(Modifier.width(8.dp))
                            MoneyText(
                                formatted = Formatters.money(item.amount, currencySymbol),
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                        Spacer(Modifier.height(10.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                            TextButton(onClick = { onDismissItem(item) }) {
                                Text(stringResource(R.string.sms_dismiss))
                            }
                            Spacer(Modifier.width(8.dp))
                            Button(onClick = { onAccept(item) }) {
                                Text(stringResource(R.string.sms_accept))
                            }
                        }
                    }
                }
            }
        }
    }
}
