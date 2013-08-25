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
public class EditProfileController {
	private final OptionPaneHelper optionPane;
	private final UserAPI userAPI;

	private JFrame frame;
	public JTextField lastnameField;
	public JTextField firstnameField;
	public JTextField genderField;

	/**
	 * Erstellt einen neuen EditProfileController.<br/>
	 * Instanzen dieser Klasse sollten per Dependency Injection durch Guice erstellt werden.
	 *
	 * @param optionPaneHelper Ein OptionPaneHelper zum Anzeigen von Dialog-Fenstern.
	 * @param userAPI          Die UserAPI zur Kommunikation mit dem Server.
	 */
	@Inject
	EditProfileController(OptionPaneHelper optionPaneHelper,
						  UserAPI userAPI) {
		this.optionPane = optionPaneHelper;
		this.userAPI = userAPI;
	}

	/**
	 * Öffnen des Fensters für Profilbearbeitung. Hierbei werden die Textfelder, nach Überprüfung, mit den <br/>
	 * bereits bekannten Informationen ausgefüllt.
	 */
	public void show() {
		frame = (JFrame) new SwingEngineHelper().render(this, "user/editProfile");
		frame.setVisible(true);

		User user = userAPI.getCurrentUser();

		lastnameField.setText(user.getLastname());
		firstnameField.setText(user.getFirstname());
		genderField.setText(user.getGender());
	}

	/**
	 * Speichern der Änderungen an den Profil-Informationen. Für jedes Feld wird überprüft, ob eine Eingabe optional<br/>
	 * ist oder nicht (z.B.: >5GB Speicherplatz). Wenn eine Eingabe optional ist, kann trotzdem eine Information<br/>
	 * eingetragen werden (wird gespeichert).
	 */
	public Action save = new AbstractAction() {
		public void actionPerformed(ActionEvent event) {
			User user = new User();

			user.setFirstname(firstnameField.getText());
			user.setLastname(lastnameField.getText());
			user.setLastname(lastnameField.getText());
			user.setGender(genderField.getText());

			if (userAPI.changeProfile(user)) {
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
