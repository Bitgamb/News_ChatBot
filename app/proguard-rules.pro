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
# Keep the classes in the roundedimageview library
-keep class com.makeramen.** { *; }

# Keep the classes in the play-services-ads library
-keep class com.google.android.gms.** { *; }

# Keep the classes in the androidx packages
-keep class androidx.appcompat.** { *; }
-keep class androidx.activity.** { *; }
-keep class com.google.android.material.** { *; }
-keep class androidx.constraintlayout.** { *; }
-keep class androidx.swiperefreshlayout.** { *; }

# Keep the classes in the firebase-auth and firebase-firestore libraries
-keep class com.google.firebase.auth.** { *; }
-keep class com.google.firebase.firestore.** { *; }

# Keep the classes in the retrofit and gson libraries
-keep class com.squareup.retrofit2.** { *; }
-keep class com.google.gson.** { *; }

# Keep the classes in the volley library
-keep class com.android.volley.** { *; }

# Keep the classes in the firebase-database library
-keep class com.google.firebase.database.** { *; }

# Keep the classes in the lottie library
-keep class com.airbnb.lottie.** { *; }

# Keep the classes in the picasso library
-keep class com.squareup.picasso.** { *; }

# Keep the classes in the prettytime library
-keep class org.ocpsoft.prettytime.** { *; }

# Keep the classes in the tensorflow-lite library
-keep class org.tensorflow.** { *; }

# Keep the classes in the razorpay library
-keep class com.razorpay.** { *; }

