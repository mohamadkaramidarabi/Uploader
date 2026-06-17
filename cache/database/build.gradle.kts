@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
}

// Avoid Windows file locks on the default module-local classes.jar (IDE/antivirus indexing).
// Keep default build dir on non-Windows (CI/Linux) to avoid publishing/signing path issues.
if (System.getProperty("os.name").contains("Windows", ignoreCase = true)) {
    layout.buildDirectory.set(
        rootProject.layout.buildDirectory.dir("module-builds/cache-database"),
    )
}

room {
    schemaDirectory("$projectDir/schemas")
}

kotlin {
    android {
        namespace = "io.github.mohamadkaramidarabi.cache.database"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "uploader:cache:databaseKit"
        }
    }

    jvm()

    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.room.runtime)
                implementation(libs.sqlite.bundled)
                implementation(projects.cache.cacheApi)
                implementation(projects.common)
            }
        }
    }
}

dependencies {
    add("kspAndroid", libs.room.compiler)
    add("kspIosSimulatorArm64", libs.room.compiler)
    add("kspIosX64", libs.room.compiler)
    add("kspIosArm64", libs.room.compiler)
    add("kspJvm", libs.room.compiler)
}
