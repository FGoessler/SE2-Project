package de.sharebox.user.controller;

import de.sharebox.api.UserAPI;
import de.sharebox.helpers.OptionPaneHelper;
import de.sharebox.user.PaymentInfo;
import de.sharebox.user.User;
import org.swixml.SwingEngine;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class RegisterController {
	private JFrame frame;
	public transient JTextField eMailField;
	public transient JPasswordField password1Field;
	public transient JTextField password2Field;
	public transient JTextField lastnameField;
	public transient JTextField firstnameField;
	public transient JTextField genderField;
	public transient JComboBox storageLimitField;
	public transient JTextField streetField;
	public transient JTextField additiveField;
	public transient JTextField codeField;
	public transient JTextField locationField;
	public transient JTextField countryField;
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
			frame.setVisible(false);
			UserAPI userApi = UserAPI.getUniqueInstance();
			User user = new User();
			user.setEmail(eMailField.getText());
			user.setPassword(password1Field.toString());
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
			String msg = "MESSAGE";
			optionPane.showMessageDialog(msg);
			if (userApi.registerUser(user)) {
				optionPane.showMessageDialog("Ihre Registrierung war erfolgreich!");
				frame.setVisible(false);
			} else {
				optionPane.showMessageDialog("Ihre Registrierung ist fehlgeschlagen!");
			}
			frame.setVisible(false);
		}
	};

	public Action selectBoxAction = new AbstractAction() {
		public void actionPerformed(ActionEvent e) {
			System.out.println(((JComboBox) e.getSource()).getSelectedItem().toString());
		}
	};

}

		