package de.sharebox.file.notification;

/**
 * Observer von Directories müssen dieses Interface implementieren, um zusätzlich zu den Meldungen des
 * FEntryObserver-Interfaces Meldungen über das Hinzufügen und Entfernen von Dateien und Verzeichnissen zu einem
 * Verzeichnis  zu erhalten.
 */
public interface DirectoryObserver extends FEntryObserver {
	/**
	 * Benachrichtigung das dem in der Notification enthaltenem Verzeichnis neue Dateien und/oder Verzeichnisse als
	 * Kinder hinzugefügt wurden oder entfernt wurden.
	 *
	 * @param notification Die ausgelöste Notification. Enhält alle nötigen Informationen.
	 */
	void directoryNotification(final DirectoryNotification notification);
}
