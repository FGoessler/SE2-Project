## SE2-Projekt
### Gruppe SE2-04

#### Mitglieder:
Benjamin Barth
Florian Gößler
Kay Thorsten Meißner
Julius Mertens


### Anmerkungen zum Projekt

#### Verwendte Frameworks:
- SWIxml (http://www.swixml.org) : Framework um Swing GUIs aus XML Dateien zu generieren.
- Google Guice (https://code.google.com/p/google-guice/) (Version 3.0) : Dependency Injection Framework - hauptsächlich
eingesetzt um das Testen der einzelnen Komponenten unabhängig voneinander zu erleichtern und diverse Singleton-Mechansim
zu vereinfachen. (Wir verwenden zusätzlich das Guice Addon Assisted-Inject.)
- Google Guava (https://code.google.com/p/guava-libraries/) (Version 14.0.1) : Generells Framework von Google für häufig
benötigte Funktionen, die die Java Standard Library nicht zur Verfügung stellt. Von uns wurden davon hauptsächlich
Immutable Collections und Optionals (zur Vermeidung von null-Zuweisungen bzw. "meaningfull nulls")

### Verwendete Testframeworks:
- JUnit 4
- Mockito (https://code.google.com/p/mockito/) (Version 1.9.5) : Framework um automatisch Mock- und Spy-Objekte zu erstellen.
- FEST Asserts (http://fest.easytesting.org) (Version 1.4) : Framework das zusätzliche Matcher bereitstellt bzw.
sogenannte "Fluent Assertions".
