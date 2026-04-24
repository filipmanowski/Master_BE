Cześć! 👋

Backend zmienił architekturę bezpieczeństwa — userId nie jest już w URL-u, tylko pobierany z JWT tokenu. To zwiększa bezpieczeństwo aplikacji.

## 🔄 Zmiana na froncie — co trzeba zrobić?

Dokumentacja pełna jest tutaj: `FRONTEND_UPDATE_INSTRUCTIONS.md`

**TL;DR:**

### Endpointy się zmieniły:

**Ćwiczenia:**
```
GET    /api/exercises               (było: /api/users/{userId}/exercises)
POST   /api/exercises               (było: /api/users/{userId}/exercises)
GET    /api/exercises/{id}          (było: /api/users/{userId}/exercises/{id})
PUT    /api/exercises/{id}          (było: /api/users/{userId}/exercises/{id})
DELETE /api/exercises/{id}          (było: /api/users/{userId}/exercises/{id})
GET    /api/exercises/type/{type}   (było: /api/users/{userId}/exercises/type/{type})
```

**Plany treningowe:**
```
GET    /api/workout/plans           (było: /api/workout/plans/{userId})
POST   /api/workout/plan            (bez zmian)
```

**Sesje:**
```
POST   /api/session/start
Body: { "planId": 456 }             (było: { "userId": 123, "planId": 456 })
```

**Profil:**
```
POST   /api/persons
Body: { "age": 25, ... }             (NIGDY nie wysyłaj "user_id")
```

### Ważne zasady:

1. **Zawsze wysyłaj JWT token w nagłówku:**
```javascript
headers: {
  'Authorization': `Bearer ${token}`,
  'Content-Type': 'application/json'
}
```

2. **NIE wysyłaj userId w URL ani body** — backend weźmie go z tokenu

3. **Przetestuj wszystkie endpointy** po zmianach

---

## 📋 Checklist

- [ ] Usuń `{userId}` z URL-ów
- [ ] Upewnij się, że wysyłasz JWT w nagłówku
- [ ] Nie wysyłaj `userId` w body
- [ ] Przetestuj GET, POST, PUT, DELETE
- [ ] Obsłuż błąd 401 (wylogowanie)

---

## 📚 Dodatkowe info

Szczegiły techniczne: `BACKEND_CHANGES_SUMMARY.md`

Jeśli nie wiesz co robić — czytaj `FRONTEND_UPDATE_INSTRUCTIONS.md` — jest tam kompletny kod do kopiowania.

---

**Backend jest gotowy, czeka na frontend!** 🚀

