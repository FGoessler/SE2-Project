package de.sharebox.user.controller;

import de.sharebox.api.UserAPI;
import de.sharebox.user.User;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class AccountingController {
	public transient JTextField storageLimitField;
	public transient JTextField paymentinfoField;

	public Action save = new AbstractAction() {
		public void actionPerformed( ActionEvent event ) {
			UserAPI userApi = UserAPI.getUniqueInstance();
			User user = new User();
			user.setStorageLimit(storageLimitField.getText());
			user.setPaymentInfo(paymentinfoField.getText());


			if (userApi.changeAccountingSettings(user)) {
				System.out.println("Die Änderung war erfolgreich");
			}
			else {
				System.out.println("Das ändern der Daten ist fehlgeschlagen!");
			}
			// Fenster schließen
		}
	};
}
