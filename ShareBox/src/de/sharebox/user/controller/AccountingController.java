package de.sharebox.user.controller;

import de.sharebox.api.UserAPI;
import de.sharebox.helpers.OptionPaneHelper;
import de.sharebox.user.model.PaymentInfo;
import de.sharebox.user.model.User;
import org.swixml.SwingEngine;
import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * @author Benjamin Barth
 * @author Kay Thorsten Meißner
 */

public class AccountingController {
	private JFrame frame;
	public JComboBox storageLimitField;
	public JTextField streetField;
	public JTextField additiveField;
	public JTextField codeField;
	public JTextField locationField;
	public JTextField countryField;
	public int paymentIndex;
	int i;
	int j;
	int w = 0;
	int x = 0;
	int y = 0;
	int z = 0;
	protected OptionPaneHelper optionPane = new OptionPaneHelper();
	
	/*
	 * Öffnen des Accounting-bearbeiten Fensters. Hierbei werden alle bereits bekannten Informationen in 
	 * die TextFields direkt mit übernommen. Außerdem wird überprüft,ob bereits Informationen zu den einzelnen Feldern
	 * vorhanden waren oder nicht.
	 */
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
		
		i = streetField.getSelectionStart();
		j = streetField.getSelectionEnd();
		if (i == 0 && j == 0){
			w = 1;
			}
		else 
		{
			w = 0;
		}
		i = codeField.getSelectionStart();
		j = codeField.getSelectionEnd();
		if (i == 0 && j == 0){
			x = 1;
			}
		else 
		{
			x = 0;
		}
		i = locationField.getSelectionStart();
		j = locationField.getSelectionEnd();
		if (i == 0 && j == 0){
			y = 1;
			}
		else 
		{
			y = 0;
		}
		
		i = countryField.getSelectionStart();
		j = countryField.getSelectionEnd();
		if (i == 0 && j == 0){
			z = 1;
			}
		else 
		{
			z = 0;
		}
	}

	/*
	 * Speichern der Änderungen an den Accounting Informationen. Für jedes einzelne Feld wird überprüft, ob etwas hinein
	 * geschrieben werden muss oder nicht. Wenn nichts drin stehen muss kann auch beim Speichern nichts drin stehen
	 * Wenn zuvor was drin Stand, dann muss auch wieder eine Information eingetragen werden. Beim speichern des 
	 * Speicherplatzes wird überprüft, ob alle Zahlungsinformationen eingetragen sind, anschließend wird man an
	 * das externe Bezahlsystem weitergeleitet.
	 */
	
	public Action save = new AbstractAction() {
		public void actionPerformed( ActionEvent event ) {
			UserAPI userApi = UserAPI.getUniqueInstance();
			User user = new User();
			

			PaymentInfo paymentinfo = user.getPaymentInfo();
			
			i = streetField.getSelectionStart();
			j = streetField.getSelectionEnd();
			if (w == 1)
			{
				paymentinfo.setStreet(streetField.getText());
			}
			else{
				if (i == 0 && j == 0){
					paymentinfo.setStreet("");
					}
				else
					{
						paymentinfo.setStreet(streetField.getText());
					}
			}
			i = codeField.getSelectionStart();
			j = codeField.getSelectionEnd();
			if (x == 1)
			{
				paymentinfo.setZipCode(codeField.getText());
			}
			else{
				if (i == 0 && j == 0){
					paymentinfo.setZipCode("");
					}
				else
					{
					paymentinfo.setZipCode(codeField.getText());
					}
			}
			i = locationField.getSelectionStart();
			j = locationField.getSelectionEnd();
			if (y == 1)
			{
				paymentinfo.setCity(locationField.getText());
			}
			else{
				if (i == 0 && j == 0){
						paymentinfo.setCity("");
					}
				else
					{
						paymentinfo.setCity(locationField.getText());
					}
			}
			i = countryField.getSelectionStart();
			j = countryField.getSelectionEnd();
			if (z == 1)
			{
				paymentinfo.setCountry(countryField.getText());
			}
			else{
				if (i == 0 && j == 0){
						paymentinfo.setCountry("");
					}
				else
					{
						paymentinfo.setCountry(countryField.getText());
					}
			}
			paymentinfo.setAdditionalStreet(additiveField.getText());			
			user.setPaymentInfo(paymentinfo);
			
			user.setStorageLimit(storageLimitField.getSelectedItem().toString());

			if (userApi.changeAccountingSettings(user)) {
				if(paymentIndex >= storageLimitField.getSelectedIndex()){
					frame.setVisible(false);
					optionPane.showMessageDialog("Die Änderung war erfolgreich");	
					}
					else{
							if(user.getPaymentInfo().getStreet().length() == 0 || user.getPaymentInfo().getCity().length() == 0 ||
									user.getPaymentInfo().getZipCode().length() == 0 || user.getPaymentInfo().getCountry().length() == 0){
								optionPane.showMessageDialog("Sie müssen erst die Zahlungsinformationen angeben, bevor sie ihre Speicherkapazität erhöhen können!");
							}
							else{
								frame.setVisible(false);
								optionPane.showMessageDialog("Sie müssen einen Betrag bezahlen, damit sie ihre Speicherkapazität erhöhen können!");
							}
					}
				}
					else {
						optionPane.showMessageDialog("Das ändern der Daten ist fehlgeschlagen!");
					}
			}
		};
	
	/*
	 * Ein einfacher Abbrechen Button, der das Fenster schließt und nichts ändert.
	 */
	public Action stop = new AbstractAction() {
		public void actionPerformed( ActionEvent event ) {
				frame.setVisible(false);
				optionPane.showMessageDialog("Sie haben den Vorgang abgebrochen!");
		}
	};

	/*
	 * Prüfen was in der ComboBox ausgewählt wurde
	 */
	public Action selectBoxAction = new AbstractAction() {
		public void actionPerformed(ActionEvent e) {
			System.out.println( ((JComboBox) e.getSource()).getSelectedItem().toString() );
		}
	};
}
