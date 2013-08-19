package de.sharebox.file.model;

import com.google.common.collect.ImmutableList;
import de.sharebox.api.UserAPI;

import java.util.ArrayList;
import java.util.List;

/**
 * Diese Klasse repräsentiert ein Verzeichnis, das von der Sharebox verwaltet und mit dem Server synchronisiert wird.
 */
public class Directory extends FEntry {
	private transient List<FEntry> fEntries = new ArrayList<FEntry>();

	/**
	 * Konstruktor
	 */
	public Directory() {
		super();
	}

	/**
	 * Copy Konstruktor
	 *
	 * @param sourceDirectory Das Quell-Objekt.
	 */
	public Directory(Directory sourceDirectory) {
		super(sourceDirectory);

		this.fEntries = new ArrayList<FEntry>();
		for (FEntry fEntry : sourceDirectory.fEntries) {
			if (fEntry instanceof File) {
				this.fEntries.add(new File((File) fEntry));
			} else {
				this.fEntries.add(new Directory((Directory) fEntry));
			}
		}
	}

	/**
	 * Liefert eine Liste aller Unterdateien und -verzeichnisse.
	 *
	 * @return Eine Liste aller Unterdateien und -verzeichnisse.
	 */
	public List<FEntry> getFEntries() {
		return fEntries;
	}

	/**
	 * Erstellt eine neue Datei in diesem Verzeichnis und benachrichtigt alle Observer über die Änderung.
	 *
	 * @param filename Der Name der neuen Datei.
	 * @return Die neu erstellte Datei.
	 */
	public File createNewFile(String filename) {
		File newFile = new File();
		newFile.setName(filename);
		newFile.setPermission(UserAPI.getUniqueInstance().getCurrentUser(), true, true, true);

		fEntries.add(newFile);

		fireAddedChildrenNotification(newFile);

		return newFile;
	}

	/**
	 * Erstellt ein neues Verzeichnis in diesem Verzeichnis und benachrichtigt alle Observer über die Änderung.
	 *
	 * @param dirname Der Name des neuen Verzeichnisses.
	 * @return Das neu erstellte Verzeichnis.
	 */
	public Directory createNewDirectory(String dirname) {
		Directory newDir = new Directory();
		newDir.setName(dirname);
		newDir.setPermission(UserAPI.getUniqueInstance().getCurrentUser(), true, true, true);

		fEntries.add(newDir);

		fireAddedChildrenNotification(newDir);

		return newDir;
	}

	/**
	 * Fügt dem Verzeichnis einen FEntry hinzu.
	 *
	 * @param newFEntry Der hinzuzufügende FEntry.
	 */
	public void addFEntry(FEntry newFEntry) {
		fEntries.add(newFEntry);

		fireAddedChildrenNotification(newFEntry);
	}

	/**
	 * Löscht den übergebenen FEntry aus dem Dateisystem. Handelt es sich um ein Verzeichnis werden rekursiv alle
	 * Unterdateien dieses Verzeichnisses gelöscht.
	 * Es werden die Observer aller gelöschten Objekte mit einer Löschungs-Benachrichtigung informiert und der Observer
	 * des Verzeichnisses auf dem die Methode aufgerufen wird erhält eine Änderungs-Benachrichtigung.
	 *
	 * @param fEntry Der zu löschende FEntry.
	 */
	public void deleteFEntry(FEntry fEntry) {
		if (fEntry instanceof Directory) {
			Directory dir = (Directory) fEntry;
			while (dir.getFEntries().size() > 0) {
				dir.deleteFEntry(dir.getFEntries().get(0));
			}
		}
		fEntries.remove(fEntry);

		fEntry.fireDeleteNotification();
		fireRemovedChildrenNotification(fEntry);
	}

	public void fireAddedChildrenNotification(FEntry addedFEntry) {
		ImmutableList<FEntry> addedFEntries = ImmutableList.of(addedFEntry);

		for(FEntryObserver observer : observers) {
			if(observer instanceof DirectoryObserver) {
				((DirectoryObserver) observer).addedChildrenNotification(this,addedFEntries);
			}
		}
	}

	public void fireRemovedChildrenNotification(FEntry removedFEntry) {
		ImmutableList<FEntry> removedFEntries = ImmutableList.of(removedFEntry);

		for(FEntryObserver observer : observers) {
			if(observer instanceof DirectoryObserver) {
				((DirectoryObserver) observer).removedChildrenNotification(this,removedFEntries);
			}
		}
	}
}
