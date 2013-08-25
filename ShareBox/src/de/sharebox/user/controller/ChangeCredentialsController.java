package de.sharebox.user.controller;

import com.google.inject.Inject;
import de.sharebox.api.UserAPI;
import de.sharebox.helpers.OptionPaneHelper;
import de.sharebox.helpers.SwingEngineHelper;
import de.sharebox.user.model.User;

import javax.swing.*;
import java.awt.event.ActionEvent;

/** 
 * TODO Klassenbeschreibung
 */
public class ChangeCredentialsController {
	private final OptionPaneHelper optionPane;
	private final UserAPI userAPI;

	private JFrame frame;
	public JTextField eMailField;
	public JPasswordField oldPasswordField;
	public JPasswordField newPasswordField;
	public JPasswordField newPasswordField1;

	/**
	 * Erstellt einen neuen ChangeCredentialsController.<br/>
	 * Instanzen dieser Klasse sollten per Dependency Injection durch Guice erstellt werden.
	 *
	 * @param optionPaneHelper Ein OptionPaneHelper zum Anzeigen von Dialog-Fenstern.
	 * @param userAPI          Die UserAPI zur Kommunikation mit dem Server.
	 */
	@Inject
	ChangeCredentialsController(OptionPaneHelper optionPaneHelper,
								UserAPI userAPI) {
		this.optionPane = optionPaneHelper;
		this.userAPI = userAPI;
	}

	/**
	 * Öffnen des Fensters für die Bearbeitung der Login-Daten. Hierbei werden die Textfelder, nach Überprüfung, mit den <br/>
	 * bereits bekannten Informationen ausgefüllt.
	 */
	public void show() {
		frame = (JFrame) new SwingEngineHelper().render(this, "user/changeCredentials");
		frame.setVisible(true);

		User user = userAPI.getCurrentUser();
		eMailField.setText(user.getEmail());
	}

	/**
	 * Speichern der Änderungen der Login-Informationen. Für jedes Feld wird überprüft, ob eine Eingabe optional<br/>
	 * ist oder nicht (z.B.: >5GB Speicherplatz). Wenn eine Eingabe optional ist, kann trotzdem eine Information<br/>
	 * eingetragen werden (wird gespeichert). Hierbei wird weiterhin geprüft, ob die eingegebene E-Mail-Adresse<br/>
	 * mit der Login-Adresse übereinstimmt.<br/>
	 * Die E-Mail-Adresse kann hier als Einziges nicht verändert werden. Außerdem wird überprüft, ob das neue und das<br/>
	 * alte Passwort nicht gleich sind und dass die Passwörter in den zwei Neu-Feldern übereinstimmen.
	 */
	public Action save = new AbstractAction() {
		public void actionPerformed(ActionEvent save) {
			User newUserData = new User();

			newUserData.setEmail(eMailField.getText());
			if (new String(newPasswordField.getPassword()).equals(new String(newPasswordField1.getPassword()))) {
				newUserData.setPassword(new String(newPasswordField.getPassword()));
			}

			User oldUserData = userAPI.getCurrentUser();
			oldUserData.setPassword(new String(oldPasswordField.getPassword()));
			if (userAPI.changeCredential(oldUserData, newUserData)) {
				frame.setVisible(false);
				optionPane.showMessageDialog("Ihre Änderungen wurden gespeichert!");
			} else {
				optionPane.showMessageDialog("Das Ändern der Daten ist fehlgeschlagen!");
			}
		}
	};

	/**
	 * Ein einfacher Button zum Abbrechen, der das Fenster ohne Änderungen schließt.
	 */
	public Action stop = new AbstractAction() {
		public void actionPerformed(ActionEvent event) {
			frame.setVisible(false);
			optionPane.showMessageDialog("Der Vorgang wurde abgebrochen!");
		}
	};
}
