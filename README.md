# Patitas Perdidas

App Android comunitaria para reportar mascotas perdidas o encontradas. Los usuarios publican avisos con foto y ubicación, filtran por radio cercano y marcan un aviso como resuelto cuando la mascota aparece.

**TP Integrador — Desarrollo de Aplicaciones I · UADE · 2026**
Prof. Narducci Adrian Alberto

---

## Stack tecnológico

| Capa | Tecnología |
|---|---|
| Lenguaje | Kotlin |
| UI | Jetpack Compose + Material Design 3 |
| Arquitectura | MVVM + Repository |
| Estado | StateFlow / MutableStateFlow |
| Listas | LazyColumn |
| Imágenes | Coil (AsyncImage) |
| Persistencia local | Room (SQLite) + KSP |
| Red | Retrofit + GsonConverterFactory |
| Auth | Firebase Authentication |
| Base de datos nube | Firebase Firestore |
| Almacenamiento fotos | Firebase Storage |
| Navegación | NavHost / NavController (Compose Navigation) |
| Inyección de dependencias | Service Locator (companion object Singleton) |

---

## Arquitectura MVVM + Repository

```
View (Composable)
  │  llama función del ViewModel (callback / método)
  ▼
ViewModel
  │  viewModelScope.launch { }
  │  expone StateFlow<UiState>
  ▼
Repository
  │  decide fuente de datos
  ├──► Room (local / offline)
  └──► Firestore / Firebase Auth (red)
       │
       └──► View observa StateFlow con collectAsStateWithLifecycle()
```

### Reglas del patrón (según clase)

- Un Composable **nunca** tiene lógica de negocio. Solo renderiza estado.
- El ViewModel es el único que llama al Repository.
- `Flow<T>` en el DAO para lecturas reactivas (Room notifica automáticamente).
- `suspend fun` en el DAO para escrituras puntuales.
- `StateFlow` en el ViewModel, `collectAsStateWithLifecycle()` en la View.
- `sealed class` para UiState (Loading / Success / Error).
- `data class` con `copy()` para actualizaciones parciales del estado.
- Service Locator con `companion object` como Singleton (sin Hilt).

---

## Estructura de packages

```
app/src/main/java/.../patitasperdidas/
│
├── data/
│   ├── local/
│   │   ├── AppDatabase.kt              Singleton Room (companion object)
│   │   ├── dao/
│   │   │   ├── AlertDao.kt             Flow lecturas / suspend escrituras
│   │   │   ├── PetDao.kt
│   │   │   └── UserDao.kt
│   │   ├── entity/
│   │   │   ├── AlertEntity.kt          Tabla alerts (pendingSync para offline)
│   │   │   ├── PetEntity.kt            Tabla pets
│   │   │   └── UserEntity.kt           Tabla users
│   │   └── converter/
│   │       └── Converters.kt           List<String> ↔ String (separado por "|")
│   │
│   ├── network/
│   │   ├── FirebaseAuthDataSource.kt   register / login / logout
│   │   └── FirestoreAlertDataSource.kt CRUD colección "alerts"
│   │
│   ├── repository/
│   │   ├── AlertRepository.kt          offline-first: Room + Firestore
│   │   ├── PetRepository.kt            CRUD mascotas
│   │   └── UserRepository.kt           auth + perfil
│   │
│   └── mapper/
│       ├── AlertMapper.kt              AlertEntity ↔ Alert (toDomain / toEntity)
│       ├── PetMapper.kt
│       └── UserMapper.kt
│
├── domain/
│   └── model/
│       ├── Alert.kt                    Aggregate Root con Location embebida
│       ├── Pet.kt
│       ├── User.kt
│       └── Enums.kt                    AlertType, AlertStatus, PetType
│
├── presentation/
│   ├── splash/
│   │   ├── SplashScreen.kt
│   │   └── SplashViewModel.kt          verifica sesión activa → navega
│   ├── onboarding/
│   │   └── OnboardingScreen.kt
│   ├── auth/
│   │   ├── LoginScreen.kt
│   │   ├── LoginViewModel.kt
│   │   ├── RegisterScreen.kt
│   │   ├── RegisterViewModel.kt
│   │   └── AuthUiState.kt
│   ├── home/
│   │   ├── HomeScreen.kt               bottom nav: Home / Create / Profile
│   │   ├── HomeViewModel.kt            lista activa + filtro tipo/radio
│   │   └── HomeUiState.kt
│   ├── create/
│   │   ├── CreateAlertScreen.kt
│   │   ├── CreateAlertViewModel.kt     formulario + submit
│   │   └── CreateAlertUiState.kt
│   ├── detail/
│   │   ├── AlertDetailScreen.kt
│   │   ├── AlertDetailViewModel.kt     resolver / eliminar aviso
│   │   └── AlertDetailUiState.kt
│   ├── profile/
│   │   ├── ProfileScreen.kt
│   │   ├── ProfileViewModel.kt
│   │   ├── MyAlertsScreen.kt
│   │   ├── MyAlertsViewModel.kt
│   │   ├── MyPetsScreen.kt
│   │   ├── MyPetsViewModel.kt
│   │   └── ProfileUiState.kt           ProfileUiState + MyAlertsUiState + MyPetsUiState
│   └── components/                     Composables reutilizables (pendiente)
│
└── navigation/
    ├── Screen.kt                       sealed class con todas las rutas
    └── NavGraph.kt                     NavHost con todas las destinations
```

---

## Modelo de datos (DER)

```
USER
  uid        String  PK  (viene de FirebaseAuth, no se genera con UUID propio)
  name       String
  email      String
  phone      String?
  avatarUrl  String?
  createdAt  Long

PET
  id          String  PK
  ownerId     String  FK → USER.uid
  name        String
  petType     String  "DOG" | "CAT" | "OTHER"
  breed       String?
  color       String?
  description String?
  photoUrls   String  TypeConverter → List<String> separado por "|"
  microchipId String?
  createdAt   Long

ALERT
  id           String  PK
  ownerId      String  FK → USER.uid
  petId        String? FK → PET.id  nullable: FOUND siempre null
  type         String  "LOST" | "FOUND"
  status       String  "ACTIVE" | "RESOLVED"
  petName      String  datos de mascota denormalizados (aviso autónomo)
  petType      String
  breed        String?
  color        String?
  description  String
  photoUrls    String  TypeConverter → List<String>
  latitude     Double  Location embebida como columnas planas
  longitude    Double
  address      String?
  contactPhone String?
  createdAt    Long
  updatedAt    Long
  pendingSync  Boolean true = pendiente de sync con Firestore
```

**Por qué Alert denormaliza datos de mascota:** el aviso es un Aggregate Root autónomo. Si el dueño borra su mascota, los avisos históricos no se rompen. FOUND nunca tiene `petId` porque la mascota encontrada es ajena.

**Por qué `petId` es nullable en LOST:** el dueño puede reportar una mascota no registrada en la app.

---

## Flujo de navegación

```
Splash
  ├── (sin sesión) → Onboarding → Login → Register
  └── (con sesión) → Home
                       └── bottom nav
                             ├── HomeTab       lista avisos activos + filtro
                             │     └── AlertDetail  (resolver / editar / eliminar)
                             ├── CreateTab     publicar aviso (LOST o FOUND)
                             └── ProfileTab    perfil del usuario
                                   ├── MyAlerts → AlertDetail
                                   └── MyPets   → AddPet / EditPet
```

---

## Lógica por ViewModel

| ViewModel | Responsabilidad |
|---|---|
| `SplashViewModel` | Verifica `FirebaseAuth.currentUser`. Navega a Home si hay sesión, a Onboarding si no. |
| `LoginViewModel` | `userRepository.login()` → emite `AuthUiState.Success(uid)` o `Error`. |
| `RegisterViewModel` | `userRepository.register()` + guarda perfil en Room. |
| `HomeViewModel` | Observa `alertRepository.getActiveAlerts()` como `Flow`. Aplica filtros de tipo y radio en memoria. |
| `CreateAlertViewModel` | Mantiene `CreateAlertFormState`. Al submit construye `Alert` con UUID + timestamp y llama `alertRepository.saveAlert()`. |
| `AlertDetailViewModel` | Carga alerta por ID. Expone `resolveAlert()` y `deleteAlert()` que delegan al repository. |
| `ProfileViewModel` | Observa `userRepository.getUser(uid)`. `logout()` delega al repository. |
| `MyAlertsViewModel` | Observa `alertRepository.getMyAlerts(uid)`. |
| `MyPetsViewModel` | Observa `petRepository.getPetsByOwner(uid)`. `deletePet()` delega al repository. |

---

## Estrategia offline (pendingSync)

1. Al guardar/editar/eliminar → se escribe en Room con `pendingSync = true`.
2. Se intenta sincronizar con Firestore.
3. Si hay red → Firestore actualiza → `pendingSync = false` en Room.
4. Si no hay red → el registro queda local con `pendingSync = true`.
5. Al reconectar → `syncFromFirestore()` en `AlertRepository` procesa la cola.

---

## Setup del proyecto

### 1. Clonar

```bash
git clone https://github.com/desarrolloAplicaciones1/patitasPerdidasApp.git
cd patitasPerdidasApp
```

### 2. Configurar Firebase

1. Crear un proyecto en [Firebase Console](https://console.firebase.google.com/).
2. Registrar la app Android con el package `com.desarrolloaplicaciones1.patitasperdidas`.
3. Descargar `google-services.json` y copiarlo en `app/`.
4. Habilitar **Authentication** → Email/Password.
5. Crear la colección `alerts` en **Firestore**.
6. Crear un bucket en **Firebase Storage**.

### 3. Abrir en Android Studio

Abrir la carpeta raíz. Android Studio sincroniza Gradle automáticamente.

> Si aparece un error de versión KSP, actualizar `ksp` en `gradle/libs.versions.toml` al valor sugerido por el IDE.

### 4. Ejecutar

Conectar dispositivo o iniciar emulador (API 24+) y presionar **Run**.

---

## Historias de usuario

### Primera entrega

| ID | Historia |
|---|---|
| HU-01 | Onboarding inicial (primera instalación) |
| HU-02 | Registro con email y contraseña |
| HU-03 | Login con cuenta existente |
| HU-04 | Ver listado de avisos cercanos |
| HU-05 | Publicar aviso con foto de mascota |
| HU-06 | Editar aviso propio |
| HU-07 | Marcar aviso como resuelto |
| HU-08 | Eliminar aviso propio |
| HU-09 | Filtrar por tipo de animal y radio (km) |
| HU-10 | Usar la app sin internet (modo offline) |
| HU-11 | Modo oscuro según configuración del sistema |

### Segunda entrega

- Integración real con Firestore
- Sincronización offline completa
- Unit tests: ViewModels (JUnit4 + Mockito)
- UI tests: Compose Testing
- Firebase Analytics
- Release APK firmado
