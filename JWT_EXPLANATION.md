# JWT w tym projekcie

Ten dokument wyjaśnia, jak działa uwierzytelnianie JWT w projekcie `Master_BE`, gdzie token jest ustawiany, co robi oraz jaka jest idea tego rozwiązania.

## 1. Po co w ogóle JWT?

JWT (`JSON Web Token`) służy tutaj do **bezstanowego uwierzytelniania**.

Zamiast trzymać sesję po stronie backendu, aplikacja działa tak:

1. użytkownik loguje się loginem i hasłem,
2. backend sprawdza dane,
3. jeśli są poprawne, generuje token JWT,
4. frontend zapisuje token,
5. przy kolejnych requestach frontend wysyła token w nagłówku `Authorization`.

Dzięki temu backend wie, kto wykonuje żądanie, bez potrzeby trzymania sesji w pamięci.

## 2. Gdzie JWT jest ustawiane?

Najważniejsze miejsce to:

- `src/main/java/org/example/master_be/Config/JwtService.java`

To właśnie tam:

- tworzony jest token,
- podpisywany jest sekretem,
- ustawiany jest czas ważności,
- odczytywany jest username z tokenu,
- sprawdzana jest poprawność tokenu.

### Źródło sekretu

W `src/main/resources/application.properties` masz:

- `jwt.secret=${JWT_SECRET:...}`
- `jwt.expiration-ms=${JWT_EXPIRATION_MS:86400000}`

Czyli:

- sekret JWT jest pobierany z zmiennej środowiskowej `JWT_SECRET`,
- jeśli jej nie ma, używana jest wartość domyślna,
- czas ważności tokenu domyślnie wynosi `86400000` ms, czyli **24 godziny**.

## 3. Jak powstaje token?

W `JwtService` token jest generowany w metodzie:

- `generateToken(UserDetails userDetails)`
- `generateToken(Map<String, Object> extraClaims, UserDetails userDetails)`

### Co zawiera token?

W obecnej implementacji token zawiera przede wszystkim:

- `subject` = email użytkownika,
- `issuedAt` = moment wygenerowania,
- `expiration` = czas wygaśnięcia,
- podpis HMAC oparty o `jwt.secret`.

Nie ma tu rozbudowanych custom claims — token jest prosty i służy głównie do identyfikacji użytkownika.

## 4. Gdzie token jest zwracany frontendowi?

Token jest generowany po poprawnym logowaniu w:

- `src/main/java/org/example/master_be/Controller/UserController.java`

Przepływ wygląda tak:

1. frontend wysyła `email` i `password` do `/api/users/auth/login`,
2. `UserService.login(...)` sprawdza dane,
3. jeśli wszystko się zgadza, `UserController` pobiera `UserDetails`,
4. `JwtService.generateToken(userDetails)` tworzy JWT,
5. backend zwraca `AuthResponse` z tokenem.

## 5. Co zwraca login?

Obiekt odpowiedzi to:

- `src/main/java/org/example/master_be/DTO/AuthResponse.java`

Obecnie zawiera pola:

- `token`
- `id`
- `email`
- `enabled`
- `role`
- `needsProfileCompletion`

### Znaczenie tych pól

- `token` — JWT do dalszych requestów,
- `id` — identyfikator użytkownika,
- `email` — email użytkownika,
- `enabled` — stan konta w bazie,
- `role` — rola użytkownika,
- `needsProfileCompletion` — flaga dla frontendu, że po zalogowaniu trzeba pokazać ankietę / profil, a nie ekran główny.

## 6. Ważny przypadek: konto bez profilu

W tej aplikacji konto może być początkowo `enabled = false`.

To nie musi oznaczać błędu logowania.

### Co się dzieje teraz?

Jeśli użytkownik ma poprawne hasło, ale profil osoby jeszcze nie został uzupełniony:

- backend **dalej loguje użytkownika**,
- generuje JWT,
- zwraca `needsProfileCompletion = true`.

Dzięki temu frontend może zdecydować:

- **nie przekierowuj do dashboardu**,
- **pokaż formularz / ankietę uzupełniającą profil**.

To jest ważne, bo wcześniej taki stan był traktowany jak błąd logowania.

## 7. Jak backend sprawdza token przy kolejnych requestach?

Za to odpowiada:

- `src/main/java/org/example/master_be/Config/JwtAuthenticationFilter.java`

### Przepływ filtra

1. filtr sprawdza nagłówek `Authorization`,
2. jeśli nie ma `Bearer <token>`, request idzie dalej bez uwierzytelnienia,
3. jeśli token jest obecny, filtr wyciąga username,
4. ładuje użytkownika przez `UserDetailsService`,
5. sprawdza, czy token jest poprawny,
6. jeśli tak, ustawia uwierzytelnienie w `SecurityContextHolder`.

### W praktyce

Każdy chroniony endpoint może rozpoznać użytkownika na podstawie tokenu, bez sesji po stronie serwera.

## 8. Rola `SecurityConfig`

Plik:

- `src/main/java/org/example/master_be/Config/SecurityConfig.java`

ustawia podstawowe zasady bezpieczeństwa.

### Najważniejsze rzeczy

- `csrf` jest wyłączone,
- endpointy `/api/users/auth/**` są publiczne,
- wszystkie inne endpointy wymagają uwierzytelnienia,
- aplikacja działa w trybie `STATELESS`, więc nie używa sesji,
- `JwtAuthenticationFilter` jest dodany przed standardowym filtrem logowania.

To oznacza, że JWT jest głównym mechanizmem autoryzacji w całej aplikacji.

## 9. Rola `CustomUserDetailsService`

Plik:

- `src/main/java/org/example/master_be/Config/CustomUserDetailsService.java`

Ten serwis ładuje użytkownika z bazy po emailu i zamienia go na `UserDetails` wymagany przez Spring Security.

### Co robi dokładnie?

- znajduje użytkownika po emailu,
- ustawia login jako email,
- ustawia hasło,
- dodaje rolę w formie `ROLE_<rola>`,
- ustawia `.disabled(...)` na podstawie `enabled`.

### Dlaczego to ważne?

Bo JWT sam w sobie nie wykonuje autoryzacji. On tylko identyfikuje użytkownika.

Dopiero `UserDetails` i Spring Security decydują:

- czy konto istnieje,
- czy jest aktywne,
- jakie ma role.

## 10. Login krok po kroku

### Scenariusz poprawny

1. Front wysyła `email` i `password`.
2. `UserService.login(...)` sprawdza dane.
3. Jeśli dane są poprawne:
   - jeśli profil nieukończony → `needsProfileCompletion = true`,
   - jeśli profil ukończony → `needsProfileCompletion = false`.
4. Backend zwraca `AuthResponse` z tokenem.
5. Front zapisuje token.
6. Front na tej podstawie wybiera widok.

### Scenariusz błędny

Jeśli hasło lub email są złe:

- backend zwraca `401 Unauthorized`,
- w body jest komunikat `Wrong credentials`.

## 11. Jak frontend powinien używać tokenu?

Po zalogowaniu frontend powinien:

1. zapisać `token`,
2. do każdego chronionego requestu dodać nagłówek:

```http
Authorization: Bearer <token>
```

3. sprawdzać `needsProfileCompletion`,
4. jeśli `true`, pokazać ekran uzupełnienia profilu,
5. jeśli `false`, przejść do aplikacji głównej.

## 12. Idea zastosowania JWT w tym projekcie

To rozwiązanie ma kilka zalet:

- **brak sesji po stronie backendu**,
- **łatwe skalowanie**,
- **prosty podział odpowiedzialności** między frontend i backend,
- **możliwość szybkiego sprawdzenia użytkownika** przy każdym requestcie,
- **wygodna obsługa API** dla aplikacji webowej.

W praktyce JWT w tym projekcie jest „przepustką” do chronionych endpointów.

## 13. Podsumowanie

W skrócie:

- logowanie zwraca JWT,
- frontend przechowuje token,
- backend sprawdza token w filtrze,
- `SecurityConfig` wymusza autoryzację na chronionych endpointach,
- `needsProfileCompletion` pozwala frontendowi pokazać ankietę zamiast dashboardu,
- konto bez profilu nie jest już traktowane jako błąd logowania.

Jeśli chcesz, mogę też przygotować drugą wersję tego dokumentu w bardziej technicznym stylu albo krótszą wersję „dla pracy dyplomowej”.

