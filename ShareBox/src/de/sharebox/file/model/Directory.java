package de.sharebox.file.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Diese Klasse repräsentiert ein Verzeichnis, das von der Sharebox verwaltet und mit dem Server synchronisiert wird.
 */
public class Directory extends FEntry {
	private transient List<FEntry> fEntries = new ArrayList<FEntry>();
	private String name;

	/**
	 * Ändert den Namen des Verzeichnisses und benachrichtigt alle Observer über die Änderung.
	 * @param name Der neue Name.
	 */
	public void setName(String name) {
		this.name = name;

		fireChangeNotification();
	}

	/**
	 * Liefert den aktuellen Namen des Verzeichnisses.
	 * @return Der aktuelle Name des Verzeichnisses
	 */
	public String getName() {
		return name;
	}

	/**
	 * Liefert eine Liste aller Unterdateien und -verzeichnisse.
	 * @return Eine Liste aller Unterdateien und -verzeichnisse.
	 */
	public List<FEntry> getFEntries() {
		return fEntries;
	}

	/**
	 * Erstellt eine neue Datei in diesem Verzeichnis und benachrichtigt alle Observer über die Änderung.
	 * @param filename Der Name der neuen Datei.
	 * @return Die neu erstellte Datei.
	 */
	public File createNewFile(String filename) {
		File newFile = new File();
		newFile.setFileName(filename);

		fEntries.add(newFile);

		fireChangeNotification();

		return newFile;
	}

	/**
	 * Erstellt ein neues Verzeichnis in diesem Verzeichnis und benachrichtigt alle Observer über die Änderung.
	 * @param dirname Der Name des neuen Verzeichnisses.
	 * @return Das neu erstellte Verzeichnis.
	 */
	public Directory createNewDirectory(String dirname) {
		Directory newDir = new Directory();
		newDir.setName(dirname);

		fEntries.add(newDir);

		fireChangeNotification();

		return newDir;
	}

	/**
	 * Löscht den übergebenen FEntry aus dem Dateisystem. Handelt es sich um ein Verzeichnis werden rekursiv alle
	 * Unterdateien dieses Verzeichnisses gelöscht.
	 * Es werden die Observer aller gelöschten Objekte mit einer Löschungs-Benachrichtigung informiert und der Observer
	 * des Verzeichnisses auf dem die Methode aufgerufen wird erhält eine Änderungs-Benachrichtigung.
	 * @param fEntry Der zu löschende FEntry.
	 */
	public void deleteFEntry(FEntry fEntry) {
		if(fEntry instanceof Directory) {
			Directory dir = (Directory) fEntry;
			while(dir.getFEntries().size() > 0) {
				dir.deleteFEntry(dir.getFEntries().get(0));
			}
		}

		fEntry.fireDeleteNotification();
		fireChangeNotification();

		fEntries.remove(fEntry);
	}
}
