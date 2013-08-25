package de.sharebox.file.model;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import de.sharebox.api.UserAPI;
import de.sharebox.user.model.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Diese Klasse ist die abstrakte Oberklasse aller Dateiobjekte wie Files und Directories. Hauptsächlich implementiert<br/>
 * sie Observer-Mechanismen und eine ID anhand der Dateien und Verzeichnisse eindeutig unterschieden werden können.
 */
public class FEntry {
	private final UserAPI userAPI;

	private Integer identifier;
	private String name;

	private final List<FEntryPermission> permissions = new ArrayList<FEntryPermission>();
	protected final List<LogEntry> logEntries = new ArrayList<LogEntry>();
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
		permissions.add(new FEntryPermission(creatingUser, this, true, true, true));
		addLogEntry(LogEntry.LogMessage.CREATED);
	}

	/**
	 * Der Copy Konstruktor. Permissions und LogEntries werden ebenfalls mit ihrem Copy Konstruktor kopiert.
	 * Observer werden nicht übertragen.
	 *
	 * @param sourceFEntry Das zu kopierende Objekt.
	 */
	public FEntry(final FEntry sourceFEntry) {
		this.userAPI = sourceFEntry.getUserAPI();
		this.name = sourceFEntry.name;
		this.identifier = sourceFEntry.identifier;

		//copy permissions
		for (final FEntryPermission oldPermission : sourceFEntry.getPermissions()) {
			permissions.add(new FEntryPermission(oldPermission));
		}
		for (final LogEntry oldLogEntry : sourceFEntry.getLogEntries()) {
			logEntries.add(new LogEntry(oldLogEntry));
		}
	}

	/**
	 * Liefert die aktuell für diesen FEntry relevante UserAPI. Wird dazu benötigt den aktuell eingeloggten Nutzer zu<br/>
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
	public Integer getIdentifier() {
		return identifier;
	}

	/**
	 * Setzt die eindeutige ID des Objekts. Sollte nur geändert werden, falls der Server entsprechende Änderungen sendet.<br/>
	 * Hinweis: Es werden keine Rechte überprüft! Diese hat der Aufrufer dieser Methode vorher zu überprüfen, falls<br/>
	 * diese Methode auf eine Aktion des Nutzers hin aufgerufen wird und nicht aufgrund von Änderungen seitens der API.
	 *
	 * @param identifier Die neue ID dieses Objekts.
	 */
	public void setIdentifier(final Integer identifier) {
		this.identifier = identifier;
	}

	/**
	 * Ändert den Namen des FEntries('Dateiname') und benachrichtigt alle Observer über die Änderung.<br/>
	 * Hinweis: Es werden keine Rechte überprüft! Diese hat der Aufrufer dieser Methode vorher zu überprüfen, falls<br/>
	 * diese Methode auf eine Aktion des Nutzers hin aufgerufen wird und nicht aufgrund von Änderungen seitens der API.
	 *
	 * @param name Der neue Name.
	 */
	public void setName(final String name) {
		this.name = name;
		addLogEntry(LogEntry.LogMessage.RENAMED);
		fireChangeNotification(FEntryObserver.ChangeType.NAME_CHANGED);
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
	 * Benachrichtigt alle Observer das eine Änderung stattgefunden hat.
	 */
	public void fireChangeNotification(final FEntryObserver.ChangeType reason) {
		final ArrayList<FEntryObserver> localObservers = new ArrayList<FEntryObserver>(observers);
		for (final FEntryObserver observer : localObservers) {
			observer.fEntryChangedNotification(this, reason);
		}
	}

	/**
	 * Benachrichtigt alle Observer, dass dieses Objekt aus dem Dateisystem gelöscht wurde. Das Java-Objekt an sich ist<br/>
	 * aber noch nicht notwendigerweise gelöscht.
	 */
	public void fireDeleteNotification() {
		final ArrayList<FEntryObserver> localObservers = new ArrayList<FEntryObserver>(observers);
		for (final FEntryObserver observer : localObservers) {
			observer.fEntryDeletedNotification(this);
		}
	}

	/**
	 * Setzt die Rechte eines Nutzers an diesem FEntry. Löst entsprechende PERMISSION_CHANGED-Notifikationen aus.<br/>
	 * Hinweis: Es werden keine Rechte überprüft! Diese hat der Aufrufer dieser Methode vorher zu überprüfen, falls<br/>
	 * diese Methode auf eine Aktion des Nutzers hin aufgerufen wird und nicht aufgrund von Änderungen seitens der API.
	 *
	 * @param user   Der Nutzer der die Rechte erhält.
	 * @param read   Der neue Wert für Leserechte.
	 * @param write  Der neue Wert für Schreibrechte.
	 * @param manage Der neue Wert für Verwaltungsrechte.
	 */
	public void setPermission(final User user, final boolean read, final boolean write, final boolean manage) {
		final FEntryPermission permission = getPermissionOfUser(user);

		if (read || write || manage) {
			if (!permissions.contains(permission)) {
				permissions.add(permission);
			}
			permission.setPermissions(read, write, manage);        //Notification und LogMessage werden vom FEntryPermission Objekt erstellt
		} else {
			permissions.remove(permission);
			addLogEntry(LogEntry.LogMessage.PERMISSION);
			fireChangeNotification(FEntryObserver.ChangeType.PERMISSION_CHANGED);
		}
	}

	/**
	 * Liefert eine unveränderbare Liste aller an Nutzer vergebenen Rechte. Nutzer die keinerlei Rechte an einem<br/>
	 * FEntry besitzen werden in der Liste nicht mit einem eigenen fEntryPermission-Objekt aufgeführt.<br/>
	 * Um Änderungen an den Rechten vorzunehmen, sollten die Objekte direkt manipuliert oder die setPermission-Methode<br/>
	 * verwendet werden.
	 *
	 * @return Liste aller vergebenen FEntryPermissions.
	 */
	public ImmutableList<FEntryPermission> getPermissions() {
		return ImmutableList.copyOf(permissions);
	}

	/**
	 * Gibt die Rechte des gegebenen Benutzers als FEntryPermission Objekt zurück.
	 *
	 * @param user Der Nutzer dessen Rechte abgefragt werden sollen.
	 * @return Das FEntryPermission-Objekt mit allen Informationen über die Rechte des Nutzers an dem FEntry.
	 */
	public FEntryPermission getPermissionOfUser(final User user) {
		Optional<FEntryPermission> permission = Optional.absent();

		try {
			for (final FEntryPermission perm : permissions) {
				if (perm.getUser().getEmail().equals(user.getEmail())) {
					permission = Optional.of(perm);
				}
			}
		} finally {
			if (!permission.isPresent()) {
				permission = Optional.of(new FEntryPermission(user, this));
			}
		}

		return permission.get();
	}

	/**
	 * Gibt die Rechte des aktuell eingeloggten Benutzers (basierend auf den Daten der UserAPI) als FEntryPermission
	 * Objekt zurück.
	 *
	 * @return Das FEntryPermission Objekt mit allen Informationen über die Rechte des Nutzers an dem FEntry.
	 */
	public FEntryPermission getPermissionOfCurrentUser() {
		return getPermissionOfUser(getUserAPI().getCurrentUser());
	}

	public ImmutableList<LogEntry> getLogEntries() {
		return ImmutableList.copyOf(logEntries);
	}

	public void addLogEntry(final LogEntry.LogMessage message) {
		logEntries.add(new LogEntry(message));
	}
}
