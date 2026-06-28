# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.

# Hilt rules
-keep public class * extends android.app.Service
-keep public class * extends android.app.Application
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Fragment
-keep public class * extends android.app.backup.BackupAgent
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.Service

# Room rules
-keep class * extends androidx.room.RoomDatabase
-keep class com.nabil.ahmed.waterfilter.data.entity.** { *; }
-keep class com.nabil.ahmed.waterfilter.data.dao.** { *; }

# WorkManager
-keep class com.nabil.ahmed.waterfilter.worker.** { *; }

# DataStore / UserSettings
-keep class com.nabil.ahmed.waterfilter.data.repository.UserSettings { *; }

# Retrofit & Moshi (Proactive for when they are used)
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keepattributes RuntimeInvisibleAnnotations, RuntimeInvisibleParameterAnnotations
-keepclassmembers,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}
-keep class com.nabil.ahmed.waterfilter.data.model.** { *; }
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }

# Google Play Services
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.android.gms.**

# Keep generic signatures for reflection (important for Hilt and Room)
-keepattributes Signature

# Keep line numbers for better crash reports
-keepattributes SourceFile,LineNumberTable
