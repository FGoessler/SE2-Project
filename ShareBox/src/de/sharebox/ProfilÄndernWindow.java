package de.sharebox;

import org.swixml.SwingEngine;
import de.sharebox.api.UserAPI;
import de.sharebox.user.User;
import javax.swing.*;

import java.awt.event.ActionEvent;

public class ProfilÄndernWindow {
	
	 public transient JTextField lastnameField;
	 public transient JTextField firstnameField;
	 public transient JTextField genderField;

	 public Action save = new AbstractAction() {
			public void actionPerformed( ActionEvent event ) {
	            UserAPI userApi = UserAPI.getUniqueInstance();
	            User user = new User();
	            user.setLastname(lastnameField.getText());
	            user.setFirstname(firstnameField.getText());
	            user.setGender(genderField.getText());

	            
	            if (userApi.changeProfile(user)) {
	            	System.out.println("Das ändern der Profilinformationen war erfolgreich!");
	            }
	            else {
	            	System.out.println("Das ändern der Daten ist fehlgeschlagen!");
	            }  
	            // Fenster schließen
	        }
	    };

	public ProfilÄndernWindow() {
		// TODO Auto-generated constructor stub
	}

}
