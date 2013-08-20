package de.sharebox.file.model;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import de.sharebox.user.model.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Diese Klasse ist die abstrakte Oberklasse aller Dateiobjekte wie Files und Directories. Hauptsächlich implementiert
 * sie Observer Mechanismen und eine ID anhand der Dateien und Verzeichnisse eindeutig unterschieden werden können.
 */
public class FEntry {

	private Integer identifier;
	private String name;

	private transient List<FEntryPermission> permissions = new ArrayList<FEntryPermission>();
	protected transient List<FEntryObserver> observers = new ArrayList<FEntryObserver>();

	/**
	 * Der Standard-Konstruktor.
	 */
	public FEntry() {
		super();
	}

	/**
	 * Der Copy Konstruktor.
	 *
	 * @param sourceFEntry Das zu kopierende Objekt.
	 */
	public FEntry(FEntry sourceFEntry) {
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
	 *
	 * @param identifier Die neue ID dieses Objekts.
	 */
	public void setIdentifier(Integer identifier) {
		//TODO: check permission

		this.identifier = identifier;
	}

	/**
	 * Ändert den Namen des FEntries('Dateiname') und benachrichtigt alle Observer über die Änderung.
	 *
	 * @param name Der neue Name.
	 */
	public void setName(String name) {
		//TODO: check permission

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
	 *
	 * @param user   Der Nutzer der die Rechte erhält.
	 * @param read   Der neue Wert für Leserechte.
	 * @param write  Der neue Wert für Schreibrechte.
	 * @param manage Der neue Wert für Verwaltungsrechte.
	 */
	public void setPermission(User user, boolean read, boolean write, boolean manage) {
		//TODO: check permission

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

		for (FEntryPermission perm : permissions) {
			if (perm.getUser().getEmail().equals(user.getEmail())) {
				permission = Optional.of(perm);
			}
		}

		if (!permission.isPresent()) {
			permission = Optional.of(new FEntryPermission(user, this));
		}

		return permission.get();
	}
}
