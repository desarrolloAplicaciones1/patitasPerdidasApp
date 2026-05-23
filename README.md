# Patitas Perdidas

App Android comunitaria para reportar mascotas perdidas o encontradas. Los usuarios pueden publicar avisos, consultar alertas activas, ver sus propios avisos y administrar sus mascotas.

**TP Integrador — Desarrollo de Aplicaciones I · UADE · 2026**  
Prof. Narducci Adrian Alberto

---

## Estado actual de la arquitectura

Se aplicó un refactor para acercar el proyecto a una implementación de **Clean Architecture + MVVM**.

### Cambios incorporados

- Se agregaron **interfaces de repositorio** en `domain/repository`.
- Se agregaron **use cases** en `domain/usecase`.
- Los `ViewModel` dejaron de depender de repositorios concretos y ahora dependen de **use cases**.
- Los repositorios de `data` ahora implementan los contratos definidos en `domain`.
- Se incorporó un `AppContainer` manual para construir dependencias sin tocar la UI ni `navigation`.
- Se agregó `PatitasPerdidasApplication` como punto de entrada de dependencias.

---

## Stack tecnológico

| Capa | Tecnología |
|---|---|
| Lenguaje | Kotlin |
| UI | Jetpack Compose + Material Design 3 |
| Arquitectura | Clean Architecture + MVVM |
| Estado | StateFlow / MutableStateFlow |
| Persistencia local | Room (SQLite) + KSP |
| Servicios remotos | Firebase Authentication + Firebase Firestore |
| Imágenes | Coil |
| Navegación | Navigation Compose |
| Corrutinas | Kotlin Coroutines |
| Inyección de dependencias | `AppContainer` manual (`Application` + Service Locator) |
| Dependencias preparadas | Retrofit + OkHttp |

---

## Arquitectura Clean Architecture + MVVM

```text
Composable (View)
  -> observa StateFlow y dispara eventos
ViewModel
  -> transforma eventos de UI en acciones
  -> expone UiState
UseCase
  -> representa una acción de negocio puntual
Repository (interface en domain)
  -> define el contrato
Repository (implementación en data)
  -> decide si usa Room, Firebase o ambas
DAO / DataSource
  -> acceso concreto a datos
Room / Firebase
```

### Regla de dependencias

```text
presentation -> domain
data -> domain
domain -> no depende de presentation ni data
```

### Reglas aplicadas

- Un `Composable` no contiene lógica de negocio.
- Un `ViewModel` no conoce Room ni Firebase directamente.
- Un `ViewModel` usa `use cases`, no implementaciones concretas de repositorio.
- Los contratos de repositorio viven en `domain`.
- Las implementaciones concretas viven en `data`.
- Los `DAO` usan `Flow<T>` para lecturas reactivas y `suspend fun` para escrituras.
- Los `ViewModel` exponen `StateFlow` con `UiState` basado en `sealed class`.
- `AppContainer` centraliza el armado de dependencias sin usar Hilt.

---

## Estructura de packages

```text
app/src/main/java/.../patitasperdidas/
|
|-- PatitasPerdidasApplication.kt        Punto de entrada de la app
|-- di/
|   `-- AppContainer.kt                  Construye repositorios y use cases
|
|-- data/
|   |-- local/
|   |   |-- AppDatabase.kt               Base Room
|   |   |-- converter/
|   |   |   `-- Converters.kt            Conversores de Room
|   |   |-- dao/
|   |   |   |-- AlertDao.kt
|   |   |   |-- PetDao.kt
|   |   |   `-- UserDao.kt
|   |   `-- entity/
|   |       |-- AlertEntity.kt
|   |       |-- PetEntity.kt
|   |       `-- UserEntity.kt
|   |
|   |-- mapper/
|   |   |-- AlertMapper.kt               AlertEntity <-> Alert
|   |   |-- PetMapper.kt                 PetEntity <-> Pet
|   |   `-- UserMapper.kt                UserEntity <-> User
|   |
|   |-- network/
|   |   |-- FirebaseAuthDataSource.kt    Login / register / logout
|   |   `-- FirestoreAlertDataSource.kt  CRUD remoto de alertas
|   |
|   `-- repository/
|       |-- AlertRepository.kt           Implementa domain.repository.AlertRepository
|       |-- PetRepository.kt             Implementa domain.repository.PetRepository
|       `-- UserRepository.kt            Implementa domain.repository.UserRepository
|
|-- domain/
|   |-- model/
|   |   |-- Alert.kt
|   |   |-- Pet.kt
|   |   |-- User.kt
|   |   `-- Enums.kt
|   |
|   |-- repository/
|   |   |-- AlertRepository.kt           Contrato
|   |   |-- PetRepository.kt             Contrato
|   |   `-- UserRepository.kt            Contrato
|   |
|   `-- usecase/
|       |-- alert/
|       |   |-- GetActiveAlertsUseCase.kt
|       |   |-- GetAlertByIdUseCase.kt
|       |   |-- GetMyAlertsUseCase.kt
|       |   |-- CreateAlertUseCase.kt
|       |   |-- UpdateAlertUseCase.kt
|       |   |-- ResolveAlertUseCase.kt
|       |   |-- DeleteAlertUseCase.kt
|       |   `-- SyncAlertsUseCase.kt
|       |-- auth/
|       |   |-- GetCurrentUserIdUseCase.kt
|       |   |-- IsLoggedInUseCase.kt
|       |   |-- LoginUseCase.kt
|       |   |-- LogoutUseCase.kt
|       |   `-- RegisterUserUseCase.kt
|       |-- pet/
|       |   |-- GetMyPetsUseCase.kt
|       |   |-- GetPetByIdUseCase.kt
|       |   |-- SavePetUseCase.kt
|       |   |-- UpdatePetUseCase.kt
|       |   `-- DeletePetUseCase.kt
|       `-- user/
|           |-- GetCurrentUserUseCase.kt
|           `-- UpdateUserProfileUseCase.kt
|
|-- presentation/
|   |-- auth/
|   |-- create/
|   |-- detail/
|   |-- home/
|   |-- onboarding/
|   |-- profile/
|   `-- splash/
|
|-- navigation/
|   |-- Screen.kt
|   `-- NavGraph.kt
|
`-- ui/theme/
    |-- Color.kt
    |-- Theme.kt
    `-- Type.kt
```

---

## Responsabilidad por capa

### `domain`

Contiene la lógica de negocio independiente de frameworks.

- `model/`: modelos puros del negocio.
- `repository/`: contratos que necesita el dominio.
- `usecase/`: acciones concretas de la aplicación.

### `data`

Resuelve de dónde salen los datos y cómo se persisten.

- `local/`: Room, entidades, DAOs y converters.
- `network/`: Firebase Auth y Firestore.
- `mapper/`: traducción entre entidades locales y modelos de dominio.
- `repository/`: implementaciones concretas de los contratos de dominio.

### `presentation`

Contiene estado de UI y coordinación de pantalla.

- `Screen`: renderiza la UI.
- `ViewModel`: usa `use cases`, emite `UiState` y procesa eventos.

### `di`

Centraliza la construcción manual de dependencias.

- `PatitasPerdidasApplication`: expone el container.
- `AppContainer`: crea repositorios y use cases para los `ViewModel`.

---

## Flujo de una funcionalidad

Ejemplo: crear un aviso.

```text
CreateAlertScreen
  -> CreateAlertViewModel.submitAlert()
  -> CreateAlertUseCase
  -> AlertRepository (interface)
  -> data.repository.AlertRepository
  -> AlertDao + FirestoreAlertDataSource
  -> Room / Firestore
```

---

## Modelo de datos

```text
USER
  uid        String  PK
  name       String
  email      String
  phone      String?
  avatarUrl  String?
  createdAt  Long

PET
  id          String  PK
  ownerId     String  FK -> USER.uid
  name        String
  petType     String  "DOG" | "CAT" | "OTHER"
  breed       String?
  color       String?
  description String?
  photoUrls   String
  microchipId String?
  createdAt   Long

ALERT
  id           String  PK
  ownerId      String  FK -> USER.uid
  petId        String? FK -> PET.id
  type         String  "LOST" | "FOUND"
  status       String  "ACTIVE" | "RESOLVED"
  petName      String
  petType      String
  breed        String?
  color        String?
  description  String
  photoUrls    String
  latitude     Double
  longitude    Double
  address      String?
  contactPhone String?
  createdAt    Long
  updatedAt    Long
  pendingSync  Boolean
```

### Decisiones de modelado

- `Alert` denormaliza datos de mascota para que el aviso siga siendo autónomo.
- `petId` es nullable porque un aviso perdido puede publicarse aunque la mascota no esté registrada en la app.
- `pendingSync` permite sostener la estrategia offline-first de alertas.

---

## Lógica por ViewModel

| ViewModel | Responsabilidad actual |
|---|---|
| `SplashViewModel` | Usa `IsLoggedInUseCase` para decidir navegación inicial. |
| `LoginViewModel` | Usa `LoginUseCase` y expone `AuthUiState`. |
| `RegisterViewModel` | Usa `RegisterUserUseCase` para registrar y guardar perfil. |
| `HomeViewModel` | Usa `GetActiveAlertsUseCase` y combina alertas con filtros de UI. |
| `CreateAlertViewModel` | Usa `GetCurrentUserIdUseCase` y `CreateAlertUseCase`. |
| `AlertDetailViewModel` | Usa `GetAlertByIdUseCase`, `ResolveAlertUseCase` y `DeleteAlertUseCase`. |
| `ProfileViewModel` | Usa `GetCurrentUserUseCase` y `LogoutUseCase`. |
| `MyAlertsViewModel` | Usa `GetMyAlertsUseCase`. |
| `MyPetsViewModel` | Usa `GetMyPetsUseCase` y `DeletePetUseCase`. |

---

## Estrategia offline en alertas

1. Se guarda primero en Room con `pendingSync = true`.
2. Luego se intenta sincronizar con Firestore.
3. Si la sincronización remota sale bien, se actualiza Room con `pendingSync = false`.
4. Si falla, el dato sigue disponible localmente.
5. `SyncAlertsUseCase` delega en `AlertRepository.syncFromFirestore()` para resincronizar.

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
4. Habilitar **Authentication** -> Email/Password.
5. Crear la colección `alerts` en **Firestore**.
6. Crear un bucket en **Firebase Storage**.

### 3. Abrir en Android Studio

Abrir la carpeta raíz. Android Studio sincroniza Gradle automáticamente.

> Si aparece un error de versión KSP, revisar `gradle/libs.versions.toml` y ajustar la versión sugerida por el IDE o por la documentación oficial del plugin.

### 4. Ejecutar

Conectar un dispositivo o iniciar un emulador (API 24+) y presionar **Run**.

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
- Refactor a Clean Architecture con interfaces y use cases
- Sincronización offline de alertas
- Unit tests de ViewModel y use cases
- UI tests con Compose Testing
- Firebase Analytics
- Release APK firmado
