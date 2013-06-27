package de.sharebox.file.model;

/**
 * Observer von FEntries müssen dieses Interface implementieren um über Änderungen und die Löschung von FEntry Objekten
 * benachrichtigt zu werden.
 */
public interface FEntryObserver {
	/**
	 * Der übergebene FEntry wurde auf irgendeine Art und Weise geändert (zB.: Name wurde geändert, Unterdateien wurde
	 * hinzugefügt, Dateiinhalt hat sich geändert)
	 * @param fEntry Der FEntry, der sich geändert hat.
	 */
	void fEntryChangedNotification(FEntry fEntry);

	/**
	 * Der übergebene FEntry wurde aus seinem Elternverzeichnis gelöscht.
	 * @param fEntry Der gelöscht FEntry.
	 */
	void fEntryDeletedNotification(FEntry fEntry);

}