plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.langoverlay.prototype.keyspike"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.langoverlay.prototype.keyspike"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0-spike"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
}
