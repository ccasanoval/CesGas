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
    buildFeatures {
        compose = true
    }
    testOptions {
        // Compose runtime (presenter tests) calls android.os.Trace; return defaults instead of throwing
        unitTests.isReturnDefaultValues = true
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

kotlin {
    jvmToolchain(21)
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
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockk)
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
    implementation(libs.circuit.foundation)//TODO: Si actualizas a 0.33.1 tendras que actulizar todo lo demas...
    implementation(libs.circuit.runtime)
    implementation(libs.circuit.overlay)
    testImplementation(libs.circuit.test)

    /// Navigation
    implementation(libs.androidx.navigation.compose)

    /// MAPS
    implementation(libs.osmdroid.android)
}