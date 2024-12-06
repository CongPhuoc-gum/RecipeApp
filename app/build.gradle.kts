import shadow.bundletool.com.android.tools.r8.internal.v7

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.foodrecipeapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.foodrecipeapp"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    packaging {
        resources {
            excludes += "META-INF/DEPENDENCIES"
            excludes += "META-INF/LICENSE"
            excludes += "META-INF/LICENSE.txt"
            excludes += "META-INF/NOTICE"
            excludes += "META-INF/NOTICE.txt"
            excludes += "META-INF/ASL2.0"
        }
    }
}

dependencies {
    implementation("com.github.bumptech.glide:glide:4.15.1")
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.messaging)
    annotationProcessor("com.github.bumptech.glide:compiler:4.15.1") // Nếu sử dụng Kotlin

    implementation ("com.github.bumptech.glide:glide:4.15.1")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.15.1")

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.database)
    implementation(libs.firebase.storage)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    // Firebase UI Database
    implementation("com.firebaseui:firebase-ui-database:8.0.2")

    // Glide for image loading
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation("com.github.dhaval2404:imagepicker:2.1")

    implementation("com.google.guava:guava:32.0.1-android")
    implementation("com.google.auth:google-auth-library-oauth2-http:1.19.0")

    implementation ("com.android.volley:volley:1.2.1")


}