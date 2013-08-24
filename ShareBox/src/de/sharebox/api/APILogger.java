package de.sharebox.api;

import de.sharebox.file.model.FEntry;

import java.text.DateFormat;
import java.util.Date;

public final class APILogger {

	/**
	 * Logging flag.
	 */
	public static final boolean LOGGING = true;

	private APILogger() {
	}

	/**
	 * Loggt die gegebene Meldung.
	 *
	 * @param message Die zu loggende Meldung.
	 */
	public static void logMessage(String message) {
		if (LOGGING) {
			System.out.println(DateFormat.getDateTimeInstance().format(new Date(System.currentTimeMillis())) + ": " + message);
		}
	}

	/**
	 * Erstellt einen Action String (zur Verwendung in logSuccess und CO.) der die gegebene Action und den FEntry benennt.
	 *
	 * @param action Die Aktion, die geloggt werden soll.
	 * @param fEntry Der FEntry auf dem die Aktion ausgeführt wurde.
	 * @return Der String der die Aktion und Information über den FEntry enthält.
	 */
	public static String actionStringForFEntryAction(String action, FEntry fEntry) {
		return action + " (" + fEntry.getName() + ")";
	}

	/**
	 * Gibt eine Log Message aus, die besagt das die übergebene Aktion erfolgreich war.
	 *
	 * @param action Ein Text der die Aktion benennt.
	 */
	public static void logSuccess(String action) {
		logMessage(action + " successful.");
	}

	/**
	 * Gibt eine Log Message aus, die besagt das die übergebene Aktion fehlgeschalgen ist.
	 *
	 * @param action Ein Text der die Aktion benennt.
	 */
	public static void logFailure(String action) {
		logMessage(action + " failed.");
	}

	/**
	 * Gibt eine Log Message aus, die besagt das die übergebene Aktion wegen der Reason fehlgeschlagen ist.
	 *
	 * @param action Ein Text der die Aktion benennt.
	 * @param reason Ein Text der den Grund für den Fehler benennt.
	 */
	public static void logFailure(String action, String reason) {
		logMessage(action + " failed. Reason: " + reason);
	}

	/**
	 * Gibt eine Log Message aus, die bassierend auf dem success parameter besagt ob die übergebene Aktion
	 * fehlgeschlagen ist oder erfolgreich war.
	 *
	 * @param action  Ein Text der die Aktion benennt.
	 * @param success True wenn die Aktion als erfolgreich geloggt werden soll, Fales wenn sie als fehlgeschlagen
	 *                geloggt werden soll.
	 */
	public static void logResult(String action, Boolean success) {
		if (success) {
			APILogger.logSuccess(action);
		} else {
			APILogger.logFailure(action);
		}
	}
}