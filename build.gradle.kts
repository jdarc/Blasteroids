plugins {
    id("org.jetbrains.kotlin.js") version "1.5.0-RC"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.3")
}

kotlin {
    js(IR) {
        binaries.executable()
        browser {
            commonWebpackConfig {
                cssSupport.enabled = true
                outputFileName = "game.js"
                sourceMaps = false
            }
        }
    }
}
