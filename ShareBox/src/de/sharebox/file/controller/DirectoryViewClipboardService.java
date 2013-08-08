package de.sharebox.file.controller;

import de.sharebox.file.model.Directory;
import de.sharebox.file.model.FEntry;
import de.sharebox.file.model.File;

import java.util.ArrayList;
import java.util.List;

/**
 * Diese Klasse kappselt die Verwaltung der zwischenablage um Dateien und Verzeichnisse zu kopieren.
 */
public class DirectoryViewClipboardService {
	private List<FEntry> clipboard = new ArrayList<FEntry>();

	/**
	 * Fügt einen einzelnen FEntry in die Zwischenablage ein. Der bsiherige Inhalt der Zwischenablage wird dabei nicht
	 * gelöscht. Verwende resetClipboard() um die Zwischenablage zu leeren.
	 * @param copiedFEntry Der zu kopierende FEntry, der in die Zwischenablage abgelegt werden soll.
	 */
	public void addToClipboard(FEntry copiedFEntry) {
		if(copiedFEntry instanceof File) {
			clipboard.add(new File((File)copiedFEntry));
		} else {
			clipboard.add(new Directory((Directory)copiedFEntry));
		}
	}

	/**
	 * Fügt einen mehrere FEntry in die Zwischenablage ein. Der bsiherige Inhalt der Zwischenablage wird dabei nicht
	 * gelöscht. Verwende resetClipboard() um die Zwischenablage zu leeren.
	 * @param fEntries die zu kopierenden FEntries, die in die Zwischenablage abgelegt werden sollen.
	 */
	public void addToClipboard(List<FEntry> fEntries) {
		for(FEntry fEntry : fEntries) {
			addToClipboard(fEntry);
		}
	}

	/**
	 * Fügt den Inhalt der Zwischenablage in das gegebene Verzeichnis ein.
	 * @param targetDirectory Das Verzeichnis in das die Inhalte eingefügt werden sollen.
	 * @return Das Verzeichnis nach dem Einfügenvorgang.
	 */
	public Directory pasteClipboardContent(Directory targetDirectory) {
		if(!clipboard.isEmpty()) {
			for(FEntry pasteFEntry : clipboard) {
				targetDirectory.addFEntry(pasteFEntry);
			}
		}
		return targetDirectory;
	}

	/**
	 * Löscht den aktuellen Inhalt der Zwischenablage. Sollte vor jeder neuen "Kopieren"-Aktion eines Nutzers aufgerufen
	 * werden, da ansonsten beim Einfügen der alte Inhalt mit eingefügt wird.
	 */
	public void resetClipboard() {
		clipboard.clear();
	}

}
