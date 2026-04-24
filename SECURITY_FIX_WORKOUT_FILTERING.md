# 🔒 Bezpieczeństwo — Filtrowanie ćwiczeń po użytkowniku

## ⚠️ Problem (NAPRAWIONY)

**Było (NIEBEZPIECZNE):**
```
Każdy użytkownik widział WSZYSTKIE ćwiczenia z KAŻDEGO planu
Niezależnie na które konto się zalogował
```

**Przyczyna:**
- Endpoint `/api/workout/plan/{planId}` nie sprawdzał czy ten plan należy do zalogowanego użytkownika
- Zwracał ćwiczenia dla każdego `planId`, nawet nieautoryzowanego

---

## ✅ Co się zmieniło na backendzie

### 1. WorkoutPlanRepository
- Dodana metoda: `findByIdAndUserId(Long id, Long userId)`
- Szuka planu o danym ID **i** danym użytkowniku

### 2. WorkoutService
- Metoda `getPlanExercisesDto(Long planId, Long userId)` teraz:
  - ✅ Sprawdza czy plan należy do użytkownika
  - ✅ Jeśli nie — zwraca `401 Unauthorized`
  - ✅ Jeśli tak — zwraca ćwiczenia

### 3. WorkoutController
- Pobiera `userId` z JWT tokenu
- Przesyła go do serwisu

---

## 📊 Scenariusze

### PRZED (⚠️ NIEBEZPIECZNE)

```
Użytkownik A zalogowany:
GET /api/workout/plan/1
→ Widzi ćwiczenia planu 1 (nawet jeśli to plan użytkownika B!)
```

### TERAZ (✅ BEZPIECZNE)

```
Użytkownik A zalogowany:
GET /api/workout/plan/1
Token z A
→ Backend sprawdza: czy plan 1 należy do A?
→ Jeśli NIE: 401 Unauthorized
→ Jeśli TAK: zwraca ćwiczenia
```

---

## 🎯 Co frontend musi zmienić

### ✅ NIC!

Frontend nie musi nic zmieniać. Backend obsługuje to automatycznie.

Ale warto obsłużyć nowy błąd:

```dart
try {
  final exercises = await WorkoutApi.getPlanExercises(planId);
  // ... wyświetl ćwiczenia
} catch (e) {
  if (e.toString().contains('401')) {
    print('❌ Brak dostępu do tego planu');
    // Może to plan kogoś innego
  } else {
    print('❌ Błąd: $e');
  }
}
```

---

## 📋 Podsumowanie

| Aspekt | Było | Jest teraz |
|--------|------|-----------|
| **Plan = dla każdego?** | ✅ TAK (bug!) | ❌ NIE (fix!) |
| **Autoryzacja** | Brak | Po userId z JWT |
| **Błąd 401** | Nigdy | Jeśli nie moje ćwiczenia |

**Backend now fully secure!** 🔒

