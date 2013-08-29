package de.sharebox.file.model;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import de.sharebox.api.UserAPI;
import de.sharebox.file.FileManager;
import de.sharebox.file.notification.FEntryNotification;
import de.sharebox.file.notification.FEntryObserver;
import de.sharebox.user.model.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Diese Klasse ist die abstrakte Oberklasse aller Dateiobjekte wie Files und Directories.<br/>Hauptsächlich implementiert
 * sie Observer-Mechanismen und eine ID, anhand der Dateien und Verzeichnisse eindeutig unterschieden werden können.
 */
public class FEntry {
	private final UserAPI userAPI;

	private Long identifier = null;
	private String name;

	protected List<LogEntry> logEntries = new ArrayList<LogEntry>();
	private final List<Permission> permissions = new ArrayList<Permission>();
	protected final List<FEntryObserver> observers = new ArrayList<FEntryObserver>();

	/**
	 * Der Standard-Konstruktor.
	 *
	 * @param userAPI Die aktuell für diesen FEntry relevante UserAPI. Wird dazu benötigt den aktuell eingeloggten
	 *                Nutzer zu bestimmen und Rechte zu überprüfen.
	 */
	public FEntry(final UserAPI userAPI) {
		this.userAPI = userAPI;
	}

	/**
	 * Erstellt einen neuen FEntry mit den gegebenen Werten, feuert dabei allerdings keine Notifications und erstellt
	 * auch nur einen "Created" LogEntry anstatt eines "Renamed" und "PermissionChanged" LogEntry.
	 *
	 * @param userAPI      Die aktuell für diesen FEntry relevante UserAPI. Wird dazu benötigt den aktuell eingeloggten
	 *                     Nutzer zu bestimmen und Rechte zu überprüfen.
	 * @param name         Der Name des FEntries.
	 * @param creatingUser Der Nutzer, der initial alle Rechte auf diesem FEntry erhält.
	 */
	public FEntry(final UserAPI userAPI, final String name, final User creatingUser) {
		this.userAPI = userAPI;
		this.name = name;
		permissions.add(new Permission(creatingUser, this, true, true, true));
		addLogEntry(LogEntry.LogMessage.CREATED);
	}

	/**
	 * Der Copy-Konstruktor. Permissions und LogEntries werden ebenfalls mit dem Copy-Konstruktor kopiert.
	 * Observer werden nicht übertragen.
	 *
	 * @param sourceFEntry Das zu kopierende Objekt.
	 */
	public FEntry(final FEntry sourceFEntry) {
		this.userAPI = sourceFEntry.getUserAPI();
		this.name = sourceFEntry.name;
		this.identifier = sourceFEntry.identifier;

		//copy permissions
		for (final Permission oldPermission : sourceFEntry.getPermissions()) {
			permissions.add(new Permission(oldPermission));
		}
		for (final LogEntry oldLogEntry : sourceFEntry.getLogEntries()) {
			logEntries.add(new LogEntry(oldLogEntry));
		}
	}

	/**
	 * Liefert die aktuell für diesen FEntry relevante UserAPI. Wird dazu benötigt den aktuell eingeloggten Nutzer zu
	 * bestimmen und Rechte zu überprüfen.
	 *
	 * @return Die aktuell für diesen FEntry relevante UserAPI.
	 */
	protected UserAPI getUserAPI() {
		return userAPI;
	}

	/**
	 * Die eindeutige ID des Objekts im ShareBox System. Wird vom Server vergeben und daher erst gesetzt wenn das Objekt
	 * erfolgreich synchronisiert wurde.
	 *
	 * @return Die eindeutige ID des Objekts.
	 */
	public Long getIdentifier() {
		return identifier;
	}

	/**
	 * Setzt die eindeutige ID des Objekts. Sollte nur geändert werden, falls der Server entsprechende Änderungen sendet.<br/>
	 * Hinweis: Es werden keine Rechte überprüft! Diese hat der Aufrufer dieser Methode vorher zu überprüfen, falls
	 * diese Methode auf eine Aktion des Nutzers hin aufgerufen wird und nicht aufgrund von Änderungen seitens der API.
	 *
	 * @param identifier Die neue ID dieses Objekts.
	 */
	public void setIdentifier(final Long identifier) {
		this.identifier = identifier;
	}

	/**
	 * Ändert den Namen des FEntries('Dateiname') und benachrichtigt alle Observer über die Änderung.<br/>
	 * Hinweis: Es werden keine Rechte überprüft! Diese hat der Aufrufer dieser Methode vorher zu überprüfen, falls
	 * diese Methode auf eine Aktion des Nutzers hin aufgerufen wird und nicht aufgrund von Änderungen seitens der API.
	 *
	 * @param name Der neue Name.
	 */
	public void setName(final String name) {
		this.name = name;
		addLogEntry(LogEntry.LogMessage.RENAMED);
		fireNotification(FEntryNotification.ChangeType.NAME_CHANGED, this);
	}

	/**
	 * Liefert den aktuellen Namen des FEntries('Dateiname').
	 *
	 * @return Der aktuelle Name des FEntries('Dateiname')
	 */
	public String getName() {
		return name;
	}

	/**
	 * Registriert ein Objekt als Observer. Dieser erhält dann Benachrichtigungen über Änderung und Löschung des Objekts.
	 *
	 * @param observer Der Observer der benachrichtigt werden soll.
	 */
	public void addObserver(final FEntryObserver observer) {
		observers.add(observer);
	}

	/**
	 * Entfernt den Observer, sodass dieser nicht mehr benachrichtigt wird.
	 *
	 * @param observer Der Observer der entfernt werden soll.
	 */
	public void removeObserver(final FEntryObserver observer) {
		observers.remove(observer);
	}

	/**
	 * Benachrichtigt alle Observer, dass eine Änderung des gegebenen Typs stattgefunden hat.
	 *
	 * @param reason Art der stattgefunden Änderung.
	 * @param source Das Objekt, das die Änderung ausgelöst hat - im Zweifel den FEntry selbst setzen.
	 */
	public void fireNotification(final FEntryNotification.ChangeType reason, final Object source) {
		final ImmutableList<FEntryObserver> localObservers = ImmutableList.copyOf(observers);
		for (final FEntryObserver observer : localObservers) {
			observer.fEntryNotification(new FEntryNotification(this, reason, source));
		}
	}

	/**
	 * Setzt die Rechte eines Nutzers an diesem FEntry. Löst entsprechende PERMISSION_CHANGED-Notifikationen aus.<br/>
	 * Hinweis: Es werden keine Rechte überprüft! Diese hat der Aufrufer dieser Methode vorher zu überprüfen, falls
	 * diese Methode auf eine Aktion des Nutzers hin aufgerufen wird und nicht aufgrund von Änderungen seitens der API.
	 *
	 * @param user   Der Nutzer, der die Rechte erhält.
	 * @param read   Der neue Wert für Leserechte.
	 * @param write  Der neue Wert für Schreibrechte.
	 * @param manage Der neue Wert für Verwaltungsrechte.
	 */
	public void setPermission(final User user, final boolean read, final boolean write, final boolean manage) {
		final Permission permission = getPermissionOfUser(user);

		if (read || write || manage) {
			if (!permissions.contains(permission)) {
				permissions.add(permission);
			}
			permission.setPermissions(read, write, manage);        //Notification und LogMessage werden vom Permission Objekt erstellt
		} else {
			permissions.remove(permission);
			addLogEntry(LogEntry.LogMessage.PERMISSION);
			fireNotification(FEntryNotification.ChangeType.PERMISSION_CHANGED, this);
		}
	}

	/**
	 * Liefert eine immutable List aller an Nutzer vergebenen Rechte. Nutzer die keinerlei Rechte an einem
	 * FEntry besitzen werden in der Liste nicht mit einem eigenen fEntryPermission-Objekt aufgeführt.<br/>
	 * Um Änderungen an den Rechten vorzunehmen, sollten die Objekte direkt manipuliert oder die setPermission-Methode
	 * verwendet werden.
	 *
	 * @return Liste aller vergebenen FEntryPermissions.
	 */
	public ImmutableList<Permission> getPermissions() {
		return ImmutableList.copyOf(permissions);
	}

	/**
	 * Gibt die Rechte des gegebenen Benutzers als Permission-Objekt zurück.
	 *
	 * @param user Der Nutzer dessen Rechte abgefragt werden sollen.
	 * @return Das Permission-Objekt mit allen Informationen über die Rechte des Nutzers an dem FEntry.
	 */
	public Permission getPermissionOfUser(final User user) {
		Optional<Permission> permission = Optional.absent();

		try {
			for (final Permission perm : permissions) {
				if (perm.getUser().getEmail().equals(user.getEmail())) {
					permission = Optional.of(perm);
				}
			}
		} finally {
			if (!permission.isPresent()) {
				permission = Optional.of(new Permission(user, this));
			}
		}

		return permission.get();
	}

	/**
	 * Gibt die Rechte des aktuell eingeloggten Benutzers (basierend auf den Daten der UserAPI) als
	 * Permission-Objekt zurück.
	 *
	 * @return Das Permission Objekt mit allen Informationen über die Rechte des Nutzers an dem FEntry.
	 */
	public Permission getPermissionOfCurrentUser() {
		return getPermissionOfUser(getUserAPI().getCurrentUser());
	}

	/**
	 * Liefert eine ImmutableList aller LogEntries.
	 *
	 * @return Eine ImmutableList aller LogEntries.
	 */
	public ImmutableList<LogEntry> getLogEntries() {
		return ImmutableList.copyOf(logEntries);
	}

	/**
	 * Erstellt einen neuen LogEntry mit der gegebenen Meldung für diesen FEntry.
	 *
	 * @param message Die Meldung des LogEntries.
	 */
	public void addLogEntry(final LogEntry.LogMessage message) {
		logEntries.add(new LogEntry(message));
	}

	/**
	 * Aktualisiert diesen FEntry mit den Informationen des updatedFEntry. Sämtliche Änderungsnotifications erhalten
	 * als Source den gegebenen FileManager.
	 *
	 * @param updatedFEntry Der FEntry mit neuen Informationen.
	 * @param fileManager   Der FileManager, der als Source der Notificatiosn gesetzt werden soll.
	 */
	public void applyChangesFromAPI(final FEntry updatedFEntry, final FileManager fileManager) {

		logEntries = new ArrayList<LogEntry>(updatedFEntry.getLogEntries());

		if (!name.equals(updatedFEntry.getName())) {
			name = updatedFEntry.getName();
			fireNotification(FEntryNotification.ChangeType.NAME_CHANGED, fileManager);
		}

		if (!permissions.equals(updatedFEntry.getPermissions())) {
			//TODO: set permissions
		}
	}
}
