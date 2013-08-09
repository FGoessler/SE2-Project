package de.sharebox.user.controller;

import de.sharebox.api.UserAPI;
import de.sharebox.user.User;
import javax.swing.*;

import java.awt.event.ActionEvent;

public class AccountingController {
	public transient JComboBox storageLimitField;
	public transient JTextField streetField;
	public transient JTextField additiveField;
	public transient JTextField codeField;
	public transient JTextField locationField;
	public transient JTextField countryField;
	

	public Action save = new AbstractAction() {
		public void actionPerformed( ActionEvent event ) {
			UserAPI userApi = UserAPI.getUniqueInstance();
			User user = new User();
			user.setStorageLimit(storageLimitField.getSelectedItem().toString());
			user.getPaymentinfo.setStreet(streetField.getText());
			user.getPaymentinfo.setAditionalStreet(additiveField.getText());
			user.getPaymentinfo.setZipCode(codeField.getText());
			user.getPaymentinfo.setLocation(locationField.getText());
			user.getPaymentInfo.setCounty(countryField.getText());


			if (userApi.changeAccountingSettings(user)) {
				System.out.println("Die Änderung war erfolgreich");
			}
			else {
				System.out.println("Das ändern der Daten ist fehlgeschlagen!");
			}
			// Fenster schließen
		}
		
		public Action DO_SELECT = new AbstractAction() {
		    public void actionPerformed(ActionEvent e) {
		      System.out.println( ((JComboBox) e.getSource()).getSelectedItem().toString() );
		    }
		  };
	};
}
