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
            baseName = "uploader:commonKit"
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

}
