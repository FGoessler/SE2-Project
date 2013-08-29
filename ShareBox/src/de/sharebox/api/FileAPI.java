package de.sharebox.api;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.sharebox.file.model.Directory;
import de.sharebox.file.model.FEntry;
import de.sharebox.file.model.File;
import de.sharebox.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * TODO Klassenbeschreibung und Methoden neu beschreiben, allgemeines Storage Prinzip erklären
 */
@Singleton
public class FileAPI {
	private static final String FILE_NOT_FOUND = "File not found.";

	/**
	 * Ein simulierter Speicher - Unterliste von Speichereinträgen für die Versionierung usw.<br/>
	 * (Liste der fEntry hat einen Zeitstempel und FEntry)
	 */
	private final Map<Long, List<StoredFEntry>> storage = new HashMap<Long, List<StoredFEntry>>();

	/**
	 * Eine einfache Zählervariable um fortlaufende eindeutige IDs für erstellte FEntries zu erzeugen.
	 */
	private Long idCounter = 0L;

	public enum Status {
		OK,
		DELETED
	}

	/**
	 * Diese Klasse versieht einen FEntry mit Timestap und Status Informationen, die benötigt werden wenn der FEntry
	 * in der MockAPI gespeichert wird.
	 */
	private class StoredFEntry {
		private final Long timestamp;
		private final FEntry fEntry;
		private Status status;

		public StoredFEntry(final Long timestamp, final FEntry fEntry) {
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

		public Status getStatus() {
			return this.status;
		}

		public void setStatus(final Status newStatus) {
			this.status = newStatus;
		}
	}

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
	public FEntry getFEntryWithId(final Long fEntryId) {
		FEntry foundFEntry = null;

		final Optional<StoredFEntry> latestStorageEntryForFEntryID = getLatestStorageEntryForFEntryID(fEntryId);
		if (latestStorageEntryForFEntryID.isPresent() && latestStorageEntryForFEntryID.get().getStatus() != Status.DELETED) {
			foundFEntry = latestStorageEntryForFEntryID.get().getFEntry();
		}

		return foundFEntry;
	}

	/**
	 * Erstellt einen neuen FEntry und weißt ihm eine neue ID vor.
	 *
	 * @param newFEntry Der neue zu erstellende FEntry.
	 * @return Die ID des erstellten FEntries.
	 */
	public Long createNewFEntry(final FEntry newFEntry) {
		//generate id
		newFEntry.setIdentifier(idCounter++);

		//create new sublist to account for new file if no existing file was found
		final StoredFEntry newEntry = new StoredFEntry(System.currentTimeMillis(), createTypeAwareFEntryCopy(newFEntry));
		final List<StoredFEntry> versions = new ArrayList<StoredFEntry>();
		versions.add(newEntry);
		storage.put(newFEntry.getIdentifier(), versions);

		APILogger.logSuccess(APILogger.actionStringForFEntryAction("File Creation", newFEntry));

		return newFEntry.getIdentifier();
	}


	/**
	 * Aktulisiert den gegebenen FEntry bzw. seine lokale Kopie in der API. 2 FEntries werden hierbei als gleich betrachtet
	 * wenn sie die selbe ID besitzen.
	 *
	 * @param updatedFEntry zu bearbeitendes File
	 * @return True, wenn der FEntry gefunden und aktualisiert wurde. False, sonst.
	 */
	public boolean updateFEntry(final FEntry updatedFEntry) {
		final List<StoredFEntry> versions = storage.get(updatedFEntry.getIdentifier());

		if (versions == null) {
			//no file found found - error!
			APILogger.logFailure(APILogger.actionStringForFEntryAction("FEntry Update", updatedFEntry), FILE_NOT_FOUND);
		} else {
			final StoredFEntry newEntry = new StoredFEntry(System.currentTimeMillis(), createTypeAwareFEntryCopy(updatedFEntry));
			versions.add(newEntry);
			APILogger.logSuccess(APILogger.actionStringForFEntryAction("FEntry Update", updatedFEntry));
		}

		return versions != null;
	}

	/**
	 * Löscht den gegebenen FEntry bzw. seine lokale Kopie in der API. 2 FEntries werden hierbei als gleich betrachtet
	 * wenn sie die selbe ID besitzen.
	 *
	 * @param deletedFEntry Der zu löschende FEntry.
	 * @return True, wenn der FEntry gefunden und gelöscht wurde. False, sonst.
	 */
	public boolean deleteFEntry(final FEntry deletedFEntry) {
		final List<StoredFEntry> versions = storage.get(deletedFEntry.getIdentifier());

		if (versions == null) {
			APILogger.logFailure(APILogger.actionStringForFEntryAction("FEntry Deletion", deletedFEntry), FILE_NOT_FOUND);
		} else {
			final StoredFEntry deletionEntry = new StoredFEntry(System.currentTimeMillis(), createTypeAwareFEntryCopy(deletedFEntry));
			deletionEntry.setStatus(Status.DELETED);
			versions.add(deletionEntry);
			APILogger.logSuccess(APILogger.actionStringForFEntryAction("FEntry Deletion", deletedFEntry));
		}

		return versions != null;
	}

	/**
	 * Liefert alle FEntries, welche sich nach dem gegebenen Zeitpunkt geändert haben oder erstellt wurden.
	 *
	 * @param timeOfLastPoll Timestamp der letzten Abfrage in ms.
	 * @return ImmutableList von FEntries die sich geändert haben bzw. neu erstellt wurden.
	 */
	public ImmutableList<FEntry> getChangesSince(final long timeOfLastPoll) {
		final List<FEntry> changedFEntries = new ArrayList<FEntry>();

		for (final Long key : storage.keySet()) {
			final StoredFEntry latestStorageEntryForFEntryID = getLatestStorageEntryForFEntryID(key).get();
			if (latestStorageEntryForFEntryID.getTimestamp() >= timeOfLastPoll && latestStorageEntryForFEntryID.getStatus() != Status.DELETED) {
				changedFEntries.add(latestStorageEntryForFEntryID.getFEntry());
			}
		}

		return ImmutableList.copyOf(changedFEntries);
	}

	/**
	 * Kopiert einen FEntry mit dem entsprechenden Copy-Konstruktor.
	 *
	 * @param fEntryToCopy Der FEntry der kopiert werden soll.
	 * @return Die Kopie des FEntries.
	 */
	private FEntry createTypeAwareFEntryCopy(final FEntry fEntryToCopy) {
		FEntry copy = null;
		if (fEntryToCopy instanceof Directory) {
			copy = new Directory((Directory) fEntryToCopy);
		} else if (fEntryToCopy instanceof File) {
			copy = new File((File) fEntryToCopy);
		}
		return copy;
	}

	/**
	 * Liefert den aktuellsten StoredFEntry des FEntries mit der gegebenen ID.
	 *
	 * @param fEntryId Die ID des FEntries.
	 * @return Der aktuellste StoredFEntry, des FEntries, als Optional.
	 */
	private Optional<StoredFEntry> getLatestStorageEntryForFEntryID(final Long fEntryId) {
		Optional<StoredFEntry> foundEntry = Optional.absent();

		final List<StoredFEntry> versions = storage.get(fEntryId);

		if (versions != null) {
			long latestTimestamp = 0;
			for (final StoredFEntry storedFEntry : versions) {
				if (storedFEntry.getTimestamp() >= latestTimestamp) {
					foundEntry = Optional.of(storedFEntry);
					latestTimestamp = storedFEntry.getTimestamp();
				}
			}
		}

		return foundEntry;
	}

	//TODO: check permissions?
	//TODO: realize invitation
	//TODO: rethink: deletion of shared file/directories?

	/**
	 * Diese Funktion erstellt einige Beispieldateien und -verzeichnisse, die für Testzwecke benötigt werden.
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
			final StoredFEntry newEntry = new StoredFEntry(System.currentTimeMillis(), fEntry);
			final List<StoredFEntry> newStorage = new ArrayList<StoredFEntry>();
			newStorage.add(newEntry);
			storage.put(newEntry.getFEntry().getIdentifier(), newStorage);
		}

	}
}
