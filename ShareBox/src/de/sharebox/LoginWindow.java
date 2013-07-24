package de.sharebox;

import org.swixml.SwingEngine;

import de.sharebox.api.UserAPI;
import de.sharebox.user.User;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class LoginWindow {
	
    public transient JTextField eMailField;
    public transient JTextField passwordField;  

    public Action submit = new AbstractAction() {
		public void actionPerformed( ActionEvent event ) {
            UserAPI userApi = UserAPI.getUniqueInstance();
            User user = new User();
            user.setEmail(eMailField.getText());
            user.setPassword(passwordField.getText());
            
            if (userApi.authenticateUser(user)) {
            	System.out.println("Hurra");
            }
            else {
            	System.out.println("Error");
            }   
        }
    };
    
    public Action register = new AbstractAction() {
		public void actionPerformed( ActionEvent event ) {
			//Browser öffnen
        }
    };

    public LoginWindow() {
		try {
			new SwingEngine( this ).render( "resources/xml/LoginLayout.xml" ).setVisible(true);
		} catch(Exception e) {
			System.out.println("Couldn't create Swing Window!");
		}
    }
}
