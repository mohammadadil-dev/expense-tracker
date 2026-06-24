package com.expensetracker.app.util;

/**
 * Best-effort, bank-agnostic parser for Indian-style debit/UPI/card transaction SMS.
 *
 * This deliberately does not target any single bank's exact template — the user said they get
 * SMS from "different bank, UPI, card etc.", so instead of hard-coding per-bank formats this
 * looks for a currency-prefixed amount plus a debit-ish verb anywhere in the message, and skips
 * anything that looks like an OTP, an incoming credit/refund/salary, a collect request, or a
 * marketing message. False positives/negatives are expected — every match still lands in a
 * review queue before it can become a real expense, so being wrong is cheap and being silent
 * about a real transaction is the worse failure mode.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000:\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\b\u0006\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0010\u0010\f\u001a\u00020\b2\u0006\u0010\r\u001a\u00020\bH\u0002J%\u0010\u000e\u001a\u0004\u0018\u00010\u000f2\b\u0010\u0010\u001a\u0004\u0018\u00010\b2\f\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00120\u0006\u00a2\u0006\u0002\u0010\u0013J\u0010\u0010\u0014\u001a\u0004\u0018\u00010\u00152\u0006\u0010\u0016\u001a\u00020\bR\u000e\u0010\u0003\u001a\u00020\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R \u0010\u0005\u001a\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\b0\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\t\u001a\b\u0012\u0004\u0012\u00020\b0\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\n\u001a\b\u0012\u0004\u0012\u00020\b0\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0017"}, d2 = {"Lcom/expensetracker/app/util/SmsExpenseParser;", "", "()V", "AMOUNT_REGEX", "Lkotlin/text/Regex;", "CATEGORY_KEYWORDS", "", "Lkotlin/Pair;", "", "DEBIT_KEYWORDS", "EXCLUDE_KEYWORDS", "MERCHANT_REGEX", "defaultDescription", "lower", "guessCategoryId", "", "categoryNameKeyGuess", "categories", "Lcom/expensetracker/app/data/CategoryEntity;", "(Ljava/lang/String;Ljava/util/List;)Ljava/lang/Long;", "parse", "Lcom/expensetracker/app/util/ParsedSmsTransaction;", "message", "app_debug"})
public final class SmsExpenseParser {
    @org.jetbrains.annotations.NotNull()
    private static final kotlin.text.Regex AMOUNT_REGEX = null;
    @org.jetbrains.annotations.NotNull()
    private static final kotlin.text.Regex MERCHANT_REGEX = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.List<java.lang.String> DEBIT_KEYWORDS = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.List<java.lang.String> EXCLUDE_KEYWORDS = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.List<kotlin.Pair<java.lang.String, java.lang.String>> CATEGORY_KEYWORDS = null;
    @org.jetbrains.annotations.NotNull()
    public static final com.expensetracker.app.util.SmsExpenseParser INSTANCE = null;
    
    private SmsExpenseParser() {
        super();
    }
    
    /**
     * Returns null if [message] doesn't look like a completed debit/UPI/card transaction.
     */
    @org.jetbrains.annotations.Nullable()
    public final com.expensetracker.app.util.ParsedSmsTransaction parse(@org.jetbrains.annotations.NotNull()
    java.lang.String message) {
        return null;
    }
    
    /**
     * Maps a parsed category guess (by nameKey) to a real category id, if that category exists.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Long guessCategoryId(@org.jetbrains.annotations.Nullable()
    java.lang.String categoryNameKeyGuess, @org.jetbrains.annotations.NotNull()
    java.util.List<com.expensetracker.app.data.CategoryEntity> categories) {
        return null;
    }
    
    private final java.lang.String defaultDescription(java.lang.String lower) {
        return null;
    }
}