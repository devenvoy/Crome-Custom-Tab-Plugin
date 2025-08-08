plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.devansh.common.core"
    compileSdk = 35

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    api(libs.gson)
    api(libs.androidx.core.ktx)
    api(libs.androidx.appcompat)
    api(libs.androidx.material)

    implementation(libs.shimmer)
    implementation(libs.sdp.android)
    implementation(libs.ssp.android)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.cardview)

}