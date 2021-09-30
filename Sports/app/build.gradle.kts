plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("android.extensions")
    id("dagger.hilt.android.plugin")
    id("com.google.gms.google-services")

    id("kotlin-kapt")
    id("kotlin-android")

}

android {
    compileSdkVersion(30)
    buildToolsVersion = "30.0.3"

    defaultConfig {
        applicationId = "com.hfad.sports"
        minSdkVersion(23)
        targetSdkVersion(30)
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release"){
            isMinifyEnabled =  false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility  = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
    }

    buildFeatures {
        viewBinding = true
        dataBinding = true
    }



}

dependencies {
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    val kotlinVersion = rootProject.extra.get("kotlinVersion")

    implementation ("org.jetbrains.kotlin:kotlin-stdlib:${kotlinVersion}}")
    implementation ("androidx.core:core-ktx:1.3.2")
    implementation ("androidx.appcompat:appcompat:1.2.0")
    implementation ("com.google.android.material:material:1.3.0")
    implementation ("androidx.fragment:fragment-ktx:1.3.0")
    implementation ("androidx.coordinatorlayout:coordinatorlayout:1.1.0")
    implementation ("androidx.constraintlayout:constraintlayout:2.0.4")
    testImplementation ("junit:junit:4.13.1")
    androidTestImplementation ("androidx.test.ext:junit:1.1.2")
    androidTestImplementation ("androidx.test.espresso:espresso-core:3.3.0")

    // LeakCanary
    debugImplementation ("com.squareup.leakcanary:leakcanary-android:2.6")

    //Live data
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:2.3.1")


    // Retrofit and Gson Converter
    val retrofitVersion = "2.9.0"
    implementation ("com.squareup.retrofit2:retrofit:${retrofitVersion}")
    implementation ("com.squareup.retrofit2:converter-gson:${retrofitVersion}")

    // ViewModel
    val lifecycleVersion = "2.2.0"
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion")

    // Life Cycle
    implementation ("androidx.lifecycle:lifecycle-common-java8:$lifecycleVersion")


    // Kotlin coroutines
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.4.3")

    //Hilt/Dagger
    val hiltVersion = rootProject.extra.get("hiltVersion")
    implementation ("com.google.dagger:hilt-android:$hiltVersion")
    kapt ("com.google.dagger:hilt-android-compiler:$hiltVersion")
    implementation ("androidx.hilt:hilt-navigation-fragment:1.0.0-beta01")

    //Navigation Component
    val navVersion = "2.3.3"
    // Kotlin
    implementation ("androidx.navigation:navigation-fragment-ktx:$navVersion")
    implementation ("androidx.navigation:navigation-ui-ktx:$navVersion")

    //Data Store
    implementation ("androidx.datastore:datastore-preferences:1.0.0-beta02")

    // Google Play Services
    implementation ("com.google.android.gms:play-services-location:18.0.0")
    implementation ("com.google.android.gms:play-services-maps:17.0.1")

    //Google Places Service
    implementation ("com.google.android.libraries.places:places:2.4.0")
    // Room Library
    val roomVersion = "2.3.0"
    implementation("androidx.room:room-runtime:$roomVersion")
    kapt("androidx.room:room-compiler:$roomVersion")

    // optional - Kotlin Extensions and Coroutines support for Room
    implementation ("androidx.room:room-ktx:$roomVersion")


    // Paging Library - 3
    val pagingVersion = "3.0.0-beta02"
    implementation ("androidx.paging:paging-runtime:$pagingVersion")

    // Glide Image Library
    val glideVersion = "4.11.0"
    implementation("com.github.bumptech.glide:glide:$glideVersion")

    // Firebase Messaging Service
    implementation("com.google.firebase:firebase-messaging:21.1.0")


}


kapt {
    correctErrorTypes = true
}