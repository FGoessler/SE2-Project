package de.sharebox.file.controller;

import de.sharebox.file.model.FEntry;
import de.sharebox.helpers.OptionPaneHelper;
import de.sharebox.user.model.User;

public class SharingService {
	protected OptionPaneHelper optionPaneHelper = new OptionPaneHelper();

	public void showShareFEntryDialog(FEntry fEntry) {
		String newUserMail = optionPaneHelper.showInputDialog("Bitte geben Sie die Emailadresse des Benutzers ein für den Sie diese Datei/Verzeichnis freigeben möchten", "");

		//TODO: call API to invite/set permissions for the user!
		User newUser = new User();
		newUser.setEmail(newUserMail);

		fEntry.setPermission(newUser, true, true, false);
	}
}
