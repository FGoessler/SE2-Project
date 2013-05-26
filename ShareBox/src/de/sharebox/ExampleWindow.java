package de.sharebox;

import org.swixml.SwingEngine;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class ExampleWindow {
    private int clicks = 0;

    public JTextField textField;
    public JLabel counter;

    public Action submit = new AbstractAction() {
		public void actionPerformed( ActionEvent e ) {
            textField.setText( textField.getText() + '#' );
            counter.setText(String.valueOf( ++clicks ));
        }
    };

    public ExampleWindow() throws Exception {
        new SwingEngine( this ).render( "resources/xml/testlayout.xml" ).setVisible(true);
    }

    public String getCounterText() {
        return counter.getText();
    }
}
