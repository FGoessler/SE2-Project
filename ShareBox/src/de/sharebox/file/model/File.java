package de.sharebox.file.model;

import de.sharebox.api.UserAPI;
import de.sharebox.user.model.User;

/**
 * Diese Klasse repräsentiert eine Datei, die von der Sharebox verwaltet und mit dem Server synchronisiert wird.
 */
public class File extends FEntry {

	/**
	 * Der Standard-Konstruktor.
	 *
	 * @param userAPI Die aktuell für diesen FEntry relevante UserAPI. Wird dazu benötigt, um den aktuell eingeloggten
	 *                Nutzer zu bestimmen und Rechte zu überprüfen.
	 */
	public File(final UserAPI userAPI) {
		super(userAPI);
	}

	/**
	 * Erstellt ein neues File mit den gegebenen Werten, feuert dabei allerdings keine Notifications und erstellt
	 * auch nur einen "Created"-LogEntry, anstatt eines "Renamed"- und "PermissionChanged"-LogEntry.
	 *
	 * @param userAPI      Die aktuell für diesen File relevante UserAPI. Wird dazu benötigt, um den aktuell eingeloggten
	 *                	   Nutzer zu bestimmen und Rechte zu überprüfen.
	 * @param name         Der Name des Files.
	 * @param creatingUser Der Nutzer, der initial alle Rechte auf diesem File erhält.
	 */
	public File(final UserAPI userAPI, final String name, final User creatingUser) {
		super(userAPI, name, creatingUser);
	}

	/**
	 * Copy Konstruktor
	 *
	 * @param sourceFile Das Quell-Objekt.
	 */
	public File(final File sourceFile) {
		super(sourceFile);
	}

}
