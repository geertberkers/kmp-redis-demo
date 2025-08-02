plugins {
    kotlin("multiplatform") version "1.8.21"
    id("org.jetbrains.compose") version "1.5.0"
}

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

kotlin {
    jvm("desktop") {
        compilations.all {
            kotlinOptions.jvmTarget = "17"
        }
        java {
            toolchain {
                languageVersion.set(JavaLanguageVersion.of(17))
            }
        }
        withJava()
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material)
            }
        }
        val desktopMain by getting {
            dependencies {
                implementation(kotlin("stdlib"))
                implementation(compose.desktop.currentOs)
                implementation("io.lettuce:lettuce-core:6.7.1.RELEASE")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactive:1.7.3") // 🔥 required for Lettuce coroutines
            }
        }
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"
    }
}
