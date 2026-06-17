plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidMultiplatformLibrary) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.kotlinJvm) apply false
    id("com.vanniktech.maven.publish") version "0.36.0"
}

subprojects {
    if (project.name in listOf("composeApp", "androidApp", "desktopApp", "webApp")) return@subprojects
    group = "io.github.mohamadkaramidarabi"
    version = findProperty("version")?.toString() ?: "0.1.8-SNAPSHOT"
    apply(plugin = "com.vanniktech.maven.publish")


    mavenPublishing {
        publishToMavenCentral()
        signAllPublications()
        coordinates(
            groupId = group.toString(),
            artifactId = "uploader-${project.name}",
            version = version.toString()
        )
        pom {
            name = "Uploader library"
            description = "An uploader for drive"
            inceptionYear = "2025"
            url = "https://github.com/mohamadkaramidarabi/uploader"
            licenses {
                license {
                    name = "The Apache License, Version 2.0"
                    url = "https://www.apache.org/licenses/LICENSE-2.0.txt"
                    distribution = "https://www.apache.org/licenses/LICENSE-2.0.txt"
                }
            }
            developers {
                developer {
                    id = "mohamadkaramidarabi"
                    name = "Mohamad Karami Darabi"
                    url = "https://github.com/mohamadkaramidarabi"
                }
            }
            scm {
                url = "https://github.com/mohamadkaramidarabi/uploader"
                connection = "scm:git:git://github.com/mohamadkaramidarabi/uploader.git"
                developerConnection = "scm:git:ssh://github.com/mohamadkaramidarabi/uploader.git"
            }
        }
    }
}

tasks.register("publishAllModules") {
    dependsOn(subprojects.mapNotNull {
        runCatching {
            it.tasks.named("publishAndReleaseToMavenCentral")
        }.getOrNull()
    })
}