plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    id("com.vanniktech.maven.publish") version "0.35.0"
}

subprojects {
    if (project.name == "composeApp") return@subprojects
    group = "io.github.mohamadkaramidarabi"
    version = "0.0.8"
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
            url = "http://www.mobintadbir.ir/"
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
                url = "http://www.mobintadbir.ir/"
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