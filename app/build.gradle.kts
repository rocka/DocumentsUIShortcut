import com.android.build.gradle.tasks.PackageApplication
import org.gradle.api.internal.provider.AbstractProperty
import org.gradle.api.internal.provider.Providers
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("com.android.application")
    kotlin("android")
}

val packageName = "rocka.documentsuishortcut"

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_1_8
    }
}

android {
    namespace = packageName
    compileSdk = 35
    buildToolsVersion = "35.0.0"

    defaultConfig {
        applicationId = packageName
        minSdk = 26
        targetSdk = 35
        versionCode = 10
        versionName = "0.1.0"
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        all {
            // remove META-INF/version-control-info.textproto
            @Suppress("UnstableApiUsage")
            vcsInfo.include = false
        }
    }

    androidResources {
        @Suppress("UnstableApiUsage")
        generateLocaleConfig = true
    }

    dependenciesInfo {
        includeInApk = false
        includeInBundle = false
    }

    packaging {
        resources {
            excludes += setOf(
                "/META-INF/*.version",
                "/META-INF/androidx/**",
                "/kotlin/**",
                "/kotlin-tooling-metadata.json"
            )
        }
    }
}

// remove META-INF/com/android/build/gradle/app-metadata.properties
tasks.withType<PackageApplication> {
    val valueField =
        AbstractProperty::class.java.declaredFields.find { it.name == "value" } ?: run {
            println("class AbstractProperty field value not found, something could have gone wrong")
            return@withType
        }
    valueField.isAccessible = true
    doFirst {
        valueField.set(appMetadata, Providers.notDefined<RegularFile>())
        allInputFilesWithNameOnlyPathSensitivity.removeAll { true }
    }
}

dependencies {
}
