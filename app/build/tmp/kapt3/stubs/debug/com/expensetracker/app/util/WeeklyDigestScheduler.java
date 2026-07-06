package com.expensetracker.app.util;

/**
 * Schedules [WeeklyDigestWorker] to fire every Sunday at 20:00 local time.
 *
 * Uses KEEP so a pre-existing identical schedule isn't cancelled and rescheduled every
 * app launch — the work is only enqueued if it doesn't already exist.
 *
 * Call [schedule] once from Application.onCreate (or whenever notifications are enabled).
 * Call [cancel] if the user opts out of weekly digests.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0002\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u000e\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\bJ\b\u0010\t\u001a\u00020\nH\u0002J\u000e\u0010\u000b\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\bR\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\f"}, d2 = {"Lcom/expensetracker/app/util/WeeklyDigestScheduler;", "", "()V", "WORK_TAG", "", "cancel", "", "context", "Landroid/content/Context;", "initialDelayMs", "", "schedule", "app_debug"})
public final class WeeklyDigestScheduler {
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String WORK_TAG = "weekly_digest_work";
    @org.jetbrains.annotations.NotNull()
    public static final com.expensetracker.app.util.WeeklyDigestScheduler INSTANCE = null;
    
    private WeeklyDigestScheduler() {
        super();
    }
    
    /**
     * Target: next Sunday at 20:00.
     */
    private final long initialDelayMs() {
        return 0L;
    }
    
    public final void schedule(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
    }
    
    public final void cancel(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
    }
}