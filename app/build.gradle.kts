plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")


}

android {
    namespace = "com.example.holly"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.holly"
        minSdk = 29
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures{viewBinding = true}
}

dependencies {
    //Navigation
    implementation (libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    //Reciclerview
    implementation(libs.androidx.recyclerview)

    //BOOM FIREBASE
    implementation(platform(libs.firebase.bom))



    //FIREBASE CASHLYTUCS

    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.analytics)

    //FIREBASE AUTH
    implementation(libs.firebase.ui.auth)

    //FIREBASE DATABASE
    implementation (libs.firebase.database)

    //FIREBASE STORAGE
    implementation(libs.firebase.storage)

    //Colli
    implementation(libs.coil)

    //Auth GOOGLE
    implementation(libs.play.services.auth)

    //Corrutinas
    implementation(libs.androidx.lifecycle.runtime.ktx)

    //GOOGLE CREDENCIALES
    implementation( libs.androidx.credentials)

    // Hilt main dependency
    implementation(libs.hilt.android)
    implementation(libs.androidx.navigation.fragment)
    implementation(libs.firebase.firestore.ktx)



    kapt(libs.hilt.android.compiler)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}