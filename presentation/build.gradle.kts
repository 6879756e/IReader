plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("org.jetbrains.compose")
    id("kotlinx-serialization")
    id("com.google.devtools.ksp")
}

kotlin {
    android {
        compilations {
            all {
                kotlinOptions.jvmTarget = ProjectConfig.androidJvmTarget.toString()
            }
        }
    }
    jvm("desktop") {
        compilations {
            all {
                kotlinOptions.jvmTarget = ProjectConfig.desktopJvmTarget.toString()
            }
        }
    }

    sourceSets {
         val commonMain by getting {
            dependencies {
                implementation(project(Modules.domain))
                implementation(project(Modules.coreApi))
                implementation(project(Modules.sourceApi))
                implementation(project(Modules.commonResources))

                api(compose.foundation)
                api(compose.runtime)
                api(compose.animation)
                api(compose.animationGraphics)
                api(compose.materialIconsExtended)
                api(compose.preview)
                api(compose.ui)
                api(compose.material)
                @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
                api(compose.material3)
                api(compose.materialIconsExtended)


                implementation(libs.voyager.navigator)
                implementation(libs.voyager.tab.navigator)
                implementation(libs.voyager.transitions)
                implementation(libs.voyager.kodein)
                api(libs.kodein.core)
                api(libs.kodein.compose)
                api(libs.imageLoader)

                api(accompanist.gosyer.flowLayout)
                api(accompanist.gosyer.pagerIndicators)
                api(accompanist.gosyer.pager)
            }
        }
        val jvmMain by creating {
            dependsOn(commonMain)
            dependencies {


            }
        }
        val androidMain by getting {
            dependsOn(jvmMain)
            dependencies {

                api(androidx.biometric)
//                api(libs.coil.core)
//                api(libs.coil.gif)
//                api(composeLib.compose.coil)
                api(libs.bundles.simplestorage)
                api("androidx.core:core-splashscreen:1.0.0")
                api(composeLib.compose.googlFonts)

                api(composeLib.compose.paging)
                api(composeLib.material3.windowsizeclass)
                api(composeLib.compose.navigation)

                api(composeLib.compose.lifecycle)
                api(composeLib.compose.ui.util)
                api(composeLib.compose.constraintlayout)
               // api(accompanist.flowlayout)
                api(accompanist.navAnimation)
               // api(accompanist.pagerIndicator)
                api(accompanist.systemUiController)
               // api(accompanist.pager)
                api(accompanist.permissions)
                api(accompanist.web)
                api(androidx.appCompat)
                api(androidx.media)
                api(libs.bundles.media3)
                api(androidx.emoji)
                api(androidx.work.runtime)



            }
        }
        val desktopMain by getting {
            kotlin.srcDir("./src/jvmMain/kotlin")
            dependencies {
                api(compose.desktop.currentOs)
                api("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.6.4")

                val lwjglVersion = "3.3.1"
                listOf("lwjgl", "lwjgl-nfd").forEach { lwjglDep ->
                    implementation("org.lwjgl:${lwjglDep}:${lwjglVersion}")
                    listOf(
                        "natives-windows", "natives-windows-x86", "natives-windows-arm64",
                        "natives-macos", "natives-macos-arm64",
                        "natives-linux", "natives-linux-arm64", "natives-linux-arm32"
                    ).forEach { native ->
                        runtimeOnly("org.lwjgl:${lwjglDep}:${lwjglVersion}:${native}")
                    }
                }
            }
        }
    }
}

android {
    namespace = "ireader.presentation"
    compileSdk = ProjectConfig.compileSdk
    defaultConfig {
        minSdk = ProjectConfig.minSdk
        targetSdk = ProjectConfig.targetSdk
    }
    compileOptions {
        sourceCompatibility = ProjectConfig.androidJvmTarget
        targetCompatibility = ProjectConfig.androidJvmTarget
    }
    lint {
        baseline = file("lint-baseline.xml")
    }
    androidComponents.onVariants { variant ->
        val name = variant.name
        sourceSets {
            getByName(name).kotlin.srcDir("${buildDir.absolutePath}/generated/ksp/${name}/kotlin")

        }
    }
}

dependencies {
//    debugImplementation(composeLib.compose.uiTooling)
//    testImplementation(test.bundles.common)
//    androidTestImplementation(test.bundles.common)
//    androidTestImplementation(composeLib.compose.uiTestManifest)
//    androidTestImplementation(composeLib.compose.testing)
//    androidTestImplementation(composeLib.compose.composeTooling)
//    detektPlugins("com.twitter.compose.rules:detekt:0.0.5")
}
