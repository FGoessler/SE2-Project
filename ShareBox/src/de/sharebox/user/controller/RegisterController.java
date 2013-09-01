package de.sharebox.user.controller;

import com.google.inject.Inject;
import de.sharebox.api.UserAPI;
import de.sharebox.helpers.OptionPaneHelper;
import de.sharebox.helpers.SwingEngineHelper;
import de.sharebox.user.enums.Gender;
import de.sharebox.user.model.AddressInfo;
import de.sharebox.user.model.User;

import javax.swing.*;

/**
 * Dieser Controller ist verantwortlich für das Registrieren-Fenster mit dem sich ein Nutzer für Sharebox registrieren kann.
 */
public class RegisterController {
	private final OptionPaneHelper optionPane;
	private final UserAPI userAPI;

	private JFrame frame;
	protected JTextField mailField;
	protected JPasswordField passwordField1;
	protected JPasswordField passwordField2;
	protected JTextField lastnameField;
	protected JTextField firstnameField;
	protected JComboBox<Gender> genderField;
	protected JTextField streetField;
	protected JTextField additiveField;
	protected JTextField codeField;
	protected JTextField locationField;
	protected JTextField countryField;

	/**
	 * Erstellt einen neuen RegisterController.<br/>
	 * Instanzen dieser Klasse sollten per Dependency Injection durch Guice erstellt werden.
	 *
	 * @param optionPaneHelper Ein OptionPaneHelper zum Anzeigen von Dialog-Fenstern.
	 * @param userAPI          Die UserAPI zur Kommunikation mit dem Server.
	 */
	@Inject
	RegisterController(final OptionPaneHelper optionPaneHelper,
					   final UserAPI userAPI) {
		this.optionPane = optionPaneHelper;
		this.userAPI = userAPI;
	}

	/**
	 * Öffnen des Fensters zum Registrieren.
	 */
	public void show() {
		frame = (JFrame) new SwingEngineHelper().render(this, "user/register");
		frame.setVisible(true);
	}

	/**
	 * Speichern der eingegebenen Informationen. Hierbei sind das E-Mail- und das Passwortfeld Pflicht.
	 * Alle anderen Felder können, zunächst, leer bleiben.<br/>
	 * Wenn der Nutzer eine Speicherkapazität von mehr als 5GB auswählt, wird er aufgefordert die Zahlungsinformationen
	 * einzugeben und anschließend wird er an das Bezahlsystem weitergeleitet.<br/>
	 * Wird per SWIxml an das GUI Element gebunden.
	 */
	public void register() {
		final User user = new User();

		user.setEmail(mailField.getText());

		user.setPassword(new String(passwordField1.getPassword()));

		user.setLastname(lastnameField.getText());
		user.setFirstname(firstnameField.getText());
		user.setGender((Gender) genderField.getSelectedItem());

		final AddressInfo addressInfo = user.getAddressInfo();
		addressInfo.setStreet(streetField.getText());
		addressInfo.setAdditionalAddressInfo(additiveField.getText());
		addressInfo.setZipCode(codeField.getText());
		addressInfo.setCity(locationField.getText());
		addressInfo.setCountry(countryField.getText());
		user.setAddressInfo(addressInfo);

		if (new String(passwordField2.getPassword()).equals(new String(passwordField1.getPassword())) &&
				userAPI.registerUser(user)) {
			frame.setVisible(false);
			optionPane.showMessageDialog("Die Registrierung war erfolgreich");
		} else {
			optionPane.showMessageDialog("Die Registrierung ist fehlgeschlagen!");
		}
	}

	/**
	 * Ein einfacher Button zum Abbrechen, der das Fenster ohne Änderungen schließt.<br/>
	 * Wird per SWIxml an das GUI Element gebunden.
	 */
	public void stop() {
		frame.setVisible(false);
		optionPane.showMessageDialog("Der Vorgang wurde abgebrochen!");
	}
}