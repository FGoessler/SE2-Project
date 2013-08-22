package de.sharebox.file;

import de.sharebox.api.FileAPI;
import de.sharebox.file.model.FEntry;
import de.sharebox.file.model.File;
import de.sharebox.file.model.Directory;
import java.util.ArrayList;
import java.util.List;

public class FileManager {

    private static FileManager instance = new FileManager();
    private static FileAPI fileAPI = FileAPI.getUniqueInstance();

    private transient List<FileAPI.StorageEntry> storage = new ArrayList<FileAPI.StorageEntry>();

	/**
     * Methode um das Singleton-Objekt zu erhalten.
	 * @return Das Singleton-Objekt.
     */
    public static FileManager getUniqueInstance() {
        return instance;
	}

	/**
     * Registriert ein neues File in der FileAPI
	 * @param f Der hinzuzufügende FEntry.
	 * @return ob die Operation erfolgreich war.
     */
    public boolean registerFEntry (FEntry f) {
        //log entry
        boolean success = false;
        if (f.getClass().getSimpleName().equals("File")) {
            if (fileAPI.createNewFile((File)f)) {
                success = true;
            }
        }
        if (f.getClass().getSimpleName().equals("Directory")) {
            if (fileAPI.createNewDirectory((Directory)f)) {
                success = true;
            }
        }
        return success;
    }

	/**
     * Sucht nach Änderungen seitens der FileAPI und updated/added die geänderten FEntries.
	 * @return ob alle Operationen erfolgreich waren.
     */
    public boolean pollAPIForChanges () {
        boolean success = true;
        List<FEntry> changes = fileAPI.getChangesSince(System.currentTimeMillis());
        for (FEntry fEntry : changes) {
            if (fEntry.getClass().getSimpleName().equals("File") || fEntry.getClass().getSimpleName().equals("Directory")) {
                instance.updateFEntry((File)fEntry);
            }
            else {
                success = false;
            }
        }

        return success;
    }

	/**
     * Sucht nach Änderungen seitens des Filesystems und updated/added die geänderten FEntries.
	 * @return ob alle Operationen erfolgreich waren.
     */
    public static boolean pollFileSystemForChanges () {
        //poll filesystem
        return true;
    }

    /**
     * Überschreibt/updated einen FEntry im lokalen Speicher.
     * @param updatedFile zu bearbeitendes FEntry.
     * @return ob erfolgreich.
     */
    public boolean updateFEntry(FEntry updatedFile) {
		boolean success = false;

        //search through existing files, see createNewFile
		for (FileAPI.StorageEntry aStorage : storage) {
			//check for correct ID
			if (aStorage.getFEntry().getIdentifier().equals(updatedFile.getIdentifier())) {
				aStorage.setTimestamp(System.currentTimeMillis());
                aStorage.setFEntry(new FEntry(updatedFile));
                success = true;
				break;
			}
		}
		if (!success) {
			//file found, create new version
			FileAPI.StorageEntry newEntry = fileAPI.new StorageEntry(System.currentTimeMillis(), new FEntry(updatedFile));
			storage.add(newEntry);
            success = true;
		}
        return success;
    }

    /**
     * Löscht FEntry mit ID des gegebenen FEntry.
     * @param deletedFile zu löschender FEntry.
     * @return ob erfolgreich.
     */
    public boolean deleteFEntry(File deletedFile) {
		boolean success = false;

        //search through existing files, see createNewFile
		for (int i = 0; i < storage.size(); i++) {
			//check for correct ID
			if (storage.get(i).getFEntry().getIdentifier().equals(deletedFile.getIdentifier())) {
                storage.remove(i);
				break;
			}
		}
        return success;
    }
}
