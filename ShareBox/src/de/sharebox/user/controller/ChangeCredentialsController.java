package de.sharebox.user.controller;

import de.sharebox.api.UserAPI;
import de.sharebox.user.User;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class ChangeCredentialsController {
	public transient JTextField eMailField;
	public transient JPasswordField oldPasswordField;
	public transient JPasswordField newPasswordField;
	public transient JPasswordField newPasswordField1;


	public Action save = new AbstractAction() {
		public void actionPerformed( ActionEvent save ) {
			UserAPI userApi = UserAPI.getUniqueInstance();
			User user = new User();
			user.setEmail(eMailField.getText());
			user.setPassword(newPasswordField.toString());

			User currentUser = userApi.getCurrentUser();
			currentUser.setPassword(oldPasswordField.toString());

			if (newPasswordField.toString() == newPasswordField1.toString()){
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
