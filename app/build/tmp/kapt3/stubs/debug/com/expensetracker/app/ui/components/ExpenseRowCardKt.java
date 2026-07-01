package com.expensetracker.app.ui.components;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u00002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\u001aV\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\b\u0010\u0004\u001a\u0004\u0018\u00010\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\u00072\f\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00010\n2\b\b\u0002\u0010\u000b\u001a\u00020\f2\u0012\u0010\r\u001a\u000e\u0012\u0004\u0012\u00020\u0007\u0012\u0004\u0012\u00020\u000f0\u000eH\u0007\u00a8\u0006\u0010"}, d2 = {"ExpenseRowCard", "", "expense", "Lcom/expensetracker/app/data/ExpenseEntity;", "category", "Lcom/expensetracker/app/data/CategoryEntity;", "currencySymbol", "", "dateText", "onClick", "Lkotlin/Function0;", "modifier", "Landroidx/compose/ui/Modifier;", "colorFromHex", "Lkotlin/Function1;", "Landroidx/compose/ui/graphics/Color;", "app_debug"})
public final class ExpenseRowCardKt {
    
    /**
     * A single expense row card. Tapping opens the action sheet (Edit / Delete).
     * Shared by the Day, Category, and Calendar list layouts.
     */
    @androidx.compose.runtime.Composable()
    public static final void ExpenseRowCard(@org.jetbrains.annotations.NotNull()
    com.expensetracker.app.data.ExpenseEntity expense, @org.jetbrains.annotations.Nullable()
    com.expensetracker.app.data.CategoryEntity category, @org.jetbrains.annotations.NotNull()
    java.lang.String currencySymbol, @org.jetbrains.annotations.NotNull()
    java.lang.String dateText, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onClick, @org.jetbrains.annotations.NotNull()
    androidx.compose.ui.Modifier modifier, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.String, androidx.compose.ui.graphics.Color> colorFromHex) {
    }
}