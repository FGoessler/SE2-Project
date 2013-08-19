package de.sharebox.user.controller;

import de.sharebox.api.UserAPI;
import de.sharebox.helpers.OptionPaneHelper;
import de.sharebox.user.model.PaymentInfo;
import de.sharebox.user.model.User;
import org.swixml.SwingEngine;

import javax.swing.*;
import java.awt.event.ActionEvent;

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
	protected OptionPaneHelper optionPane = new OptionPaneHelper();


	public RegisterController() {
		try {
			SwingEngine swix = new SwingEngine(this);
			frame = (JFrame) swix.render("resources/xml/register.xml");
			frame.setVisible(true);
		} catch (Exception exception) {
			System.out.println("Couldn't create register window!");
		}
	}

	public Action register = new AbstractAction() {
		public void actionPerformed(ActionEvent event) {
			UserAPI userApi = UserAPI.getUniqueInstance();
			User user = new User();
			user.setEmail(mailField.getText());
			user.setPassword(passwordField1.toString());
			user.setPassword(passwordField2.toString());
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
				frame.setVisible(false);
				optionPane.showMessageDialog("Ihre Registrierung war erfolgreich!");
				
			} else {
				optionPane.showMessageDialog("Ihre Registrierung ist fehlgeschlagen! Ihre E-Mail Adresse ist bereits vergeben.");
			}
		}
	};

	public Action selectBoxAction = new AbstractAction() {
		public void actionPerformed(ActionEvent e) {
			System.out.println(((JComboBox) e.getSource()).getSelectedItem().toString());
		}
	};

}

		