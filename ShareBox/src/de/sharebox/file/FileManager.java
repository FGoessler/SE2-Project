package de.sharebox.file;

import de.sharebox.file.model.FEntry;

public class FileManager {

    private static FileManager instance = new FileManager();

    public static FileManager getUniqueInstance() {
        return instance;
	}

    public boolean registerFEntry (FEntry f) {
        return true;
    }

    public boolean pollAPIForChanges () {
        return true;
    }

    public boolean pollFileSystemForChanges () {
        return true;
    }
}
