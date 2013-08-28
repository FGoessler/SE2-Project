package de.sharebox.file;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.sharebox.api.APILogger;
import de.sharebox.api.FileAPI;
import de.sharebox.file.model.FEntry;
import de.sharebox.file.notification.DirectoryNotification;
import de.sharebox.file.notification.DirectoryObserver;
import de.sharebox.file.notification.FEntryNotification;

import java.util.HashMap;
import java.util.Map;

/**
 * TODO Klassenbeschreibung
 */
@Singleton
public class FileManager implements DirectoryObserver {
	private final FileAPI fileAPI;

	private long lastAPIPoll = 0;

	private final Map<Long, FEntry> registeredFEntries = new HashMap<Long, FEntry>();

	/**
	 * Erstellt einen neuen FileManager. Als Singleton konzipiert.<br/>
	 * Sollte nur mittels Dependency Injection durch Guice erstellt werden.
	 *
	 * @param fileAPI Die FileAPI zur Kommunikation mit dem Server.
	 */
	@Inject
	FileManager(final FileAPI fileAPI) {
		this.fileAPI = fileAPI;
	}

	//TODO: execute poll methods regularly -> thread

	/**
	 * Registriert ein neues File in der FileAPI
	 *
	 * @param newFEntry Der hinzuzufügende FEntry.
	 * @return ob die Operation erfolgreich war
	 */
	public boolean registerFEntry(final FEntry newFEntry) {
		newFEntry.addObserver(this);
		registeredFEntries.put(fileAPI.createNewFEntry(newFEntry), newFEntry);

		return true;
	}

	/**
	 * Sucht nach Änderungen der FileAPI und aktualisiert/ergänzt die veränderten FEntries.
	 *
	 * @return ob alle Operationen erfolgreich waren.
	 */
	public boolean pollAPIForChanges() {
		APILogger.logMessage("Polling changes from API...");

		final long currentAPIPoll = System.currentTimeMillis();

		final ImmutableList<FEntry> changedFEntries = fileAPI.getChangesSince(lastAPIPoll);
		for (final FEntry changedFEntry : changedFEntries) {
			final FEntry currentFEntry = registeredFEntries.get(changedFEntry.getIdentifier());
			if (currentFEntry != null) {
				currentFEntry.applyChangesFromAPI(changedFEntry, this);
			}
		}

		lastAPIPoll = currentAPIPoll;

		return true;
	}

	/**
	 * Sucht nach Änderungen des Dateisystems und aktualisiert/ergänzt die veränderten FEntries.
	 *
	 * @return ob alle Operationen erfolgreich waren
	 */
	public boolean pollFileSystemForChanges() {
		APILogger.logMessage("Polling changes from local filesystem... NOT implemented in this Prototype!");

		return true;
	}

	@Override
	public void directoryNotification(final DirectoryNotification notification) {
		if (notification.getSource() != this) {    //do not publish changes that came from the api/filemanager
			fileAPI.updateFEntry(notification.getChangedFEntry());
		}
	}


	@Override
	public void fEntryNotification(final FEntryNotification notification) {
		if (notification.getSource() != this) {    //do not publish changes that came from the api/filemanager
			if (notification.getChangeType().equals(FEntryNotification.ChangeType.DELETED)) {
				fileAPI.deleteFEntry(notification.getChangedFEntry());
			} else {
				fileAPI.updateFEntry(notification.getChangedFEntry());
			}
		}
	}
}
