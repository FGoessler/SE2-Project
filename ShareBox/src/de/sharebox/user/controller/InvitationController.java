package de.sharebox.user.controller;

import com.google.inject.Inject;
import de.sharebox.api.UserAPI;
import de.sharebox.helpers.OptionPaneHelper;
import de.sharebox.user.model.User;

import static com.google.common.base.Strings.isNullOrEmpty;

public class InvitationController {
	/**
	 * RegEx-Pattern zum Erkennen einer (vom Format her) gültigen Email-Addresse.
	 */
	private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

	private final OptionPaneHelper optionPane;
	private final UserAPI userAPI;

	/**
	 * Erstellt einen neuen InvitationController.<br/>
	 * Instanzen dieser Klasse solten per Dependency Injection durch Guice erstellt werden.
	 *
	 * @param optionPaneHelper Ein OptionPaneHelper zum Anzeigen von Dialog-Fenstern
	 * @param userAPI          Die UserAPI zur Kommunikation mit dem Server.
	 */
	@Inject
	InvitationController(final OptionPaneHelper optionPaneHelper,
						 final UserAPI userAPI) {
		this.optionPane = optionPaneHelper;
		this.userAPI = userAPI;
	}

	/**
	 * Öffnen des Einladen Dialog-Fensters.
	 */
	public void show() {
		final String newUserMail = optionPane.showInputDialog("Bitte geben Sie die Emailadresse der Person ein, die Sie zu Sharebox Ultimate einladen möchten.", "");

		if (!isNullOrEmpty(newUserMail) && newUserMail.matches(EMAIL_PATTERN)) {
			final User invitedUser = new User();
			invitedUser.setEmail(newUserMail);

			if (userAPI.inviteUser(userAPI.getCurrentUser(), invitedUser)) {
				optionPane.showMessageDialog(invitedUser.getEmail() + " wurde eingeladen!");
			} else {
				optionPane.showMessageDialog(invitedUser.getEmail() + " ist bereits registriert.");
			}
		} else {
			optionPane.showMessageDialog("Die eingegebene Emailadresse war ungültig!");
		}
	}

}
