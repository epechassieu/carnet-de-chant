# Architecture - Vue d'ensemble

## Stack technique

```mermaid
graph TB;
    UI["Composable UI<br/>(Jetpack Compose)"];
    VM["ViewModel<br/>(StateFlow / UiState)"];
    UC["UseCase<br/>(logique métier)"];
    SR["SongRepository"];
    AR["AudioPlayerRepository"];
    SETR["SettingsRepository"];
    DAO["Room DAO<br/>(SongDao)"];
    REMOTE["Ktor Client<br/>(SongApiService)"];
    PLAYER["Media3 ExoPlayer"];
    DS["DataStore<br/>(Preferences)"];
    DB[("Room Database<br/>(SQLite)")];
    API["🌐 chants.epechassieu.fr<br/>(chants.json + mp3)"];

    UI -->|observe l'état| VM;
    VM -->|appelle| UC;
    VM -->|contrôle la lecture| AR;
    VM -->|lit/écrit| SETR;
    UC --> SR;
    SR -->|local| DAO;
    SR -->|import distant| REMOTE;
    AR --> PLAYER;
    DAO <-->|lecture/écriture| DB;
    REMOTE -->|HTTP| API;
    PLAYER -->|streaming| API;
    SETR --> DS;

    style UI fill:#e1f5ff;
    style VM fill:#f3e5f5;
    style UC fill:#fce4ec;
    style SR fill:#ede7f6;
    style AR fill:#ede7f6;
    style SETR fill:#ede7f6;
    style DB fill:#fff3e0;
    style API fill:#e8f5e9;
    style DS fill:#fff9c4;
```

- **SongRepository** : chants (Room en local, JSON distant pour l'import).
- **AudioPlayerRepository** : pilote Media3 ExoPlayer pour la lecture des mp3.
- **SettingsRepository** : préférences utilisateur (ex. taille du texte), persistées via DataStore.