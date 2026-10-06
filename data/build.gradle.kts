plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.cesoft.data"
    compileSdk = 36

    defaultConfig {
        minSdk = 36

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
    testOptions {
        // android.util.Log is called from production code; return defaults instead of throwing in JVM tests
        unitTests.isReturnDefaultValues = true
    }

    buildFeatures.buildConfig = true
    flavorDimensions += "deploy"
    productFlavors {
        create("devel") {
            dimension = "deploy"
            buildConfigField("String", "API_URL", "\"https://sedeaplicaciones.minetur.gob.es\"")
        }
        create("prod") {
            dimension = "deploy"
            buildConfigField("String", "API_URL", "\"https://sedeaplicaciones.minetur.gob.es\"")
        }
    }
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    /// Modules
    implementation(project(":domain"))

    /// Core
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockk)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // DI
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)

    /// Retrofit
    implementation(libs.retrofit)
    implementation(libs.okhttp)
    implementation(libs.converter.gson)
    implementation(libs.logging.interceptor)

    /// Preferences
    implementation(libs.androidx.datastore.preferences)
    //implementation("androidx.security:security-crypto:1.0.0")
}