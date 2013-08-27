package de.sharebox.file.model;

import java.util.Date;

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

	public LogEntry(final LogMessage message) {
		this.message = message;
		this.date = new Date();
	}

	public LogEntry(final LogEntry logEntryToCopy) {
		this.message = logEntryToCopy.message;
		this.date = (Date) logEntryToCopy.date.clone();
	}

	public LogMessage getMessage() {
		return message;
	}

	public Date getDate() {
		return date;
	}
}
