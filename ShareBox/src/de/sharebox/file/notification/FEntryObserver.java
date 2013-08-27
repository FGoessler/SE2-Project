package de.sharebox.file.notification;

/**
 * Observer von FEntries müssen dieses Interface implementieren, um über Änderungen und die Löschung von FEntry Objekten
 * benachrichtigt zu werden.
 */
public interface FEntryObserver {

	/**
	 * Der übergebene FEntry wurde auf irgendeine Art und Weise geändert oder gelöscht (zB.: Name wurde geändert,
	 * Dateiinhalt hat sich geändert, Rechte geändert, ...)
	 *
	 * @param notification Die ausgelöste Notification. Enhält alle nötigen Informationen.
	 */
	void fEntryNotification(final FEntryNotification notification);
}