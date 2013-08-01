package de.sharebox;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JTextField;

import de.sharebox.api.UserAPI;
import de.sharebox.user.User;

public class LoginInformationen�ndernWindow {

	 public transient JTextField eMailField;
	 public transient JTextField password1Field;
	 public transient JTextField password2Field;
	 public transient JTextField oldpasswordField;


	 public Action save = new AbstractAction() {
			public void actionPerformed( ActionEvent event ) {
	            UserAPI userApi = UserAPI.getUniqueInstance();
	            User user = new User();
	            user.setEmail(eMailField.getText());
	            user.setPassword(password1Field.getText());

	            User currentUser = userApi.getCurrentUser();
	            currentUser.setPassword(oldpasswordField.getText());

	            if (password1Field.getText() == password2Field.getText()){
	            	if (userApi.authenticateUser(currentUser)) {
	            		if (userApi.changeCredential(currentUser, user)) {
	            			System.out.println("Das �ndern der Daten war erfolgreich!");
	            	}
	            	else {
	            		System.out.println("Das �ndern der Daten ist fehlgeschlagen!");
	            	}  
	            	// Fenster schlie�en
	            }
	        }
	           
	    };
	
	public LoginInformationen�ndernWindow() {
		// TODO Auto-generated constructor stub
	}

}
