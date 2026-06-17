import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    js {
        browser()
        binaries.executable()
        useEsModules()
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        binaries.executable()
    }

    sourceSets {
        webMain.dependencies {
            implementation(projects.composeApp)
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
        }
    }
}

val indexedDbJs = rootProject.layout.projectDirectory.file(
    "api/src/webMain/kotlin/ir/sharif/drive/uploader/cache/indexeddb/indexedDb.mjs",
)
val webAppKotlinPackageDir = "${rootProject.name}-webApp/kotlin"

tasks.register<Copy>("copyIndexedDbForJs") {
    from(indexedDbJs)
    into(rootProject.layout.buildDirectory.dir("js/packages/$webAppKotlinPackageDir"))
}

tasks.register<Copy>("copyIndexedDbForWasm") {
    from(indexedDbJs)
    into(rootProject.layout.buildDirectory.dir("wasm/packages/$webAppKotlinPackageDir"))
}

listOf(
    "wasmJsDevelopmentExecutableCompileSync",
    "wasmJsProductionExecutableCompileSync",
).forEach { compileSyncTask ->
    tasks.matching { it.name == compileSyncTask }.configureEach {
        finalizedBy(tasks.named("copyIndexedDbForWasm"))
    }
}

listOf(
    "jsDevelopmentExecutableCompileSync",
    "jsProductionExecutableCompileSync",
).forEach { compileSyncTask ->
    tasks.matching { it.name == compileSyncTask }.configureEach {
        finalizedBy(tasks.named("copyIndexedDbForJs"))
    }
}

tasks.matching { it.name.endsWith("BrowserDevelopmentWebpack") || it.name.endsWith("BrowserProductionWebpack") }.configureEach {
    when {
        name.startsWith("js") -> dependsOn(tasks.named("copyIndexedDbForJs"))
        name.startsWith("wasmJs") -> dependsOn(tasks.named("copyIndexedDbForWasm"))
    }
}

tasks.matching { it.name.endsWith("BrowserDevelopmentRun") || it.name.endsWith("BrowserProductionRun") }.configureEach {
    when {
        name.startsWith("js") -> dependsOn(tasks.named("copyIndexedDbForJs"))
        name.startsWith("wasmJs") -> dependsOn(tasks.named("copyIndexedDbForWasm"))
    }
}
