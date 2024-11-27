plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.cesoft.data"
    compileSdk = 34

    defaultConfig {
        minSdk = 30

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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
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

dependencies {
    /// Modules
    implementation(project(":domain"))

    /// Core
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // DI
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)

    /// Retrofit
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.logging.interceptor)

    /// Preferences
    implementation(libs.androidx.datastore.preferences)
    //implementation("androidx.security:security-crypto:1.0.0")
}