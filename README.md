# 🐸 Lodówka - Twój osobisty asystent dat ważności

Aplikacja mobilna na system Android, która pomaga zarządzać produktami spożywczymi. "Lodówka" pilnuje terminów ważności za Ciebie i przypomina o nich o wybranej przez Ciebie porze.

## Główne Funkcje (Wersja 1.0)

*   **Zarządzanie produktami:** Szybkie dodawanie produktów wraz z terminem przydatności.
*   **Intuicyjna lista:** Przejrzysty widok produktów oparty na kartach (Material Design).
*   **Swipe-to-Dismiss:** Szybkie usuwanie zużytych produktów jednym przesunięciem palca.
*   **Inteligentne Powiadomienia:** Codzienne przypomnienie o produktach, którym kończy się termin (3 dni przed datą).
*   **Pełna Personalizacja:** 
    *   Ustawianie własnej godziny powiadomień.
    *   Wybór motywu: Jasny, Ciemny lub zgodny z systemem.
*   **Niezawodność:** Obsługa automatycznego przywracania powiadomień po zrestartowaniu telefonu


## Technologie

*   **Język:** Java
*   **UI:** Jetpack (RecyclerView, CoordinatorLayout, Material Components 3)
*   **Pamięć:** SharedPreferences (ustawienia), Pamięć wewnętrzna (produkty)
*   **System:** AlarmManager (setAlarmClock)

## Jak uruchomić projekt?

1.  Sklonuj repozytorium: `git clone https://github.com/SzymonRozwoda/lodowka.git`
2.  Otwórz folder projektu w **Android Studio**.
3.  Poczekaj na synchronizację Gradle (projekt używa Gradle Wrapper, więc nie musisz nic doinstalowywać).
4.  Uruchom aplikację na emulatorze lub fizycznym urządzeniu (wymagane API 26+)

