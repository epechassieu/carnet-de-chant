# Setup - Installation

## Prérequis

- ✅ **Android Studio** (dernière version)
- ✅ **JDK 11+**
- ✅ **Android SDK** (API 26+)
- ✅ **Git**

## 1. Cloner le repo

```bash
git clone https://github.com/epechassieu/carnet-de-chants.git
cd carnet-de-chants
```

## 2. Ouvrir dans Android Studio

```bash
# Option 1: Depuis terminal
open . -a "Android Studio"

# Option 2: Depuis Android Studio
File → Open → Sélectionner le dossier
```

## 3. Attendre la sync Gradle

Android Studio va auto-downloader:
- ✅ Dépendances Maven
- ✅ Kotlin compiler
- ✅ Android SDK tools

**Temps**: ~5 minutes (1ère fois)

## 4. Configurer AVD (Émulateur)

```
Tools → Device Manager → Create Device
→ Pixel 5 (API 31) → Next → Finish
```

## 5. Build & Run

```bash
# Terminal:
./gradlew build

# Ou depuis Android Studio:
Shift + F10 (Windows/Linux)
Cmd + R (Mac)
```

## Troubleshooting

### Build fails: "cannot find symbol"

```bash
# Solution: Rebuild
./gradlew clean
./gradlew build
```

### Émulateur lent

Activer KVM (Linux):
```bash
# Vérifier support
egrep -o 'vmx|svm' /proc/cpuinfo

# Si absent, l'émulateur sera lent mais fonctionnera
```

### Dépendances Hilt non trouvées

```bash
# Nettoyer cache Gradle
rm -rf ~/.gradle/caches
./gradlew clean build
```

## Vérifier installation

```bash
./gradlew tasks
# Devrait lister toutes les tâches disponibles
```

Si ✅ pas d'erreurs = Ready to code! 🎉

---

**Next**: [Dépendances](dependances.md)
