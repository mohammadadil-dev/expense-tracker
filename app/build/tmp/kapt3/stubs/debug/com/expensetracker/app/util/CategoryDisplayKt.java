package com.expensetracker.app.util;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000\u000e\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\u001a\u0010\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u0003H\u0007\u00a8\u0006\u0004"}, d2 = {"categoryDisplayName", "", "category", "Lcom/expensetracker/app/data/CategoryEntity;", "app_debug"})
public final class CategoryDisplayKt {
    
    /**
     * Resolves a category's display name. Built-in categories carry a [CategoryEntity.nameKey]
     * which is mapped to a localized string resource, so they automatically show in Arabic or
     * English depending on the app's current language. User-renamed/created categories just use
     * the free-text [CategoryEntity.customName] as typed.
     */
    @androidx.compose.runtime.Composable()
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String categoryDisplayName(@org.jetbrains.annotations.NotNull()
    com.expensetracker.app.data.CategoryEntity category) {
        return null;
    }
}