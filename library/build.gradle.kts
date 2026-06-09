plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    `maven-publish`
}

android {
    namespace = "dev.eddev.compospresso"
    compileSdk = 34

    defaultConfig {
        minSdk = 24
        consumerProguardFiles("consumer-rules.pro")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }
}

dependencies {
    api(libs.androidx.appcompat)
    api(libs.androidx.coordinatorlayout)
    api(libs.google.material)
    api(libs.androidx.espresso.core)
    api(libs.androidx.espresso.intents)
    api(libs.androidx.uiautomator)
    api(platform(libs.androidx.compose.bom))
    api(libs.androidx.compose.ui.test.junit4)
    implementation(libs.androidx.test.core)
}

publishing {
    publications {
        register<MavenPublication>("release") {
            groupId = "dev.eddev"
            artifactId = "compospresso"
            version = "0.1.0"
            afterEvaluate {
                from(components["release"])
            }
        }
    }
}
