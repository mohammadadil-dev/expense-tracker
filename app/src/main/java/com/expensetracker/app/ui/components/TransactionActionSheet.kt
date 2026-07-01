package com.expensetracker.app.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.expensetracker.app.R
import com.expensetracker.app.data.CategoryEntity
import com.expensetracker.app.data.ExpenseEntity
import com.expensetracker.app.ui.theme.DangerRed
import com.expensetracker.app.util.Formatters
import com.expensetracker.app.util.categoryDisplayName
import com.expensetracker.app.ui.theme.TextMuted

/**
 * Bottom sheet shown when the user taps any expense row/grid card.
 * Presents Edit and Delete actions clearly so both are always discoverable.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionActionSheet(
    expense: ExpenseEntity,
    category: CategoryEntity?,
    currencySymbol: String,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(bottom = 16.dp)
        ) {
            // Transaction summary header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 12.dp)
            ) {
                Text(
                    text = expense.description.ifBlank {
                        category?.let { categoryDisplayName(it) } ?: ""
                    },
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))
                // Use MoneyText so Saudi Riyal renders as icon, not raw text
                Row(verticalAlignment = Alignment.CenterVertically) {
                    MoneyText(
                        formatted = Formatters.money(expense.amount, currencySymbol),
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                    val meta = buildString {
                        category?.let { append("  ·  ${categoryDisplayName(it)}") }
                        append("  ·  ${expense.date}")
                    }
                    Text(
                        text = meta,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
            Spacer(Modifier.height(8.dp))

            ActionItem(
                icon = Icons.Filled.Edit,
                label = stringResource(R.string.edit),
                tint = MaterialTheme.colorScheme.primary,
                onClick = { onDismiss(); onEdit() }
            )

            ActionItem(
                icon = Icons.Filled.Delete,
                label = stringResource(R.string.delete),
                tint = DangerRed,
                onClick = { onDismiss(); onDelete() }
            )
        }
    }
}

@Composable
private fun ActionItem(
    icon: ImageVector,
    label: String,
    tint: Color,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = Color.Transparent,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(22.dp)
            )
            Spacer(Modifier.width(16.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                color = tint
            )
        }
    }
}
