package de.sharebox.user.controller;

import de.sharebox.api.UserAPI;
import de.sharebox.user.PaymentInfo;
import de.sharebox.user.User;
import javax.swing.*;

import org.swixml.SwingEngine;

import java.awt.event.ActionEvent;

public class AccountingController {
	private JFrame frame;
	public JComboBox storageLimitField;
	public JTextField streetField;
	public JTextField additiveField;
	public JTextField codeField;
	public JTextField locationField;
	public JTextField countryField;
	
	
	public AccountingController() {
		try {
			SwingEngine swix = new SwingEngine(this);
			frame = (JFrame) swix.render("resources/xml/editAccounting.xml");
			frame.setVisible(true);
		} catch (Exception exception) {
			System.out.println("Couldn't create register window!");
		}
		
		User user = UserAPI.getUniqueInstance().getCurrentUser();
		int index = 0;
		
		for(int i = 0; i < storageLimitField.getItemCount(); i++){
			if(user.getStorageLimit() == storageLimitField.getItemAt(i).toString()){
				index = storageLimitField.getSelectedIndex();
			}
		}
		PaymentInfo paymentinfo = user.getPaymentInfo();
			
		storageLimitField.setSelectedIndex(index);
		streetField.setText(paymentinfo.getStreet());
		additiveField.setText(paymentinfo.getAdditionalStreet());
		codeField.setText(paymentinfo.getZipCode());
		locationField.setText(paymentinfo.getCity());
		countryField.setText(paymentinfo.getCountry());
	}

	public Action save = new AbstractAction() {
		public void actionPerformed( ActionEvent event ) {
			UserAPI userApi = UserAPI.getUniqueInstance();
			User user = new User();
			user.setStorageLimit(storageLimitField.getSelectedItem().toString());

			PaymentInfo paymentinfo = user.getPaymentInfo();
			paymentinfo.setStreet(streetField.getText());
			paymentinfo.setAdditionalStreet(additiveField.getText());
			paymentinfo.setZipCode(codeField.getText());
			paymentinfo.setCity(locationField.getText());
			paymentinfo.setCountry(countryField.getText());
			user.setPaymentInfo(paymentinfo);


			if (userApi.changeAccountingSettings(user)) {	
				System.out.println("Die Änderung war erfolgreich");
			}
			else {
				System.out.println("Das ändern der Daten ist fehlgeschlagen!");
			}
			frame.setVisible(false);
			// Fenster schließen
		}
		
		public Action DO_SELECT = new AbstractAction() {
		    public void actionPerformed(ActionEvent e) {
		      System.out.println( ((JComboBox) e.getSource()).getSelectedItem().toString() );
		    }
		  };
	};
}
