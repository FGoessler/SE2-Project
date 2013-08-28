package de.sharebox.api;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.sharebox.file.model.Directory;
import de.sharebox.file.model.FEntry;
import de.sharebox.file.model.File;
import de.sharebox.user.model.User;

import java.util.ArrayList;
import java.util.List;

/*
 * TODO Klassenbeschreibung (Die Klasse FileAPI kappselt die Zugriffsmethoden auf die FEntrys? ...)
 */
@Singleton
public class FileAPI {

    public enum Status {
        OK,
        DELETED
    }

	/*
	* TODO Klassenbeschreibung (Die Klasse StorageEntry ist für ...)
	*/
	public class StorageEntry {
		private long timestamp;
		private FEntry fEntry;
		private Status status;

		public StorageEntry(long timestamp, FEntry fEntry) {
			this.timestamp = timestamp;
			this.fEntry = fEntry;
			this.status = Status.OK;
		}

		public long getTimestamp() {
			return timestamp;
		}

		public FEntry getFEntry() {
			return fEntry;
		}

		public void setFEntry(FEntry fEntry) {
			this.fEntry = fEntry;
		}

		public void setTimestamp(long timestamp) {
			this.timestamp = timestamp;
		}

		public Status getStatus() {
			return this.status;
		}

		public void setStatus(Status newStatus) {
			this.status = newStatus;
		}
	}

	/**
	 * Ein simulierter Speicher - Unterliste von Speichereinträgen für die Versionierung usw.<br/>
	 * (Liste der fEntry hat einen Zeitstempel und FEntry)
	 */
	private final List<List<StorageEntry>> storage = new ArrayList<List<StorageEntry>>();

	/**
	 * Eine einfache Zählervariable um fortlaufende eindeutige IDs für erstellte FEntries zu erzeugen.
	 */
	private Integer idCounter = 0;

	//various stat getters

	/**
	 * Generiert eine Meldung mit generellen Daten über den benutzten Speicher.
	 */
	public void logStatistics() {
		APILogger.logMessage("- Storage -\n    Entries: " + storage.size() + "\n    Files: " + getVersionCount() + "\n");
	}

	/**
	 * Generiert eine Meldung mit generellen Daten über einen gegebenen Speichereintrag.
	 *
	 * @param indexOfStorage Index des gewünschten Objekts
	 */
	public void logStatistics(int indexOfStorage) {
		APILogger.logMessage("- Storage: File #" + indexOfStorage + " -\n    entries: " + storage.get(indexOfStorage).size() + "\n");
	}

	/**
	 * Zählt Versionen innerhalb des Speichers.
	 *
	 * @return Anzahl der vorhandenen Versionen
	 */
	public int getVersionCount() {
		int versionCount = 0;
		for (List<StorageEntry> aStorage : storage) {
			versionCount += aStorage.size();
		}
		return versionCount;
	}

	/**
	 * Methode um die Anzahl der Storage-Einträge zu erhalten.
	 *
	 * @return Größe vom Speicher
	 */
	public int getFileCount() {
		return storage.size();
	}

	//actually relevant code-------------------------------------------------------------------------------

	private static final String FILE_NOT_FOUND = "File not found.";
	private static final String FILE_EXISTS = "File already exists!";

	/**
	 * Erstellt ein FileAPI-Objekt. Als Singleton konzipiert.<br/>
	 * Sollte nur mittels Dependency Injection durch Guice erstellt werden.
	 */
	@Inject
	FileAPI() {
		//empty package constructor to avoid direct instantiation
	}

	/**
	 * Liefert den FEntry mit der gegebenen ID.
	 *
	 * @param fEntryId Die ID des FEntries.
	 * @return Der aktuellste FEntry mit dieser ID.
	 */
	public FEntry getFEntryWithId(long fEntryId) {
		FEntry foundFEntry = null;

		for (List<StorageEntry> aStorage : storage) {
			long latestTimestamp = 0;
			for (StorageEntry storageEntry : aStorage) {
				if (latestTimestamp < storageEntry.getTimestamp() && storageEntry.getFEntry().getIdentifier() == fEntryId) {
					foundFEntry = storageEntry.getFEntry();
				}
			}
			if (foundFEntry != null) {
				break;
			}
		}

		return foundFEntry;
	}

	/**
	 * Erstellt einen neuen File-Eintrag im Speicher.
	 *
	 * @param newFile das zu erzeugende File
	 * @return ob die Erstellung erfolgreich war
	 */
	public boolean createNewFile(File newFile) {
		//generate id
		newFile.setIdentifier(idCounter++);

		//create new sublist to account for new file if no existing file was found
		List<StorageEntry> newStorage = new ArrayList<StorageEntry>();
		storage.add(newStorage);

		StorageEntry newEntry = new StorageEntry(System.currentTimeMillis(), new File(newFile));
		newStorage.add(newEntry);


		APILogger.logSuccess(APILogger.actionStringForFEntryAction("File Creation", newFile));

		return true;
	}


	/**
	 * überschreibt/aktualisiert einen FEntry vom Typ File.
	 *
	 * @param updatedFile zu bearbeitendes File
	 * @return ob die Aktualisierung erfolgreich war
	 */
	public boolean updateFile(File updatedFile) {
		List<StorageEntry> foundStorage = null;

		//search through existing files, see createNewFile
		for (List<StorageEntry> aStorage : storage) {
			//check for correct ID
			if (aStorage.get(0).fEntry.getIdentifier().equals(updatedFile.getIdentifier())) {
				foundStorage = aStorage;
				break;
			}
		}

		if (foundStorage == null) {
			//no file found found - error!
			APILogger.logFailure(APILogger.actionStringForFEntryAction("File Update", updatedFile), FILE_NOT_FOUND);
		} else {
			//file found, create new version
			StorageEntry newEntry = new StorageEntry(System.currentTimeMillis(), new File(updatedFile));
			foundStorage.add(newEntry);
			APILogger.logSuccess(APILogger.actionStringForFEntryAction("File Update", updatedFile));
		}

		return foundStorage != null;
	}

	/**
	 * Löscht das File mit der ID, die übergeben wurde.
	 *
	 * @param deletedFile zu löschendes File
	 * @return ob Löschung erfolgreich war
	 */
	public boolean deleteFile(File deletedFile) {
		Boolean fileExists = false;

		//search through existing files, to see if you're just confused and/or still reading this
		for (int i = 0; i < storage.size(); i++) {
			if (storage.get(i).get(0).fEntry.getIdentifier().equals(deletedFile.getIdentifier())) {
				//storage.remove(i);
				storage.get(i).get(storage.get(i).size() - 1).setStatus(Status.DELETED);
				storage.get(i).get(storage.get(i).size() - 1).setTimestamp(System.currentTimeMillis());
				APILogger.logSuccess(APILogger.actionStringForFEntryAction("File Deletion", deletedFile));
				fileExists = true;
				break;
			}
		}

		if (!fileExists) {
			APILogger.logFailure(APILogger.actionStringForFEntryAction("File Deletion", deletedFile), FILE_NOT_FOUND);
		}

		return fileExists;
	}

	/**
	 * Erstellt einen neuen Directory-Eintrag im Speicher.
	 *
	 * @param newDirectory zu erzeugendes Directory
	 * @return ob die Directory-Erstellung erfolgreich war
	 */
	public boolean createNewDirectory(Directory newDirectory) {
		//generate id
		newDirectory.setIdentifier(idCounter++);

		List<StorageEntry> fList = new ArrayList<StorageEntry>();
		StorageEntry newEntry = new StorageEntry(System.currentTimeMillis(), new Directory(newDirectory));
		fList.add(newEntry);
		storage.add(fList);

		APILogger.logSuccess(APILogger.actionStringForFEntryAction("Directory Creation", newDirectory));

		return true;
	}

	/**
	 * Überschreibt/aktualisiert einen FEntry vom Typ Directory.
	 *
	 * @param updatedDirectory zu bearbeitendes Directory
	 * @return ob die Aktualisierung erfolgreich war
	 */
	public boolean updateDirectory(Directory updatedDirectory) {
		Boolean directoryFound = false;

		for (List<StorageEntry> aStorage : storage) {
			if (aStorage.get(0).fEntry.getIdentifier().equals(updatedDirectory.getIdentifier())) {
				aStorage.get(0).fEntry = new Directory(updatedDirectory);
				aStorage.get(0).timestamp = System.currentTimeMillis();
				APILogger.logSuccess(APILogger.actionStringForFEntryAction("Directory Update", updatedDirectory));
				directoryFound = true;
				break;
			}
		}

		if (!directoryFound) {
			APILogger.logFailure(APILogger.actionStringForFEntryAction("Directory Update (", updatedDirectory), FILE_NOT_FOUND);
		}

		return directoryFound;
	}

	/**
	 * Löscht das Directory mit der ID, die übergeben wurde.
	 *
	 * @param deletedDirectory zu löschendes Directory
	 * @return ob die Löschung erfolgreich war
	 */
	public boolean deleteDirectory(Directory deletedDirectory) {
		Boolean directoryFound = false;

		for (int i = 0; i < storage.size(); i++) {
			if (storage.get(i).get(0).fEntry.getIdentifier().equals(deletedDirectory.getIdentifier())) {
				//storage.remove(i);
				storage.get(i).get(storage.get(i).size() - 1).setStatus(Status.DELETED);
				storage.get(i).get(storage.get(i).size() - 1).setTimestamp(System.currentTimeMillis());
				APILogger.logSuccess(APILogger.actionStringForFEntryAction("Directory Deletion", deletedDirectory));
				directoryFound = true;
				break;
			}
		}

		if (!directoryFound) {
			APILogger.logFailure(APILogger.actionStringForFEntryAction("Directory Deletion", deletedDirectory), FILE_NOT_FOUND);
		}

		return directoryFound;
	}

	/* TODO For later use!
	public static boolean addPermission(FEntry f, User u,FEntryPermission fp) {

        return true;
    }

    public static boolean changePermission(FEntryPermission fp) {

        return true;
    }

    public static boolean deletePermission(FEntryPermission fp) {

        return true;
    }
    */

	/**
	 * Gibt die Liste der Speichereinträge, welche nach einem bestimmten Datumsstempel erstellt worden sind.
	 *
	 * @param timeOfLastChange Timestamp der letzten Änderung in ms.
	 * @return Liste von FEntries die sich geändert haben bzw. neu erstellt wurden.
	 */
	public List<FEntry> getChangesSince(long timeOfLastChange) {
		List<FEntry> changedFiles = new ArrayList<FEntry>();

		for (List<StorageEntry> aStorage : storage) {
			for (StorageEntry storageEntry : aStorage) {
				if (storageEntry.timestamp >= timeOfLastChange) {
					changedFiles.add(storageEntry.fEntry);
				}
			}
		}

		return changedFiles;
	}

	/**
	 * Gibt den API storage zurück. Nirgends benutzen außer für Sync.
	 *
	 * @return API File Storage.
	 */
	public List<List<StorageEntry>> getStorage() {
		return storage;
	}

	/**
	 * Diese Funktion erstellt lediglich einige Beispieldateien und -verzeichnisse, die für Testzwecke benötigt werden.
	 *
	 * @param userAPI Die UserAPI - wird benötigt, um sie an die erstellten FEntries weiterzugeben.
	 */
	public void createSampleContent(final UserAPI userAPI) {
		//create the same 2 users as available in the UserAPI
		final User user1 = new User();
		user1.setEmail("Max@Mustermann.de");
		final User user2 = new User();
		user2.setEmail("admin");

		final List<FEntry> sampleFEntries = new ArrayList<FEntry>();

		//create sample content for user1
		final Directory root1 = new Directory(userAPI, "Sharebox", user1);
		root1.setIdentifier(idCounter++);
		sampleFEntries.add(root1);
		final File file1 = new File(userAPI, "A file", user1);
		file1.setIdentifier(idCounter++);
		root1.addFEntry(file1);
		sampleFEntries.add(file1);

		//create sample content for user2
		final Directory root2 = new Directory(userAPI, "Sharebox", user2);
		root2.setIdentifier(idCounter++);
		sampleFEntries.add(root2);
		final Directory subDir1 = new Directory(userAPI, "A Subdirectory", user2);
		subDir1.setIdentifier(idCounter++);
		root2.addFEntry(subDir1);
		sampleFEntries.add(subDir1);
		final File file2 = new File(userAPI, "A file", user2);
		file2.setIdentifier(idCounter++);
		root2.addFEntry(file2);
		sampleFEntries.add(file2);

		for (final FEntry fEntry : sampleFEntries) {
			final StorageEntry newEntry = new StorageEntry(System.currentTimeMillis(), fEntry);
			final List<StorageEntry> newStorage = new ArrayList<StorageEntry>();
			newStorage.add(newEntry);
			storage.add(newStorage);
		}

	}
}
