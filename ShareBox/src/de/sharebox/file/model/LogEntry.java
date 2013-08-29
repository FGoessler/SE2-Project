package de.sharebox.file.model;

import java.util.Date;

/**
 * Objekte dieser Klasse werden für jede durchgeführte Änderung an einem FEntry erstellt und enthalten Informationen
 * darüber was und wann etwas geändert wurde.
 */
public class LogEntry {
	private final LogMessage message;
	private final Date date;

	public enum LogMessage {
		CHANGED("Geändert"),
		RENAMED("Umbenannt"),
		PERMISSION("Rechte geändert"),
		CREATED("Erstellt"),
		ADDED_DIRECTORY("Verzeichnis hinzugefügt"),
		ADDED_FILE("Datei hinzugefügt"),
		REMOVED_DIRECTORY("Verzeichnis gelöscht"),
		REMOVED_FILE("Datei gelöscht");

		private final String text;

		private LogMessage(final String text) {
			this.text = text;
		}

		@Override
		public String toString() {
			return text;
		}
	}

	/**
	 * Erstellt einen LogEntry mit der gegebenen Message und dem aktuellen Zeitpunkt als date.
	 *
	 * @param message Die Meldung des LogEntries.
	 */
	public LogEntry(final LogMessage message) {
		this.message = message;
		this.date = new Date();
	}

	/**
	 * Der Copy-Konstruktor.
	 *
	 * @param logEntryToCopy Der zu kopierende LogEntry.
	 */
	public LogEntry(final LogEntry logEntryToCopy) {
		this.message = logEntryToCopy.message;
		this.date = (Date) logEntryToCopy.date.clone();
	}

	/**
	 * Liefert die Message des LogEntries.
	 *
	 * @return Die Message des LogEntries.
	 */
	public LogMessage getMessage() {
		return message;
	}

	/**
	 * Liefert den Zeitpunkt, an dem der LogEntry erstellt wurde.
	 *
	 * @return Der Zeitpunkt, an dem der LogEntry erstellt wurde.
	 */
	public Date getDate() {
		return date;
	}
}
