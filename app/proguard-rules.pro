# ── Room ──────────────────────────────────────────────────────────────────────
# Keep Room database class and all its members (generated Impl subclass must survive)
-keep class * extends androidx.room.RoomDatabase { *; }
# Keep all @Entity data classes with their fields and constructors intact so Room
# can read/write columns via reflection-generated code.
-keep @androidx.room.Entity class * { *; }
# Keep all @Dao interfaces so Room's generated _Impl classes compile and survive.
-keep @androidx.room.Dao interface * { *; }
-dontwarn androidx.room.paging.**

# ── Kotlin ────────────────────────────────────────────────────────────────────
# Kotlin metadata is required for reflection-based libraries (Room, Coroutines).
-keepattributes *Annotation*, Signature, InnerClasses, EnclosingMethod
-keep class kotlin.Metadata { *; }
# Kotlin coroutines — keep the DebugProbes used for structured concurrency.
-dontwarn kotlinx.coroutines.debug.*
-keep class kotlinx.coroutines.** { *; }
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}

# ── WorkManager ───────────────────────────────────────────────────────────────
-keep class * extends androidx.work.Worker { *; }
-keep class * extends androidx.work.ListenableWorker { *; }
-keep class * extends androidx.work.CoroutineWorker { *; }
-keepclassmembers class * extends androidx.work.ListenableWorker {
    public <init>(android.content.Context, androidx.work.WorkerParameters);
}

# ── AdMob / GMS ───────────────────────────────────────────────────────────────
# AdMob ships its own consumer ProGuard rules but the below avoids any gaps.
-keep class com.google.android.gms.** { *; }
-keep class com.google.ads.** { *; }
-dontwarn com.google.android.gms.**

# ── App model/entity classes ───────────────────────────────────────────────────
# Keeps all entity/repository/viewmodel classes in our package with their members.
-keep class com.expensetracker.app.data.** { *; }
-keep class com.expensetracker.app.util.** { *; }
-keep class com.expensetracker.app.viewmodel.** { *; }

# ── BroadcastReceivers & Application ──────────────────────────────────────────
# These are in the manifest so they should survive, but be explicit.
-keep class com.expensetracker.app.ExpenseApp { *; }
-keep class com.expensetracker.app.MainActivity { *; }
-keep class com.expensetracker.app.util.ReminderReceiver { *; }
-keep class com.expensetracker.app.util.ReminderWorker { *; }

# ── Google Drive / API client ──────────────────────────────────────────────────
# Keep all Google Drive and API client classes — they're accessed reflectively and
# via the HTTP transport layer; stripping them breaks uploads/downloads at runtime.
-keep class com.google.api.services.drive.** { *; }
-keep class com.google.api.client.** { *; }
-keep class com.google.api.client.googleapis.** { *; }
-keep class com.google.api.client.http.** { *; }
-keep class com.google.api.client.json.** { *; }
-keep class com.google.api.client.json.gson.** { *; }
-dontwarn com.google.api.client.**
-dontwarn com.google.apis.**

# ── Suppress warnings for optional dependencies ────────────────────────────────
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**

# ── Auto-generated missing_rules (Google API client HTTP transport) ────────────
-dontwarn javax.naming.InvalidNameException
-dontwarn javax.naming.NamingException
-dontwarn javax.naming.directory.Attribute
-dontwarn javax.naming.directory.Attributes
-dontwarn javax.naming.ldap.LdapName
-dontwarn javax.naming.ldap.Rdn
-dontwarn org.ietf.jgss.GSSContext
-dontwarn org.ietf.jgss.GSSCredential
-dontwarn org.ietf.jgss.GSSException
-dontwarn org.ietf.jgss.GSSManager
-dontwarn org.ietf.jgss.GSSName
-dontwarn org.ietf.jgss.Oid
