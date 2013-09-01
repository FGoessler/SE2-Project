package de.sharebox.file;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.sharebox.api.APILogger;
import de.sharebox.api.FileAPI;
import de.sharebox.file.model.Directory;
import de.sharebox.file.model.FEntry;
import de.sharebox.file.notification.DirectoryNotification;
import de.sharebox.file.notification.DirectoryObserver;
import de.sharebox.file.notification.FEntryNotification;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Der FileManager dient als Bindeglied zwischen FileAPI und der Applikationen. Jeder in der Applikation verwendete
 * FEntry sollte sich beim FileManager registrieren. Der FileManager registriert sich bei jedem so registrierten FEntry
 * als Observer. Jede Änderungsbenachrichtigung sendet der FileManager an die FileAPI weiter. Zudem ruft der FileManager
 * regelmäßig (derzeit alle 30sek) Änderungen bei der FileAPI ab und führt diese auf den registrierten FEntries aus und
 * löst die entsprechenden Notifications aus, wobei als Source der FileManager gesetzt wird.
 * Durch das Setzen der Source der Notification kann der FileManager Änderungsbenachrichtigungen, die von ihm selbst
 * erzeugt wurden, ignorieren und verhindert somit einen endlosen 'Notificationkreis'.
 */
@Singleton
public class FileManager implements DirectoryObserver {
	private final FileAPI fileAPI;
	private Timer timer;

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

	/**
	 * Startet einen Timer, der alle 30 Sekunden Änderungen der API und des Dateisystems abfragt.
	 */
	public void startPolling() {
		stopPolling();

		timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				pollAPIForChanges();
				pollFileSystemForChanges();
			}
		}, 0, 30000);
	}

	/**
	 * Stoppt den laufenden Timer, der Änderungen der API und des Dateisystems abfragt.
	 */
	public void stopPolling() {
		if (timer != null) {
			timer.cancel();
		}
	}

	/**
	 * Registriert einen neuen oder existierenden FEntry, der ab sofort vom FileManager beoabachtet und mit der FileAPI
	 * abgeglichen wird. Handelt es sich um ein Directory werden auch rekursiv alle enthaltenen FEntries registriert.
	 * Besitzt der FEntry keine ID so wird er der API als neuer FEntry gemeldet und die erhaltene ID wird zugeweißen.
	 *
	 * @param fEntry Der zu registrierende FEntry.
	 * @return ob die Operation erfolgreich war
	 */
	public boolean registerFEntry(final FEntry fEntry) {
		fEntry.addObserver(this);

		if (fEntry.getIdentifier() == null) {
			fEntry.setIdentifier(fileAPI.createNewFEntry(fEntry));
		}

		registeredFEntries.put(fEntry.getIdentifier(), fEntry);

		if (fEntry instanceof Directory) {
			for (final FEntry child : ((Directory) fEntry).getFEntries()) {
				registerFEntry(child);
			}
		}

		return true;
	}

	/**
	 * Fragt Änderungen seit dem letzten Poll von der FIleAPI ab und aktualisiert/ergänzt die veränderten FEntries.
	 */
	public void pollAPIForChanges() {
		APILogger.logMessage("Polling changes from API...");

		final long currentAPIPoll = System.currentTimeMillis();

		final ImmutableList<FEntry> changedFEntries = fileAPI.getChangesSince(lastAPIPoll);
		for (final FEntry changedFEntry : changedFEntries) {
			final FEntry currentFEntry = registeredFEntries.get(changedFEntry.getIdentifier());
			if (currentFEntry != null) {
				currentFEntry.applyChanges(changedFEntry, this);
			}
		}

		lastAPIPoll = currentAPIPoll;
	}

	/**
	 * Sucht nach Änderungen im Dateisystem seit dem letzten Poll und aktualisiert/ergänzt die veränderten FEntries.
	 */
	public void pollFileSystemForChanges() {
		APILogger.logMessage("Polling changes from local filesystem... NOT implemented in this Prototype!");
	}

	@Override
	public void directoryNotification(final DirectoryNotification notification) {
		if (notification.getSource() != this) {    //do not publish changes that came from the api/filemanager
			if (notification.getChangeType().equals(FEntryNotification.ChangeType.ADDED_CHILDREN)) {
				for (final FEntry addedChild : notification.getAffectedChildren()) {
					registerFEntry(addedChild);
				}
			}
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
