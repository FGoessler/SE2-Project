package de.sharebox.file.services;

import com.google.common.base.Joiner;
import com.google.inject.Inject;
import de.sharebox.api.UserAPI;
import de.sharebox.file.model.FEntry;
import de.sharebox.helpers.OptionPaneHelper;
import de.sharebox.user.model.User;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * Dieser Klasse stellt Methoden zur Verfügung um FEntries für andere Nutzer freizugeben.
 */
public class SharingService {
	/**
	 * RegEx-Pattern zum Erkennen einer korrekt formatierten E-Mail-Addresse.
	 */
	private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

	private final OptionPaneHelper optionPane;
	private final UserAPI userAPI;

	/**
	 * Erstellt einen neuen SharingService.<br/>
	 * Instanzen dieser Klasse sollten per Dependency Injection durch Guice erstellt werden.
	 *
	 * @param optionPaneHelper Ein OptionPaneHelper zum Erstellen von Dialog-Fenstern.
	 * @param userAPI          Die UserAPI. Wird hier benötigt um Nutzer einladen zu können.
	 */
	@Inject
	SharingService(final OptionPaneHelper optionPaneHelper,
				   final UserAPI userAPI) {
		this.optionPane = optionPaneHelper;
		this.userAPI = userAPI;
	}

	/**
	 * Zeigt einen Input-Dialog an und gibt den gegebenen FEntry für den Nutzer mit der im Dialog eingegebenen
	 * E-Mail-Adresse frei. Es werden Lese- und Schreibrechte gewährt.
	 *
	 * @param fEntry Der freizugebende FEntry.
	 */
	public void showShareFEntryDialog(final FEntry fEntry) {
		final List<FEntry> fEntries = new ArrayList<FEntry>();
		fEntries.add(fEntry);
		showShareFEntryDialog(fEntries);
	}

	/**
	 * Zeigt einen Input-Dialog an und gibt die gegebenen FEntries für den Nutzer mit der im Dialog eingegebenen
	 * E-Mail-Adresse frei. Es werden Lese- und Schreibrechte gewährt.
	 *
	 * @param fEntries Der freizugebende FEntry.
	 */
	public void showShareFEntryDialog(final List<FEntry> fEntries) {
		final String newUserMail = optionPane.showInputDialog("Bitte geben Sie die Emailadresse des Benutzers ein für den Sie diese Datei/Verzeichnis freigeben möchten", "");

		if (!isNullOrEmpty(newUserMail) && newUserMail.matches(EMAIL_PATTERN)) {
			final User newUser = new User();
			newUser.setEmail(newUserMail);

			userAPI.inviteUser(userAPI.getCurrentUser(), newUser);

			final List<String> namesOfNotChangedFEntries = new ArrayList<String>();
			for (final FEntry fEntry : fEntries) {
				if (fEntry.getPermissionOfCurrentUser().getManageAllowed()) {
					fEntry.setPermission(newUser, true, true, false);
				} else {
					namesOfNotChangedFEntries.add(fEntry.getName());
				}
			}
			if (!namesOfNotChangedFEntries.isEmpty()) {
				optionPane.showMessageDialog("Folgende Dateien/Verzeichnisse konnten nicht freigegeben werden: " + Joiner.on(", ").skipNulls().join(namesOfNotChangedFEntries));
			}
		} else {
			optionPane.showMessageDialog("Die eingegebene Emailadresse war ungültig!");
		}
	}
}
