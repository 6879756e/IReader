plugins {
    id("com.android.library")
    kotlin("android")
    id("kotlin-kapt")
    id("kotlin-parcelize")
    kotlin("plugin.serialization")
}

android {
    compileSdk = ProjectConfig.compileSdk

    defaultConfig {
        minSdk = ProjectConfig.minSdk
        targetSdk = ProjectConfig.targetSdk


        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro")
        }
    }
    kapt {
        arguments {
            arg("room.schemaLocation", "$projectDir/schemas")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = Compose.composeVersion
    }
}
dependencies {
    addBaseDependencies()
    implementation(project(Modules.source))
    implementation(project(Modules.core))
    implementation(project(Modules.domain))
    implementation(Room.roomRuntime)
    implementation(Compose.runtime)
    "kapt"(Room.roomCompiler)
    implementation(Room.roomKtx)
    implementation(Room.roomPaging)
    implementation(Moshi.moshi)
    implementation(Moshi.moshiKotlin)
    implementation(Jsoup.jsoup)
    implementation(OkHttp.okHttp3Interceptor)

    /** Coroutine **/
    implementation(Coroutines.core)
    implementation(Coroutines.android)

    /** Retrofit **/
    implementation(Retrofit.retrofit)
    implementation(Retrofit.moshiConverter)
    implementation(Testing.truth)

}
fun DependencyHandler.addBaseDependencies() {
    implementation(AndroidX.coreKtx)
    implementation(AndroidX.appCompat)
    implementation(AndroidX.webkit)
    implementation(AndroidX.browser)
    implementation(AndroidX.material)
    implementation(AndroidX.activity)

    implementation(Kotlin.jsonSerialization)


    kapt(DaggerHilt.hiltCompiler)
    implementation(DaggerHilt.hiltAndroid)
    implementation(DaggerHilt.hiltAndroidCompiler)
    implementation(Timber.timber)

    /** LifeCycle **/
    implementation(LifeCycle.runtimeKtx)
    implementation(LifeCycle.viewModel)


    testImplementation(Testing.junit4)
    testImplementation(Testing.junitAndroidExt)
    testImplementation(Testing.truth)
    testImplementation(Testing.coroutines)
    testImplementation(Testing.composeUiTest)


    androidTestImplementation(Testing.junit4)
    androidTestImplementation(Testing.junitAndroidExt)
    androidTestImplementation(Testing.truth)
    androidTestImplementation(Testing.coroutines)
    androidTestImplementation(Testing.composeUiTest)
    androidTestImplementation(Testing.hiltTesting)
    // Instrumented Unit Tests
    androidTestImplementation("androidx.arch.core:core-testing:2.1.0")
    androidTestImplementation("androidx.test.ext:junit:1.1.1")
    androidTestImplementation("org.mockito:mockito-core:2.21.0")
    implementation(kotlin("stdlib"))
    implementation(Ktor.core)
    implementation(Ktor.serialization)
    implementation(Ktor.okhttp)
    implementation(Ktor.ktor_jsoup)
}