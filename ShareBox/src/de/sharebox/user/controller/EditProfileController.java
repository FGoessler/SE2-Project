package de.sharebox.user.controller;

import de.sharebox.api.UserAPI;
import de.sharebox.user.User;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class EditProfileController {
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
				System.out.println("Das Ã¤ndern der Profilinformationen war erfolgreich!");
			}
			else {
				System.out.println("Das Ã¤ndern der Daten ist fehlgeschlagen!");
			}
			// Fenster schlieÃen
		}
	};
}
