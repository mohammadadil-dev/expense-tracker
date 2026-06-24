package com.expensetracker.app.ui.components;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u00002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u001aj\u0010\u0000\u001a\u00020\u00012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\f\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00060\u00032\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\n2\f\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\u00010\f2\u0012\u0010\r\u001a\u000e\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u00010\u000e2\u0012\u0010\u000f\u001a\u000e\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u00010\u000eH\u0007\u00a8\u0006\u0010"}, d2 = {"SmsReviewSheet", "", "items", "", "Lcom/expensetracker/app/data/PendingSmsExpense;", "categories", "Lcom/expensetracker/app/data/CategoryEntity;", "currencySymbol", "", "locale", "Ljava/util/Locale;", "onDismiss", "Lkotlin/Function0;", "onAccept", "Lkotlin/Function1;", "onDismissItem", "app_debug"})
public final class SmsReviewSheetKt {
    
    /**
     * Review queue for transactions the SMS parser picked up. Nothing here has been saved as a
     * real expense yet — each row is either turned into one via [onAccept] (which opens
     * [AddEditExpenseSheet] pre-filled so the user can fix anything before it's saved) or thrown
     * away via [onDismissItem].
     */
    @kotlin.OptIn(markerClass = {androidx.compose.material3.ExperimentalMaterial3Api.class})
    @androidx.compose.runtime.Composable()
    public static final void SmsReviewSheet(@org.jetbrains.annotations.NotNull()
    java.util.List<com.expensetracker.app.data.PendingSmsExpense> items, @org.jetbrains.annotations.NotNull()
    java.util.List<com.expensetracker.app.data.CategoryEntity> categories, @org.jetbrains.annotations.NotNull()
    java.lang.String currencySymbol, @org.jetbrains.annotations.NotNull()
    java.util.Locale locale, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onDismiss, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super com.expensetracker.app.data.PendingSmsExpense, kotlin.Unit> onAccept, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super com.expensetracker.app.data.PendingSmsExpense, kotlin.Unit> onDismissItem) {
    }
}