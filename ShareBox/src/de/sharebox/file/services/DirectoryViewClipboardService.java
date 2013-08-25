package de.sharebox.file.services;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.sharebox.file.model.Directory;
import de.sharebox.file.model.FEntry;
import de.sharebox.file.model.File;
import de.sharebox.helpers.OptionPaneHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Diese Klasse kapselt die Verwaltung der Zwischenablage um Dateien und Verzeichnisse zu kopieren.<br/>
 * Diese Klasse ist als Singleton gedacht, so dass wann immer ein DirectoryViewClipboardService per Guice injected wird<br/>
 * alle Objekte Zugriff auf die selbe Instanz und somit auch auf die globalen Inhalte der Zwischenablage besitzen.
 */
@Singleton
public class DirectoryViewClipboardService {
	private final OptionPaneHelper optionPane;

	private List<FEntry> clipboard = new ArrayList<FEntry>();

	/**
	 * Erstellt einen neuen DirectoryViewClipboardService.<br/>
	 * Instanzen dieser Klasse sollten per Dependency Injection durch Guice erstellt werden.
	 *
	 * @param optionPaneHelper Ein OptionPaneHelper zum Erstellen von Dialogfenstern.
	 */
	@Inject
	DirectoryViewClipboardService(OptionPaneHelper optionPaneHelper) {
		this.optionPane = optionPaneHelper;
	}

	/**
	 * Fügt einen einzelnen FEntry in die Zwischenablage ein. Der bisherige Inhalt der Zwischenablage wird dabei nicht
	 * gelöscht. Verwende resetClipboard() um die Zwischenablage zu leeren.
	 *
	 * @param copiedFEntry Der zu kopierende FEntry, der in die Zwischenablage abgelegt werden soll.
	 */
	public void addToClipboard(FEntry copiedFEntry) {
		if (copiedFEntry instanceof File) {
			clipboard.add(new File((File) copiedFEntry));
		} else {
			clipboard.add(new Directory((Directory) copiedFEntry));
		}
	}

	/**
	 * Fügt mehrere FEntry in die Zwischenablage ein. Der bisherige Inhalt der Zwischenablage wird dabei nicht<br/>
	 * gelöscht. Verwende resetClipboard() um die Zwischenablage zu leeren.
	 *
	 * @param fEntries Die zu kopierenden FEntries, die in die Zwischenablage abgelegt werden sollen.
	 */
	public void addToClipboard(List<FEntry> fEntries) {
		for (FEntry fEntry : fEntries) {
			addToClipboard(fEntry);
		}
	}

	/**
	 * Fügt den Inhalt der Zwischenablage in das gegebene Verzeichnis ein.
	 *
	 * @param targetDirectory Das Verzeichnis in das die Inhalte eingefügt werden sollen.
	 * @return Das Verzeichnis nach dem Einfügen.
	 */
	public Directory pasteClipboardContent(Directory targetDirectory) {
		if (targetDirectory.getPermissionOfCurrentUser().getWriteAllowed()) {
			if (!clipboard.isEmpty()) {
				for (FEntry pasteFEntry : clipboard) {
					targetDirectory.addFEntry(pasteFEntry);
				}
			}
		} else {
			optionPane.showMessageDialog("Sie besitzen für das Zielverzeichnis leider nicht die nötigen Rechte.");
		}
		return targetDirectory;
	}

	/**
	 * Löscht den aktuellen Inhalt der Zwischenablage. Sollte vor jeder neuen "Kopieren"-Aktion eines Nutzers aufgerufen
	 * werden, da sonst beim Einfügen der alte Inhalt ebenfalls eingefügt wird.
	 */
	public void resetClipboard() {
		clipboard.clear();
	}

}
