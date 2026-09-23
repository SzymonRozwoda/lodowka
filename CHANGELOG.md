# Changelog — Lodówka

Wszystkie istotne zmiany w projekcie będą dokumentowane w tym pliku.

---

## [1.1] — 2025-04-15

### Nowe funkcje i ulepszenia
- **Przejście na bazę danych Jetpack Room (SQLite):**
  - Zamiana pliku tekstowego `foods.txt` na strukturę bazy danych SQLite z interfejsem Room (`AppDatabase`, `FoodDao`, `FoodItem`).
  - Automatyczna i bezpieczna migracja istniejących danych z pliku tekstowego bez ryzyka ich utraty.
  - Obsługa danych w czasie rzeczywistym (`LiveData`) – automatyczne odświeżanie listy na ekranie po dodaniu lub usunięciu produktu.
- **Spersonalizowane przypomnienia:**
  - Możliwość ustawienia indywidualnej liczby dni przypomnienia przed końcem ważności dla każdego produktu (domyślnie 3 dni).
  - Dynamiczne i czytelne formatowanie powiadomień (np. `- Mleko 2 dni do terminu (16/04/2025)` lub `- Jogurt dzisiaj mija termin!`).
- **Ulepszenia wizualne UI:**
  - Podświetlanie nazwy i daty na **czerwono** dla produktów, które wygasły lub kwalifikują się do przypomnienia.
  - Automatyczne sortowanie produktów rosnąco według daty ważności (najbardziej przeterminowane na samej górze).
  - Dodano wyświetlanie dzisiejszej daty w prawym górnym rogu ekranu głównego.
- **Ustawienia powiadomień:**
  - Dodano przełącznik *„Tylko gdy produkty blisko terminu”* – pozwala wyciszyć codzienne powiadomienia, jeśli żaden produkt nie wymaga przypomnienia.

---

## [1.0] — Wersja początkowa (Initial Release)

### Podstawowe funkcje
- **Zarządzanie produktami:**
  - Dodawanie produktów z nazwą i wyborem daty ważności z kalendarza (`DatePickerDialog`).
  - Wyświetlanie listy produktów w interfejsie `RecyclerView`.
  - Usuwanie produktów z listy za pomocą gestu przesunięcia (*swipe-to-delete*).
- **System powiadomień:**
  - Codzienne automatyczne powiadomienia o produktach kończących ważność za 3 dni lub mniej.
  - Ustawianie godziny powiadomień (domyślnie 9:00) za pomocą `AlarmManager`.
  - Automatyczne przywracanie harmonogramu powiadomień po ponownym uruchomieniu telefonu (`BootReceiver`).
- **Ustawienia i wygląd:**
  - Włączanie i wyłączanie powiadomień.
  - Wybór godziny wysyłania codziennego przypomnienia.
  - Wybór motywu interfejsu (Jasny, Ciemny, Systemowy).
- **Przechowywanie danych:**
  - Zapis produktów w lokalnym pliku tekstowym (`foods.txt`).
  - Zapis ustawień użytkownika w `SharedPreferences`.
