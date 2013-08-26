package de.sharebox.file.model;

import com.google.common.collect.ImmutableList;

/**
 * Observer von Directories müssen dieses Interface implementieren, um zusätzlich zu den Meldungen des
 * FEntryObserver-Interfaces Meldungen über das Hinzufügen und Entfernen von Dateien und Verzeichnissen zu einem
 * Verzeichnis  zu erhalten.
 */
public interface DirectoryObserver extends FEntryObserver {
	/**
	 * Benachrichtigung das dem übergebenen Verzeichnis neue Dateien und/oder Verzeichnisse als Kinder hinzugefügt wurden.
	 *
	 * @param parent      Das Verzeichnis zu dem Dateien und/oder Verzeichnisse hinzugefügt wurden.
	 * @param newChildren Eine ImmutableList aller hinzugefügten FEntries.
	 */
	void addedChildrenNotification(final Directory parent, final ImmutableList<FEntry> newChildren);

	/**
	 * Benachrichtigung das aus dem übergebenen Verzeichnis Dateien und/oder Verzeichnisse entfernt wurden..
	 *
	 * @param parent          Das Verzeichnis aus dem Dateien und/oder Verzeichnisse entfernt wurden.
	 * @param removedChildren Eine ImmutableList aller entfernten FEntries.
	 */
	void removedChildrenNotification(final Directory parent, final ImmutableList<FEntry> removedChildren);
}
