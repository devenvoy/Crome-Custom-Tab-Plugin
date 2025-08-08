plugins {
    id("com.android.library")
    id("com.google.gms.google-services")
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
}
android {
    namespace = "com.devansh.commons"
    compileSdk = 35

    defaultConfig { minSdk = 24 }

    buildTypes {
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
    }
    buildFeatures {
        viewBinding = true
        compose = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.6.2")
    implementation("com.google.android.gms:play-services-ads:24.5.0")

    implementation(libs.gson)

    api(project(":common:googleAds"))
    api(project(":common:fbAds"))
    api(project(":common:core"))

}
