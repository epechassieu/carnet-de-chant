# Setup - Dépendances

## libs.versions.toml (Version Catalog)

Carnet de Chants utilise le **Version Catalog** Gradle moderne pour centraliser toutes les versions.

```toml
[versions]
# Android & Kotlin
agp = "9.1.0"
kotlin = "2.3.0"
coreKtx = "1.17.0"
junit = "4.13.2"
junitVersion = "1.3.0"
espressoCore = "3.7.0"
lifecycleRuntimeKtx = "2.10.0"

# Compose
activityCompose = "1.12.2"
composeBom = "2025.12.01"
navigationCompose = "2.9.6"

# Room et Hilt
room = "2.8.4"
hilt = "2.57.2"
hiltNavigationCompose = "1.3.0"

# Serialization
kotlinxSerialization = "1.8.0"

# Ktor
ktor = "3.1.3"

# Ksp (Kotlin Symbol Processing - plus rapide que Kapt)
ksp = "2.3.4"

# Font
uiTextGoogleFonts = "1.6.0"
material3 = "1.4.0"

# Tests
mockk = "1.13.9"
kotlinxCoroutinesTest = "1.7.3"
turbine = "1.0.0"

# Audio
media3 = "1.9.2"
runtime = "1.10.4"
```

## build.gradle.kts (App)

```kotlin
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
    id("kotlinx-serialization")
}

android {
    compileSdk = 35
    
    defaultConfig {
        targetSdk = 35
        minSdk = 26
    }
    
    buildFeatures {
        compose = true
    }
    
    composeOptions {
        kotlinCompilerExtensionVersion = "2.3.0"
    }
}

dependencies {
    // Core Android
    implementation(libs.coreKtx)
    implementation(libs.lifecycleRuntimeKtx)
    
    // Jetpack Compose
    implementation(platform(libs.composeBom))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation(libs.activityCompose)
    implementation(libs.navigationCompose)
    
    // Room Database
    implementation(libs.room)
    ksp(libs.room.compiler)
    implementation(libs.room.ktx)
    
    // Hilt Dependency Injection
    implementation(libs.hilt)
    ksp(libs.hilt.compiler)
    implementation(libs.hiltNavigationCompose)
    
    // Ktor Client HTTP
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.android)
    
    // Kotlin Serialization
    implementation(libs.kotlinxSerialization)
    
    // Audio (Media3 - moderne, remplace MediaPlayer)
    implementation(libs.media3)
    
    // Material Design 3
    implementation(libs.material3)
    implementation(libs.uiTextGoogleFonts)
    
    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinxCoroutinesTest)
    testImplementation(libs.turbine)
    
    androidTestImplementation(libs.junitVersion)
    androidTestImplementation(libs.espressoCore)
    androidTestImplementation(platform(libs.composeBom))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
}
```

## Tableau des dépendances

| Dépendance | Version | Rôle | Importance |
|-----------|---------|------|-----------|
| Kotlin | 2.3.0 | Langage | 🔴 Core |
| Compose BOM | 2025.12.01 | UI Framework | 🔴 Core |
| Room | 2.8.4 | Database ORM | 🔴 Core |
| Hilt | 2.57.2 | DI Container | 🔴 Core |
| Ktor | 3.1.3 | HTTP Client | 🟡 Réseau |
| Media3 | 1.9.2 | Audio playback | 🟡 Features |
| Navigation Compose | 2.9.6 | Routing | 🟡 Navigation |
| KSP | 2.3.4 | Code generation | 🟢 Build |
| Material3 | 1.4.0 | Design System | 🟢 UI |

## Avantages du Version Catalog

✅ **Centralisé**: Toutes les versions au même endroit (`libs.versions.toml`)
✅ **Type-safe**: Vérification à la compilation
✅ **Maintenable**: Un changement = une ligne
✅ **Moderne**: Standard Gradle depuis 7.0+

### Exemple: Mettre à jour Kotlin

Avant (ancien style):
```kotlin
// build.gradle.kts
implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.10")
implementation("org.jetbrains.kotlin:kotlin-reflect:1.9.10")
// ... répéter partout
```

Après (Version Catalog):
```toml
# libs.versions.toml
kotlin = "2.3.0"
```

Puis dans `build.gradle.kts`:
```kotlin
implementation(libs.kotlin)
```

## Kotlin Symbol Processing (KSP)

Au lieu de **Kapt** (plus lent):
```kotlin
id("kotlin-kapt")
kapt("com.google.dagger:hilt-compiler:2.57.2")
```

Tu utilises **KSP** (plus rapide):
```kotlin
id("com.google.devtools.ksp")
ksp(libs.hilt.compiler)
```

**Gain**: Compilation ~2x plus rapide! ⚡

## Media3 pour Audio

Carnet de Chants utilise **Media3** au lieu de `MediaPlayer`:
- ✅ API moderne et stable
- ✅ Gestion automatique du threading
- ✅ Support offline avancé
- ✅ Recommandé par Google

Voir [Gestion audio](../architecture/audio.md) pour implémentation.

---

**Documentation officielle**:
- [Gradle Version Catalog](https://docs.gradle.org/current/userguide/platforms.html)
- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Media3](https://developer.android.com/guide/topics/media/media3)
- [Room](https://developer.android.com/training/data-storage/room)
- [Hilt](https://developer.android.com/training/dependency-injection/hilt-android)
- [KSP](https://kotlinlang.org/docs/ksp-overview.html)

**Next**: [Retour à Architecture](../architecture/vue-ensemble.md)
