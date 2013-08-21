package de.sharebox.file;

import de.sharebox.api.FileAPI;
import de.sharebox.file.model.FEntry;
import de.sharebox.file.model.File;
import de.sharebox.file.model.Directory;
import java.util.List;

public class FileManager {

    private static FileManager instance = new FileManager();
    private static FileAPI fileAPI = FileAPI.getUniqueInstance();

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
    public static boolean registerFEntry (FEntry f) {
        //log entry
        boolean success = false;
        if (f.getClass().getSimpleName().contains("File")) {
            if (fileAPI.createNewFile((File)f)) {
                success = true;
            }
        }
        if (f.getClass().getSimpleName().contains("Directory")) {
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
    public static boolean pollAPIForChanges () {
        List<FEntry> changes = fileAPI.getChangesSince(System.currentTimeMillis());
        for (FEntry fEntry : changes) {
            //update filesystem
        }

        return true;
    }

	/**
     * Sucht nach Änderungen seitens des Filesystems und updated/added die geänderten FEntries.
	 * @return ob alle Operationen erfolgreich waren.
     */
    public static boolean pollFileSystemForChanges () {
        //poll filesystem
        return true;
    }
}
