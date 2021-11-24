plugins {
    id("com.android.library")
    id("kotlin-android")
    id("org.jetbrains.dokka")
    id("com.vanniktech.maven.publish")
}

android {
    compileSdk = 31

    defaultConfig {
        minSdk = 21
        targetSdk = 31

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFile("consumer-rules.pro")
    }

    buildTypes {
        buildTypes {
            named("release") {
                isMinifyEnabled = false
                proguardFiles("proguard-android-optimize.txt", "proguard-rules.pro")
            }
        }
    }
    compileOptions.apply {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation("androidx.paging:paging-runtime-ktx:3.1.0-beta01")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.5.2")
    implementation("app.cash.turbine:turbine:0.7.0")
    implementation("junit:junit:4.13.2")
}

tasks.dokkaHtml.configure {
    outputDirectory.set(buildDir.resolve("dokka"))
}