plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.empiretycoon.idleconquest"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.empiretycoon.idleconquest"
        minSdk = 23
        targetSdk = 35
        versionCode = 1
        versionName = "0.1.0"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    sourceSets["main"].assets.srcDir(layout.buildDirectory.dir("generated/artAssets"))
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    testImplementation("junit:junit:4.13.2")
}

val syncArtAssets by tasks.registering(Copy::class) {
    from(rootProject.layout.projectDirectory.dir("assets/art"))
    into(layout.buildDirectory.dir("generated/artAssets/art"))
}

tasks.named("preBuild").configure {
    dependsOn(syncArtAssets)
}
