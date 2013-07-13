package de.sharebox.api;

import de.sharebox.file.model.FEntry;

import java.text.DateFormat;
import java.util.Date;

public final class APILogger {
	/**
	 * Logging flag.
	 */
	public static final boolean LOGGING = true;

	private APILogger() {}

	/**
	 * Loggt die gegebene Meldung.
	 * @param message Die zu loggende Meldung.
	 */
	public static void logMessage(String message) {
		if(LOGGING) {
			System.out.println(DateFormat.getDateTimeInstance().format(new Date(System.currentTimeMillis())) + ": " + message);
		}
	}

	/**
	 * Erstellt einen Action String (zur Verwendung in debugSuccess und CO.) der die gegebene Action und den FEntry benennt.
	 * @param action Die Aktion, die geloggt werden soll.
	 * @param fEntry Der FEntry auf dem die Aktion ausgeführt wurde.
	 * @return Der String der die Aktion und Information über den FEntry enthält.
	 */
	public static String actionStringForFEntryAction(String action, FEntry fEntry) {
		return action + " (" + fEntry.getName() + ")";
	}

	/**
	 * Gibt eine Log Message aus, die besagt das die übergebene Aktion erfolgreich war.
	 * @param action Ein Text der die AKtion benennt.
	 */
	public static void debugSuccess(String action) {
		if (LOGGING) {
			System.out.println(DateFormat.getDateTimeInstance().format(new Date(System.currentTimeMillis())) + ": " + action + " successful.");
		}
	}

	/**
	 * Gibt eine Log Message aus, die besagt das die übergebene Aktion fehlgeschalgen ist.
	 * @param action Ein Text der die AKtion benennt.
	 */
	public static void debugFailure(String action) {
		if (LOGGING) {
			System.out.println(DateFormat.getDateTimeInstance().format(new Date(System.currentTimeMillis())) + ": " + action + " failed.");
		}
	}

	/**
	 * Gibt eine Log Message aus, die besagt das die übergebene Aktion wegen der Reason fehlgeschlagen ist.
	 * @param action Ein Text der die AKtion benennt.
	 * @param reason Ein Text der den Grund für den Fehler benennt.
	 */
	public static void debugFailure(String action, String reason) {
		if (LOGGING) {
			System.out.println(DateFormat.getDateTimeInstance().format(new Date(System.currentTimeMillis())) + ": " + action + " failed. Reason: " + reason);
		}
	}
}