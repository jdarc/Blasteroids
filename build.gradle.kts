plugins {
    kotlin("js") version "1.5.0-M2"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.2")
}

kotlin {
    js(IR) {
        browser {
            webpackTask {
                cssSupport.enabled = true
                outputFileName = "game.js"
                sourceMaps = false
            }
            runTask {
                cssSupport.enabled = true
                outputFileName = "game.js"
                sourceMaps = false
            }
            binaries.executable()
        }
    }
}
