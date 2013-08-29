package de.sharebox.file.model;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import de.sharebox.api.UserAPI;
import de.sharebox.file.FileManager;
import de.sharebox.file.notification.DirectoryNotification;
import de.sharebox.file.notification.DirectoryObserver;
import de.sharebox.file.notification.FEntryNotification;
import de.sharebox.file.notification.FEntryObserver;
import de.sharebox.user.model.User;

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
	 * Erstellt ein neues Directory mit den gegebenen Werten, feuert dabei allerdings keine Notifications und erstellt
	 * auch nur einen "Created" LogEntry anstatt eines "Renamed" und "PermissionChanged" LogEntry.
	 *
	 * @param userAPI      Die aktuell für dieses Directory relevante UserAPI. Wird dazu benötigt den aktuell eingeloggten
	 *                     Nutzer zu bestimmen und Rechte zu überprüfen.
	 * @param name         Der Name des Directories.
	 * @param creatingUser Der Nutzer, der initial alle Rechte auf diesem Directory erhält.
	 */
	public Directory(final UserAPI userAPI, final String name, final User creatingUser) {
		super(userAPI, name, creatingUser);
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
	 * Erstellt eine neue Datei in diesem Verzeichnis und benachrichtigt alle Observer über die Änderung.<br/>
	 * Hinweis: Es werden keine Rechte überprüft! Diese hat der Aufrufer dieser Methode vorher zu überprüfen, falls
	 * diese Methode auf eine Aktion des Nutzers hin aufgerufen wird und nicht aufgrund von Änderungen seitens der API.
	 *
	 * @param filename Der Name der neuen Datei.
	 * @return Die neu erstellte Datei als Optional. Im Falle eines Fehlers (zB. es gibt bereits Datei mit diesem Namen)
	 *         liefert es ein Optional.absent().
	 */
	public Optional<File> createNewFile(final String filename) {
		Optional<File> newFile = Optional.absent();

		if (!fEntryExists(filename)) {
			newFile = Optional.of(new File(getUserAPI(), filename, getUserAPI().getCurrentUser()));

			fEntries.add(newFile.get());
			addLogEntry(LogEntry.LogMessage.ADDED_FILE);
			fireDirectoryNotification(FEntryNotification.ChangeType.ADDED_CHILDREN, newFile.get(), this);
		}

		return newFile;
	}

	/**
	 * Erstellt ein neues Verzeichnis in diesem Verzeichnis und benachrichtigt alle Observer über die Änderung.<br/>
	 * Hinweis: Es werden keine Rechte überprüft! Diese hat der Aufrufer dieser Methode vorher zu überprüfen, falls<br/>
	 * diese Methode auf eine Aktion des Nutzers hin aufgerufen wird und nicht aufgrund von Änderungen seitens der API.
	 *
	 * @param dirname Der Name des neuen Verzeichnisses.
	 * @return Das neu erstellte Verzeichnis als Optional. Im Falle eines Fehlers (zB. es gibt bereits Datei mit diesem
	 *         Namen) liefert es ein Optional.absent().
	 */
	public Optional<Directory> createNewDirectory(final String dirname) {
		Optional<Directory> newDir = Optional.absent();

		if (!fEntryExists(dirname)) {
			newDir = Optional.of(new Directory(getUserAPI(), dirname, getUserAPI().getCurrentUser()));

			fEntries.add(newDir.get());
			addLogEntry(LogEntry.LogMessage.ADDED_DIRECTORY);
			fireDirectoryNotification(FEntryNotification.ChangeType.ADDED_CHILDREN, newDir.get(), this);
		}

		return newDir;
	}

	/**
	 * Fügt dem Verzeichnis einen FEntry hinzu.<br/>
	 * Hinweis: Es werden keine Rechte überprüft! Diese hat der Aufrufer dieser Methode vorher zu überprüfen, falls
	 * diese Methode auf eine Aktion des Nutzers hin aufgerufen wird und nicht aufgrund von Änderungen seitens der API.
	 *
	 * @param newFEntry Der hinzuzufügende FEntry.
	 */
	public void addFEntry(final FEntry newFEntry) {
		fEntries.add(newFEntry);

		if (newFEntry instanceof File) {
			addLogEntry(LogEntry.LogMessage.ADDED_FILE);
		} else if (newFEntry instanceof Directory) {
			addLogEntry(LogEntry.LogMessage.ADDED_DIRECTORY);
		}

		fireDirectoryNotification(FEntryNotification.ChangeType.ADDED_CHILDREN, newFEntry, this);
	}

	/**
	 * Löscht den übergebenen FEntry aus dem Dateisystem. Handelt es sich um ein Verzeichnis, dann werden alle
	 * Unterdateien dieses Verzeichnisses rekursiv gelöscht. Es werden die Observer aller gelöschten Objekte mit einer
	 * Löschungsbenachrichtigung informiert und der Observer des Verzeichnisses, auf dem die Methode aufgerufen wird,
	 * erhält eine Änderungsbenachrichtigung.<br/>
	 * Hinweis: Es werden keine Rechte überprüft! Diese hat der Aufrufer dieser Methode vorher zu überprüfen, falls
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
		removeChild(fEntry);

		if (fEntry instanceof File) {
			addLogEntry(LogEntry.LogMessage.REMOVED_FILE);
		} else if (fEntry instanceof Directory) {
			addLogEntry(LogEntry.LogMessage.REMOVED_DIRECTORY);
		}

		fEntry.fireNotification(FEntryNotification.ChangeType.DELETED, this);
		fireDirectoryNotification(FEntryNotification.ChangeType.REMOVE_CHILDREN, fEntry, this);
	}

	/**
	 * Feuert eine DirectoryNotification auf den registrierten DirectoryObservern. Etwaige reine
	 * FEntryObserver erhalten keine Benachrichtigung. Die Notifikation enthält unter anderem das Directory, dem FEntries
	 * hinzugefügt oder entfernt wurden, sowie die FEntries die hinzugefügt bzw. gelöscht wurden. Die Art der Änderung
	 * kann am ChangeType abgelesen werden.
	 *
	 * @param reason        Die Art der Änderung - entweder REMOVE_CHILDREN oder ADDED_CHILDREN.
	 * @param affectedChild Der hinzugefügt/entfernte FEntry.
	 * @param source        Das Objekt, das die Änderung ausgelöst hat - im Zweifel das Directory selbst setzen.
	 */
	public void fireDirectoryNotification(final FEntryNotification.ChangeType reason,
										  final FEntry affectedChild, final Object source) {
		final ImmutableList<FEntry> addedFEntries = ImmutableList.of(affectedChild);

		final ImmutableList<FEntryObserver> observers = ImmutableList.copyOf(this.observers);
		for (final FEntryObserver observer : observers) {
			if (observer instanceof DirectoryObserver) {
				((DirectoryObserver) observer).directoryNotification(new DirectoryNotification(this, reason, source, addedFEntries));
			}
		}
	}

	private Boolean fEntryExists(final String fileName) {
		boolean exists = false;
		for (final FEntry fEntry : fEntries) {
			if (fEntry.getName().equals(fileName)) {
				exists = true;
			}
		}
		return exists;
	}

	@Override
	public void applyChangesFromAPI(final FEntry updatedFEntry, final FileManager fileManager) {
		super.applyChangesFromAPI(updatedFEntry, fileManager);

		final Directory updatedDirectory = (Directory) updatedFEntry;

		final List<FEntry> addedChildren = new ArrayList<FEntry>();
		for (final FEntry childOfUpdatedDir : updatedDirectory.getFEntries()) {
			if (!directoryContainsFEntry(this, childOfUpdatedDir)) {
				addedChildren.add(childOfUpdatedDir);
			}
		}
		for (final FEntry addedChild : addedChildren) {
			fEntries.add(addedChild);
			fireDirectoryNotification(FEntryNotification.ChangeType.ADDED_CHILDREN, addedChild, fileManager);
		}

		final List<FEntry> removedChildren = new ArrayList<FEntry>();
		for (final FEntry childOfCurrentDir : this.getFEntries()) {
			if (!directoryContainsFEntry(updatedDirectory, childOfCurrentDir)) {
				removedChildren.add(childOfCurrentDir);
			}
		}
		for (final FEntry removedChild : removedChildren) {
			removeChild(removedChild);
			fireDirectoryNotification(FEntryNotification.ChangeType.REMOVE_CHILDREN, removedChild, fileManager);
		}
	}

	private Boolean directoryContainsFEntry(final Directory directory, final FEntry containedFEntry) {
		Boolean found = false;
		for (final FEntry fEntry : directory.getFEntries()) {
			if (fEntry.getIdentifier().equals(containedFEntry.getIdentifier())) {
				found = true;
				break;
			}
		}
		return found;
	}

	private void removeChild(final FEntry removedChild) {
		FEntry foundFEntry = null;
		for (final FEntry fEntry : fEntries) {
			if ((removedChild.getIdentifier() != null && fEntry.getIdentifier() != null &&
					fEntry.getIdentifier().equals(removedChild.getIdentifier())) ||
					fEntry.equals(removedChild)) {
				foundFEntry = fEntry;
				break;
			}
		}
		if (foundFEntry != null) {
			fEntries.remove(foundFEntry);
		}
	}
}
