# Architektura projektu

Backend Spring Boot aplikacji treningowej. Projekt ma prostą architekturę warstwową:

- `Controller` - endpointy REST i pobieranie aktualnego użytkownika z JWT przez `AuthUtil`.
- `Service` - logika biznesowa, walidacja i agregacje.
- `Repository` - Spring Data JPA.
- `Model` - encje JPA.
- `DTO` - obiekty wejścia/wyjścia API.
- `Config` - konfiguracja security, JWT i pomocnicze klasy uwierzytelniania.

Nie ma osobnego pakietu mapperów; mapowanie DTO jest wykonywane w serwisach.

# Struktura katalogów

- `src/main/java/org/example/master_be/Controller` - kontrolery REST.
- `src/main/java/org/example/master_be/Service` - serwisy aplikacyjne.
- `src/main/java/org/example/master_be/Repository` - repozytoria JPA.
- `src/main/java/org/example/master_be/Model` - encje domenowe.
- `src/main/java/org/example/master_be/DTO` - request/response DTO.
- `src/main/java/org/example/master_be/Config` - JWT, Spring Security, aktualny użytkownik.
- `src/main/resources/application.properties` - konfiguracja aplikacji i bazy.

# Modele

- `User` - konto użytkownika, email, hasło, rola, status aktywacji.
- `Person` - profil użytkownika i dane antropometryczne.
- `Exercise` - ćwiczenie użytkownika, nazwa, typ, opis.
- `PlanExercise` - ćwiczenie przypisane do planu, docelowe serie/powtórzenia/ciężar/czas.
- `WorkoutPlan` - plan treningowy użytkownika.
- `WorkoutSession` - rozpoczęta/zakończona sesja treningowa.
- `PerformedExercise` - wykonane ćwiczenie w sesji; źródło danych dla progresu.
- `NutritionEntry` - wpis żywieniowy: posiłek, kalorie i makroskładniki.
- `NutritionMicronutrient` - opcjonalny mikroskładnik przypisany do wpisu żywieniowego.
- `HydrationEntry` - wpis wypitej wody w ml.

# Relacje między encjami

- `User 1:1 Person`.
- `User 1:N Exercise`.
- `User 1:N WorkoutPlan`.
- `User 1:N WorkoutSession`.
- `WorkoutPlan N:1 User`.
- `WorkoutPlan N:1 WorkoutPlan parentPlan` dla pochodnych planów.
- `PlanExercise N:1 WorkoutPlan`.
- `PlanExercise N:1 Exercise`.
- `WorkoutSession N:1 User`.
- `WorkoutSession N:1 WorkoutPlan`.
- `PerformedExercise N:1 WorkoutSession`.
- `PerformedExercise N:1 Exercise`.
- `NutritionEntry N:1 User`.
- `NutritionEntry 1:N NutritionMicronutrient` z cascade i orphan removal.
- `HydrationEntry N:1 User`.

# Główne endpointy

Autoryzacja i użytkownik:

- `POST /api/user/register` - rejestracja.
- `POST /api/user/login` - logowanie.
- `GET /api/person` - dane profilu.
- `PUT /api/person` - aktualizacja profilu.

Ćwiczenia i plany:

- `GET /api/exercise` - ćwiczenia użytkownika.
- `POST /api/exercise` - zapis ćwiczenia.
- `POST /api/workout/plan` - tworzy plan.
- `GET /api/workout/plans` - lista planów użytkownika.
- `GET /api/workout/plan/{planId}` - ćwiczenia w planie.
- `POST /api/workout/plan/{planId}/exercise` - dodaje ćwiczenie do planu.
- `PUT /api/workout/plan-exercise/{planExerciseId}` - aktualizuje ćwiczenie w planie.
- `DELETE /api/workout/plan/{planId}` - usuwa plan.

Sesje:

- `POST /api/session/start` - start sesji.
- `POST /api/session/exercise` - zapis wykonanego ćwiczenia.
- `POST /api/session/end/{sessionId}` - koniec sesji.

Kalorie:

- `POST /api/nutrition/entries` - tworzy wpis żywieniowy.
- `GET /api/nutrition/entries` - lista wpisów użytkownika.
- `GET /api/nutrition/summary/day?date=YYYY-MM-DD` - suma kalorii i makro dla dnia; bez `date` zwraca dzisiaj.

Nawodnienie:

- `POST /api/hydration/entries` - tworzy wpis wypitej wody.
- `GET /api/hydration/entries` - lista wpisów użytkownika.
- `GET /api/hydration/today` - suma wody dzisiaj.
- `GET /api/hydration/day?date=YYYY-MM-DD` - suma wody w wybranym dniu.
- `GET /api/hydration/average/week` - średnia dzienna z ostatnich 7 dni.
- `GET /api/hydration/average/month` - średnia dzienna z ostatniego miesiąca.

Progres ćwiczeń:

- `GET /api/progress/exercises/{exerciseId}` - podsumowanie, historia i rekordy.
- `GET /api/progress/exercises/{exerciseId}/summary` - maksymalny ciężar, serie, powtórzenia, objętość, najlepszy i średni wynik.
- `GET /api/progress/exercises/{exerciseId}/history` - historia wykonań.
- `GET /api/progress/exercises/{exerciseId}/charts/weight` - punkty wykresu ciężaru w czasie.
- `GET /api/progress/exercises/{exerciseId}/charts/reps` - punkty wykresu powtórzeń w czasie.
- `GET /api/progress/exercises/{exerciseId}/charts/volume` - punkty wykresu objętości w czasie.
- `GET /api/progress/exercises/{exerciseId}/charts/workouts` - liczba wykonań ćwiczenia per dzień.
- `GET /api/progress/exercises/{exerciseId}/records` - rekordy osobiste.

Dashboard:

- `GET /api/dashboard/summary?from=YYYY-MM-DD&to=YYYY-MM-DD` - statystyki dashboardu. Bez parametrów używa ostatnich 30 dni.

# Przepływ danych

Frontend -> Controller -> Service -> Repository -> Database.

Kontrolery nie powinny zawierać logiki biznesowej. Aktualny użytkownik powinien być pobierany przez `AuthUtil.getCurrentUserId()`, a filtrowanie danych użytkownika musi odbywać się w serwisach/repozytoriach.

# Zasady projektu

- Nie zmieniaj istniejących modeli bez potrzeby.
- Nie usuwaj endpointów.
- Używaj istniejących serwisów i stylu projektu.
- Nie czytaj całego repozytorium, jeżeli wystarczy analiza konkretnego modułu.
- Najpierw sprawdzaj `README_AI.md`.
- Wykonuj możliwie najmniejsze zmiany.
- Progres ćwiczeń licz z `PerformedExercise`; nie duplikuj wyliczalnych danych.
- Nowe endpointy powinny filtrować dane po aktualnie zalogowanym użytkowniku.
- Dla wykresów używaj prostych DTO z `dateTime`, `label`, `value`.
- `spring.jpa.hibernate.ddl-auto=update`, więc nowe encje tworzą tabele automatycznie w środowisku deweloperskim.

# Uwagi implementacyjne

- Objętość treningowa = `sets * reps * weight`.
- `score` dla ćwiczeń siłowych bazuje na objętości; jeżeli objętość wynosi 0, używany jest czas trwania lub `sets * reps`.
- `DashboardSummaryResponse.mostFrequentMuscleGroups` obecnie zwraca pustą listę, bo `Exercise` nie posiada pola grupy mięśniowej.
- `totalLiftedWeight` w dashboardzie jest sumą objętości treningowej dla ukończonych ćwiczeń.
