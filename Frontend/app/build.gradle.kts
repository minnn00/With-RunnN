import java.util.Properties

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(localPropertiesFile.inputStream())
}

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)

    id("org.jetbrains.kotlin.kapt")
    id("kotlin-parcelize")
    id("androidx.navigation.safeargs.kotlin") version "2.7.7"
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin") version "2.0.1"
}

android {
    namespace = "com.with_runn"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.with_runn"
        minSdk = 24
        targetSdk = 35

        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "TMAP_API_KEY", "\"${localProperties.getProperty("TMAP_API_KEY") ?: ""}\"")
        manifestPlaceholders["tmapApiKey"] = localProperties.getProperty("TMAP_API_KEY") ?: ""

        buildConfigField("String", "NAVER_CLIENT_ID", "\"${localProperties.getProperty("NAVER_CLIENT_ID") ?: ""}\"")
        manifestPlaceholders["naverClientId"] = localProperties.getProperty("NAVER_CLIENT_ID") ?: ""

        buildConfigField("String", "NAVER_CLIENT_SECRET", "\"${localProperties.getProperty("NAVER_CLIENT_SECRET") ?: ""}\"")
        manifestPlaceholders["naverClientSecret"] = localProperties.getProperty("NAVER_CLIENT_SECRET") ?: ""

        buildConfigField("String", "GOOGLE_MAP_API_KEY", "\"${localProperties.getProperty("GOOGLE_MAP_API_KEY") ?: ""}\"")
        manifestPlaceholders["googleMapApiKey"] = localProperties.getProperty("GOOGLE_MAP_API_KEY") ?: ""

        buildConfigField("String", "PET_API_KEY", "\"${localProperties.getProperty("PET_API_KEY") ?: ""}\"")
        manifestPlaceholders["petApiKey"] = localProperties.getProperty("PET_API_KEY") ?: ""
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

    buildFeatures {
        viewBinding = true
        dataBinding = true
        buildConfig = true
    }

}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    implementation(libs.navigation.fragment.ktx)
    implementation(libs.navigation.ui.ktx)

    implementation(libs.androidx.recyclerview)
    implementation(libs.navigation.fragment.ktx)
    implementation(libs.navigation.ui.ktx)
    implementation(libs.androidx.viewpager2)
    implementation(libs.speed.dial)
    implementation(libs.naver.maps)
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation("com.squareup.retrofit2:converter-scalars:2.9.0")
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)
    implementation(libs.gson)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.google.maps)
    implementation(libs.google.places)
    implementation(libs.play.services.location)
    implementation(libs.maps.utils)
    implementation(libs.security.crypto)
    implementation(libs.glide)
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)
    kapt(libs.glide.compiler)

    // WebSocket 관련 의존성 (안정적인 버전으로 교체)
    implementation("org.java-websocket:Java-WebSocket:1.5.5")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    
    // STOMP 웹소켓 라이브러리
    implementation("com.github.NaikSoftware:StompProtocolAndroid:1.6.6")
    
    // RxJava 의존성 (STOMP 라이브러리에서 필요)
    implementation("io.reactivex.rxjava2:rxjava:2.2.21")
    implementation("io.reactivex.rxjava2:rxandroid:2.1.1")

    // Glide for image loading
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")

    // T MAP 로컬 의존성
    implementation(files("libs/vsm-tmap-sdk-v2-android-1.7.45.aar"))
    implementation(files("libs/tmap-sdk-3.0.aar"))

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
