package com.expensetracker.app.ui.components;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u00004\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\u001ar\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\b\u0010\u0004\u001a\u0004\u0018\u00010\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\u00072\f\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00010\n2\f\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\u00010\n2\f\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u00010\n2\b\b\u0002\u0010\r\u001a\u00020\u000e2\u0012\u0010\u000f\u001a\u000e\u0012\u0004\u0012\u00020\u0007\u0012\u0004\u0012\u00020\u00110\u0010H\u0007\u00a8\u0006\u0012"}, d2 = {"ExpenseRowCard", "", "expense", "Lcom/expensetracker/app/data/ExpenseEntity;", "category", "Lcom/expensetracker/app/data/CategoryEntity;", "currencySymbol", "", "dateText", "onClick", "Lkotlin/Function0;", "onEdit", "onDelete", "modifier", "Landroidx/compose/ui/Modifier;", "colorFromHex", "Lkotlin/Function1;", "Landroidx/compose/ui/graphics/Color;", "app_release"})
public final class ExpenseRowCardKt {
    
    /**
     * A single expense's row card — shared by the Day, Category, and Calendar list layouts so
     * tapping/editing/deleting behaves identically no matter which view the user is in. Gives a
     * small press-scale dip for tactile feedback on top of the card's own ripple. Tinted with a
     * pale, lightened version of the expense's own category color (rather than a flat white fill)
     * so each row reads as its own small "tile" — matching the colorful Bento tiles above — while
     * staying light enough that the description/date/amount text underneath is still easy to read.
     */
    @androidx.compose.runtime.Composable()
    public static final void ExpenseRowCard(@org.jetbrains.annotations.NotNull()
    com.expensetracker.app.data.ExpenseEntity expense, @org.jetbrains.annotations.Nullable()
    com.expensetracker.app.data.CategoryEntity category, @org.jetbrains.annotations.NotNull()
    java.lang.String currencySymbol, @org.jetbrains.annotations.NotNull()
    java.lang.String dateText, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onClick, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onEdit, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onDelete, @org.jetbrains.annotations.NotNull()
    androidx.compose.ui.Modifier modifier, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.String, androidx.compose.ui.graphics.Color> colorFromHex) {
    }
}