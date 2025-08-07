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
//    implementation (fileTree(dir: 'libs', include: ['*.jar']))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation("androidx.compose.material:material:1.8.3")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.6.2")

    implementation("com.google.android.play:app-update:2.1.0")
    implementation("com.google.android.play:app-update-ktx:2.1.0")

    implementation("com.google.android.ump:user-messaging-platform:3.2.0")

    //sdp
    implementation(libs.sdp.android)
    //sdp

    //ads
    implementation("com.google.android.gms:play-services-ads:24.5.0")

    //gson
    implementation(libs.gson)
    //gson

    //lifecycle
    implementation("androidx.lifecycle:lifecycle-process:2.9.2")
    //lifecycle

    //compose
    implementation(libs.androidx.activity.compose)
    //compose

    //simmer layout
    implementation(libs.shimmer)
    // Skds
    implementation(libs.audience.network.sdk)
    implementation("com.unity3d.ads:unity-ads:4.16.0")
    // Mediation Adopter
    implementation("com.google.ads.mediation:facebook:6.20.0.0")
    implementation("com.google.ads.mediation:unity:4.16.0.0")
    implementation(libs.androidx.cardview)
}
