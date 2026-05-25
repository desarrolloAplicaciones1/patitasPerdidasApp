# Huellitas

App Android comunitaria para reportar mascotas perdidas o encontradas. Los usuarios pueden publicar avisos, consultar alertas activas, ver sus propios reportes, explorar un mapa interactivo y contactar directamente al dueño de una mascota.

**TP Integrador — Desarrollo de Aplicaciones I · UADE · 2026**  
Prof. Narducci Adrian Alberto

---

## Stack tecnológico

| Capa | Tecnología |
|---|---|
| Lenguaje | Kotlin |
| UI | Jetpack Compose + Material Design 3 |
| Arquitectura | Clean Architecture + MVVM |
| Estado | StateFlow / MutableStateFlow |
| Persistencia local | Room (SQLite) + KSP |
| Autenticación | Firebase Authentication (email/contraseña) |
| Base de datos remota | Cloud Firestore |
| Almacenamiento de imágenes | Firebase Storage |
| Carga de imágenes | Coil |
| Navegación | Navigation Compose |
| Corrutinas | Kotlin Coroutines |
| Inyección de dependencias | AppContainer manual (Application + Service Locator) |

---

## Arquitectura — Clean Architecture + MVVM

La app sigue los principios de Clean Architecture con separación estricta en tres capas. La regla de dependencias es:

```
presentation  →  domain  ←  data
```

El dominio no depende de Compose, Room ni Firebase. Los `ViewModel` usan `use cases`, nunca acceden a repositorios concretos directamente.

```
Composable (View)
  → observa StateFlow y dispara eventos
ViewModel
  → transforma eventos de UI en acciones
  → expone UiState (sealed class)
UseCase
  → representa una acción de negocio puntual
Repository (interface en domain)
  → define el contrato
Repository (implementación en data)
  → decide si usa Room, Firebase o ambas
DAO / DataSource
  → acceso concreto a datos (Room / Firestore)
```

### Reglas aplicadas

- Un `Composable` no contiene lógica de negocio.
- Un `ViewModel` no conoce Room ni Firebase directamente.
- Un `ViewModel` usa `use cases`, no implementaciones concretas de repositorio.
- Los contratos de repositorio viven en `domain/repository`.
- Las implementaciones concretas viven en `data/repository`.
- Los `DAO` usan `Flow<T>` para lecturas reactivas y `suspend fun` para escrituras.
- Los `ViewModel` exponen `StateFlow<UiState>` basado en `sealed class`.
- `AppContainer` centraliza el armado de dependencias sin usar Hilt.

---

## Estructura de packages

```
app/src/main/java/com/uade/huellitas/
│
├── PatitasPerdidasApplication.kt        Punto de entrada de la app
│
├── di/
│   └── AppContainer.kt                  Construye repositorios y use cases
│
├── data/
│   ├── local/
│   │   ├── AppDatabase.kt               Base Room
│   │   ├── converter/
│   │   │   └── Converters.kt
│   │   ├── dao/
│   │   │   ├── AlertDao.kt
│   │   │   ├── PetDao.kt
│   │   │   └── UserDao.kt
│   │   └── entity/
│   │       ├── AlertEntity.kt
│   │       ├── PetEntity.kt
│   │       └── UserEntity.kt
│   ├── mapper/
│   │   ├── AlertMapper.kt               AlertEntity <-> Alert
│   │   ├── PetMapper.kt                 PetEntity <-> Pet
│   │   └── UserMapper.kt                UserEntity <-> User
│   ├── network/
│   │   ├── FirebaseAuthDataSource.kt    Login / register / logout
│   │   └── FirestoreAlertDataSource.kt  CRUD remoto de alertas
│   └── repository/
│       ├── AlertRepository.kt           Implementa domain.repository.AlertRepository
│       ├── PetRepository.kt             Implementa domain.repository.PetRepository
│       └── UserRepository.kt            Implementa domain.repository.UserRepository
│
├── domain/
│   ├── model/
│   │   ├── Alert.kt
│   │   ├── Pet.kt
│   │   ├── User.kt
│   │   └── Enums.kt                     AlertType, PetType, AlertStatus
│   ├── repository/
│   │   ├── AlertRepository.kt           Contrato
│   │   ├── PetRepository.kt             Contrato
│   │   └── UserRepository.kt            Contrato
│   └── usecase/
│       ├── alert/
│       │   ├── GetActiveAlertsUseCase.kt
│       │   ├── GetAlertByIdUseCase.kt
│       │   ├── GetMyAlertsUseCase.kt
│       │   ├── CreateAlertUseCase.kt
│       │   ├── UpdateAlertUseCase.kt
│       │   ├── ResolveAlertUseCase.kt
│       │   ├── DeleteAlertUseCase.kt
│       │   └── SyncAlertsUseCase.kt
│       ├── auth/
│       │   ├── GetCurrentUserIdUseCase.kt
│       │   ├── IsLoggedInUseCase.kt
│       │   ├── LoginUseCase.kt
│       │   ├── LogoutUseCase.kt
│       │   └── RegisterUserUseCase.kt
│       ├── pet/
│       │   ├── GetMyPetsUseCase.kt
│       │   ├── GetPetByIdUseCase.kt
│       │   ├── SavePetUseCase.kt
│       │   ├── UpdatePetUseCase.kt
│       │   └── DeletePetUseCase.kt
│       └── user/
│           ├── GetCurrentUserUseCase.kt
│           └── UpdateUserProfileUseCase.kt
│
├── presentation/
│   ├── alert/
│   │   ├── create/                      CreateAlertScreen + ViewModel
│   │   ├── detail/                      AlertDetailScreen + ViewModel
│   │   └── express/                     ExpressAlertScreen + ViewModel
│   ├── auth/                            LoginScreen + RegisterScreen + ViewModels
│   ├── home/                            HomeScreen + ViewModel + FilterDialog
│   ├── map/                             MapScreen + ViewModel
│   ├── onboarding/                      OnboardingScreen + ViewModel
│   ├── profile/
│   │   ├── alerts/                      MyAlertsScreen
│   │   ├── edit/                        EditProfileScreen + ViewModel
│   │   └── pets/                        MyPetsScreen
│   └── splash/                          SplashScreen + ViewModel
│
├── navigation/
│   ├── Screen.kt
│   └── NavGraph.kt
│
└── ui/theme/
    ├── Color.kt
    ├── Theme.kt
    └── Type.kt
```

---

## Responsabilidad por capa

### `domain`
Contiene la lógica de negocio, completamente independiente de Android, Compose y Firebase.
- `model/`: modelos puros del negocio.
- `repository/`: contratos (interfaces) que necesita el dominio.
- `usecase/`: acciones concretas de la aplicación, una por responsabilidad.

### `data`
Resuelve de dónde salen los datos y cómo se persisten.
- `local/`: Room, entidades, DAOs y converters.
- `network/`: Firebase Auth y Firestore.
- `mapper/`: traducción entre entidades locales y modelos de dominio.
- `repository/`: implementaciones concretas de los contratos de dominio.

### `presentation`
Contiene estado de UI y coordinación de pantalla.
- Composables: renderizan la UI, no contienen lógica de negocio.
- ViewModels: usan `use cases`, emiten `UiState` basado en `sealed class`, procesan eventos de la UI.

### `di`
Centraliza la construcción manual de dependencias.
- `PatitasPerdidasApplication`: expone el container.
- `AppContainer`: instancia repositorios y use cases que luego consumen los ViewModels.

---

## Flujo de una funcionalidad

Ejemplo: publicar un aviso.

```
CreateAlertScreen
  → CreateAlertViewModel.submitAlert()
  → CreateAlertUseCase
  → AlertRepository (interface en domain)
  → data.repository.AlertRepository
  → AlertDao (Room)  +  FirestoreAlertDataSource (Firestore)
```

---

## Modelo de datos

```
USER
  uid        String   PK
  name       String
  email      String
  phone      String?
  avatarUrl  String?
  createdAt  Long

PET
  id          String   PK
  ownerId     String   FK → USER.uid
  name        String
  petType     String   "DOG" | "CAT" | "OTHER"
  breed       String?
  color       String?
  description String?
  photoUrls   String
  microchipId String?
  createdAt   Long

ALERT
  id           String   PK
  ownerId      String   FK → USER.uid
  petId        String?  FK → PET.id  (nullable)
  type         String   "LOST" | "FOUND"
  status       String   "ACTIVE" | "RESOLVED"
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

- `Alert` denormaliza datos de mascota para que el aviso sea autónomo y no requiera un join.
- `petId` es nullable porque un aviso puede publicarse aunque la mascota no esté registrada en la app.
- `pendingSync` sostiene la estrategia offline-first: permite saber qué alertas aún no se sincronizaron con Firestore.

---

## Lógica por ViewModel

| ViewModel | Responsabilidad |
|---|---|
| `SplashViewModel` | Usa `IsLoggedInUseCase` para decidir la navegación inicial. |
| `LoginViewModel` | Usa `LoginUseCase`, expone `AuthUiState`. |
| `RegisterViewModel` | Usa `RegisterUserUseCase` para crear cuenta y guardar perfil local. |
| `HomeViewModel` | Usa `GetActiveAlertsUseCase`, combina alertas con filtros de tipo, especie y radio. |
| `CreateAlertViewModel` | Usa `GetCurrentUserIdUseCase` y `CreateAlertUseCase`. |
| `ExpressAlertViewModel` | Crea un aviso rápido sin foto, solo con zona y tipo. |
| `AlertDetailViewModel` | Usa `GetAlertByIdUseCase`, `UpdateAlertUseCase`, `ResolveAlertUseCase` y `DeleteAlertUseCase`. |
| `MapViewModel` | Usa `GetActiveAlertsUseCase` para poblar el mapa con pines por zona y radio. |
| `ProfileViewModel` | Usa `GetCurrentUserUseCase`, `GetMyAlertsUseCase` y `LogoutUseCase`. |
| `EditProfileViewModel` | Usa `UpdateUserProfileUseCase` para persistir cambios de nombre y contacto. |
| `MyAlertsViewModel` | Usa `GetMyAlertsUseCase` filtrado por el usuario autenticado. |
| `MyPetsViewModel` | Usa `GetMyPetsUseCase` y `DeletePetUseCase`. |

---

## Estrategia offline en alertas

1. Se guarda primero en Room con `pendingSync = true`.
2. Luego se intenta sincronizar con Firestore.
3. Si la sincronización remota sale bien, se actualiza Room con `pendingSync = false`.
4. Si falla, el dato sigue disponible localmente y puede resincronizarse más tarde.
5. `SyncAlertsUseCase` delega en `AlertRepository.syncFromFirestore()` para recuperar el estado remoto.

---

## Historias de usuario

### Primera entrega (H1)

| ID | Historia | Estado |
|---|---|---|
| HU-01 | Como usuario, quiero ver un onboarding al abrir la app por primera vez para entender qué hace Huellitas. | ✅ |
| HU-02 | Como usuario, quiero registrarme con email y contraseña para crear mi cuenta. | ✅ |
| HU-03 | Como usuario, quiero iniciar sesión con mis credenciales para acceder a la app. | ✅ |
| HU-04 | Como usuario, quiero ver un listado de avisos cercanos en formato cards para explorar reportes activos. | ✅ |
| HU-05 | Como usuario, quiero publicar un aviso con foto, descripción y datos de contacto para reportar una mascota. | ✅ |
| HU-06 | Como usuario, quiero editar los datos de un aviso que publiqué para corregir información. | ✅ |
| HU-07 | Como usuario, quiero marcar un aviso como resuelto para indicar que la mascota fue encontrada. | ✅ |
| HU-08 | Como usuario, quiero eliminar un aviso propio que ya no es relevante. | ✅ |
| HU-09 | Como usuario, quiero filtrar el feed por tipo de animal, tipo de aviso y radio en km para ver solo lo que me interesa. | ✅ |
| HU-10 | Como usuario, quiero poder usar la app sin conexión y ver los avisos cacheados localmente. | ✅ |
| HU-11 | Como usuario, quiero que la app respete el modo oscuro de mi sistema operativo. | ✅ |
| HU-12 | Como usuario, quiero ver el detalle completo de un aviso (foto, descripción, especie, color, zona, contacto) para decidir si puedo ayudar. | ✅ |
| HU-13 | Como usuario, quiero contactar al dueño de una mascota directamente por WhatsApp desde el aviso. | ✅ |
| HU-14 | Como usuario, quiero ver los avisos de mi zona en un mapa para entender visualmente dónde están las mascotas reportadas. | ✅ |
| HU-15 | Como usuario, quiero publicar un aviso express sin foto, solo indicando la zona, cuando veo algo rápido. | ✅ |
| HU-18 | Como usuario, quiero cerrar sesión para proteger mi cuenta en dispositivos compartidos. | ✅ |
| HU-20 | Como usuario, quiero usar la galería de mi celular para adjuntar una foto al aviso que estoy publicando. | ✅ |

### Segunda entrega (H2)

| ID | Historia |
|---|---|
| HU-16 | Como usuario, quiero ver un listado de los avisos que yo publiqué para hacer seguimiento de mis reportes activos. |
| HU-17 | Como usuario, quiero editar mi nombre y datos de contacto en mi perfil. |
| HU-19 | Como usuario, quiero recibir un email para restablecer mi contraseña si la olvidé. |
| HU-21 | Como usuario, quiero ver los avisos filtrados por distancia GPS real desde mi ubicación actual. |
| HU-22 | Como usuario, quiero buscar avisos por nombre de mascota desde el feed. |
| HU-23 | Como usuario, quiero subir una foto de perfil para que otros usuarios puedan identificarme. |

---

## Casos de uso formales

### CU-01: Publicar aviso de mascota perdida o encontrada

**Actor principal:** Usuario autenticado.  
**Objetivo:** Permitir que el usuario publique un aviso comunitario con información descriptiva, foto, ubicación y datos de contacto.

**Precondiciones:** El usuario debe haber iniciado sesión. Si el aviso incluye foto, la app debe tener acceso a la galería. La base local debe estar disponible para registrar el aviso incluso sin conexión.

**Disparador:** El usuario selecciona "Reportar mascota" desde la navegación principal.

**Flujo principal:**
1. El usuario accede a la pantalla de creación desde la barra de navegación o el banner principal.
2. El sistema muestra el formulario de creación.
3. El usuario selecciona el tipo de aviso (Perdido / Encontrado).
4. El usuario completa los datos: nombre, especie, raza, color, descripción, barrio y teléfono de contacto.
5. El usuario adjunta una foto desde la galería.
6. El sistema valida que los campos obligatorios estén completos y con formato correcto.
7. El usuario confirma la publicación.
8. El sistema guarda el aviso localmente (Room) y lo sincroniza con Firestore.
9. La app muestra confirmación y regresa al feed.

**Flujos alternativos:**
1. Si faltan datos obligatorios, el sistema informa los errores sin perder la información cargada.
2. Si no hay conexión, el aviso se guarda localmente con `pendingSync = true` y se sincroniza al reconectar.
3. Si el usuario abandona el formulario, no se registra ningún aviso.

**Postcondiciones:** El aviso queda publicado y visible en el feed de otros usuarios.

**Criterios de aceptación:**
- Dado un usuario autenticado que completa el formulario correctamente, cuando confirma, el sistema debe crear el aviso y mostrarlo en el feed.
- Dado un formulario inválido, cuando el usuario intenta publicar, el sistema debe mostrar errores claros sin perder la información ya cargada.
- Dado un usuario sin conexión, cuando publica un aviso, el sistema debe guardarlo localmente y sincronizarlo automáticamente al recuperar la conexión.

---

### CU-02: Consultar feed de avisos

**Actor principal:** Usuario autenticado.  
**Objetivo:** Visualizar un listado de avisos activos filtrados por tipo de animal, tipo de aviso y radio de cercanía.

**Precondiciones:** El usuario debe tener sesión activa.

**Disparador:** El usuario ingresa a la app luego del Splash / Login.

**Flujo principal:**
1. El usuario ingresa a la pantalla de avisos.
2. El sistema carga y muestra los avisos activos en formato cards con foto, tipo, nombre y zona.
3. El usuario puede modificar los filtros de tipo de aviso, especie y radio en km.
4. El usuario selecciona un aviso para ver su detalle.

**Flujos alternativos:**
1. Si no hay conexión, el sistema muestra los avisos cacheados en Room.
2. Si no hay avisos que cumplan los filtros, el sistema muestra un estado vacío.

**Postcondiciones:** El usuario visualiza avisos activos, puede filtrar el feed y navegar al detalle.

**Criterios de aceptación:**
- Dado un usuario autenticado, cuando ingresa al feed, el sistema debe mostrar los avisos activos disponibles.
- Dado un usuario sin conexión, cuando ingresa al feed, el sistema debe mostrar los avisos cacheados localmente.
- Dado un filtro aplicado, cuando el usuario lo modifica, el feed debe actualizarse mostrando solo los avisos que cumplen el criterio.
- Dado un aviso visible en el feed, cuando el usuario lo selecciona, la app debe navegar al detalle correspondiente.

---

### CU-03: Ver detalle de un aviso y contactar al dueño

**Actor principal:** Usuario autenticado.  
**Objetivo:** Permitir que el usuario visualice la información completa de un aviso y contacte al dueño.

**Precondiciones:** El usuario tiene sesión activa. El aviso existe y está activo. Fue seleccionado desde el feed o el mapa.

**Disparador:** El usuario toca una card del feed o un pin del mapa.

**Flujo principal:**
1. El usuario toca un aviso.
2. El sistema navega a la pantalla de detalle.
3. El sistema muestra foto, nombre, especie, raza, color, descripción, zona y datos de contacto.
4. El usuario toca "Contactar por WhatsApp".
5. El sistema abre WhatsApp con un mensaje predefinido dirigido al número de contacto.

**Flujos alternativos:**
1. Si el aviso no tiene número de contacto, el botón aparece deshabilitado.
2. Si el usuario es el dueño del aviso, el sistema muestra además las opciones de editar, marcar como resuelto y eliminar.
3. Si el aviso fue eliminado entre que el usuario cargó el feed y accedió al detalle, el sistema muestra un error y permite volver.

**Postcondiciones:** El usuario visualizó el aviso completo. Si inició contacto, WhatsApp se abre con el mensaje preparado.

**Criterios de aceptación:**
- Dado un aviso activo, cuando el usuario lo selecciona, el sistema debe mostrar todos los campos disponibles con imagen.
- Dado un aviso con teléfono de contacto, cuando el usuario toca "Contactar", el sistema debe abrir WhatsApp con número y mensaje predefinido.
- Dado que el usuario es el dueño del aviso, cuando accede al detalle, el sistema debe mostrar las opciones de gestión.

---

### CU-04: Registrarse e iniciar sesión

**Actor principal:** Usuario no autenticado.  
**Objetivo:** Crear una cuenta o iniciar sesión para acceder a las funcionalidades de la app.

**Precondiciones:** La app debe tener conexión a internet. El usuario no debe tener sesión activa.

**Disparador:** El usuario abre la app por primera vez luego del onboarding, o fue redirigido al login por cerrar sesión.

**Flujo principal — Registro:**
1. El usuario selecciona "Crear cuenta".
2. El sistema muestra el formulario con nombre, email y contraseña.
3. El usuario completa los campos y confirma.
4. El sistema valida el formato del email y la contraseña.
5. El sistema crea la cuenta en Firebase Authentication y persiste el perfil localmente.
6. El sistema redirige al usuario al feed.

**Flujo principal — Login:**
1. El usuario ingresa email y contraseña.
2. El sistema valida las credenciales contra Firebase Authentication.
3. El sistema hidrata el perfil del usuario en la base local.
4. El sistema redirige al usuario al feed.

**Flujos alternativos:**
1. Si el email ya está registrado al crear cuenta, el sistema informa el error sin borrar los campos.
2. Si las credenciales son incorrectas, el sistema muestra un mensaje de error.
3. Si el usuario olvida su contraseña, puede solicitar un email de recuperación.
4. Si no hay conexión, el sistema informa que no es posible autenticarse.

**Postcondiciones:** El usuario queda autenticado con sesión activa y es redirigido al feed.

**Criterios de aceptación:**
- Dado credenciales válidas, cuando el usuario inicia sesión, el sistema debe autenticarlo y redirigirlo al feed.
- Dado un email no registrado al hacer login, el sistema debe mostrar un error sin cerrar la pantalla.
- Dado un usuario que completa el registro correctamente, el sistema debe crear su cuenta y llevarlo al feed sin pasos adicionales.
- Dado un usuario que solicita recuperar contraseña con un email registrado, el sistema debe enviar el email e informarlo.

---

## Setup del proyecto

### 1. Clonar

```bash
git clone https://github.com/desarrolloAplicaciones1/patitasPerdidasApp.git
cd patitasPerdidasApp
```

### 2. Configurar Firebase

1. Crear un proyecto en [Firebase Console](https://console.firebase.google.com/).
2. Registrar la app Android con el package `com.uade.huellitas`.
3. Descargar `google-services.json` y copiarlo en `app/`.
4. Habilitar **Authentication → Email/Password**.
5. Crear la colección `alerts` en **Firestore**.
6. Crear un bucket en **Firebase Storage**.

### 3. Abrir en Android Studio

Abrir la carpeta raíz. Android Studio sincroniza Gradle automáticamente.

> Si aparece un error de versión KSP, revisar `gradle/libs.versions.toml` y ajustar la versión sugerida por el IDE.

### 4. Ejecutar

Conectar un dispositivo o iniciar un emulador (API 24+) y presionar **Run**.

---

*TP Integrador · Desarrollo de Aplicaciones I · UADE · 2026*