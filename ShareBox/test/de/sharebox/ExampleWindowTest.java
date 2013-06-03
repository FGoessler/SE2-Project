package de.sharebox;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)      //Diese Zeile sorgt dafür, das Mockito funktioniert!
/**
 * Dies ist eine kleine Testklasse um JUnit, Mockito und FEST zu demonstrieren.
 * @warning Sollte vor der Abgabe entfernt werden!
 */
public class ExampleWindowTest {

    private ExampleWindow window;

    @Mock   //mit @Mock sagt man Mockito das er hier automatisch ein gemocktes Objekt erzeugt werden soll
    private ExampleWindow anotherWindow;

    @Before
    /**
     * Diese Methode ist mit @Before markiert und wird daher vor _jedem_ Test ausgeführt. Hier kann man schön Zeug
     * unterbringen um seinen Test vorzubereiten, um das nicht jedes Mal neu machen zu müssen.
     * Asserts sollten hier nicht hin!
     * Es kann auch mehrere @Before Methoden geben, diese werden dann alle nacheinander ausgeführt.
     */
    public void setUp() throws Exception {
        window = new ExampleWindow();
    }

    @After
    /**
     * Wird nach jedem Test ausgeführt. Meist muss man hier nichts machen, da JUnit für jede einzelne Testmethode
     * alles neu erstellt, man also keine Objekte versehentlich von einem Test in den anderen mitschleppen kann.
     * Würde man ein solches Verhalten benötigen gibt es die Annotations @beforeClass und @afterClass.
     * Allerdings kann es durchaus sein, das man irgendwelche globalen Settings für den Test ändern möchte - das sollte
     * man hier wieder rückgängig machen, da sich andere Test darauf verlassen könnten das sie bestimmte Standardsettings
     * vorfinden.
     */
    public void tearDown() throws Exception {
        //man braucht hier für diesen Test nix machen
    }

    @Test
    /**
     * JUnit erkennt anhand der @Test Annotation, das es sich hier um einen Testcase handelt.
     *
     * Dieser Test demonstriert den Unterschied zwischen JUnit asserts und FEST asserts.
     * FEST asserts sind besser lesbar und leichter zu schreiben, da die IDE basierend auf Typprüfung nur asserts vorschlägt,
     * die für diesen Typ auch möglich sind. Das geht bei JUnit nicht so schön - da muss man mehr wissen welche asserts
     * sich für was einigen und wie sie heißen.
     */
    public void aSmallTest() throws Exception {
        //JUnit native assert
        assertEquals("Textfield should have some value!", "Swixml", window.textField.getText());

        //FEST asserts
        assertThat(window.textField.getText()).isEqualTo("Swixml");
    }

    @Test
    /**
     * Dieser Test demonstriert Mockito (zumindest einen Ausschnitt^^).
     * Mit when() kann einem Mock-Objekt (siehe @Mock) gesagt werden das es bei der angegebenen Methode etwas bestimmtes
     * machen soll. Es gibt zB. thenReturn(...), thenThrow(...),...
     * Dies wird dann immer ausgeführt wenn im folgenden irgendwo die entsprechende Methode aufgerufen wird (anstatt
     * der eigentlichen Ausführung).
     * Somit kann Klassen und Objekte testen ohne vom "wirklichen" Funktionieren anderer Klassen abhängig zu sein.
     * Mit verfiy() kann man zudem überprüfen, das es eine bestimmte Methode zuvor auch wirklich aufgerufen wurde.
     *
     * Hinweiß: static Methoden können mit Mockito nicht (so ohne weiteres) gemockt werden! Dies ist besonders bei der
     * Verwendung des Singleton Patterns zu beachten! Siehe dazu auch das Konzept des "Singleton by Choice"!
     * (Generell sind Singletons zum testen etwas blöd und man sollte sie daher nur da einsetzen wo sie nötig sind.)
     */
    public void aMockitoTest() throws Exception {
        when(anotherWindow.getCounterText()).thenReturn("Wadde hadde du de da?!");

        assertThat(anotherWindow.getCounterText()).isEqualTo("Wadde hadde du de da?!");

        verify(anotherWindow).getCounterText();
    }
}
