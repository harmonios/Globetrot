plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "ca.uwaterloo.team_102_7"
    compileSdk = 34

    defaultConfig {
        applicationId = "ca.uwaterloo.team_102_7"
        minSdk = 34
        targetSdk = 34
        versionCode = 1
        versionName = "0.40"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    lint {
        baseline = file("lint-baseline.xml")
        disable.add("MutableCollectionMutableState")
        disable.add("AutoboxingStateCreation")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        isCoreLibraryDesugaringEnabled = true
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            //excludes += "/META-INF/{AL2.0,LGPL2.1}"
            //excludes += "/META-INF/native-image/org.mongodb/bson/native-image.properties"
            //excludes += "/META-INF/native-image/org.mongodb/mongodb-driver-core/native-image.properties"
            //excludes += "/META-INF/native-image/native-image.properties"
            //excludes += "/META-INF/native-image/org.mongodb/bson/**"
            //excludes += "/META-INF/native-image/org.mongodb/mongodb-driver-core/**"
            //excludes += "/META-INF/native-image/**"

            excludes += "/META-INF/**"
        }
    }
}

dependencies {

    implementation(libs.androidx.ui.tooling.data.android)
    implementation(libs.androidx.foundation.layout.android)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.runtime.livedata)
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:1.2.0")


    testImplementation("io.mockk:mockk:1.13.8")


    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.junit.jupiter)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation(platform("androidx.compose:compose-bom:2023.08.00"))
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui-tooling-preview:1.5.0") // Required for the Preview annotation
    debugImplementation("androidx.compose.ui:ui-tooling:1.5.0") // Required for preview tooling in debug builds

    // Android Maps Compose composables for the Maps SDK for Android
    implementation("com.google.maps.android:maps-compose:6.1.0")
    implementation ("com.google.android.gms:play-services-location:21.0.1")

    // Android Places API
    implementation ("com.google.android.libraries.places:places:2.6.0")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.6.1'")
    implementation("com.google.android.gms:play-services-maps:18.0.2")


    // Android compose icons
    implementation ("androidx.compose.material:material-icons-extended:1.5.0")

    // Android compose material3
    implementation ("androidx.compose.material3:material3:1.0.0")

    // Testing
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.9.0")

    androidTestImplementation("io.mockk:mockk-android:1.13.8")
    androidTestImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test:core:1.5.0")
    androidTestImplementation("androidx.test:runner:1.5.2")
    androidTestImplementation("androidx.test:rules:1.5.0")
    androidTestImplementation("androidx.arch.core:core-testing:2.2.0")

    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")

    // Supabase
    implementation(platform("io.github.jan-tennert.supabase:bom:3.0.0"))
    implementation("io.github.jan-tennert.supabase:postgrest-kt")
    implementation("io.github.jan-tennert.supabase:auth-kt")
    implementation("io.ktor:ktor-client-android:3.0.0-rc-1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")
    implementation("io.github.jan-tennert.supabase:supabase-kt:0.8.3")

    // Google SSO
    implementation("androidx.credentials:credentials:1.3.0")
    implementation("com.google.android.libraries.identity.googleid:googleid:1.1.1")

    implementation("io.github.cdimascio:dotenv-kotlin:6.4.2")

    // testing
    implementation ("androidx.compose.ui:ui:1.0.0")
    implementation ("androidx.compose.material:material:1.0.0")
    implementation ("androidx.compose.ui:ui-tooling:1.0.0")
    implementation ("androidx.activity:activity-compose:1.3.0")

    // Checklist
    implementation("androidx.datastore:datastore-preferences:1.0.0")
}