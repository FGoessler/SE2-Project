package de.sharebox.user.controller;

import com.google.inject.Inject;
import de.sharebox.api.UserAPI;
import de.sharebox.helpers.OptionPaneHelper;
import de.sharebox.helpers.SwingEngineHelper;
import de.sharebox.user.model.AddressInfo;
import de.sharebox.user.model.User;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class RegisterController {
	private final OptionPaneHelper optionPane;
	private final UserAPI userAPI;

	private JFrame frame;
	public JTextField mailField;
	public JPasswordField passwordField1;
	public JPasswordField passwordField2;
	public JTextField lastnameField;
	public JTextField firstnameField;
	public JTextField genderField;
	public JTextField streetField;
	public JTextField additiveField;
	public JTextField codeField;
	public JTextField locationField;
	public JTextField countryField;

	/**
	 * Erstellt einen neuen RegisterController.<br/>
	 * Instanzen dieser Klasse solten per Dependency Injection durch Guice erstellt werden.
	 *
	 * @param optionPaneHelper Ein OptionPaneHelper zum Anzeigen von Dialog-Fenstern.
	 * @param userAPI          Die UserAPI zur Kommunikation mit dem Server.
	 */
	@Inject
	RegisterController(OptionPaneHelper optionPaneHelper,
					   UserAPI userAPI) {
		this.optionPane = optionPaneHelper;
		this.userAPI = userAPI;
	}

	/**
	 * Öffnen des Registrieren Fensters.
	 */
	public void show() {
		frame = (JFrame) new SwingEngineHelper().render(this, "user/register");
		frame.setVisible(true);
	}

	/**
	 * Speichern der eingegebenen Informationen. Hierbei sind das E-Mail- und das Passwortfeld Pflicht.
	 * Alle anderen Felder können zunächst leer bleiben.
	 * Wenn der Nutzer eine Speicherapazität von mehr als 5GB auswählt, wird er aufgefordert sie Zahlunngsinformationen
	 * anzugeben und anschließend wird er an das Bezahlsystem weitergeleitet.
	 */
	public Action register = new AbstractAction() {
		public void actionPerformed(ActionEvent event) {
			User user = new User();

			user.setEmail(mailField.getText());

			user.setPassword(new String(passwordField1.getPassword()));

			user.setLastname(lastnameField.getText());
			user.setFirstname(firstnameField.getText());
			user.setGender(genderField.getText());

			AddressInfo paymentinfo = user.getAddressInfo();
			paymentinfo.setStreet(streetField.getText());
			paymentinfo.setAdditionalStreet(additiveField.getText());
			paymentinfo.setZipCode(codeField.getText());
			paymentinfo.setCity(locationField.getText());
			paymentinfo.setCountry(countryField.getText());
			user.setAddressInfo(paymentinfo);

			if (new String(passwordField2.getPassword()).equals(new String(passwordField1.getPassword())) &&
					userAPI.registerUser(user)) {
				frame.setVisible(false);
				optionPane.showMessageDialog("Die Registrierung war erfolgreich");
			} else {
				optionPane.showMessageDialog("Die Registrierung ist fehlgeschlagen!");
			}
		}
	};

	/**
	 * Ein einfacher Abbrechen Button, der das Fenster schließt und nichts ändert.
	 */
	public Action stop = new AbstractAction() {
		public void actionPerformed(ActionEvent event) {
			frame.setVisible(false);
			optionPane.showMessageDialog("Sie haben den Vorgang abgebrochen!");
		}
	};
}