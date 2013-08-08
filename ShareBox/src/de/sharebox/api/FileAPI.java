package de.sharebox.api;

import de.sharebox.file.model.Directory;
import de.sharebox.file.model.FEntry;
import de.sharebox.file.model.File;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Julius Mertens
 */

public class FileAPI {

	public class StorageEntry {
        private long timestamp;
        private FEntry fEntry;

        public StorageEntry(long timestamp, FEntry fEntry) {
            this.timestamp = timestamp;
            this.fEntry = fEntry;
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
	}

	/**
	 * simulated storage; sublist of storage Entries is for versioning/etc. (list fEntry has timestamp and FEntry);
	 */
	private transient List<List<StorageEntry>> storage = new ArrayList<List<StorageEntry>>();

	//various stat getters
    /**
     * Generiert eine Meldung mit generellen Daten über den benutzten Storage.
     */
    public void logStatistics() {
        APILogger.logMessage("- Storage -\n    Entries: " + storage.size() + "\n    Files: " + getVersionCount() + "\n");
    }

    /**
     * Generiert eine Meldung mit generellen Daten über einen gegebenen Storage-Eintrag.
     * @param indexOfStorage Index des gewünschten Objekts
     */
    public void logStatistics(int indexOfStorage) {
		APILogger.logMessage("- Storage: File #" + indexOfStorage + " -\n    entries: " + storage.get(indexOfStorage).size() + "\n");
    }
    /**
     * Zählt Versionen innerhalb des Storage.
     * @return Anzahl der Versionen
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
     * @return Größe von Storage
     */
	public int getFileCount() {
		return storage.size();
	}

    //actually relevant code-------------------------------------------------------------------------------

    private static FileAPI instance = new FileAPI();

	private static final String FILE_NOT_FOUND = "File not found.";
	private static final String FILE_EXISTS = "File already exists!";

	/**
     * Methode um das Singleton-Objekt zu erhalten.
	 * @return Das Singleton-Objekt.
     */
    public static FileAPI getUniqueInstance() {
        return instance;
	}

	/**
     * Setzt die Singleton Instanz von außen. Soll nur in Test Cases verwendet werden, um die die FileAPI zu mocken.
	 * @param newFileAPI Das neue FileAPI Objekt, das ab sofort beim Aufruf von getUniqueInstance() zurückgegeben werden soll.
     */
	public static void injectSingletonInstance(FileAPI newFileAPI) {
		instance = newFileAPI;
	}

	/**
     * Liefert den FEntry mit der gegebenen ID.
	 * @param fEntryId Die ID des FEntries.
	 * @return Der aktuellste FEntry mit dieser ID.
     */
	public FEntry getFEntryWithId(long fEntryId) {
		FEntry foundFEntry = null;

		for(List<StorageEntry> aStorage : storage) {
			long latestTimestamp = 0;
			for(StorageEntry storageEntry : aStorage) {
				if(latestTimestamp < storageEntry.getTimestamp() && storageEntry.getFEntry().getIdentifier() == fEntryId) {
					foundFEntry = storageEntry.getFEntry();
				}
			}
			if(foundFEntry != null) {
				break;
			}
		}

		return foundFEntry;
	}

	/**
     * Erstellt einen neuen File-Eintrag im Storage.
     * @param newFile zu erzeugendes File
     * @return ob erfolgreich
     */
    public boolean createNewFile(File newFile) {
		Boolean fileAlreadyExists = false;

        //search through existing files to prevent duplicates
		for (List<StorageEntry> aStorage : storage) {
			if (aStorage.get(0).fEntry.getIdentifier().equals(newFile.getIdentifier())) {
				fileAlreadyExists = true;
				break;
			}
		}

		if(fileAlreadyExists) {
			APILogger.debugFailure(APILogger.actionStringForFEntryAction("File Creation", newFile), FILE_EXISTS);
		} else {
			//create new sublist to account for new file if no existing file was found
			List<StorageEntry> newStorage = new ArrayList<StorageEntry>();
			storage.add(newStorage);

			StorageEntry newEntry = new StorageEntry(System.currentTimeMillis(), new File(newFile));
			newStorage.add(newEntry);

			APILogger.debugSuccess(APILogger.actionStringForFEntryAction("File Creation",newFile));
		}

        return !fileAlreadyExists;
    }


    /**
     * Überschreibt/updated einen FEntry vom Typ Directory.
     * @param updatedFile zu bearbeitendes File
     * @return ob erfolgreich
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

		if(foundStorage == null) {
			//no file found found - error!
			APILogger.debugFailure(APILogger.actionStringForFEntryAction("File Update",updatedFile), FILE_NOT_FOUND);
		} else {
			//file found, create new version
			StorageEntry newEntry = new StorageEntry(System.currentTimeMillis(), new File(updatedFile));
			foundStorage.add(newEntry);
			APILogger.debugSuccess(APILogger.actionStringForFEntryAction("File Update",updatedFile));
		}

        return foundStorage != null;
    }

    /**
     * Löscht File mit ID des gegebenen Files.
     * @param deletedFile zu löschendes File
     * @return ob erfolgreich
     */
    public boolean deleteFile(File deletedFile) {
		Boolean fileExists = false;

        //search through existing files, to see if you're just confused and/or still reading this
        for (int i = 0; i < storage.size(); i++) {
            if (storage.get(i).get(0).fEntry.getIdentifier().equals(deletedFile.getIdentifier())) {
                storage.remove(i);
				APILogger.debugSuccess(APILogger.actionStringForFEntryAction("File Deletion", deletedFile));
                fileExists = true;
				break;
            }
        }

		if(!fileExists) {
			APILogger.debugFailure(APILogger.actionStringForFEntryAction("File Deletion", deletedFile), FILE_NOT_FOUND);
		}

        return fileExists;
    }

    /**
     * Erstellt einen neuen Directory-Eintrag im Storage.
     * @param newDirectory zu erzeugendes Directory
     * @return ob erfolgreich
     */
    public boolean createNewDirectory(Directory newDirectory) {
        Boolean dirAlreadyExists = false;

		//check if directory already exist
		for (List<StorageEntry> aStorage : storage) {
			if (aStorage.get(0).fEntry.getIdentifier().equals(newDirectory.getIdentifier())) {
				dirAlreadyExists = true;
				break;
			}
		}

		if(dirAlreadyExists) {
			APILogger.debugFailure(APILogger.actionStringForFEntryAction("Directory Creation", newDirectory));
		} else {
			List<StorageEntry> fList = new ArrayList<StorageEntry>();
			StorageEntry newEntry = new StorageEntry(System.currentTimeMillis(),new Directory(newDirectory));
			fList.add(newEntry);
			storage.add(fList);

			APILogger.debugSuccess(APILogger.actionStringForFEntryAction("Directory Creation", newDirectory));
		}

        return !dirAlreadyExists;
    }

    /**
     * Überschreibt/updated einen FEntry vom Typ Directory.
     * @param updatedDirectory zu bearbeitendes Directory
     * @return ob erfolgreich
     */
    public boolean updateDirectory(Directory updatedDirectory) {
		Boolean directoryFound = false;

		for (List<StorageEntry> aStorage : storage) {
			if (aStorage.get(0).fEntry.getIdentifier().equals(updatedDirectory.getIdentifier())) {
				aStorage.get(0).fEntry = new Directory(updatedDirectory);
				aStorage.get(0).timestamp = System.currentTimeMillis();
				APILogger.debugSuccess(APILogger.actionStringForFEntryAction("Directory Update", updatedDirectory));
				directoryFound = true;
				break;
			}
		}

		if(!directoryFound) {
			APILogger.debugFailure(APILogger.actionStringForFEntryAction("Directory Update (", updatedDirectory), FILE_NOT_FOUND);
		}

        return directoryFound;
    }

    /**
     * Löscht Directory mit ID des gegebenen Directorys.
     * @param deletedDirectory zu löschendes Directory
     * @return ob erfolgreich
     */
    public boolean deleteDirectory(Directory deletedDirectory) {
        Boolean directoryFound = false;

        for (int i = 0; i < storage.size(); i++) {
            if (storage.get(i).get(0).fEntry.getIdentifier().equals(deletedDirectory.getIdentifier())) {
                storage.remove(i);
				APILogger.debugSuccess(APILogger.actionStringForFEntryAction("Directory Deletion", deletedDirectory));
				directoryFound = true;
				break;
            }
        }

		if(!directoryFound) {
			APILogger.debugFailure(APILogger.actionStringForFEntryAction("Directory Deletion", deletedDirectory), FILE_NOT_FOUND);
		}

        return directoryFound;
    }

	/* For later use!
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
     * Gibt die Liste der Storage-Einträge, welche nach einem bestimmten Timestamp erstellt worden sind.
     * @param timeOfLastChange ist ein timestamp in ms
     * @return Liste von FEntries die sich geändert haben/neu erstellt wurden.
     */
    public List<FEntry> getChangesSince(long timeOfLastChange) {
        List<FEntry> changedFiles = new ArrayList<FEntry>();

		for (List<StorageEntry> aStorage : storage) {
			for (StorageEntry storageEntry : aStorage) {
				if (storageEntry.timestamp > timeOfLastChange) {
					changedFiles.add(storageEntry.fEntry);
				}
			}
		}

        return changedFiles;
    }
}
