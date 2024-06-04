plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
}

android {
    namespace = "com.ilmiddin1701.qrcodemaster"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.ilmiddin1701.qrcodemaster"
        minSdk = 24
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    viewBinding { enable = true }
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

    //noinspection UseTomlInstead
    implementation ("com.google.zxing:core:3.4.1")
    //noinspection UseTomlInstead
    implementation ("com.journeyapps:zxing-android-embedded:4.2.0")

    //noinspection UseTomlInstead
    implementation("com.itextpdf:itext7-core:7.1.17")
    //noinspection UseTomlInstead
    implementation("com.itextpdf:kernel:7.1.17")
    //noinspection UseTomlInstead
    implementation("com.itextpdf:layout:7.1.17")
    //noinspection UseTomlInstead
    implementation("com.itextpdf:io:7.1.17")
    //noinspection UseTomlInstead
    implementation("com.itextpdf:pdfa:7.1.17")
}