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
# AppCompat
-keep class androidx.appcompat.** { *; }

# Activity
-keep class androidx.activity.** { *; }

# Material Design
-keep class com.google.android.material.** { *; }

# ConstraintLayout
-keep class androidx.constraintlayout.** { *; }

# Firebase Authentication
-keep class com.google.firebase.auth.** { *; }

# JUnit
-dontwarn org.junit.**
-dontwarn junit.**
-keep class org.junit.** { *; }
-keep class junit.** { *; }

# AndroidX Test
-keep class androidx.test.ext.junit.** { *; }

# Espresso
-keep class androidx.test.espresso.** { *; }
