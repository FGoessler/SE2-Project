package de.sharebox.file.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Diese Klasse ist die abstrakte Oberklasse aller Dateiobjekte wie Files und Directories. Hauptsächlich implementiert
 * sie Observer Mechanismen und eine ID anhand der Dateien und Verzeichnisse eindeutig unterschieden werden können.
 */
public class FEntry {

	public enum ChangeType {
		NAME_CHANGED,
		REMOVED_CHILDREN, ADDED_CHILDREN
	}

	private Integer identifier;
	private String name;
	private transient List<FEntryObserver> observers = new ArrayList<FEntryObserver>();

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
		for (FEntryObserver observer : observers) {
			observer.fEntryChangedNotification(this, reason);
		}
	}

	/**
	 * Benachrichtigt alle Observer das dieses Objekt aus dem Dateisystem gelöscht wurde. Das Java-Objekt an sich ist
	 * aber noch nicht notwendigerweise gelöscht.
	 */
	protected void fireDeleteNotification() {
		for (FEntryObserver observer : observers) {
			observer.fEntryDeletedNotification(this);
		}
	}
}
