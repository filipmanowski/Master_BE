# Podsumowanie zmian: userId z JWT

## Co się zmieniło na backendzie

### 1. **Nowy custom `UserDetails`** 
📁 `src/main/java/org/example/master_be/Config/CustomUserDetails.java`
- Implementuje interface `UserDetails`
- **Przechowuje userId zalogowanego użytkownika**
- Metoda `getUserId()` zwraca ID użytkownika

### 2. **Utility do pobierania userId z tokenu**
📁 `src/main/java/org/example/master_be/Config/AuthUtil.java`
- Komponent Spring `@Component`
- Metoda `getCurrentUserId()` — wyciąga userId z JWT tokenu
- Pobiera token z `SecurityContextHolder`
- Rzuca wyjątek `401` jeśli token jest niepoprawny

### 3. **Aktualizacja `CustomUserDetailsService`**
📁 `src/main/java/org/example/master_be/Config/CustomUserDetailsService.java`
- Zamiast zwracać standardowego `User` z Spring Security
- Teraz tworzy i zwraca `CustomUserDetails` z `userId`

### 4. **Usunięcie userId z URL-ów**

| Controller | Zmiana |
|-----------|--------|
| **ExerciseController** | `/api/users/{userId}/exercises` → `/api/exercises` |
| **WorkoutController** | `/api/workout/plans/{userId}` → `/api/workout/plans` |
| **PersonController** | userId pobierany z tokenu automatycznie |
| **SessionController** | StartSessionRequest: usunięty `userId`, pozostał tylko `planId` |

### 5. **userId pobierany z tokenu**
Każdy endpoint teraz robi tak:
```java
Long userId = authUtil.getCurrentUserId();  // ← z JWT tokenu
```

---

## Bezpieczeństwo

### PRZED (niebezpieczne):
```http
GET /api/users/999/exercises
Authorization: Bearer <mój-token>
```
❌ Attacker może zmienić `999` na inny userId i zobaczyć cudze dane

### TERAZ (bezpieczne):
```http
GET /api/exercises
Authorization: Bearer <mój-token>
```
✅ Backend wie, że to mój token, dlatego zwraca moje ćwiczenia

---

## Co frontend musi zmienić

📄 Pełna dokumentacja: `FRONTEND_UPDATE_INSTRUCTIONS.md`

Krótko:
1. ❌ Usuń wszystkie `{userId}` z URL-ów
2. ❌ Nie wysyłaj `userId` w body requestów
3. ✅ Wysyłaj `Authorization: Bearer <token>` w nagłówku
4. ✅ Przetestuj wszystkie endpointy

---

## Testy

```bash
./gradlew compileJava  # ✅ BUILD SUCCESSFUL
```

---

## Wdrażanie

**Krok 1:** Wysłij ten plik frontendowcowi: `FRONTEND_UPDATE_INSTRUCTIONS.md`

**Krok 2:** Backend jest gotowy — czeka na frontend

**Krok 3:** Frontend zmienia URL-y i wysyłanie requestów

**Krok 4:** End-to-end testing

---

## Pliki zmienione

- ✅ `CustomUserDetails.java` — NOWY
- ✅ `AuthUtil.java` — NOWY
- ✅ `CustomUserDetailsService.java` — ZMIENIONY
- ✅ `ExerciseController.java` — ZMIENIONY
- ✅ `PersonController.java` — ZMIENIONY
- ✅ `SessionController.java` — ZMIENIONY
- ✅ `WorkoutController.java` — ZMIENIONY
- ✅ `StartSessionRequest.java` — ZMIENIONY

---

## Pytania?

Pytaj w kanale `#backend` lub poczcie przesłanym do agenta frontendu.

