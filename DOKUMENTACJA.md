# 📋 Dokumentacja Master_BE - Aplikacja do Zarządzania Treningami

## 🎯 Przegląd Projektu

**Master_BE** to backend aplikacji do zarządzania planami treningowymi i sesjami ćwiczeń. Projekt jest zbudowany na bazie **Spring Boot 4.0.4** z wykorzystaniem **Java 17**, **PostgreSQL** i **Spring Security**.

### Technologia
- **Framework**: Spring Boot 4.0.4
- **Język**: Java 17
- **Baza danych**: PostgreSQL
- **ORM**: Spring Data JPA / Hibernate
- **Bezpieczeństwo**: Spring Security z BCrypt
- **Build tool**: Gradle
- **Biblioteki**: Lombok

---

## 📦 Struktura Projektu

```
src/
├── main/
│   ├── java/org/example/master_be/
│   │   ├── MasterBeApplication.java        # Główna klasa aplikacji
│   │   ├── Config/
│   │   │   └── SecurityConfig.java         # Konfiguracja bezpieczeństwa
│   │   ├── Controller/                     # Warstwy REST API
│   │   │   ├── UserController.java
│   │   │   ├── PersonController.java
│   │   │   ├── SessionController.java
│   │   │   └── WorkoutController.java
│   │   ├── Service/                        # Logika biznesowa
│   │   │   ├── UserService.java
│   │   │   ├── PersonService.java
│   │   │   ├── SessionService.java
│   │   │   └── WorkoutService.java
│   │   ├── Model/                          # Modele JPA (Entity)
│   │   │   ├── User.java
│   │   │   ├── Person.java
│   │   │   ├── UserExercise.java
│   │   │   ├── WorkoutPlan.java
│   │   │   ├── PlanExercise.java
│   │   │   ├── WorkoutSession.java
│   │   │   ├── SessionExercise.java
│   │   │   └── ExerciseType.java
│   │   ├── Repository/                     # Data Access Layer
│   │   │   ├── UserRepository.java
│   │   │   ├── PersonRepository.java
│   │   │   ├── UserExerciseRepository.java
│   │   │   ├── WorkoutPlanRepository.java
│   │   │   ├── PlanExerciseRepository.java
│   │   │   ├── WorkoutSessionRepository.java
│   │   │   └── SessionExerciseRepository.java
│   │   └── DTO/                            # Data Transfer Objects
│   │       ├── LoginRequest.java
│   │       ├── RegisterRequest.java
│   │       ├── PersonRequest.java
│   │       ├── PersonResponse.java
│   │       ├── UserResponse.java
│   │       ├── PlanExerciseResponse.java
│   │       ├── SaveExerciseRequest.java
│   │       └── StartSessionRequest.java
│   └── resources/
│       └── application.properties           # Konfiguracja aplikacji
└── test/
    └── java/org/example/master_be/
        └── MasterBeApplicationTests.java
```

---

## 🗄️ Modele Danych (Entity)

### 1. **User** - Użytkownik Systemu
**Plik**: `Model/User.java`

| Pole | Typ | Opis |
|------|-----|------|
| `id` | Long | ID użytkownika (Primary Key) |
| `email` | String | Email użytkownika (UNIQUE, NOT NULL) |
| `password` | String | Hasło zaszyfrowane BCrypt (NOT NULL) |
| `createdAt` | Timestamp | Data rejestracji |
| `enabled` | Boolean | Status aktywności użytkownika (NOT NULL) |
| `role` | String | Rola użytkownika (np. "USER") |

**Konstruktor**:
```java
public User(String email, String password)
```
- Automatycznie ustawia `enabled = false` i `role = "USER"`
- Ustawia `createdAt` na aktualny czas

**Relacje**:
- 1:1 relacja z `Person`
- 1:N relacja z `WorkoutPlan`
- 1:N relacja z `UserExercise`
- 1:N relacja z `WorkoutSession`

---

### 2. **Person** - Profil Fizyczny Użytkownika
**Plik**: `Model/Person.java`

| Pole | Typ | Opis |
|------|-----|------|
| `id` | Long | ID profilu (Primary Key) |
| `user` | User | Powiązany użytkownik (Foreign Key, UNIQUE) |
| `gender` | String | Płeć (np. "MALE", "FEMALE") |
| `age` | Integer | Wiek |
| `weight` | Double | Waga (kg) |
| `height` | Integer | Wzrost (cm) |
| `activityLevel` | String | Poziom aktywności |
| `sleepHours` | Double | Godziny snu |
| `waistCircumference` | Double | Obwód talii (cm) |
| `hipsCircumference` | Double | Obwód bioder (cm) |
| `thighCircumference` | Double | Obwód ud (cm) |
| `bicepsCircumference` | Double | Obwód bicepsa (cm) |
| `chestCircumference` | Double | Obwód klatki piersiowej (cm) |

**Ograniczenia**:
- Każdy użytkownik może mieć tylko jeden profil `Person`
- Relacja 1:1 z `User`

---

### 3. **UserExercise** - Ćwiczenie Zdefiniowane przez Użytkownika
**Plik**: `Model/UserExercise.java`

| Pole | Typ | Opis |
|------|-----|------|
| `id` | Long | ID ćwiczenia (Primary Key) |
| `name` | String | Nazwa ćwiczenia (np. "Wyciskanie sztangi") |
| `type` | ExerciseType | Typ: STRENGTH lub TIMED |
| `description` | String | Opis ćwiczenia |
| `createdAt` | LocalDateTime | Data dodania ćwiczenia |
| `user` | User | Właściciel ćwiczenia (Foreign Key) |

**Typ enum `ExerciseType`**:
- `STRENGTH` - Ćwiczenia siłowe (serie, powtórzenia, waga)
- `TIMED` - Ćwiczenia czasowe (czas trwania)

---

### 4. **WorkoutPlan** - Plan Treningowy
**Plik**: `Model/WorkoutPlan.java`

| Pole | Typ | Opis |
|------|-----|------|
| `id` | Long | ID planu (Primary Key) |
| `name` | String | Nazwa planu (np. "Plan masy mięśniowej") |
| `description` | String | Opis planu |
| `isTemplate` | Boolean | Czy to szablon (domyślnie: false) |
| `user` | User | Właściciel planu (Foreign Key) |
| `parentPlan` | WorkoutPlan | Plan rodzicielski (dla kopii szablonu) |

**Relacje**:
- 1:N relacja z `PlanExercise`
- 1:N relacja z `WorkoutSession`

---

### 5. **PlanExercise** - Ćwiczenie w Planie
**Plik**: `Model/PlanExercise.java`

| Pole | Typ | Opis |
|------|-----|------|
| `id` | Long | ID (Primary Key) |
| `sets` | Integer | Liczba serii |
| `reps` | Integer | Liczba powtórzeń |
| `weight` | Double | Waga (kg) |
| `duration` | Integer | Czas trwania (sekund) |
| `orderIndex` | Integer | Kolejność ćwiczenia w planie |
| `plan` | WorkoutPlan | Plan do którego należy (Foreign Key) |
| `exercise` | UserExercise | Ćwiczenie (Foreign Key) |

---

### 6. **WorkoutSession** - Sesja Treningowa
**Plik**: `Model/WorkoutSession.java`

| Pole | Typ | Opis |
|------|-----|------|
| `id` | Long | ID sesji (Primary Key) |
| `startedAt` | LocalDateTime | Czas rozpoczęcia (domyślnie: teraz) |
| `endedAt` | LocalDateTime | Czas zakończenia |
| `user` | User | Użytkownik wykonujący trening (Foreign Key) |
| `plan` | WorkoutPlan | Plan treningowy (Foreign Key) |

---

### 7. **SessionExercise** - Ćwiczenie w Sesji
**Plik**: `Model/SessionExercise.java`

| Pole | Typ | Opis |
|------|-----|------|
| `id` | Long | ID (Primary Key) |
| `sets` | Integer | Liczba serii wykonanych |
| `reps` | Integer | Liczba powtórzeń wykonanych |
| `weight` | Double | Waga użyta (kg) |
| `duration` | Integer | Czas trwania (sekund) |
| `completed` | Boolean | Czy ćwiczenie ukończone (domyślnie: false) |
| `session` | WorkoutSession | Sesja (Foreign Key) |
| `exercise` | UserExercise | Ćwiczenie (Foreign Key) |

---

## 📡 DTO (Data Transfer Objects)

### 1. **LoginRequest**
```java
@Getter @Setter
public class LoginRequest {
    private String email;
    private String password;
}
```
**Użycie**: Logowanie użytkownika

---

### 2. **RegisterRequest**
```java
@Getter @Setter
public class RegisterRequest {
    private String email;
    private String password;
}
```
**Użycie**: Rejestracja nowego użytkownika

---

### 3. **PersonRequest**
```java
@Getter @Setter
public class PersonRequest {
    @JsonProperty("user_id")
    private Long userId;
    @JsonProperty("gender")
    private String gender;
    // ... pozostałe pola
}
```
**Użycie**: Tworzenie profilu fizycznego użytkownika

---

### 4. **PersonResponse**
```java
public record PersonResponse(
    Long id,
    Long userId,
    String gender,
    Integer age,
    Double weight,
    Integer height,
    // ... pozostałe pola
) {}
```
**Użycie**: Odpowiedź z danymi profilu

---

### 5. **UserResponse**
```java
public record UserResponse(
    Long id,
    String email,
    boolean enabled,
    String role
) {}
```
**Użycie**: Odpowiedź z danymi użytkownika

---

### 6. **PlanExerciseResponse**
```java
@Data
public class PlanExerciseResponse {
    private Long id;
    private String name;
    private String type;
    private Integer sets;
    private Integer reps;
    private Double weight;
    private Integer Duration;
}
```
**Użycie**: Odpowiedź z danymi ćwiczenia w planie

---

### 7. **SaveExerciseRequest**
```java
@Data
public class SaveExerciseRequest {
    private Long sessionId;
    private Long exerciseId;
    private Integer sets;
    private Integer reps;
    private Double weight;
    private Integer duration;
    private Boolean completed;
}
```
**Użycie**: Zapis ćwiczenia w sesji

---

### 8. **StartSessionRequest**
```java
@Data
public class StartSessionRequest {
    private Long userId;
    private Long planId;
}
```
**Użycie**: Rozpoczęcie sesji treningowej

---

## 🔌 REST API - Kontrolery

### 1. **UserController** (`/api/users`)
**Plik**: `Controller/UserController.java`

#### POST `/api/users/auth/register`
Rejestracja nowego użytkownika

**Request**:
```json
{
    "email": "user@example.com",
    "password": "haslo123"
}
```

**Response** (201):
```json
{
    "id": 1,
    "email": "user@example.com",
    "password": "$2a$10$...",
    "createdAt": "2024-01-01T10:00:00",
    "enabled": false,
    "role": "USER"
}
```

**Metoda serwisu**: `UserService.register()`

---

#### POST `/api/users/auth/login`
Logowanie użytkownika

**Request**:
```json
{
    "email": "user@example.com",
    "password": "haslo123"
}
```

**Response** (200):
```json
{
    "id": 1,
    "email": "user@example.com",
    "enabled": false,
    "role": "USER"
}
```

**Response** (401):
```json
{
    "message": "Wrong credentials"
}
```

**Metoda serwisu**: `UserService.login()`

---

### 2. **PersonController** (`/api/persons`)
**Plik**: `Controller/PersonController.java`

#### POST `/api/persons`
Tworzenie profilu fizycznego użytkownika

**Request**:
```json
{
    "user_id": 1,
    "gender": "male",
    "age": 25,
    "weight": 80.5,
    "height": 180,
    "activity_level": "HIGH",
    "sleep_hours": 8.0,
    "waist_circumference": 85.0,
    "hips_circumference": 95.0,
    "thigh_circumference": 55.0,
    "biceps_circumference": 35.0,
    "chest_circumference": 105.0
}
```

**Response** (200):
```json
{
    "id": 1,
    "userId": 1,
    "gender": "MALE",
    "age": 25,
    "weight": 80.5,
    "height": 180,
    "activityLevel": "HIGH",
    "sleepHours": 8.0,
    "waistCircumference": 85.0,
    "hipsCircumference": 95.0,
    "thighCircumference": 55.0,
    "bicepsCircumference": 35.0,
    "chestCircumference": 105.0
}
```

**Metoda serwisu**: `PersonService.createPerson()`

---

### 3. **WorkoutController** (`/api/workout`)
**Plik**: `Controller/WorkoutController.java`

#### POST `/api/workout/plan`
Tworzenie nowego planu treningowego

**Request**:
```json
{
    "name": "Plan masy mięśniowej",
    "description": "3-dniowy plan na wzrost masy",
    "isTemplate": false,
    "user": {"id": 1}
}
```

**Response** (200):
```json
{
    "id": 1,
    "name": "Plan masy mięśniowej",
    "description": "3-dniowy plan na wzrost masy",
    "isTemplate": false,
    "user": {"id": 1}
}
```

**Metoda serwisu**: `WorkoutService.createPlan()`

---

#### GET `/api/workout/plans/{userId}`
Pobranie wszystkich planów użytkownika

**Response** (200):
```json
[
    {
        "id": 1,
        "name": "Plan masy mięśniowej",
        "description": "3-dniowy plan na wzrost masy",
        "isTemplate": false
    }
]
```

**Metoda serwisu**: `WorkoutService.getUserPlans()`

---

#### POST `/api/workout/plan-exercise`
Dodanie ćwiczenia do planu

**Request**:
```json
{
    "sets": 4,
    "reps": 8,
    "weight": 80.0,
    "duration": 0,
    "orderIndex": 1,
    "plan": {"id": 1},
    "exercise": {"id": 1}
}
```

**Response** (200):
```json
{
    "id": 1,
    "sets": 4,
    "reps": 8,
    "weight": 80.0,
    "duration": 0,
    "orderIndex": 1
}
```

**Metoda serwisu**: `WorkoutService.addExerciseToPlan()`

---

#### GET `/api/workout/plan/{planId}`
Pobranie wszystkich ćwiczeń w planie (z mapowaniem na DTO)

**Response** (200):
```json
[
    {
        "id": 1,
        "name": "Wyciskanie sztangi",
        "type": "STRENGTH",
        "sets": 4,
        "reps": 8,
        "weight": 80.0,
        "Duration": 0
    }
]
```

**Metoda serwisu**: `WorkoutService.getPlanExercisesDto()`

---

### 4. **SessionController** (`/api/session`)
**Plik**: `Controller/SessionController.java`

#### POST `/api/session/start`
Rozpoczęcie nowej sesji treningowej

**Request**:
```json
{
    "userId": 1,
    "planId": 1
}
```

**Response** (200):
```json
{
    "id": 1,
    "startedAt": "2024-01-01T10:00:00",
    "endedAt": null,
    "user": {"id": 1},
    "plan": {"id": 1}
}
```

**Metoda serwisu**: `SessionService.startSession()`

---

#### POST `/api/session/exercise`
Zapis/aktualizacja ćwiczenia w sesji

**Request**:
```json
{
    "sessionId": 1,
    "exerciseId": 1,
    "sets": 4,
    "reps": 8,
    "weight": 80.0,
    "duration": 0,
    "completed": true
}
```

**Response** (200):
```json
{
    "id": 1,
    "sets": 4,
    "reps": 8,
    "weight": 80.0,
    "duration": 0,
    "completed": true,
    "session": {"id": 1},
    "exercise": {"id": 1}
}
```

**Metoda serwisu**: `SessionService.saveExercise()`

---

#### POST `/api/session/end/{sessionId}`
Zakończenie sesji treningowej

**Response** (200):
```json
{
    "id": 1,
    "startedAt": "2024-01-01T10:00:00",
    "endedAt": "2024-01-01T11:30:00",
    "user": {"id": 1},
    "plan": {"id": 1}
}
```

**Metoda serwisu**: `SessionService.endSession()`

---

## 🧠 Serwisy (Service Layer)

### 1. **UserService**
**Plik**: `Service/UserService.java`

**Klasy wewnętrzne**:
```java
public record LoginResult(boolean success, User user) {}
```

**Metody**:

#### `register(String email, String password) -> User`
- Sprawdza czy email już istnieje
- Szyfruje hasło BCrypt
- Tworzy nowego użytkownika z rolą "USER"
- Zwraca zapisanego użytkownika

**Wyjątki**: `RuntimeException("User already exists")`

---

#### `login(String email, String password) -> LoginResult`
- Szuka użytkownika po emailu
- Weryfikuje hasło z BCrypt
- Zwraca LoginResult z wynikiem i użytkownikiem

**Zwraca**: Record z polami `success` i `user`

---

### 2. **PersonService**
**Plik**: `Service/PersonService.java`

**Metody**:

#### `createPerson(PersonRequest request) -> Person`
- Pobiera użytkownika po ID z `PersonRequest.userId`
- Sprawdza czy użytkownik już ma profil (1:1)
- Mapuje dane z RequestO na encję `Person`
- Konwertuje płeć: "mężczyzna"/"mezczyzna"/"male"/"m" → "MALE"
- Konwertuje płeć: "kobieta"/"female"/"f" → "FEMALE"
- Zapisuje w bazie

**Wyjątki**:
- `RuntimeException("User not found")`
- `RuntimeException("Person already exists for this user")`
- `RuntimeException("Invalid gender: ...")`

---

#### `mapGender(String gender) -> String` (private)
- Switch case do mapowania płci
- Normalizuje różne formaty wejściowe

---

### 3. **SessionService**
**Plik**: `Service/SessionService.java`

**Metody**:

#### `startSession(Long userId, Long planId) -> WorkoutSession`
- Tworzy nową sesję
- Pobiera użytkownika i plan
- Ustawia automatycznie `startedAt = LocalDateTime.now()`
- Zapisuje w bazie

---

#### `saveExercise(SaveExerciseRequest req) -> SessionExercise`
- Tworzy/aktualizuje ćwiczenie w sesji
- Pobiera sesję i ćwiczenie
- Mapuje wszystkie pola (sets, reps, weight, duration, completed)
- Zapisuje w bazie

---

#### `endSession(Long sessionId) -> WorkoutSession`
- Pobiera sesję
- Ustawia `endedAt = LocalDateTime.now()`
- Zapisuje w bazie

---

### 4. **WorkoutService**
**Plik**: `Service/WorkoutService.java`

**Metody**:

#### `createPlan(WorkoutPlan plan) -> WorkoutPlan`
- Zapisuje nowy plan treningowy
- Zwraca zapisany plan

---

#### `getUserPlans(Long userId) -> List<WorkoutPlan>`
- Pobiera wszystkie plany użytkownika
- Zwraca listę planów

---

#### `addExerciseToPlan(PlanExercise pe) -> PlanExercise`
- Dodaje ćwiczenie do planu
- Zwraca zapisane ćwiczenie

---

#### `getPlanExercises(Long planId) -> List<PlanExercise>`
- Pobiera wszystkie ćwiczenia w planie
- Zwraca listę PlanExercise

---

#### `getPlanExercisesDto(Long planId) -> List<PlanExerciseResponse>`
- Pobiera ćwiczenia z planu
- Mapuje na DTO (w tym nazwę i typ z `UserExercise`)
- Zwraca listę ResponseDTO

---

#### `mapToDto(PlanExercise pe) -> PlanExerciseResponse` (private)
- Konwertuje encję na DTO
- Pobiera nazwę i typ z powiązanego `UserExercise`

---

## 💾 Repozytoria (Repository)

Wszystkie repozytoria rozszerzają `JpaRepository<Entity, Long>` i zapewniają podstawowe operacje CRUD.

### 1. **UserRepository**
```java
Optional<User> findByEmail(String email);
```
- Wyszukiwanie użytkownika po emailu

---

### 2. **PersonRepository**
```java
Optional<Person> findByUserId(Long userId);
```
- Wyszukiwanie profilu po ID użytkownika (1:1)

---

### 3. **WorkoutPlanRepository**
```java
List<WorkoutPlan> findByUserId(Long userId);
```
- Wyszukiwanie wszystkich planów użytkownika

---

### 4. **PlanExerciseRepository**
```java
List<PlanExercise> findByPlanId(Long planId);
```
- Wyszukiwanie wszystkich ćwiczeń w planie

---

### 5. **UserExerciseRepository**
```java
List<UserExercise> findByUserId(Long userId);
```
- Wyszukiwanie wszystkich ćwiczeń zdefiniowanych przez użytkownika

---

### 6. **WorkoutSessionRepository**
- Brak custom metod, używa tylko CRUD operacji z JpaRepository

---

### 7. **SessionExerciseRepository**
```java
List<SessionExercise> findByExerciseId(Long exerciseId);
```
- Wyszukiwanie wszystkich instancji ćwiczenia w sesjach

---

## ⚙️ Konfiguracja

### SecurityConfig
**Plik**: `Config/SecurityConfig.java`

**Fasolki (Beans)**:

#### `passwordEncoder() -> PasswordEncoder`
- Zwraca `BCryptPasswordEncoder`
- Używany do szyfrowania i weryfikacji haseł

---

#### `filterChain(HttpSecurity http) -> SecurityFilterChain`
- Wyłącza ochronę CSRF
- Zezwala na wszystkie requesty bez autoryzacji (`permitAll()`)
- **⚠️ Brak JWT/Session Management** - trzeba będzie dodać!

---

## 📊 Diagram Relacji Bazy Danych

```
┌─────────────────────┐
│      User           │
│───────────────────│
│ id (PK)             │
│ email (UNIQUE)      │
│ password            │
│ createdAt           │
│ enabled             │
│ role                │
└─────────────────────┘
        │ 1:1      1:N        1:N          1:N
        ├──────────┤──────────┤──────────┤
        │          │          │          │
        ▼          ▼          ▼          ▼
    ┌────────┐ ┌─────────────┐ ┌──────────────┐ ┌────────────────┐
    │ Person │ │UserExercise │ │WorkoutPlan   │ │WorkoutSession  │
    └────────┘ └─────────────┘ └──────────────┘ └────────────────┘
                     │              │                    │
                     │ 1:N          │ 1:N               │ 1:N
                     │              │                  │
                     ▼              ▼                  ▼
              ┌──────────────┐ ┌──────────────┐ ┌──────────────┐
              │PlanExercise  │ │SessionExercise
              └──────────────┘ └──────────────┘
```

---

## 🔐 Bezpieczeństwo

- ✅ Hasła szyfrowane BCrypt
- ✅ CSRF wyłączony (REST API)
- ❌ Brak JWT Token Authentication
- ❌ Brak Session Management
- ⚠️ Wszystkie endpointy bez autoryzacji (`permitAll()`)

### ⚠️ Rekomendacje:
1. Dodać JWT Token Authentication
2. Dodać Role-Based Access Control (RBAC)
3. Dodać @Transactional do operacji modyfikujących
4. Dodać walidację input (Bean Validation)

---

## 🗄️ Konfiguracja Bazy Danych

**Plik**: `application.properties`

```properties
spring.application.name=Master_BE

spring.datasource.url=jdbc:postgresql://localhost:5432/Master
spring.datasource.username=postgres
spring.datasource.password=pass

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

- **DDL**: `update` (auto-aktualizuje schemat)
- **SQL Logging**: Włączony (`show-sql=true`)
- **Baza**: PostgreSQL na localhost:5432

---

## 📝 Główna Klasa Aplikacji

**Plik**: `MasterBeApplication.java`

```java
@SpringBootApplication
public class MasterBeApplication {
    public static void main(String[] args) {
        SpringApplication.run(MasterBeApplication.class, args);
    }
}
```

- Uruchomienie aplikacji Spring Boot
- Automatyczne skanowanie komponentów

---

## 🚀 Przepływ Użytkownika - Scenariusz Kompletny

### 1. **Rejestracja**
```
POST /api/users/auth/register
{email, password}
    ↓
UserService.register()
    ↓
Szyfrowanie BCrypt
    ↓
UserRepository.save()
    ↓
✅ Nowy user w bazie (enabled=false)
```

---

### 2. **Logowanie**
```
POST /api/users/auth/login
{email, password}
    ↓
UserService.login()
    ↓
UserRepository.findByEmail()
    ↓
Weryfikacja BCrypt
    ↓
✅ UserResponse lub 401 Error
```

---

### 3. **Tworzenie Profilu Fizycznego**
```
POST /api/persons
{user_id, gender, age, weight, ...}
    ↓
PersonService.createPerson()
    ↓
Walidacja (1 Person per User)
    ↓
PersonRepository.save()
    ↓
✅ PersonResponse
```

---

### 4. **Tworzenie Planu Treningowego**
```
POST /api/workout/plan
{name, description, user}
    ↓
WorkoutService.createPlan()
    ↓
WorkoutPlanRepository.save()
    ↓
✅ WorkoutPlan ID
```

---

### 5. **Dodanie Ćwiczenia do Planu**
```
POST /api/workout/plan-exercise
{plan_id, exercise_id, sets, reps, weight, ...}
    ↓
WorkoutService.addExerciseToPlan()
    ↓
PlanExerciseRepository.save()
    ↓
✅ PlanExercise
```

---

### 6. **Rozpoczęcie Sesji**
```
POST /api/session/start
{user_id, plan_id}
    ↓
SessionService.startSession()
    ↓
WorkoutSessionRepository.save()
    ↓
✅ WorkoutSession (startedAt=now)
```

---

### 7. **Zapis Wykonanego Ćwiczenia**
```
POST /api/session/exercise
{session_id, exercise_id, sets, reps, weight, completed}
    ↓
SessionService.saveExercise()
    ↓
SessionExerciseRepository.save()
    ↓
✅ SessionExercise
```

---

### 8. **Zakończenie Sesji**
```
POST /api/session/end/{session_id}
    ↓
SessionService.endSession()
    ↓
WorkoutSessionRepository.save()
    ↓
✅ WorkoutSession (endedAt=now)
```

---

## 🎯 Podsumowanie Funkcjonalności

| Funkcja | Endpoint | Metoda HTTP | Status |
|---------|----------|-------------|--------|
| Rejestracja | `/api/users/auth/register` | POST | ✅ |
| Logowanie | `/api/users/auth/login` | POST | ✅ |
| Profil fizyczny | `/api/persons` | POST | ✅ |
| Tworzenie planu | `/api/workout/plan` | POST | ✅ |
| Pobranie planów | `/api/workout/plans/{userId}` | GET | ✅ |
| Dodanie ćwiczenia | `/api/workout/plan-exercise` | POST | ✅ |
| Pobranie ćwiczeń | `/api/workout/plan/{planId}` | GET | ✅ |
| Start sesji | `/api/session/start` | POST | ✅ |
| Zapis ćwiczenia | `/api/session/exercise` | POST | ✅ |
| Koniec sesji | `/api/session/end/{sessionId}` | POST | ✅ |

---

## 🔧 Techniczny Stack

| Komponent | Wersja | Opis |
|-----------|--------|------|
| Spring Boot | 4.0.4 | Framework aplikacyjny |
| Java | 17 | Język programowania |
| PostgreSQL | Latest | Baza danych |
| Spring Data JPA | Latest | ORM |
| Spring Security | Latest | Bezpieczeństwo |
| Lombok | Latest | Generowanie kodu |
| Gradle | Latest | Build tool |

---

## 📋 Notatki i Uwagi

1. **Hasła szyfrowane**: Używany BCrypt (algo 10 rounds - standard)
2. **Timestamps**: Automatyczne ustawianie czasu przy tworzeniu
3. **Relacje**: Prawidłowo zdefiniowane relacje 1:1, 1:N
4. **DTOs**: Separacja danych wewnętrznych od API
5. **Enums**: ExerciseType (STRENGTH/TIMED)
6. **Mapowanie**: PersonRequest ↔ Person (z konwersją płci)
7. **Transactional**: PersonService ma @Transactional

---

## 🚨 TODO / Problemy do Rozwiązania

- [ ] Dodać JWT Token Authentication
- [ ] Dodać Role-Based Access Control
- [ ] Dodać Exception Handling (GlobalExceptionHandler)
- [ ] Dodać Input Validation (Bean Validation)
- [ ] Dodać Unit Tests
- [ ] Dodać Integration Tests
- [ ] Dodać API Documentation (Swagger/OpenAPI)
- [ ] Dodać Pagination dla list queries
- [ ] Dodać Error Response DTO
- [ ] Dodać logowanie (SLF4J / Logback)

---

**Wygenerowano**: 2024
**Wersja**: 1.0.0
**Status**: Dokumentacja Kompletna ✅

