@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlinMultiplatform)
}

kotlin {
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "uploader:apiKit"
        }
    }

    jvm()

    js(IR) {
        browser {
            binaries.executable()
        }
    }

    wasmJs {
        browser {
            binaries.executable()
        }
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.androidx.lifecycle.viewmodelCompose)
                implementation(projects.cache.cacheApi)
                implementation(libs.atomicfu)
                implementation(libs.uri.kmp)
                api(projects.common)
            }
        }

        jvmMain {
            dependencies {
                implementation(projects.cache.database)
            }
        }
    }

}
