package de.sharebox.api;

import de.sharebox.file.model.FEntry;

import java.text.DateFormat;
import java.util.Date;

/*
 * TODO Klassenbeschreibung (Die Klasse APILogger ist für das Loggen von API-Aktionen?)
 */
public final class APILogger {

    /** 
     * Logging Flag.
     */
	public static final boolean LOGGING = true;
    /**
     * Flag für Datumsausgabe: TRUE wenn volles Datumsformat gewünscht wird, FALSE sonst.
     */
    public static final boolean READABLEDATE = false;

	private APILogger() {
	}

	/**
	 * Loggt die gegebene Meldung.
	 *
	 * @param message Die zu loggende Meldung.
	 */
	public static void logMessage(final String message) {
		if (LOGGING) {
			System.out.println("["+System.currentTimeMillis()+"]"+DateFormat.getDateTimeInstance().format(new Date(System.currentTimeMillis())) + ": " + message);
		}
	}

	/**
	 * Erstellt einen Action String (zur Verwendung in logSuccess und CO.) der die gegebene Action und den FEntry benennt.
	 *
	 * @param action Die Aktion, die geloggt werden soll.
	 * @param fEntry Der FEntry, auf dem die Aktion ausgeführt wurde.
	 * @return Der String der die Aktion und Information über den FEntry enthält.
	 */
	public static String actionStringForFEntryAction(final String action, final FEntry fEntry) {
		return action + " (" + fEntry.getName() + ")";
	}

	/**
	 * Gibt eine Log Message aus, die besagt, dass die übergebene Aktion erfolgreich war.
	 *
	 * @param action Ein Text der die Aktion benennt.
	 */
	public static void logSuccess(final String action) {
		logMessage(action + " successful.");
	}

	/**
	 * Gibt eine Log Message aus, die besagt, dass die übergebene Aktion fehlgeschalgen ist.
	 *
	 * @param action Ein Text der die Aktion benennt.
	 */
	public static void logFailure(final String action) {
		logMessage(action + " failed.");
	}

	/**
	 * Gibt eine Log Message aus, die besagt, dass die übergebene Aktion wegen der "Reason" fehlgeschlagen ist.
	 *
	 * @param action Ein Text der die Aktion benennt.
	 * @param reason Ein Text der den Grund für den Fehler benennt.
	 */
	public static void logFailure(final String action, final String reason) {
		logMessage(action + " failed. Reason: " + reason);
	}

	/**
	 * Gibt eine Log Message aus, die basierend auf dem "success"-parameter besagt ob die übergebene Aktion
	 * fehlgeschlagen ist oder erfolgreich war.
	 *
	 * @param action  Ein Text der die Aktion benennt.
	 * @param success True wenn die Aktion als erfolgreich geloggt werden soll, False wenn sie als fehlgeschlagen
	 *                geloggt werden soll.
	 */
	public static void logResult(final String action, final Boolean success) {
		if (success) {
			APILogger.logSuccess(action);
		} else {
			APILogger.logFailure(action);
		}
	}
}