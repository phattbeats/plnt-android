plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.plnt.client"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.plnt.client"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "0.1.0"
    }

    // One fixed signing key for every CI build, so a new APK installs over
    // the last one instead of Android refusing it ("app cannot be installed")
    // because each runner made up its own debug key. CI decodes the key from
    // repo secrets into PLNT_KEYSTORE_FILE; local builds without it keep the
    // normal per-machine debug key.
    val sharedKeystore = System.getenv("PLNT_KEYSTORE_FILE")?.let(::file)?.takeIf { it.exists() }
    signingConfigs {
        if (sharedKeystore != null) {
            create("shared") {
                storeFile = sharedKeystore
                storePassword = System.getenv("PLNT_KEYSTORE_PASSWORD")
                keyAlias = "plnt"
                keyPassword = System.getenv("PLNT_KEYSTORE_PASSWORD")
            }
        }
    }

    buildTypes {
        debug {
            if (sharedKeystore != null) signingConfig = signingConfigs.getByName("shared")
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
        // TopAppBar and friends are still @ExperimentalMaterial3Api in the
        // Material3 version this project pins.
        freeCompilerArgs += "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api"
    }
    buildFeatures {
        compose = true
    }
    packaging {
        // The .so files cargo-ndk produces are placed here by build.sh —
        // no Gradle dependency, just a directory layout.
        jniLibs {
            useLegacyPackaging = false
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    debugImplementation(libs.androidx.compose.ui.tooling)

    // #3078: identity store, bookmarks/settings store, media-button PTT.
    implementation(libs.androidx.security.crypto)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.media)

    // PHA-3289: screen-share viewer (org.webrtc).
    implementation(libs.webrtc)

    // PHA-4108: QR encoding for one-tap invite links.
    implementation(libs.zxing.core)

    testImplementation(libs.junit)

    // plnt-core generated Kotlin bindings + JNA-compatible loader.
    // The generated file is written to src/main/kotlin/com/plnt/client/plnt by
    // `cargo run --bin uniffi-bindgen generate src/plnt_core.udl --language kotlin`
    // invoked from build.sh.
    // UniFFI's generated bindings load the native lib via JNA; Android needs the
    // @aar artifact (bundles the JNA native dispatch libs), not the plain jar.
    implementation("net.java.dev.jna:jna:5.14.0@aar")
}
