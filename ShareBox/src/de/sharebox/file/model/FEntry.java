package de.sharebox.file.model;

import de.sharebox.user.model.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Diese Klasse ist die abstrakte Oberklasse aller Dateiobjekte wie Files und Directories. Hauptsächlich implementiert
 * sie Observer Mechanismen und eine ID anhand der Dateien und Verzeichnisse eindeutig unterschieden werden können.
 */
public class FEntry {

	public enum ChangeType {
		NAME_CHANGED,
		REMOVED_CHILDREN,
		ADDED_CHILDREN,
		PERMISSION_CHANGED
	}

	private Integer identifier;
	private String name;
	private transient List<FEntryPermission> permissions = new ArrayList<FEntryPermission>();
	private transient List<FEntryObserver> observers = new ArrayList<FEntryObserver>();

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
			newPermission.setReadAllowed(oldPermission.getReadAllowed());
			newPermission.setWriteAllowed(oldPermission.getWriteAllowed());
			newPermission.setManageAllowed(oldPermission.getManageAllowed());

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
		this.identifier = identifier;
	}

	/**
	 * Ändert den Namen des FEntries('Dateiname') und benachrichtigt alle Observer über die Änderung.
	 *
	 * @param name Der neue Name.
	 */
	public void setName(String name) {
		this.name = name;

		fireChangeNotification(ChangeType.NAME_CHANGED);
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
	protected void fireChangeNotification(ChangeType reason) {
		ArrayList<FEntryObserver> localObservers = new ArrayList<FEntryObserver>(observers);
		for (FEntryObserver observer : localObservers) {
			observer.fEntryChangedNotification(this, reason);
		}
	}

	/**
	 * Benachrichtigt alle Observer das dieses Objekt aus dem Dateisystem gelöscht wurde. Das Java-Objekt an sich ist
	 * aber noch nicht notwendigerweise gelöscht.
	 */
	protected void fireDeleteNotification() {
		ArrayList<FEntryObserver> localObservers = new ArrayList<FEntryObserver>(observers);
		for (FEntryObserver observer : localObservers) {
			observer.fEntryDeletedNotification(this);
		}
	}

	/**
	 *
	 * @param user
	 * @param read
	 * @param write
	 * @param manage
	 */
	public void setPermission(User user, boolean read, boolean write, boolean manage) {
		FEntryPermission permission = getPermissionOfUser(user);

		if (read || write || manage) {
			if (!permissions.contains(permission)) {
				permissions.add(permission);
			}

			permission.setReadAllowed(read);
			permission.setWriteAllowed(write);
			permission.setManageAllowed(manage);

			fireChangeNotification(ChangeType.PERMISSION_CHANGED);
		} else {
			permissions.remove(permission);
		}
	}

	/**
	 *
	 * @return
	 */
	public List<FEntryPermission> getPermissions() {
		return permissions;
	}

	/**
	 *
	 * @param user
	 * @return
	 */
	public FEntryPermission getPermissionOfUser(User user) {
		FEntryPermission permission = null;

		for (FEntryPermission perm : permissions) {
			if (perm.getUser().getEmail().equals(user.getEmail())) {
				permission = perm;
			}
		}

		if (permission == null) {
			permission = new FEntryPermission(user, this);
			permission.setReadAllowed(false);
			permission.setWriteAllowed(false);
			permission.setManageAllowed(false);
		}

		return permission;
	}


}
