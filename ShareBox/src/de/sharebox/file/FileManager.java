package de.sharebox.file;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.sharebox.api.FileAPI;
import de.sharebox.file.model.DirectoryObserver;
import de.sharebox.file.model.Directory;
import de.sharebox.file.model.FEntry;
import de.sharebox.file.model.File;

import java.util.ArrayList;
import java.util.List;

@Singleton
public class FileManager implements DirectoryObserver {

	private final FileAPI fileAPI;

	private long lastAPIPoll = 0;
	private long lastStoragePoll = 0;
    private long lastSync = 0;

	private final List<FileAPI.StorageEntry> storage = new ArrayList<FileAPI.StorageEntry>();

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
        //newFEntry.addObserver();
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
        //NOTE: deleted in API? -> nonexistent; deleted locally? -> DELETED type declared

        List<List<FileAPI.StorageEntry>> APIstorage = fileAPI.getStorage();
        boolean success = true;
        boolean found;


        //search for things that are updatable in the APIstorage
        for (int i = 0; i < APIstorage.size(); i++) {
            //file newer than sync -> check for counterpart
            if (APIstorage.get(i).get(APIstorage.get(i).size()-1).getTimestamp() >= lastAPIPoll) {
                found = false;
                for (FileAPI.StorageEntry storageEntry : storage) {
                    if (storageEntry.getFEntry().getIdentifier() == APIstorage.get(i).get(0).getFEntry().getIdentifier()) {
                        found = true;
                        // case for API having a smaller timestamp than the local version, and local version actually being ok
                        if (storageEntry.getTimestamp() >= APIstorage.get(i).get(APIstorage.get(i).size()-1).getTimestamp() && storageEntry.getStatus() != FileAPI.Status.DELETED) {
                            if (storageEntry.getFEntry() instanceof File) {
                                fileAPI.updateFile((File)storageEntry.getFEntry());
                            } else if (storageEntry.getFEntry() instanceof Directory) {
                                fileAPI.updateDirectory((Directory)storageEntry.getFEntry());
                            } else {
                                success = false;
                            }
                        }
                        // case for API having a smaller timestamp than the local version, and local version is flagged as deleted
                        else if (storageEntry.getTimestamp() >= APIstorage.get(i).get(APIstorage.get(i).size()-1).getTimestamp() && storageEntry.getStatus() == FileAPI.Status.DELETED) {
                            if (storageEntry.getFEntry() instanceof File) {
                                fileAPI.deleteFile((File) storageEntry.getFEntry());
                            } else if (storageEntry.getFEntry() instanceof Directory) {
                                fileAPI.deleteDirectory((Directory) storageEntry.getFEntry());
                            } else {
                                success = false;
                            }
                        }
                        // case for API having a greater timestamp than the local version
                        else if (APIstorage.get(i).get(APIstorage.get(i).size()-1).getStatus() == FileAPI.Status.DELETED) {
                            deleteFEntry(APIstorage.get(i).get(APIstorage.get(i).size()-1).getFEntry());
                        }
                        else {
                            updateFEntry(APIstorage.get(i).get(APIstorage.get(i).size()-1).getFEntry());
                        }
                        break;
                    }

                }
                //case for local storage not having the file at all
                if (!found) {
                    updateFEntry(APIstorage.get(i).get(APIstorage.get(i).size()-1).getFEntry());
                }
            }
        }
		lastAPIPoll = System.currentTimeMillis();
        deleteFlush();
        return success;
	}

	/**
	 * Sucht nach Änderungen seitens des Filesystems und updated/added die geänderten FEntries.
	 *
	 * @return ob alle Operationen erfolgreich waren.
	 */
	public boolean pollFileSystemForChanges() {
        //NOTE: deleted in API? -> nonexistent; deleted locally? -> DELETED type declared

        List<List<FileAPI.StorageEntry>> APIstorage = fileAPI.getStorage();
        boolean success = true;
        boolean found;


        //search for things that are updatable in the APIstorage
        for (FileAPI.StorageEntry storageEntry : storage) {
            //file newer than sync -> check for counterpart
            if (storageEntry.getTimestamp() >= lastStoragePoll) {
                found = false;
                for (int i = 0; i < APIstorage.size(); i++) {
                    if (storageEntry.getFEntry().getIdentifier() == APIstorage.get(i).get(0).getFEntry().getIdentifier()) {
                        found = true;
                        // case for API having a smaller timestamp than the local version, and local version actually being ok
                        if (storageEntry.getTimestamp() >= APIstorage.get(i).get(APIstorage.get(i).size()-1).getTimestamp() && storageEntry.getStatus() != FileAPI.Status.DELETED) {
                            if (storageEntry.getFEntry() instanceof File) {
                                fileAPI.updateFile((File)storageEntry.getFEntry());
                            } else if (storageEntry.getFEntry() instanceof Directory) {
                                fileAPI.updateDirectory((Directory)storageEntry.getFEntry());
                            } else {
                                success = false;
                            }
                        }
                        // case for API having a smaller timestamp than the local version, and local version is flagged as deleted
                        else if (storageEntry.getTimestamp() >= APIstorage.get(i).get(APIstorage.get(i).size()-1).getTimestamp() && storageEntry.getStatus() == FileAPI.Status.DELETED) {
                            if (storageEntry.getFEntry() instanceof File) {
                                fileAPI.deleteFile((File) storageEntry.getFEntry());
                            } else if (storageEntry.getFEntry() instanceof Directory) {
                                fileAPI.deleteDirectory((Directory) storageEntry.getFEntry());
                            } else {
                                success = false;
                            }
                        }
                        // case for API having a greater timestamp than the local version
                        else if (APIstorage.get(i).get(APIstorage.get(i).size()-1).getStatus() == FileAPI.Status.DELETED) {
                            deleteFEntry(APIstorage.get(i).get(APIstorage.get(i).size()-1).getFEntry());
                        }
                        else {
                            updateFEntry(APIstorage.get(i).get(APIstorage.get(i).size()-1).getFEntry());
                        }
                        break;
                    }

                }
                //case for local storage not having the file at all
                if (!found) {
                    System.out.println("not found. creating...");
                    if (storageEntry.getFEntry() instanceof File) {
                        fileAPI.createNewFile((File)storageEntry.getFEntry());
                    } else if (storageEntry.getFEntry() instanceof Directory) {
                        fileAPI.createNewDirectory((Directory)storageEntry.getFEntry());
                    } else {
                        success = false;
                    }
                }
            }
        }
		lastStoragePoll = System.currentTimeMillis();
        deleteFlush();
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
				//storage.remove(i);
                storage.get(i).setStatus(FileAPI.Status.DELETED);
                storage.get(i).setTimestamp(System.currentTimeMillis());
				break;
			}
		}

		return success;
	}

    public void deleteFlush () {
		for (int i = 0; i < storage.size(); i++) {
			if (storage.get(i).getStatus() == FileAPI.Status.DELETED) {
				storage.remove(i);
			}
		}
    }

	/**
	 * TODO: Doku
	 *
	 * @return
	 */
	public int getFileCount() {
		return storage.size();
	}

    public Directory getDirByID (Integer ID) {
        Directory returnDir = null;
		for (FileAPI.StorageEntry storageEntry : storage) {
			if (storageEntry.getFEntry().getIdentifier() == ID) {
                returnDir = (Directory)storageEntry.getFEntry();
			}
		}
        return returnDir;
    }

    public void addedChildrenNotification(Directory parent, ImmutableList<FEntry> newChildren) {

    }

    public void removedChildrenNotification(Directory parent, ImmutableList<FEntry> removedChildren) {

    }

    public void fEntryChangedNotification(FEntry fEntry, ChangeType reason) {

    }

    public void fEntryDeletedNotification(FEntry fEntry) {

    }
}
