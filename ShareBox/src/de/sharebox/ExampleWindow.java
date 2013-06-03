package de.sharebox;

import org.swixml.SwingEngine;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Nur eine Testklasse um SWIxml, javadoc, TestCases usw. zu demonstrieren. Vor Abgabe löschen!
 */
public class ExampleWindow {
    private int clicks = 0;

    /**
     * Diese Textfield-Property wird aus dem weiter unten angegebenen XML gefüttert - das macht einfach SWIxml für uns!
     * Entscheidend hierbei ist das die Property genauso heißt wie die im id Attribut im XML angegeben!
     */
    public JTextField textField;
    /**
     * Hier passiert das gleiche wie für textField.
     */
    public JLabel counter;

    /**
     * Diese AbstractAction Subklasse/Property dient dazu auf Klicks auf den Button zu reagieren.
     * Auch hier übernimmt SWIxml eine Menge Arbeit für uns. Die Property muss nur genauso heißen wie im action Attribut
     * im XML angegeben. Dieser Listener wird dann automatisch mit dem Button verbunden.
     */
    public Action submit = new AbstractAction() {
		public void actionPerformed( ActionEvent e ) {
            textField.setText( textField.getText() + '#' );
            counter.setText(String.valueOf( ++clicks ));
        }
    };

    /**
     * Im Konstruktor wird mittels der SwingEngine unsere GUI aus dem XML geladen und entsprechend verlinkt.
     * @throws Exception
     */
    public ExampleWindow() throws Exception {
        new SwingEngine( this ).render( "resources/xml/testlayout.xml" ).setVisible(true);
    }

    /**
     * Dies ist nur eine Dummy-Methode um in der Testklasse Mockito zu demonstrieren.
     * @return Gibt normalerweise den aktuellen Text des counter Labels zurück.
     */
    public String getCounterText() {
        return counter.getText();
    }
}
