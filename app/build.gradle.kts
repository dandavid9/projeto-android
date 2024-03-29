import com.android.build.api.dsl.Packaging

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.projeto"
    compileSdk = 34

    buildFeatures {
        buildConfig = true
    }
    buildTypes.configureEach {
        buildConfigField("String", "TOMTOM_API_KEY", "\"${property("tomtomApiKey")}\"")
    }

    packagingOptions {
        jniLibs.pickFirsts.add("lib/**/libc++_shared.so")
    }

    defaultConfig {
        applicationId = "com.example.projeto"
        minSdk = 21
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
    kotlinOptions {
        jvmTarget = "1.8"
    }

}

dependencies {

    val version = "0.43.0"
    implementation("com.tomtom.sdk.maps:map-display:$version")
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.camera:camera-core:1.3.1")
    implementation("androidx.exifinterface:exifinterface:1.3.7")
    implementation("androidx.camera:camera-lifecycle:1.3.1")
    implementation("com.google.mlkit:object-detection-common:18.0.0")
    implementation("com.google.mlkit:object-detection:17.0.0")
    implementation("com.google.firebase:firebase-storage-ktx:20.3.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation ("org.tensorflow:tensorflow-lite-task-vision:0.3.1")
    implementation ("com.github.bumptech.glide:glide:4.12.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.12.0")
    implementation("com.tomtom.sdk.maps:map-display:0.43.0")
    implementation("com.tomtom.sdk.location:provider-android:0.43.0")
    implementation("com.tomtom.sdk.search:search-online:0.43.0")
    implementation("com.tomtom.sdk.search:ui:0.43.0")
    implementation("androidx.recyclerview:recyclerview:1.3.2")

}