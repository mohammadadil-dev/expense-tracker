package com.expensetracker.app.ui.components;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000:\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0006\n\u0002\b\u0007\u001aJ\u0010\u0000\u001a\u00020\u00012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\u0006\u0010\u0005\u001a\u00020\u00062\f\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\u00010\b2\u0012\u0010\t\u001a\u000e\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u00010\n2\b\b\u0002\u0010\u000b\u001a\u00020\fH\u0007\u001a&\u0010\r\u001a\u00020\u00012\u0006\u0010\u000e\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\f\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00010\bH\u0003\u001aK\u0010\u0010\u001a\u00020\u00012\b\u0010\u0011\u001a\u0004\u0018\u00010\u00122\b\u0010\u0013\u001a\u0004\u0018\u00010\u00142\u0006\u0010\u0005\u001a\u00020\u00062\b\b\u0002\u0010\u0015\u001a\u00020\u00122\f\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\u00010\b2\b\b\u0002\u0010\u000b\u001a\u00020\fH\u0007\u00a2\u0006\u0002\u0010\u0017\u001a\"\u0010\u0018\u001a\u00020\u00012\u0006\u0010\u0019\u001a\u00020\u00122\u0006\u0010\u001a\u001a\u00020\u00122\b\b\u0002\u0010\u000b\u001a\u00020\fH\u0007\u00a8\u0006\u001b"}, d2 = {"GoalProgressCard", "", "goals", "", "Lcom/expensetracker/app/data/GoalEntity;", "currencySymbol", "", "onAddGoal", "Lkotlin/Function0;", "onGoalTap", "Lkotlin/Function1;", "modifier", "Landroidx/compose/ui/Modifier;", "GoalProgressRow", "goal", "onClick", "PaydayCard", "daysUntilPayday", "", "dailyBudgetRemaining", "", "paydayDayOfMonth", "onConfigurePayday", "(Ljava/lang/Integer;Ljava/lang/Double;Ljava/lang/String;ILkotlin/jvm/functions/Function0;Landroidx/compose/ui/Modifier;)V", "StreakCard", "streak", "bestStreak", "app_debug"})
public final class EngagementCardsKt {
    
    /**
     * Full-width card showing daily logging streak, animated progress to next
     * milestone, and best-streak badge. No text ever wraps.
     */
    @androidx.compose.runtime.Composable()
    public static final void StreakCard(int streak, int bestStreak, @org.jetbrains.annotations.NotNull()
    androidx.compose.ui.Modifier modifier) {
    }
    
    /**
     * Full-width card. When not configured, the whole card is a clear CTA with
     * chevron + hint text. When configured, shows large countdown + daily budget
     * and a small edit button so the user can change the date inline.
     */
    @androidx.compose.runtime.Composable()
    public static final void PaydayCard(@org.jetbrains.annotations.Nullable()
    java.lang.Integer daysUntilPayday, @org.jetbrains.annotations.Nullable()
    java.lang.Double dailyBudgetRemaining, @org.jetbrains.annotations.NotNull()
    java.lang.String currencySymbol, int paydayDayOfMonth, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onConfigurePayday, @org.jetbrains.annotations.NotNull()
    androidx.compose.ui.Modifier modifier) {
    }
    
    /**
     * Compact card showing progress toward a savings goal with a labelled progress bar.
     * Tapping opens the goals screen or an add-contribution sheet.
     */
    @androidx.compose.runtime.Composable()
    public static final void GoalProgressCard(@org.jetbrains.annotations.NotNull()
    java.util.List<com.expensetracker.app.data.GoalEntity> goals, @org.jetbrains.annotations.NotNull()
    java.lang.String currencySymbol, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onAddGoal, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super com.expensetracker.app.data.GoalEntity, kotlin.Unit> onGoalTap, @org.jetbrains.annotations.NotNull()
    androidx.compose.ui.Modifier modifier) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void GoalProgressRow(com.expensetracker.app.data.GoalEntity goal, java.lang.String currencySymbol, kotlin.jvm.functions.Function0<kotlin.Unit> onClick) {
    }
}