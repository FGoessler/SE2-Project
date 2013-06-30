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
        private FEntry entry;
        public StorageEntry(long time, FEntry add) {
            timestamp = time;
            entry = add;
        }

		public long getTimestamp() {
			return timestamp;
		}

		public FEntry getEntry() {
			return entry;
		}

		public void setEntry(FEntry entry) {
			this.entry = entry;
		}

		public void setTimestamp(long timestamp) {
			this.timestamp = timestamp;
		}
	}

	/**
	 * simulated storage; sublist of storage Entries is for versioning/etc. (list entry has timestamp and FEntry);
	 */
	private transient List<List<StorageEntry>> storage = new ArrayList<List<StorageEntry>>();

    //debug flag
    public static final boolean DEBUG = true;

    //various stat getters
    public void getStats () {
        if (DEBUG) {
            System.out.println("- Storage -\n    Entries: " + storage.size() + "\n    Files: " + fileCount() + "\n");
        }
    }
    public void getStats (int numberOfStorage) {
        if (DEBUG) {
			System.out.println("- Storage: File #" + numberOfStorage + " -\n    entries: " + storage.get(numberOfStorage).size() + "\n");
		}
    }
    public int fileCount() {
        int storageSum = 0;
		for (List<StorageEntry> aStorage : storage) {
			storageSum += aStorage.size();
		}
        return storageSum;
    }

    //actually relevant code-------------------------------------------------------------------------------

    private static FileAPI instance = new FileAPI();

	private static final String FILE_NOT_FOUND = "File not found.";

	public int getStorageSize() {
		return storage.size();
	}

    public static FileAPI getUniqueInstance() {
        return instance;
    }

    /**creates new file by looking for existing file of same ID, otherwise adds one.
     * @param newFile zu erzeugendes File
     * @return ob erfolgreich**/
    public boolean createNewFile(File newFile) {
		List<StorageEntry> foundStorage = null;

        //search through existing files to prevent duplicates, see updateFile
		for (List<StorageEntry> aStorage : storage) {
			if (aStorage.get(0).entry.getIdentifier().equals(newFile.getIdentifier())) {
				foundStorage = aStorage;
				break;
			}
		}

		if(foundStorage == null) {
			//create new sublist to account for new file if no existing file was found
			foundStorage = new ArrayList<StorageEntry>();
		}
		StorageEntry newEntry = new StorageEntry(System.currentTimeMillis(), newFile);
		foundStorage.add(newEntry);

		APILogger.debugSuccess("File Creation (Update)");

        return true;
    }


    /**updates file, if found in list of existing.
     * @param updatedFile zu bearbeitendes File
     * @return ob erfolgreich**/
    public boolean updateFile(File updatedFile) {
		List<StorageEntry> foundStorage = null;

        //search through existing files, see createNewFile
		for (List<StorageEntry> aStorage : storage) {
			//check for correct ID
			if (aStorage.get(0).entry.getIdentifier().equals(updatedFile.getIdentifier())) {
				foundStorage = aStorage;
				break;
			}
		}

		if(foundStorage == null) {
			//no file found found - error!
			APILogger.debugFailure("File Update", FILE_NOT_FOUND);
		} else {
			//file found, create new version
			StorageEntry newEntry = new StorageEntry(System.currentTimeMillis(), updatedFile);
			foundStorage.add(newEntry);
			APILogger.debugSuccess("File Update");
		}

        return foundStorage != null;
    }

    /**deletes file by searching through list of existing files.
     * @param deletedFile zu löschendes File
     * @return ob erfolgreich**/
    public boolean deleteFile(File deletedFile) {
		Boolean result = false;

        //search through existing files, to see if you're just confused and/or still reading this
        for (int i = 0; i < storage.size(); i++) {
            if (storage.get(i).get(0).entry.getIdentifier().equals(deletedFile.getIdentifier())) {
                storage.remove(i);
				APILogger.debugSuccess("File Deletion");
                result = true;
				break;
            }
        }

		if(!result) {
			APILogger.debugFailure("File Deletion", FILE_NOT_FOUND);
		}

        return result;
    }

    /**@param newDirectory zu erzeugendes Directory
     * @return ob erfolgreich**/
    public boolean createNewDirectory(Directory newDirectory) {
        Boolean result = false;

		for (List<StorageEntry> aStorage : storage) {
			if (aStorage.get(0).entry.getIdentifier().equals(newDirectory.getIdentifier())) {
				aStorage.get(0).entry = newDirectory;
				aStorage.get(0).timestamp = System.currentTimeMillis();
				APILogger.debugSuccess("Directory Creation (Update)");
				result = true;
				break;
			}
		}

		if(!result) {
			List<StorageEntry> fList = new ArrayList<StorageEntry>();
			StorageEntry newEntry = new StorageEntry(System.currentTimeMillis(),newDirectory);
			fList.add(newEntry);
			if (storage.add(fList)) {
				APILogger.debugSuccess("Directory Creation");
				result = true;
			}
			else {
				APILogger.debugFailure("Directory Creation", "Not enough magic.");
				result = false;
			}
		}

        return result;
    }

    /**@param updatedDirectory zu bearbeitendes Directory
     * @return ob erfolgreich**/
    public boolean updateDirectory(Directory updatedDirectory) {
		Boolean result = false;

		for (List<StorageEntry> aStorage : storage) {
			if (aStorage.get(0).entry.getIdentifier().equals(updatedDirectory.getIdentifier())) {
				aStorage.get(0).entry = updatedDirectory;
				aStorage.get(0).timestamp = System.currentTimeMillis();
				APILogger.debugSuccess("Directory Update");
				result = true;
				break;
			}
		}

		if(!result) {
			APILogger.debugFailure("Directory Update", "File not found.");
		}

        return result;
    }

    /**@param deletedDirectory zu löschendes Directory
     * @return ob erfolgreich**/
    public boolean deleteDirectory(Directory deletedDirectory) {
        Boolean result = false;

        for (int i = 0; i < storage.size(); i++) {
            if (storage.get(i).get(0).entry.getIdentifier().equals(deletedDirectory.getIdentifier())) {
                storage.remove(i);
				APILogger.debugSuccess("Directory Deletion");
				result = true;
				break;
            }
        }

		if(!result) {
			APILogger.debugFailure("Directory Deletion", FILE_NOT_FOUND);
		}

        return result;
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

    /**searches through entries and picks out those with a timestamp later than the given one.
     * @param timeOfLastChange ist ein timestamp in ms
     * @return ob erfolgreich**/
    public List getChangesSince(long timeOfLastChange) {
        List<FEntry> changedFiles = new ArrayList<FEntry>();
		for (List<StorageEntry> aStorage : storage) {
			for (StorageEntry anAStorage : aStorage) {
				if (anAStorage.timestamp > timeOfLastChange) {
					changedFiles.add(anAStorage.entry);
				}
			}
		}
        return changedFiles;
    }
}
