# ADR-001: Migration YouTube → MP3 Autohosted

## Status
✅ **Accepted** (Implémenté et testé)

## Context

Carnet de Chants utilisait initialement YouTube pour héberger les accompagnements musicaux des 268 cantiques.

### Problèmes avec YouTube:
- 🔴 Dépendance API YouTube fragile
- 🔴 Limitations de quota API
- 🔴 Risque de suppression vidéos
- 🔴 Offline impossible
- 🔴 Qualité audio variable

## Decision

Migrer vers **MP3 autohosted** sur serveur propre.

### Détails implémentation:

**Structure MP3**:
```
cdn.example.com/hymns/
├── 001-hymn.mp3
├── 002-hymn.mp3
└── 268-hymn.mp3
```

**Migration données**:
```kotlin
// Avant
"audioUrl": "https://www.youtube.com/watch?v=dQw4w9WgXcQ"

// Après
"audioUrl": "https://cdn.example.com/hymns/001-hymn.mp3"
```

**Conversion batch**:
```bash
# Script Python pour convertir tous les MP3s
for file in *.wav; do
    ffmpeg -i "$file" -ab 128k "${file%.wav}.mp3"
done
```

## Consequences

### ✅ Avantages
- Mode offline total après premier chargement
- Contrôle qualité audio
- Pas de limitations API
- Coût serveur prévisible (~5€/mois)

### ⚠️ Challenges
- Gestion bande passante (+200 MB initial)
- Maintenance infrastructure CDN
- Besoin HTTPS obligatoire

### 📊 Comparaison

| Critère | YouTube | MP3 Autohosted |
|---------|---------|----------------|
| Offline | ❌ | ✅ |
| Qualité contrôlée | ❌ | ✅ |
| Dépendance externe | Oui | Non |
| Coût | Gratuit (API) | €5/mois |
| Risque vidéo supprimée | Élevé | Nul |

## Implementation

**Repository pattern**:
```kotlin
class HymnRepository @Inject constructor(
    private val dao: HymnDao
) : HymnDataSource {
    
    suspend fun loadHymn(id: Int): Hymn {
        // Repository abstrait la source
        // On peut changer URL MP3 sans impacter UI
        return dao.getById(id)
            ?.toDomain()
            ?: throw HymnNotFoundException(id)
    }
}
```

L'abstraction Repository permet de changer les URLs sans refonte app.

## Follow-up

- [ ] Caching HTTP avec OkHttp interceptors
- [ ] Analytics: suivi hits MP3
- [ ] A/B test: CDN vs S3

---

**Date**: Mars 2026  
**Author**: Equipe Carnet de Chants  
**Related**: [ADR-002: Handler Threading](adr-002-handler-threading.md)
