# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# R8 warning workaround for kotlinx.serialization library
# See: https://github.com/Kotlin/kotlinx.serialization/issues/1922
-dontwarn kotlinx.serialization.internal.CommonEnumSerializer

# R8 warning workaround for the suncalc library, which uses compile-only
# findbugs annotations that are not included in the final artifact.
-dontwarn edu.umd.cs.findbugs.annotations.**

# R8 warning workaround for kotlinx.serialization library
-keepnames class kotlinx.serialization.internal.*Serializer* {
    <init>(...);
}
