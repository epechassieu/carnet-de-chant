# Carnet de Chants 📖

**Application Android de gestion de cantiques pour églises protestantes**

## Vue rapide

- **Langage**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: Clean Architecture + MVVM
- **Base de données**: Room
- **Injection dépendances**: Hilt
- **Audio**: MP3 autohosted
- **Nombre de cantiques**: 268+

## Objectif

Carnet de Chants est une application permettant aux églises protestantes utilisant le carent de chants de l'église EPECHASSIEU de:
- 📱 Consulter les cantiques sur mobile
- 🎵 Écouter l'accompagnement musical
- 🔍 Chercher par titre, numéro ou auteur
- 🔍 Filtrer par catégorie
- 👥 Partager entre membres

## Démarrage rapide

1. [Installation de l'environnement](setup/installation.md)
2. [Comprendre l'architecture](architecture/vue-ensemble.md)
3. [Explorer la base de données](architecture/database.md)

## Structure de la documentation

```
architecture/      → Explique le design technique
setup/             → Guide d'installation
decisions/         → Justification des choix (ADRs)
```

## Technologies clés

| Composant | Technologie | Raison |
|-----------|-------------|--------|
| UI | Jetpack Compose | Moderne, réactive, déclarative |
| Persistance | Room | ORM Android natif |
| DI | Hilt | Intégration Jetpack, moins de boilerplate |
| Audio | MP3 autohosted | Plus stable que YouTube, offline |
| Réseau | Ktor | Client HTTP léger |

## Dernières modifications

- ✅ Migration YouTube → MP3 autohosted
- ✅ Résolution des bugs audio (Handler threading)
- ✅ Support offline complet
- 🔄 Préparation board meeting presentation

---

*Documentation générée avec MkDocs • Dernière mise à jour: 2026*
