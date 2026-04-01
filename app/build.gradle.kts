plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    id("kotlin-parcelize")
}

android {
    namespace = "com.cesoft.cesgas"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.cesoft.cesgas"
        minSdk = 36
        targetSdk = 36
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
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    //kotlinOptions { jvmTarget = "11" }
    buildFeatures {
        compose = true
    }

    buildFeatures.buildConfig = true
    flavorDimensions += "deploy"
    productFlavors {
        create("devel") {
            dimension = "deploy"
            //buildConfigField("String", "API_URL", "\"https://sedeaplicaciones.minetur.gob.es\"")
        }
        create("prod") {
            dimension = "deploy"
            //buildConfigField("String", "API_URL", "\"https://sedeaplicaciones.minetur.gob.es\"")
        }
    }
}

dependencies {
    /// Modules
    implementation(project(":data"))
    implementation(project(":domain"))

    /// Core
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.camera.camera2.pipe)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // DI
    implementation(libs.hilt.android)
    implementation (libs.androidx.hilt.navigation.compose)
    ksp(libs.hilt.android.compiler)

    /// VMI
//    implementation(libs.mvi)
//    implementation(libs.mvi.compose)
    /// MVI : Slack Circuit
    implementation("com.slack.circuit:circuit-foundation:0.31.0")//TODO: Si actualizas a 0.33.1 tendras que actulizar todo lo demas...
    implementation("com.slack.circuit:circuit-runtime:0.31.0")
    implementation("com.slack.circuit:circuit-overlay:0.31.0")

    /// Navigation
    implementation(libs.androidx.navigation.compose)

    /// MAPS
    implementation(libs.osmdroid.android)
}