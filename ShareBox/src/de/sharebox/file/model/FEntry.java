package de.sharebox.file.model;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import de.sharebox.api.UserAPI;
import de.sharebox.user.model.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Diese Klasse ist die abstrakte Oberklasse aller Dateiobjekte wie Files und Directories. Hauptsächlich implementiert
 * sie Observer Mechanismen und eine ID anhand der Dateien und Verzeichnisse eindeutig unterschieden werden können.
 */
public class FEntry {
	private final UserAPI userAPI;

	private Integer identifier;
	private String name;

	private final transient List<FEntryPermission> permissions = new ArrayList<FEntryPermission>();
	protected transient List<FEntryObserver> observers = new ArrayList<FEntryObserver>();

	/**
	 * Der Standard-Konstruktor.
	 *
	 * @param userAPI Die aktuell für diesen FEntry relevante UserAPI. Wird dazu benötigt den aktuell eingeloggten
	 *                Nutzer zu bestimmen und Rechte zu überprüfen.
	 */
	public FEntry(UserAPI userAPI) {
		this.userAPI = userAPI;
	}

	/**
	 * Der Copy Konstruktor.
	 *
	 * @param sourceFEntry Das zu kopierende Objekt.
	 */
	public FEntry(FEntry sourceFEntry) {
		this.userAPI = sourceFEntry.getUserAPI();
		this.name = sourceFEntry.name;
		this.identifier = sourceFEntry.identifier;

		//copy permissions
		for (FEntryPermission oldPermission : sourceFEntry.getPermissions()) {
			FEntryPermission newPermission = new FEntryPermission(oldPermission.getUser(), this);
			newPermission.setPermissions(oldPermission.getReadAllowed(), oldPermission.getWriteAllowed(), oldPermission.getManageAllowed());

			permissions.add(newPermission);
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
	public Integer getIdentifier() {
		return identifier;
	}

	/**
	 * Setzt die eindeutige ID des Objekts. Sollte nur geändert werden falls der Server entsprechende Änderungen sendet.
	 * <br/><br/>
	 * Hinweis: Es werden keine Permissions überprüft! Diese hat der Aufrufer dieser Methode vorher zu überprüfen, falls
	 * diese Methode auf eine Aktion des Nutzers hin aufgerufen wird und nicht aufgrund von Änderungen seitens der API.
	 *
	 * @param identifier Die neue ID dieses Objekts.
	 */
	public void setIdentifier(Integer identifier) {
		this.identifier = identifier;
	}

	/**
	 * Ändert den Namen des FEntries('Dateiname') und benachrichtigt alle Observer über die Änderung.
	 * <br/><br/>
	 * Hinweis: Es werden keine Permissions überprüft! Diese hat der Aufrufer dieser Methode vorher zu überprüfen, falls
	 * diese Methode auf eine Aktion des Nutzers hin aufgerufen wird und nicht aufgrund von Änderungen seitens der API.
	 *
	 * @param name Der neue Name.
	 */
	public void setName(String name) {
		this.name = name;

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
	 * Registriert ein Objekt als Observer. Dieses erhält dann Benachrichtigungen über Änderung und Läschung des Objekts.
	 *
	 * @param observer Der Observer der benachrichtigt werden soll.
	 */
	public void addObserver(FEntryObserver observer) {
		observers.add(observer);
	}

	/**
	 * Entfernt den Observer, sodass dieser nicht mehr Benachrichtigt wird.
	 *
	 * @param observer Der Observer der entfernt werden soll.
	 */
	public void removeObserver(FEntryObserver observer) {
		observers.remove(observer);
	}

	/**
	 * Benachrichtigt alle Observer das eine Änderung stattgefunden hat.
	 */
	public void fireChangeNotification(FEntryObserver.ChangeType reason) {
		ArrayList<FEntryObserver> localObservers = new ArrayList<FEntryObserver>(observers);
		for (FEntryObserver observer : localObservers) {
			observer.fEntryChangedNotification(this, reason);
		}
	}

	/**
	 * Benachrichtigt alle Observer das dieses Objekt aus dem Dateisystem gelöscht wurde. Das Java-Objekt an sich ist
	 * aber noch nicht notwendigerweise gelöscht.
	 */
	public void fireDeleteNotification() {
		ArrayList<FEntryObserver> localObservers = new ArrayList<FEntryObserver>(observers);
		for (FEntryObserver observer : localObservers) {
			observer.fEntryDeletedNotification(this);
		}
	}

	/**
	 * Setzt die Rechte eines Nutzers an diesem FEntry.
	 * Löst entsprechende PERMISSION_CHANGED Notifications aus.
	 * <br/><br/>
	 * Hinweis: Es werden keine Permissions überprüft! Diese hat der Aufrufer dieser Methode vorher zu überprüfen, falls
	 * diese Methode auf eine Aktion des Nutzers hin aufgerufen wird und nicht aufgrund von Änderungen seitens der API.
	 *
	 * @param user   Der Nutzer der die Rechte erhält.
	 * @param read   Der neue Wert für Leserechte.
	 * @param write  Der neue Wert für Schreibrechte.
	 * @param manage Der neue Wert für Verwaltungsrechte.
	 */
	public void setPermission(User user, boolean read, boolean write, boolean manage) {
		FEntryPermission permission = getPermissionOfUser(user);

		if (read || write || manage) {
			if (!permissions.contains(permission)) {
				permissions.add(permission);
			}

			permission.setPermissions(read, write, manage);        //Notification wird vom FEntryPermission Objekt ausgelöst
		} else {
			permissions.remove(permission);

			fireChangeNotification(FEntryObserver.ChangeType.PERMISSION_CHANGED);
		}
	}

	/**
	 * Liefert eine unveränderbare Liste aller an Nutzer vergebenen Permissions. Nutzer die keinerlei Rechte an einem
	 * FEntry besitzen werden in der Liste nicht mit einem eigenen fEntryPermission Objekt aufgeführt.
	 * Um Änderungen an den Rechten vorzunehmen sollten die Objekte direkt manipuliert oder die setPermission Methode
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
	 * @return Das FEntryPermission Objekt mit allen Informationen über die Rechte des Nutzers an dem FEntry.
	 */
	public FEntryPermission getPermissionOfUser(User user) {
		Optional<FEntryPermission> permission = Optional.absent();
		//TODO: handle null values -> use Optionals
		try {
			for (FEntryPermission perm : permissions) {
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
}
