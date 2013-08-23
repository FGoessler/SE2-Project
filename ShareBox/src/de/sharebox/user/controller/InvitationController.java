package de.sharebox.user.controller;

import com.google.inject.Inject;
import de.sharebox.api.UserAPI;
import de.sharebox.helpers.OptionPaneHelper;
import de.sharebox.helpers.SwingEngineHelper;
import de.sharebox.user.model.User;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class InvitationController {
	private final OptionPaneHelper optionPane;

	private JFrame frame;
	public JTextField mailField;

	/**
	 * Erstellt einen neuen InvitationController.<br/>
	 * Instanzen dieser Klasse solten per Dependency Injection durch Guice erstellt werden.
	 *
	 * @param optionPaneHelper Ein OptionPaneHelper zum Anzeigen von Dialog-Fenstern
	 */
	@Inject
	InvitationController(OptionPaneHelper optionPaneHelper) {
		this.optionPane = optionPaneHelper;
	}

	/**
	 * Öffnen des Einladen Fensters.
	 */
	public void show() {
		frame = (JFrame) new SwingEngineHelper().render(this, "invite");
		frame.setVisible(true);
	}

	/**
	 * Handler um auf die Auswahl das "Einladen"-Buttons zu reagieren.
	 * Die eingegebene E-Mail Adresse wird eingeladen, außerdem wird geprüft, ob die E-Mail Adresse bereits im System
	 * bekannt ist.
	 */
	public Action invite = new AbstractAction() {
		public void actionPerformed(ActionEvent event) {
			User invitedUser = new User();
			invitedUser.setEmail(mailField.getText());

			if (UserAPI.getUniqueInstance().inviteUser(UserAPI.getUniqueInstance().getCurrentUser(), invitedUser)) {
				frame.setVisible(false);
				optionPane.showMessageDialog(invitedUser.getEmail() + " wurde eingeladen!");
			} else {
				optionPane.showMessageDialog(invitedUser.getEmail() + " ist bereits registriert oder die Emailadresse ist ungültig!");
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
