# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Keep all classes in android.os package to prevent hidden API issues
-keep class android.os.** { *; }

# Keep annotation attributes for Retrofit, Gson, or other libraries that rely on annotations
-keepattributes *Annotation*

# Prevent stripping methods required for JSON serialization/deserialization
-keep class com.example.findme.** { *; }

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Prevent obfuscation of Retrofit and Gson related classes
-keep class retrofit2.** { *; }
-keep class com.google.gson.** { *; }

# Ensure methods used in Retrofit callbacks are not stripped
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

# Keep classes that implement Retrofit interfaces
-keepclasseswithmembers class * {
    public <init>();
    public *;
}

# Additional rules for avoiding runtime issues with hidden APIs
-dontwarn android.os.**
-dontwarn dalvik.system.**