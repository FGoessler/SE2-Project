package de.sharebox.user.controller;

import de.sharebox.api.UserAPI;
import de.sharebox.user.User;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class RegisterController {
	public transient JTextField eMailField;
	public transient JTextField password1Field;
	public transient JTextField password2Field;
	public transient JTextField lastnameField;
	public transient JTextField firstnameField;
	public transient JTextField genderField;
	public transient JTextField storageLimitField;
	public transient JTextField paymentinfoField;

	public Action register = new AbstractAction() {
		public void actionPerformed( ActionEvent event ) {
			UserAPI userApi = UserAPI.getUniqueInstance();
			User user = new User();
			user.setEmail(eMailField.getText());
			user.setPassword(password1Field.getText());
			user.setLastname(lastnameField.getText());
			user.setFirstname(firstnameField.getText());
			user.setGender(genderField.getText());
			user.setStorageLimit(storageLimitField.getText());
			user.setPaymentInfo(paymentinfoField.getText());


			if (userApi.registerUser(user)) {
				System.out.println("Die Registrierung war erfolgreich!");
			}
			else {
				System.out.println("Loggen sie sich mit ihrem Account ein!");
			}
			// Fenster schlie�en
		}
	};
}
