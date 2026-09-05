# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /opt/android-sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Keep our JNI bridge class — the UniFFI bindings use reflection on the
# generated loader and the native method names must survive shrinking.
-keep class com.plnt.client.plnt.** { *; }
-keepclassmembers class com.plnt.client.plnt.** { *; }
