package de.sharebox.file.model;

import de.sharebox.api.UserAPI;

/**
 * Diese Klasse repräsentiert eine Datei, die von der Sharebox verwaltet und mit dem Server synchronisiert wird.
 */
public class File extends FEntry {

	/**
	 * Der Standard-Konstruktor.
	 *
	 * @param userAPI Die aktuell für diesen FEntry relevante UserAPI. Wird dazu benötigt den aktuell eingeloggten
	 *                Nutzer zu bestimmen und Rechte zu überprüfen.
	 */
	public File(UserAPI userAPI) {
		super(userAPI);
	}

	/**
	 * Copy Konstruktor
	 *
	 * @param sourceFile Das Quell-Objekt.
	 */
	public File(File sourceFile) {
		super(sourceFile);
	}

}
