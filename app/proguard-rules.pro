# Add project specific ProGuard rules here.

# Keep data models
-keep class com.msmonaym.land.model.** { *; }
-keep class com.msmonaym.land.ui.deed.** { *; }
-keep class com.msmonaym.land.ui.survey.** { *; }

# Keep AndroidX & Compose rules
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod

# Keep WebView JavaScript interfaces
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

# Keep Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

# Avoid warning for optional libraries
-dontwarn java.lang.invoke.**
-dontwarn javax.annotation.**
