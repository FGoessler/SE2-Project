package de.sharebox.api;

import de.sharebox.file.model.FEntry;

import java.text.DateFormat;
import java.util.Date;

/** 
 * TODO Klassenbeschreibung (Klasse für das Loggen von API-Aktionen?)
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

	private APILogger() {}

	/**
	 * Loggt die gegebene Meldung.
	 * @param message Die zu loggende Meldung.
	 */
	public static void logMessage(String message) {
		if(LOGGING) {
			if (READABLEDATE) System.out.println(DateFormat.getDateTimeInstance().format(new Date(System.currentTimeMillis())) + ": " + message);
            else System.out.println(System.currentTimeMillis() + ": " + message);
		}
	}

	/**
	 * Erstellt einen Action String (zur Verwendung in debugSuccess und CO.) der die gegebene Aktion und den FEntry benennt.
	 * @param action Die Aktion, die geloggt werden soll.
	 * @param fEntry Der FEntry auf dem die Aktion ausgeführt wurde.
	 * @return Der String der die Aktion und Information über den FEntry enthält.
	 */
	public static String actionStringForFEntryAction(String action, FEntry fEntry) {
		return action + " (" + fEntry.getName() + ")";
	}

	/**
	 * Gibt eine Log Message aus, die besagt das die übergebene Aktion erfolgreich war.
	 * @param action Ein String der die Aktion benennt.
	 */
	public static void debugSuccess(String action) {
		if (LOGGING) {
			if (READABLEDATE) System.out.println(DateFormat.getDateTimeInstance().format(new Date(System.currentTimeMillis())) + ": " + action + " successful.");
            else System.out.println(System.currentTimeMillis() + ": " + action + " successful.");
		}
	}

	/**
	 * Gibt eine Log Message aus, die besagt das die übergebene Aktion fehlgeschlagen ist.
	 * @param action Ein String der die Aktion benennt.
	 */
	public static void debugFailure(String action) {
		if (LOGGING) {
			if (READABLEDATE) System.out.println(DateFormat.getDateTimeInstance().format(new Date(System.currentTimeMillis())) + ": " + action + " failed.");
            else System.out.println(System.currentTimeMillis() + ": " + action + " failed.");
		}
	}

	/**
	 * Gibt eine Log Message aus, die besagt das die übergebene Aktion wegen der Reason fehlgeschlagen ist.
	 * @param action Ein String der die Aktion benennt.
	 * @param reason Ein String der den Fehlergrund benennt.
	 */
	public static void debugFailure(String action, String reason) {
		if (LOGGING) {
			if (READABLEDATE) System.out.println(DateFormat.getDateTimeInstance().format(new Date(System.currentTimeMillis())) + ": " + action + " failed. Reason: " + reason);
            else System.out.println(System.currentTimeMillis() + ": " + action + " failed. Reason: " + reason);
		}
	}
}