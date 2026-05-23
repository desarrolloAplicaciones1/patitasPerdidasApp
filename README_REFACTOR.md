# Resumen Del Refactor

Este archivo resume los cambios aplicados para llevar el proyecto hacia una implementacion de `Clean Architecture + MVVM`.

---

## Objetivo

- Desacoplar `presentation` de `data`.
- Evitar que los `ViewModel` conozcan Room o Firebase.
- Introducir `use cases` para representar acciones del negocio.
- Mover los contratos de repositorio a `domain`.

---

## Cambios Aplicados

### 1. Interfaces de repositorio en `domain`

Se agregaron contratos para que el dominio defina lo que necesita sin depender de implementaciones concretas:

- `app/src/main/java/com/desarrolloaplicaciones1/patitasperdidas/domain/repository/AlertRepository.kt`
- `app/src/main/java/com/desarrolloaplicaciones1/patitasperdidas/domain/repository/UserRepository.kt`
- `app/src/main/java/com/desarrolloaplicaciones1/patitasperdidas/domain/repository/PetRepository.kt`

### 2. Use cases en `domain`

Se agregaron casos de uso agrupados por feature:

- `domain/usecase/auth/`
- `domain/usecase/alert/`
- `domain/usecase/pet/`
- `domain/usecase/user/`

Ejemplos:

- `LoginUseCase`
- `RegisterUserUseCase`
- `GetActiveAlertsUseCase`
- `CreateAlertUseCase`
- `ResolveAlertUseCase`
- `GetMyPetsUseCase`

### 3. Repositorios de `data` adaptados a contratos

Los repositorios existentes ahora implementan interfaces de `domain`:

- `app/src/main/java/com/desarrolloaplicaciones1/patitasperdidas/data/repository/AlertRepository.kt`
- `app/src/main/java/com/desarrolloaplicaciones1/patitasperdidas/data/repository/UserRepository.kt`
- `app/src/main/java/com/desarrolloaplicaciones1/patitasperdidas/data/repository/PetRepository.kt`

### 4. Nuevo contenedor de dependencias

Se agrego un contenedor manual para centralizar la construccion de repositorios y use cases:

- `app/src/main/java/com/desarrolloaplicaciones1/patitasperdidas/di/AppContainer.kt`

### 5. Nueva `Application`

Se agrego una clase `Application` para exponer el `AppContainer`:

- `app/src/main/java/com/desarrolloaplicaciones1/patitasperdidas/PatitasPerdidasApplication.kt`

Y se registro en:

- `app/src/main/AndroidManifest.xml`

### 6. ViewModels refactorizados

Los `ViewModel` dejaron de crear repositorios concretos y ahora consumen `use cases` desde `AppContainer`.

Archivos actualizados:

- `app/src/main/java/com/desarrolloaplicaciones1/patitasperdidas/presentation/auth/LoginViewModel.kt`
- `app/src/main/java/com/desarrolloaplicaciones1/patitasperdidas/presentation/auth/RegisterViewModel.kt`
- `app/src/main/java/com/desarrolloaplicaciones1/patitasperdidas/presentation/splash/SplashViewModel.kt`
- `app/src/main/java/com/desarrolloaplicaciones1/patitasperdidas/presentation/home/HomeViewModel.kt`
- `app/src/main/java/com/desarrolloaplicaciones1/patitasperdidas/presentation/create/CreateAlertViewModel.kt`
- `app/src/main/java/com/desarrolloaplicaciones1/patitasperdidas/presentation/detail/AlertDetailViewModel.kt`
- `app/src/main/java/com/desarrolloaplicaciones1/patitasperdidas/presentation/profile/ProfileViewModel.kt`
- `app/src/main/java/com/desarrolloaplicaciones1/patitasperdidas/presentation/profile/MyAlertsViewModel.kt`
- `app/src/main/java/com/desarrolloaplicaciones1/patitasperdidas/presentation/profile/MyPetsViewModel.kt`

---

## Que No Se Toco

Para minimizar conflictos con la rama de front:

- No se modifico `navigation/`.
- No se modificaron las `Screen.kt`.
- El refactor se concentro en `domain`, `data`, `di`, `Application` y `ViewModel`.

---

## Flujo Antes Y Despues

### Antes

```text
Screen -> ViewModel -> Repository concreto -> Room / Firebase
```

### Despues

```text
Screen -> ViewModel -> UseCase -> Repository interface -> Repository impl -> Room / Firebase
```

---

## Mejora Tecnica Importante

En `HomeViewModel` el estado de filtros ahora se combina de forma reactiva con el flujo de alertas, en lugar de depender solo del flujo de datos base.

---

## Beneficios Del Refactor

- Mejor separacion de responsabilidades.
- Menor acoplamiento entre capas.
- Base mas limpia para testing.
- Menor impacto sobre la rama de UI.
- Arquitectura mas cercana a Clean Architecture real.

---

## Pendiente

- Validar compilacion una vez resuelto el problema actual de version/configuracion de `KSP`.
- Ajustar o extender la UI si se quiere aprovechar nuevos use cases.
- Agregar tests de use cases y ViewModels.
