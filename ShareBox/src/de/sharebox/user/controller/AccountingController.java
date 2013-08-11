package de.sharebox.user.controller;

import de.sharebox.api.UserAPI;
import de.sharebox.helpers.OptionPaneHelper;
import de.sharebox.user.model.PaymentInfo;
import de.sharebox.user.model.User;
import org.swixml.SwingEngine;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class AccountingController {
	private JFrame frame;
	public JComboBox storageLimitField;
	public JTextField streetField;
	public JTextField additiveField;
	public JTextField codeField;
	public JTextField locationField;
	public JTextField countryField;
	public int paymentIndex;
	protected OptionPaneHelper optionPane = new OptionPaneHelper();
	
	
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
			if(user.getStorageLimit() != null && user.getStorageLimit().equals(storageLimitField.getItemAt(i).toString())){
				index = i;
			}
		}
		PaymentInfo paymentinfo = user.getPaymentInfo();
		paymentIndex = index;	
		
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
				if(paymentIndex < storageLimitField.getSelectedIndex()){
					frame.setVisible(false);
				}
				
				optionPane.showMessageDialog("Die Änderung war erfolgreich");
			}
			else {
				optionPane.showMessageDialog("Das ändern der Daten ist fehlgeschlagen!");
			}
		}
	};

	public Action selectBoxAction = new AbstractAction() {
		public void actionPerformed(ActionEvent e) {
			System.out.println( ((JComboBox) e.getSource()).getSelectedItem().toString() );
		}
	};
}
