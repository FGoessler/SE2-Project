package de.sharebox.user.controller;

import com.google.inject.Inject;
import de.sharebox.api.UserAPI;
import de.sharebox.helpers.OptionPaneHelper;
import de.sharebox.helpers.SwingEngineHelper;
import de.sharebox.user.model.User;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class ChangeCredentialsController {
	private final OptionPaneHelper optionPane;

	private JFrame frame;
	public JTextField eMailField;
	public JPasswordField oldPasswordField;
	public JPasswordField newPasswordField;
	public JPasswordField newPasswordField1;

	/**
	 * Erstellt einen neuen ChangeCredentialsController.<br/>
	 * Instanzen dieser Klasse solten per Dependency Injection durch Guice erstellt werden.
	 *
	 * @param optionPaneHelper Ein OptionPaneHelper zum Anzeigen von Dialog-Fenstern.
	 */
	@Inject
	ChangeCredentialsController(OptionPaneHelper optionPaneHelper) {
		this.optionPane = optionPaneHelper;
	}

	/**
	 * Öffnen des Login-Daten-bearbeiten Fensters. Hierbei werden alle bereits bekannten Informationen in
	 * die TextFields direkt mit übernommen. Außerdem wird überprüft,ob bereits Informationen zu den einzelnen Feldern
	 * vorhanden waren oder nicht.
	 */
	public void show() {
		frame = (JFrame) new SwingEngineHelper().render(this, "changeCredentials");
		frame.setVisible(true);

		User user = UserAPI.getUniqueInstance().getCurrentUser();
		eMailField.setText(user.getEmail());
	}

	/**
	 * Speichern der Änderungen an den Login Informationen. Für jedes einzelne Feld wird überprüft, ob etwas hinein
	 * geschrieben werden muss oder nicht. Wenn nichts drin stehen muss kann auch beim Speichern nichts drin stehen
	 * Wenn zuvor was drin Stand, dann muss auch wieder eine Information eingetragen werden.
	 * Hierbei wird weiterhin getestet, ob die eigegeben E-Mail Adresse mit der Login-Adresse übereinstimmt.
	 * Die E-Mail Adresse kann als einziges nicht verändert werden.
	 * Außerdem wird das alte Passwort überprüft, darauf getestet, dass das neue und das alte Passwort nicht gleich sind
	 * und dass die Passwörter in den neuen Feldern übereinstimmen
	 */
	public Action save = new AbstractAction() {
		public void actionPerformed(ActionEvent save) {
			UserAPI userApi = UserAPI.getUniqueInstance();
			User newUserData = new User();

			newUserData.setEmail(eMailField.getText());
			if (new String(newPasswordField.getPassword()).equals(new String(newPasswordField1.getPassword()))) {
				newUserData.setPassword(new String(newPasswordField.getPassword()));
			}

			User oldUserData = userApi.getCurrentUser();
			oldUserData.setPassword(new String(oldPasswordField.getPassword()));
			if (userApi.changeCredential(oldUserData, newUserData)) {
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
		public void actionPerformed(ActionEvent event) {
			frame.setVisible(false);
			optionPane.showMessageDialog("Sie haben den Vorgang abgebrochen!");
		}
	};
}
