# 🐛 Prompt na Frontend — Naprawa pobierania ćwiczeń z planu

Cześć! Backend zmienił strukturę odpowiedzi dla endpointu ćwiczeń. Musisz zaktualizować frontend żeby prawidłowo mapować dane.

---

## ❌ Co się nie działa

Frontend nie widzi ćwiczeń z planu treningowego mimo że są w bazie.

**Przyczyny:**
1. **Zły URL** — `workout/plans/{planId}` zamiast `workout/plan/{planId}`
2. **Mapowanie type** — Backend zwraca typ jako string, Frontend próbuje mapować na podstawie duration
3. **Brakujące pole** — Backend zwraca `id`, ale model Exercise go nie ma
4. **Case mismatch** — Backend zwraca `Duration` (capital D), Flutter czeka `duration`

---

## ✅ Co musisz zmienić

### Plik 1: `models/exercise.dart`

**ZAMIEŃ CAŁY PLIK NA:**

```dart
import 'package:master_fe/features/workout/models/exercise_type.dart';

class Exercise {
  long? id;  // ← DODAJ to pole!
  String name;
  ExerciseType type;
  int? sets;
  int? reps;
  double? weight;
  int? duration;
  bool isDone;

  Exercise({
    this.id,
    required this.name,
    required this.type,
    this.sets,
    this.reps,
    this.weight,
    this.duration,
    this.isDone = false,
  });

  factory Exercise.fromJson(Map<String, dynamic> json) {
    return Exercise(
      id: json['id'],  // ← DODAJ to
      name: json['name'] ?? '',
      type: _parseExerciseType(json['type']),  // ← ZMIEŃ na tę metodę
      sets: json['sets'],
      reps: json['reps'],
      weight: json['weight']?.toDouble(),
      duration: json['duration'],  // ← Zwróć uwagę na małe 'd'
      isDone: false,
    );
  }

  // ← DODAJ tę nową metodę
  static ExerciseType _parseExerciseType(String? typeStr) {
    if (typeStr == null) return ExerciseType.strength;
    
    switch (typeStr.toUpperCase()) {
      case 'STRENGTH':
        return ExerciseType.strength;
      case 'CARDIO':
        return ExerciseType.cardio;
      case 'FLEXIBILITY':
        return ExerciseType.flexibility;
      case 'TIMED':
        return ExerciseType.timed;
      default:
        return ExerciseType.strength;
    }
  }
}
```

---

### Plik 2: `api/workout_api.dart` (lub gdzie masz WorkoutApi)

**ZAMIEŃ METODĘ:**

```dart
static Future<List<Exercise>> getPlanExercises(int planId) async {
  // ZMIANA: 'workout/plans' → 'workout/plan' (usunąć 's')
  final response = await ApiClient.instance.get(
    Uri.parse('$baseUrl/workout/plan/$planId'),  // ← ZMIEŃ "plans" na "plan"
    headers: {
      'Content-Type': 'application/json',
      // Token powinien być dodawany automatycznie przez ApiClient
    },
  );

  if (response.statusCode == 200) {
    final List data = jsonDecode(response.body);
    return data.map((e) => Exercise.fromJson(e)).toList();
  } else if (response.statusCode == 401) {
    throw Exception('Unauthorized - login required');
  } else if (response.statusCode == 404) {
    throw Exception('Plan not found');
  } else {
    print('Error: ${response.statusCode} - ${response.body}');
    throw Exception('Failed to load exercises: ${response.statusCode}');
  }
}
```

---

## 📋 Checklist

- [ ] Zaktualizowałem `exercise.dart` — dodałem `id`, `_parseExerciseType()`
- [ ] Zaktualizowałem `workout_api.dart` — zmienił URL z `plans` na `plan`
- [ ] Przetestowałem — pojawily się ćwiczenia na ekranie
- [ ] Sprawdzię Network tab w DevTools — czy zwraca 200 OK

---

## 🔍 Jak debugować jeśli dalej nie działa

**W Flutter DevTools Network tab:**

```
GET http://localhost:8081/api/workout/plan/1

Response:
200 OK
[
  {
    "id": 1,
    "name": "Squat",
    "type": "STRENGTH",
    "sets": 3,
    "reps": 10,
    "weight": 50.0,
    "duration": null
  }
]
```

Jeśli widzisz to — znaczy że backend działa. Problem jest w mapowaniu Dart.

**Dodaj to do kodu debugowania:**

```dart
try {
  final exercises = await WorkoutApi.getPlanExercises(planId);
  print('✅ Loaded ${exercises.length} exercises');
  for (var ex in exercises) {
    print('Exercise: ${ex.name}, type: ${ex.type}, sets: ${ex.sets}');
  }
} catch (e) {
  print('❌ Error: $e');
}
```

---

## ❓ Jeśli wciąż nie działa

Sprawdź:

1. **Czy wysyłasz JWT token?** — Powinna być linia w ApiClient:
```dart
headers['Authorization'] = 'Bearer $token';
```

2. **Czy planId jest prawidłowy?** — Czy plan istnieje w bazie?

3. **Czy backend mówi 200 OK czy 401/403/404?** — Patrz Network tab

4. **Czy Backend vraca JSON czy coś innego?** — Sprawdź Response body

---

**Gotowe? Wklej kod i powiedz co się stało! 🚀**

