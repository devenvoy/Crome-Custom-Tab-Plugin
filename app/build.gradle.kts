plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-kapt")

    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.demo.hybrid"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.demo.hybrid"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }

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
    buildFeatures{
        viewBinding = true
        compose = true
    }

//    composeCompiler {
//        enableStrongSkippingMode = true
//    }
}


dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation("androidx.activity:activity-compose:1.10.1")
    implementation(platform(libs.compose.bom))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material:material")
    implementation("androidx.compose.ui:ui-tooling")
    implementation("androidx.compose.runtime:runtime")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation ("androidx.browser:browser:1.8.0")
    implementation(project(":customtabs"))
    implementation(project(":fbAdsCommons"))
//    implementation ("com.saurabharora.customtabs:customtabs:1.1")


}