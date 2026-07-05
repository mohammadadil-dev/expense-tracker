package com.expensetracker.app.ui.components;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000X\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\t\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0006\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\b\n\u0000\u001a\u008e\u0002\u0010\u0000\u001a\u00020\u00012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\b\u0010\u0005\u001a\u0004\u0018\u00010\u00062\u0006\u0010\u0007\u001a\u00020\b2\n\b\u0002\u0010\t\u001a\u0004\u0018\u00010\n2\f\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\u00010\f2\u008d\u0001\u0010\r\u001a\u0088\u0001\u0012\u0015\u0012\u0013\u0018\u00010\u000f\u00a2\u0006\f\b\u0010\u0012\b\b\u0011\u0012\u0004\b\b(\u0012\u0012\u0013\u0012\u00110\u000f\u00a2\u0006\f\b\u0010\u0012\b\b\u0011\u0012\u0004\b\b(\u0013\u0012\u0013\u0012\u00110\b\u00a2\u0006\f\b\u0010\u0012\b\b\u0011\u0012\u0004\b\b(\u0014\u0012\u0013\u0012\u00110\u0015\u00a2\u0006\f\b\u0010\u0012\b\b\u0011\u0012\u0004\b\b(\u0016\u0012\u0013\u0012\u00110\b\u00a2\u0006\f\b\u0010\u0012\b\b\u0011\u0012\u0004\b\b(\u0017\u0012\u0013\u0012\u00110\u0018\u00a2\u0006\f\b\u0010\u0012\b\b\u0011\u0012\u0004\b\b(\u0019\u0012\u0004\u0012\u00020\u00010\u000e2:\b\u0002\u0010\u001a\u001a4\u0012\u0013\u0012\u00110\b\u00a2\u0006\f\b\u0010\u0012\b\b\u0011\u0012\u0004\b\b(\u0011\u0012\u0013\u0012\u00110\b\u00a2\u0006\f\b\u0010\u0012\b\b\u0011\u0012\u0004\b\b(\u001c\u0012\u0004\u0012\u00020\u0001\u0018\u00010\u001bH\u0007\u001a\u0010\u0010\u001d\u001a\u00020\b2\u0006\u0010\u001e\u001a\u00020\u0015H\u0002\u001a\u0010\u0010\u001f\u001a\u00020\b2\u0006\u0010\u0012\u001a\u00020 H\u0003\u00a8\u0006!"}, d2 = {"AddEditExpenseSheet", "", "categories", "", "Lcom/expensetracker/app/data/CategoryEntity;", "existing", "Lcom/expensetracker/app/data/ExpenseEntity;", "defaultDate", "", "prefill", "Lcom/expensetracker/app/ui/components/ExpensePrefill;", "onDismiss", "Lkotlin/Function0;", "onSave", "Lkotlin/Function6;", "", "Lkotlin/ParameterName;", "name", "id", "categoryId", "description", "", "amount", "date", "", "isRecurring", "onAddCategory", "Lkotlin/Function2;", "colorHex", "formatPlain", "value", "stringResourceCompat", "", "app_release"})
public final class AddEditExpenseSheetKt {
    
    @kotlin.OptIn(markerClass = {androidx.compose.material3.ExperimentalMaterial3Api.class})
    @androidx.compose.runtime.Composable()
    public static final void AddEditExpenseSheet(@org.jetbrains.annotations.NotNull()
    java.util.List<com.expensetracker.app.data.CategoryEntity> categories, @org.jetbrains.annotations.Nullable()
    com.expensetracker.app.data.ExpenseEntity existing, @org.jetbrains.annotations.NotNull()
    java.lang.String defaultDate, @org.jetbrains.annotations.Nullable()
    com.expensetracker.app.ui.components.ExpensePrefill prefill, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onDismiss, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function6<? super java.lang.Long, ? super java.lang.Long, ? super java.lang.String, ? super java.lang.Double, ? super java.lang.String, ? super java.lang.Boolean, kotlin.Unit> onSave, @org.jetbrains.annotations.Nullable()
    kotlin.jvm.functions.Function2<? super java.lang.String, ? super java.lang.String, kotlin.Unit> onAddCategory) {
    }
    
    private static final java.lang.String formatPlain(double value) {
        return null;
    }
    
    @androidx.compose.runtime.Composable()
    private static final java.lang.String stringResourceCompat(int id) {
        return null;
    }
}