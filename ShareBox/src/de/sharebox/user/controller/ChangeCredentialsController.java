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
	private final UserAPI userAPI;

	private JFrame frame;
	protected JTextField eMailField;
	protected JPasswordField oldPasswordField;
	protected JPasswordField newPasswordField;
	protected JPasswordField newPasswordField1;

	/**
	 * Erstellt einen neuen ChangeCredentialsController.<br/>
	 * Instanzen dieser Klasse solten per Dependency Injection durch Guice erstellt werden.
	 *
	 * @param optionPaneHelper Ein OptionPaneHelper zum Anzeigen von Dialog-Fenstern.
	 * @param userAPI          Die UserAPI zur Kommunikation mit dem Server.
	 */
	@Inject
	ChangeCredentialsController(final OptionPaneHelper optionPaneHelper,
								final UserAPI userAPI) {
		this.optionPane = optionPaneHelper;
		this.userAPI = userAPI;
	}

	/**
	 * Öffnen des Login-Daten-bearbeiten Fensters. Hierbei werden alle bereits bekannten Informationen in
	 * die TextFields direkt mit übernommen. Außerdem wird überprüft,ob bereits Informationen zu den einzelnen Feldern
	 * vorhanden waren oder nicht.
	 */
	public void show() {
		frame = (JFrame) new SwingEngineHelper().render(this, "user/changeCredentials");
		frame.setVisible(true);

		eMailField.setText(userAPI.getCurrentUser().getEmail());
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
		public void actionPerformed(final ActionEvent event) {
			final User newUserData = new User();

			newUserData.setEmail(eMailField.getText());
			if (new String(newPasswordField.getPassword()).equals(new String(newPasswordField1.getPassword()))) {
				newUserData.setPassword(new String(newPasswordField.getPassword()));
			}

			final User oldUserData = userAPI.getCurrentUser();
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
	 * Ein einfacher Abbrechen Button, der das Fenster schließt und nichts ändert.
	 */
	public Action stop = new AbstractAction() {
		public void actionPerformed(final ActionEvent event) {
			frame.setVisible(false);
			optionPane.showMessageDialog("Sie haben den Vorgang abgebrochen!");
		}
	};
}
