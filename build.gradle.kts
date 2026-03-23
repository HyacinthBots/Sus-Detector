import dev.kordex.gradle.plugins.kordex.DataCollection
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
    distribution

    alias(libs.plugins.kotlin)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.detekt)
    alias(libs.plugins.kord.extensions.plugin)
    alias(libs.plugins.kord.extensions.i18n)
    alias(libs.plugins.blossom)
    alias(libs.plugins.grgit)
}

group = "org.hyacinthbots.susdetector"
version = "1.1.1"

val className = "org.hyacinthbots.susdetector.SusDetectorKt"
val javaVersion = "21"

repositories {
    mavenCentral()

    maven {
        name = "Kord Extensions (Releases)"
        url = uri("https://releases-repo.kordex.dev")
    }

    maven {
        name = "Kord Extensions (Snapshots)"
        url = uri("https://snapshots-repo.kordex.dev")
    }

    maven {
        name = "Kord Snapshots"
        url = uri("https://repo.kordex.dev/snapshots")
    }

    maven {
        name = "Kord Mirror"
        url = uri("https://mirror-repo.kordex.dev")
    }
}

dependencies {
    detektPlugins(libs.detekt)

    implementation(libs.kord.extensions.core)
    implementation(libs.kord.extensions.unsafe)

    implementation(libs.kotlin.stdlib)

    // Logging dependencies
    implementation(libs.logback)
    implementation(libs.logging)

    implementation(libs.mongodb)
    implementation(libs.mongodb.driverkx)
    implementation(libs.bsonkx)
}

distributions {
    main {
        distributionBaseName = project.name

        contents {
            from("LICENSE")
            exclude("README.md")
        }
    }
}

kordEx {
    addDependencies = false
    addRepositories = false
    kordExVersion = libs.versions.kord.extensions
    ignoreIncompatibleKotlinVersion = true

    bot {
        dataCollection(DataCollection.None)
    }
}

i18n {
    bundle("susdetector.strings", "susdetector.i18n")
}

application {
    mainClass.set(className)
}

tasks {
    withType<KotlinCompile> {
        compilerOptions {
            jvmTarget.set(JvmTarget.fromTarget(javaVersion))
            languageVersion.set(
                KotlinVersion.fromVersion(
                    libs.plugins.kotlin.get().version.requiredVersion.substringBeforeLast(
                        "."
                    )
                )
            )
            incremental = true
            freeCompilerArgs.add("-opt-in=kotlin.RequiresOptIn")
        }
    }

    java {
        sourceCompatibility = JavaVersion.toVersion(javaVersion)
        targetCompatibility = JavaVersion.toVersion(javaVersion)
    }

    jar {
        manifest {
            attributes("Main-Class" to className)
        }
    }

    wrapper {
        distributionType = Wrapper.DistributionType.BIN
    }
}

detekt {
    buildUponDefaultConfig = true
    config.setFrom("$rootDir/detekt.yml")

    autoCorrect = true
}

sourceSets {
    main {
        blossom {
            kotlinSources {
                property("build_id", grgit.head().abbreviatedId)
                property("version", project.version.toString())
            }
        }
    }
}
