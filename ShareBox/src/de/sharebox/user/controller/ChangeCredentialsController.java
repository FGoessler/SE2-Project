package de.sharebox.user.controller;

import de.sharebox.api.UserAPI;
import de.sharebox.user.User;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class ChangeCredentialsController {
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
						System.out.println("Das ändern der Daten war erfolgreich!");
					}
					else {
						System.out.println("Das ändern der Daten ist fehlgeschlagen!");
					}
					// Fenster schließen
				}
			}

		};
	};
}
