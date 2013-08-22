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

public class RegisterController {
	private JFrame frame;
	public JTextField mailField;
	public JPasswordField passwordField1;
	public JPasswordField passwordField2;
	public JTextField lastnameField;
	public JTextField firstnameField;
	public JTextField genderField;
	public JComboBox storageLimitField;
	public JTextField streetField;
	public JTextField additiveField;
	public JTextField codeField;
	public JTextField locationField;
	public JTextField countryField;
	int i;
	int j; 
	String hilf;
	String hilf2;

	protected OptionPaneHelper optionPane = new OptionPaneHelper();

	/*
	 * Öffnen des Registrieren Fensters. 
	 */

	public RegisterController() {
		try {
			SwingEngine swix = new SwingEngine(this);
			frame = (JFrame) swix.render("resources/xml/register.xml");
			frame.setVisible(true);
		} catch (Exception exception) {
			System.out.println("Couldn't create register window!");
		}
	}

	/*
	 * Speichern der eingegebenen Informationen. Hierbei sind das E-Mail- und das Passwortfeld Pflicht. 
	 * Alle anderen Felder können zunächst leer bleiben.
	 * Wenn der Nutzer eine Speicherapazität von mehr als 5GB auswählt, wird er aufgefordert sie Zahlunngsinformationen
	 * anzugeben und anschließend wird er an das Bezahlsystem weitergeleitet.
	 */
	
	public Action register = new AbstractAction() {
		public void actionPerformed(ActionEvent event) {
			UserAPI userApi = UserAPI.getUniqueInstance();
			User user = new User();
			
			i = mailField.getSelectionStart();
			j = mailField.getSelectionEnd();
			if (i == 0 && j == 0){
				user.setEmail("");
				}
			else
				{
					user.setEmail(mailField.getText());
				}
			i = passwordField1.getSelectionStart();
			j = passwordField1.getSelectionEnd();
			if (i == 0 && j == 0){
				user.setPassword("");
				}
			else
				{
				user.setPassword(new String(passwordField1.getPassword()));
				hilf = new String(passwordField1.getPassword());
				hilf2 = new String(passwordField2.getPassword());
				if (hilf.equals(hilf2)){
					
					}
				else 
					{
						user.setPassword("");
					}
				}
			user.setLastname(lastnameField.getText());
			user.setFirstname(firstnameField.getText());
			user.setGender(genderField.getText());

			PaymentInfo paymentinfo = user.getPaymentInfo();
			paymentinfo.setStreet(streetField.getText());
			paymentinfo.setAdditionalStreet(additiveField.getText());
			paymentinfo.setZipCode(codeField.getText());
			paymentinfo.setCity(locationField.getText());
			paymentinfo.setCountry(countryField.getText());
			user.setPaymentInfo(paymentinfo);

			user.setStorageLimit(storageLimitField.getSelectedItem().toString());

			if (userApi.registerUser(user)) {
				if(0 >= storageLimitField.getSelectedIndex()){
					frame.setVisible(false);
					optionPane.showMessageDialog("Die Registrirung war erfolgreich");	
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
						optionPane.showMessageDialog("Die Registrierung ist fehlgeschlagen!");
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
			System.out.println(((JComboBox) e.getSource()).getSelectedItem().toString());
		}
	};

}

		