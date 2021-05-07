plugins {
    id("org.jetbrains.kotlin.js") version "1.5.0"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.3")
}

kotlin {
    js {
        browser {
            commonWebpackConfig {
                outputFileName = "game.js"
                cssSupport.enabled = true
            }
        }
        binaries.executable()
    }
}
