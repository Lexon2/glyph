# --- Hilt / Dagger ---
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep @dagger.hilt.android.AndroidEntryPoint class * { *; }
-keep @dagger.hilt.android.HiltAndroidApp class * { *; }
-keepclasseswithmembers class * {
    @dagger.hilt.* <methods>;
}
-keepclasseswithmembers class * {
    @javax.inject.Inject <init>(...);
}

# --- Android components ---
-keep class com.langoverlay.app.LangOverlayApplication { *; }
-keep class com.langoverlay.app.MainActivity { *; }
-keep class com.langoverlay.app.receiver.** { *; }
-keep class com.langoverlay.detection.accessibility.LangOverlayAccessibilityService { *; }
-keep class * extends android.accessibilityservice.AccessibilityService { *; }

# --- Kotlin / coroutines ---
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes InnerClasses
-keepattributes EnclosingMethod
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}
-dontwarn kotlinx.coroutines.**

# --- Enum persistence (DataStore) ---
-keepclassmembers enum com.langoverlay.core.model.** {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# --- DataStore protobuf ---
-keepclassmembers class * extends androidx.datastore.preferences.protobuf.GeneratedMessageLite {
    <fields>;
}

# --- Compose (R8 handles most; keep line numbers for crash reports) ---
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# --- Strip debug logging in release builds ---
-assumenosideeffects class android.util.Log {
    public static int d(...);
    public static int v(...);
    public static int i(...);
}
