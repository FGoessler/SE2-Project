package de.sharebox.file.services;

import com.google.inject.Inject;
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
	 * RegEx-Pattern zum Erkennen einer (vom Format her) gültigen Email-Addresse.
	 */
	private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

	private final OptionPaneHelper optionPaneHelper;

	/**
	 * Erstellt einen neuen SharingService.<br/>
	 * Instanzen dieser Klasse solten per Dependency Injection durch Guice erstellt werden.
	 *
	 * @param optionPaneHelper Ein OptionPaneHelper zum Erstellen von Dialog-Fenstern.
	 */
	@Inject
	SharingService(OptionPaneHelper optionPaneHelper) {
		this.optionPaneHelper = optionPaneHelper;
	}

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

		if (!isNullOrEmpty(newUserMail) && newUserMail.matches(EMAIL_PATTERN)) {
			//TODO: call API to invite/set permissions for the user!
			User newUser = new User();
			newUser.setEmail(newUserMail);

			for (FEntry fEntry : fEntries) {
				fEntry.setPermission(newUser, true, true, false);		//TODO: evaluate success
			}
		}
	}
}
