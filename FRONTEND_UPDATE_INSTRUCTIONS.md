# Zmiana API: userId z URL-u na JWT token

## Co się zmieniło na backendzie?

Backend przestał przyjmować `userId` z URL-u. Zamiast tego, userId jest **automatycznie pobierany z JWT tokenu**, co zwiększa bezpieczeństwo.

## Jak to działa?

Backend ma teraz nowy utility `AuthUtil`, który wyciąga userId z zalogowanego użytkownika na podstawie JWT tokenu. Ty już masz token po zalogowaniu w polu `AuthResponse.token` — ta zmiana pozwala backendowi wiedzieć, czyj request wykonujesz.

## Co musisz zmienić na frontendzie?

### 1. Endpointy ćwiczeń (`/api/exercises`)

**PRZED (stare URL-y):**
```
GET    /api/users/{userId}/exercises
POST   /api/users/{userId}/exercises
GET    /api/users/{userId}/exercises/{id}
PUT    /api/users/{userId}/exercises/{id}
DELETE /api/users/{userId}/exercises/{id}
GET    /api/users/{userId}/exercises/type/{type}
```

**TERAZ (nowe URL-y bez userId):**
```
GET    /api/exercises
POST   /api/exercises
GET    /api/exercises/{id}
PUT    /api/exercises/{id}
DELETE /api/exercises/{id}
GET    /api/exercises/type/{type}
```

**Przykład requestu (JavaScript/Fetch):**
```javascript
// PRZED
fetch(`/api/users/${userId}/exercises`, {
  headers: { 'Authorization': `Bearer ${token}` }
})

// TERAZ
fetch(`/api/exercises`, {
  headers: { 'Authorization': `Bearer ${token}` }
})
```

---

### 2. Plany treningowe (`/api/workout`)

**PRZED:**
```
GET /api/workout/plans/{userId}
```

**TERAZ:**
```
GET /api/workout/plans
```

**POST dla nowego planu — backend automatycznie przypisze userId z tokenu:**
```javascript
// Request body — NIE WYSYŁAJ userId!
{
  "name": "Mój plan",
  "description": "Opis"
}

// Backend wie, że to plan dla zalogowanego użytkownika
```

---

### 3. Sesje treningowe (`/api/session`)

**PRZED:**
```json
POST /api/session/start
{
  "userId": 123,
  "planId": 456
}
```

**TERAZ:**
```json
POST /api/session/start
{
  "planId": 456
}
```

Backend wyciąga userId z JWT, więc **nie wysyłaj userId w body requestu!**

---

### 4. Profil osoby (`/api/persons`)

**PRZED:** Frontend wysyłał `user_id` w body requestu.

**TERAZ:** Backend pobiera userId z JWT i ustawia go automatycznie.

```json
// PRZED
{
  "user_id": 123,
  "age": 25,
  "weight": 80.5,
  ...
}

// TERAZ (NIE wysyłaj user_id, backend sam to zrobi)
{
  "age": 25,
  "weight": 80.5,
  "gender": "MALE",
  ...
}
```

---

## Ważne zasady

1. **Zawsze wysyłaj JWT token** w nagłówku `Authorization: Bearer <token>`:
```javascript
const headers = {
  'Authorization': `Bearer ${token}`,
  'Content-Type': 'application/json'
}
```

2. **Nie wysyłaj userId w URL-u ani w body requestu** — backend weźmie go z tokenu.

3. **Jeśli zapomnisz wysłać token**, dostaniesz błąd `401 Unauthorized` lub `400 Bad Request`.

4. **Jeśli token wygaśnie** (po 24h), użytkownik musi się zalogować ponownie.

---

## Checklist do wdrożenia

- [ ] Usuń wszystkie `{userId}` z URL-ów requestów
- [ ] Upewnij się, że wysyłasz `Authorization: Bearer <token>` w każdym requestzie
- [ ] W body requestów nie wysyłaj już `userId` ani `user_id`
- [ ] Przetestuj wszystkie endpointy (GET, POST, PUT, DELETE)
- [ ] Sprawdź, że błędy 401 są obsługiwane (np. redirect do loginu)

---

## Przykładowy kod do kopiowania (React/TypeScript)

```typescript
const API_URL = 'http://localhost:8081/api';

async function getExercises(token: string) {
  const response = await fetch(`${API_URL}/exercises`, {
    method: 'GET',
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    }
  });
  
  if (response.status === 401) {
    // Token wygasł, wyloguj użytkownika
    console.log('Session expired');
    return;
  }
  
  return await response.json();
}

async function createExercise(token: string, exercise: Exercise) {
  const response = await fetch(`${API_URL}/exercises`, {
    method: 'POST',
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(exercise)
  });
  
  return await response.json();
}

async function startWorkoutSession(token: string, planId: number) {
  const response = await fetch(`${API_URL}/session/start`, {
    method: 'POST',
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({ planId })  // Uwaga: TYLKO planId!
  });
  
  return await response.json();
}
```

---

## Podsumowanie

| Aspekt | Było | Jest teraz |
|--------|------|-----------|
| **userId w URL** | `GET /api/users/123/exercises` | `GET /api/exercises` |
| **userId w body** | `{ "userId": 123, ... }` | Bez userId |
| **Autoryzacja** | Id w URL = brak weryfikacji | JWT token = bezpieczne |
| **Jak backend wie, czyj to request** | URL i body | JWT token w nagłówku |

Wdrożenie powinno zająć ~30 minut jeśli masz zaledwie kilka endpointów do zmienienia!

