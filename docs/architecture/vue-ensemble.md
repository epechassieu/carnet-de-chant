# Architecture - Vue d'ensemble

## Stack technique
```mermaid
info

``


```mermaid
graph TB;
    UI["UI Layer<br/>(Jetpack Compose)"];
    VM["ViewModel Layer<br/>(State Management)"];
    REPO["Repository Layer<br/>(Data Abstraction)"];
    DAO["Room DAO<br/>(Database Access)"];
    REMOTE["Remote Service<br/>(Ktor Client)"];
    DB[(("Room Database<br/>(SQLite)"))];
    API["📱 MP3 Server"];
    
    UI -->|Observes State| VM
    VM -->|Requests Data| REPO
    REPO -->|Local| DAO
    REPO -->|Remote| REMOTE
    DAO <-->|Read/Write| DB
    REMOTE -->|Fetch Audio| API
    
    style UI fill:#e1f5ff
    style VM fill:#f3e5f5
    style REPO fill:#ede7f6
    style DB fill:#fff3e0
    style API fill:#e8f5e9
```