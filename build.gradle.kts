plugins {
    kotlin("js") version "1.4.30"
}

repositories {
    jcenter()
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.2")
    testImplementation(kotlin("test-js"))
}

kotlin {
    js(LEGACY) {
        browser {
            binaries.executable()
            webpackTask { cssSupport.enabled = true }
            runTask { cssSupport.enabled = true }
            testTask {
                useKarma {
                    useChromeHeadless()
                    webpackConfig.cssSupport.enabled = true
                }
            }
        }
    }
}
