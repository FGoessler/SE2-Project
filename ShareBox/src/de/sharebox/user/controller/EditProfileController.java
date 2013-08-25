package de.sharebox.user.controller;

import com.google.inject.Inject;
import de.sharebox.api.UserAPI;
import de.sharebox.helpers.OptionPaneHelper;
import de.sharebox.helpers.SwingEngineHelper;
import de.sharebox.user.enums.Gender;
import de.sharebox.user.model.User;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class EditProfileController {
	private final OptionPaneHelper optionPane;
	private final UserAPI userAPI;

	private JFrame frame;
	protected JTextField lastnameField;
	protected JTextField firstnameField;
	protected JComboBox<Gender> genderField;

	/**
	 * Erstellt einen neuen EditProfileController.<br/>
	 * Instanzen dieser Klasse solten per Dependency Injection durch Guice erstellt werden.
	 *
	 * @param optionPaneHelper Ein OptionPaneHelper zum Anzeigen von Dialog-Fenstern.
	 * @param userAPI          Die UserAPI zur Kommunikation mit dem Server.
	 */
	@Inject
	EditProfileController(final OptionPaneHelper optionPaneHelper,
						  final UserAPI userAPI) {
		this.optionPane = optionPaneHelper;
		this.userAPI = userAPI;
	}

	/**
	 * Öffnen des Profil-bearbeiten Fensters. Hierbei werden alle bereits bekannten Informationen in
	 * die TextFields direkt mit übernommen. Außerdem wird überprüft,ob bereits Informationen zu den einzelnen Feldern
	 * vorhanden waren oder nicht.
	 */
	public void show() {
		frame = (JFrame) new SwingEngineHelper().render(this, "user/editProfile");
		frame.setVisible(true);

		final User user = userAPI.getCurrentUser();
		lastnameField.setText(user.getLastname());
		firstnameField.setText(user.getFirstname());
		genderField.setSelectedItem(user.getGender());
	}

	/**
	 * Speichern der Änderungen an den Profil Informationen. Für jedes einzelne Feld wird überprüft, ob etwas hinein
	 * geschrieben werden muss oder nicht. Wenn nichts drin stehen muss kann auch beim Speichern nichts drin stehen
	 * Wenn zuvor was drin Stand, dann muss auch wieder eine Information eingetragen werden.
	 */
	public Action save = new AbstractAction() {
		public void actionPerformed(final ActionEvent event) {
			final User user = new User();
			user.setFirstname(firstnameField.getText());
			user.setLastname(lastnameField.getText());
			user.setLastname(lastnameField.getText());
			user.setGender((Gender) genderField.getSelectedItem());

			if (userAPI.changeProfile(user)) {
				frame.setVisible(false);
				optionPane.showMessageDialog("Ihre Änderungen wurden gespeichert!");
			} else {
				optionPane.showMessageDialog("Das Ändern der Daten ist fehlgeschlagen!");
			}
		}
	};

	/**
	 * Ein einfacher Abbrechen Button, der das Fenster schließt und nichts ändert.
	 */
	public Action stop = new AbstractAction() {
		public void actionPerformed(final ActionEvent event) {
			frame.setVisible(false);
			optionPane.showMessageDialog("Sie haben den Vorgang abgebrochen!");
		}
	};
}
