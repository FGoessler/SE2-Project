package de.sharebox.file.model;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import de.sharebox.api.UserAPI;

import java.util.ArrayList;
import java.util.List;

/**
 * Diese Klasse repräsentiert ein Verzeichnis, das von der Sharebox verwaltet und mit dem Server synchronisiert wird.
 */
public class Directory extends FEntry {
	private final List<FEntry> fEntries = new ArrayList<FEntry>();

	/**
	 * Der Standard-Konstruktor.
	 *
	 * @param userAPI Die aktuell für diesen FEntry relevante UserAPI. Wird dazu benötigt den aktuell eingeloggten
	 *                Nutzer zu bestimmen und Rechte zu überprüfen.
	 */
	public Directory(final UserAPI userAPI) {
		super(userAPI);
	}

	/**
	 * Copy Konstruktor
	 *
	 * @param sourceDirectory Das Quell-Objekt.
	 */
	public Directory(final Directory sourceDirectory) {
		super(sourceDirectory);

		for (final FEntry fEntry : sourceDirectory.fEntries) {
			if (fEntry instanceof File) {
				this.fEntries.add(new File((File) fEntry));
			} else {
				this.fEntries.add(new Directory((Directory) fEntry));
			}
		}
	}

	/**
	 * Liefert eine immutable List aller Unterdateien und -verzeichnisse.
	 *
	 * @return Eine immutable List aller Unterdateien und -verzeichnisse.
	 */
	public ImmutableList<FEntry> getFEntries() {
		return ImmutableList.copyOf(fEntries);
	}

	/**
	 * Erstellt eine neue Datei in diesem Verzeichnis und benachrichtigt alle Observer über die Änderung.<br/><br/>
	 * Hinweis: Es werden keine Permissions überprüft! Diese hat der Aufrufer dieser Methode vorher zu überprüfen, falls
	 * diese Methode auf eine Aktion des Nutzers hin aufgerufen wird und nicht aufgrund von Änderungen seitens der API.
	 *
	 * @param filename Der Name der neuen Datei.
	 * @return Die neu erstellte Datei als Optional. Im Falle eines Fehlers (zB. es gibt bereits Datei mit diesem Namen)
	 *         liefert es ein Optional.absent().
	 */
	public Optional<File> createNewFile(final String filename) {
		Optional<File> newFile = Optional.absent();

		if (!fEntryExists(filename)) {
			newFile = Optional.of(new File(getUserAPI()));
			newFile.get().setName(filename);
			newFile.get().setPermission(getUserAPI().getCurrentUser(), true, true, true);

			fEntries.add(newFile.get());

			fireAddedChildrenNotification(newFile.get());
		}

		return newFile;
	}

	/**
	 * Erstellt ein neues Verzeichnis in diesem Verzeichnis und benachrichtigt alle Observer über die Änderung.<br/><br/>
	 * Hinweis: Es werden keine Permissions überprüft! Diese hat der Aufrufer dieser Methode vorher zu überprüfen, falls
	 * diese Methode auf eine Aktion des Nutzers hin aufgerufen wird und nicht aufgrund von Änderungen seitens der API.
	 *
	 * @param dirname Der Name des neuen Verzeichnisses.
	 * @return Das neu erstellte Verzeichnis als Optional. Im Falle eines Fehlers (zB. es gibt bereits Datei mit diesem
	 *         Namen) liefert es ein Optional.absent().
	 */
	public Optional<Directory> createNewDirectory(final String dirname) {
		Optional<Directory> newDir = Optional.absent();

		if (!fEntryExists(dirname)) {
			newDir = Optional.of(new Directory(getUserAPI()));
			newDir.get().setName(dirname);
			newDir.get().setPermission(getUserAPI().getCurrentUser(), true, true, true);

			fEntries.add(newDir.get());

			fireAddedChildrenNotification(newDir.get());
		}

		return newDir;
	}

	/**
	 * Fügt dem Verzeichnis einen FEntry hinzu.<br/><br/>
	 * Hinweis: Es werden keine Permissions überprüft! Diese hat der Aufrufer dieser Methode vorher zu überprüfen, falls
	 * diese Methode auf eine Aktion des Nutzers hin aufgerufen wird und nicht aufgrund von Änderungen seitens der API.
	 *
	 * @param newFEntry Der hinzuzufügende FEntry.
	 */
	public void addFEntry(final FEntry newFEntry) {
		fEntries.add(newFEntry);

		fireAddedChildrenNotification(newFEntry);
	}

	/**
	 * Löscht den übergebenen FEntry aus dem Dateisystem. Handelt es sich um ein Verzeichnis werden rekursiv alle
	 * Unterdateien dieses Verzeichnisses gelöscht.
	 * Es werden die Observer aller gelöschten Objekte mit einer Löschungs-Benachrichtigung informiert und der Observer
	 * des Verzeichnisses auf dem die Methode aufgerufen wird erhält eine Änderungs-Benachrichtigung.<br/><br/>
	 * Hinweis: Es werden keine Permissions überprüft! Diese hat der Aufrufer dieser Methode vorher zu überprüfen, falls
	 * diese Methode auf eine Aktion des Nutzers hin aufgerufen wird und nicht aufgrund von Änderungen seitens der API.
	 *
	 * @param fEntry Der zu löschende FEntry.
	 */
	public void deleteFEntry(final FEntry fEntry) {
		if (fEntry instanceof Directory) {
			final Directory dir = (Directory) fEntry;
			while (dir.getFEntries().size() > 0) {
				dir.deleteFEntry(dir.getFEntries().get(0));
			}
		}
		fEntries.remove(fEntry);

		fEntry.fireDeleteNotification();
		fireRemovedChildrenNotification(fEntry);
	}

	/**
	 * Feuert eine addedChildrenNotification Notification auf den registrierten DirectoryObservern. Etwaige reine
	 * FEntryObserver erhalten keine Benachrichtigung. Die Notification erhält das Directory dem FEntries hinzugefügt
	 * wurden, sowie die FEntries, die hinzugefügt wurden.
	 *
	 * @param addedFEntry Der hinzugefügt FEntry.
	 */
	public void fireAddedChildrenNotification(final FEntry addedFEntry) {
		final ImmutableList<FEntry> addedFEntries = ImmutableList.of(addedFEntry);

		for (final FEntryObserver observer : observers) {
			if (observer instanceof DirectoryObserver) {
				((DirectoryObserver) observer).addedChildrenNotification(this, addedFEntries);
			}
		}
	}

	/**
	 * Feuert eine removedChildrenNotification Notification auf den registrierten DirectoryObservern. Etwaige reine
	 * FEntryObserver erhalten keine Benachrichtigung. Die Notification erhält das Directory dem FEntries hinzugefügt
	 * wurden, sowie die FEntries, die hinzugefügt wurden.
	 *
	 * @param removedFEntry Der entfernte FEntry.
	 */
	public void fireRemovedChildrenNotification(final FEntry removedFEntry) {
		final ImmutableList<FEntry> removedFEntries = ImmutableList.of(removedFEntry);

		for (final FEntryObserver observer : observers) {
			if (observer instanceof DirectoryObserver) {
				((DirectoryObserver) observer).removedChildrenNotification(this, removedFEntries);
			}
		}
	}

	private Boolean fEntryExists(String fileName) {
		boolean exists = false;
		for (final FEntry fEntry : fEntries) {
			if (fEntry.getName().equals(fileName)) {
				exists = true;
			}
		}
		return exists;
	}
}
