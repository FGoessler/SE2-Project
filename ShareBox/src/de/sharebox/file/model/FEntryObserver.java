package de.sharebox.file.model;

/**
 * Observer von FEntries müssen dieses Interface implementieren, um über Änderungen und die Löschung von FEntry Objekten<br/>
 * benachrichtigt zu werden.
 */
public interface FEntryObserver {
	/**
	 * Diese Enum wird verwendet um die Art der Änderung bei einer fEntryChangedNotification zu spezifizieren.
	 */
	enum ChangeType {
		NAME_CHANGED,
		PERMISSION_CHANGED
	}

	/**
	 * Der übergebene FEntry wurde auf irgendeine Art und Weise geändert (zB.: Name wurde geändert, Unterdateien wurde<br/>
	 * hinzugefügt, Dateiinhalt hat sich geändert)
	 *
	 * @param fEntry Der FEntry, der sich geändert hat.
	 * @param reason Die Art der Änderung.
	 */
	void fEntryChangedNotification(FEntry fEntry, ChangeType reason);

	/**
	 * Der übergebene FEntry wurde aus seinem Elternverzeichnis gelöscht.
	 *
	 * @param fEntry Der gelöschte FEntry.
	 */
	void fEntryDeletedNotification(FEntry fEntry);
}