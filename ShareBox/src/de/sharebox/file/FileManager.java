package de.sharebox.file;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.sharebox.api.FileAPI;
import de.sharebox.file.model.Directory;
import de.sharebox.file.model.FEntry;
import de.sharebox.file.model.File;

import java.util.ArrayList;
import java.util.List;

@Singleton
public class FileManager {

	private final FileAPI fileAPI;

	private long lastAPIPoll = 0;
	private long lastStoragePoll = 0;

	private transient List<FileAPI.StorageEntry> storage = new ArrayList<FileAPI.StorageEntry>();

	/**
	 * Erstellt einen neuen FileManager. Als Singleton konzipiert.<br/>
	 * Sollte nur mittel Dependency Injection durch Guice erstellt werden.
	 *
	 * @param fileAPI Die FileAPI zur Kommunikation mit dem Server.
	 */
	@Inject
	FileManager(FileAPI fileAPI) {
		this.fileAPI = fileAPI;
	}

	/**
	 * Registriert ein neues File in der FileAPI
	 *
	 * @param newFEntry Der hinzuzufügende FEntry.
	 * @return ob die Operation erfolgreich war.
	 */
	public boolean registerFEntry(FEntry newFEntry) {
		//log entry
		boolean success = false;

		if (newFEntry instanceof File) {
			if (fileAPI.createNewFile((File) newFEntry)) {
				success = true;
			}
		}
		if (newFEntry instanceof Directory) {
			if (fileAPI.createNewDirectory((Directory) newFEntry)) {
				success = true;
			}
		}

		return success;
	}

	/**
	 * Sucht nach Änderungen seitens der FileAPI und updated/added die geänderten FEntries.
	 *
	 * @return ob alle Operationen erfolgreich waren.
	 */
	public boolean pollAPIForChanges() {
		boolean success = true;

		List<FEntry> changes = fileAPI.getChangesSince(lastAPIPoll);
		for (FEntry fEntry : changes) {
			if (fEntry instanceof File || fEntry instanceof Directory) {    //was soll es auch sonst sein - überflüssige Abfrage?
				updateFEntry(fEntry);
			} else {
				success = false;
			}
		}
		lastAPIPoll = System.currentTimeMillis();

		return success;
	}

	/**
	 * Sucht nach Änderungen seitens des Filesystems und updated/added die geänderten FEntries.
	 *
	 * @return ob alle Operationen erfolgreich waren.
	 */
	public boolean pollFileSystemForChanges() {
		List<FEntry> changedFiles = new ArrayList<FEntry>();
		boolean success = true;

		for (FileAPI.StorageEntry storageEntry : storage) {
			if (storageEntry.getTimestamp() >= lastStoragePoll) {
				changedFiles.add(storageEntry.getFEntry());
			}
		}
		for (FEntry fEntry : changedFiles) {
			if (fEntry instanceof File) {
				System.out.println("f");
				if (!fileAPI.updateFile((File) fEntry)) {
					fileAPI.createNewFile((File) fEntry);
				}
			} else if (fEntry instanceof Directory) {
				System.out.println("d");
				if (!fileAPI.updateDirectory((Directory) fEntry)) {
					fileAPI.createNewDirectory((Directory) fEntry);
				}
			} else {
				success = false;
			}
		}
		lastStoragePoll = System.currentTimeMillis();
		return success;
	}

	/**
	 * Überschreibt/updated einen FEntry im lokalen Speicher.
	 *
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
			if (updatedFile instanceof File) {
				FileAPI.StorageEntry newEntry = fileAPI.new StorageEntry(System.currentTimeMillis(), new File((File) updatedFile));
				storage.add(newEntry);
			} else if (updatedFile instanceof Directory) {
				FileAPI.StorageEntry newEntry = fileAPI.new StorageEntry(System.currentTimeMillis(), new Directory((Directory) updatedFile));
				storage.add(newEntry);
			}
			success = true;
		}
		return success;
	}

	/**
	 * Löscht FEntry mit ID des gegebenen FEntry.
	 *
	 * @param deletedFile zu löschender FEntry.
	 * @return ob erfolgreich.
	 */
	public boolean deleteFEntry(FEntry deletedFile) {
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

	/**
	 * TODO: Doku
	 *
	 * @return
	 */
	public int getFileCount() {
		return storage.size();
	}
}
