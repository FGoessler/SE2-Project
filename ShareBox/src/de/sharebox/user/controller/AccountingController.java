package de.sharebox.user.controller;

import de.sharebox.api.UserAPI;
import de.sharebox.helpers.OptionPaneHelper;
import de.sharebox.helpers.SwingEngineHelper;
import de.sharebox.user.model.PaymentInfo;
import de.sharebox.user.model.User;

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
		frame = (JFrame) new SwingEngineHelper().render(this, "editAccounting");
		frame.setVisible(true);

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
		public void actionPerformed(ActionEvent event) {
			System.out.println( ((JComboBox) event.getSource()).getSelectedItem().toString() );
		}
	};
}
