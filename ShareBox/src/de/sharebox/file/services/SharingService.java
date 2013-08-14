package de.sharebox.file.services;

import de.sharebox.file.model.FEntry;
import de.sharebox.helpers.OptionPaneHelper;
import de.sharebox.user.model.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Dieser Klasse stellt Methoden zur Verfügung um FEntries für andere Nutzer freizugeben.
 */
public class SharingService {
	private static final String EMAIL_PATTERN ="^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

	protected OptionPaneHelper optionPaneHelper = new OptionPaneHelper();

	/**
	 * Zeigt einen Input-Dialog an und gibt den gegebenen FEntry für den Nutzer mit der im Dialog eingegebenen
	 * Mailadresse frei. Es werden Lese- und Schreibrechte gewährt.
	 *
	 * @param fEntry Der freizugebende FEntry.
	 */
	public void showShareFEntryDialog(FEntry fEntry) {
		List<FEntry> fEntries = new ArrayList<FEntry>();
		fEntries.add(fEntry);
		showShareFEntryDialog(fEntries);
	}

	/**
	 * Zeigt einen Input-Dialog an und gibt die gegebenen FEntries für den Nutzer mit der im Dialog eingegebenen
	 * Mailadresse frei. Es werden Lese- und Schreibrechte gewährt.
	 *
	 * @param fEntries Der freizugebende FEntry.
	 */
	public void showShareFEntryDialog(List<FEntry> fEntries) {
		String newUserMail = optionPaneHelper.showInputDialog("Bitte geben Sie die Emailadresse des Benutzers ein für den Sie diese Datei/Verzeichnis freigeben möchten", "");

		if (newUserMail != null && !newUserMail.equals("") && newUserMail.matches(EMAIL_PATTERN)) {
			//TODO: call API to invite/set permissions for the user!
			User newUser = new User();
			newUser.setEmail(newUserMail);

			for (FEntry fEntry : fEntries) {
				fEntry.setPermission(newUser, true, true, false);
			}
		}
	}
}
