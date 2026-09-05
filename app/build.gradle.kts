// Top-level Gradle build file.
//
// Cross-compile order is driven by `build.sh` at the repo root; this file
// only describes the Android-side project so the user can `cd app &&
// ./gradlew assembleDebug` after `build.sh` has populated `app/app/src/main/jniLibs/`.

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
}
