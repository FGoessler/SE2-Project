package de.sharebox.file.model;

import com.sun.istack.internal.NotNull;
import de.sharebox.user.model.User;

/**
 * Diese Klasse bildet die Rechte eines Nutzers an einer bestimmten Datei/Verzeichnis ab. Es gibt Lese, Schreibe und
 * Verwaltungsrechte (= Möglichkeit des Änderns, Hinzufügens und Enfernens von Rechten einzelner Nutzer an der Datei).
 */
public class FEntryPermission {
	private Boolean readAllowed = false;
	private Boolean writeAllowed = false;
	private Boolean manageAllowed = false;

	private final User user;
	private final FEntry fEntry;

	/**
	 * Erstellt ein neue FEntryPermission Objekt das den gegebenen Nutzer mit dem gegebenen FEntry in Beziehung setzt.
	 *
	 * @param user   Der Nutzer dessen Rechte an dem FEntry mit diesem Objekt definiert werden.
	 * @param fEntry Der FEntry für den die Rechte des Users definiert werden.
	 */
	public FEntryPermission(final @NotNull User user, final @NotNull FEntry fEntry) {
		this.user = user;
		this.fEntry = fEntry;
	}

	/**
	 * Gibt den Nutzer zurück für den die Rechte definiert sind.
	 *
	 * @return Der Nutzer für den die Rechte definiert sind.
	 */
	public User getUser() {
		return user;
	}

	/**
	 * Gibt den FEntry zurück für den die Rechte definiert sind.
	 *
	 * @return Der FEntry für den die Rechte definiert sind.
	 */
	public FEntry getFEntry() {
		return fEntry;
	}

	/**
	 * Bestimmt ob der Nutzer auf dem FEntry Leserechte besitzt.
	 *
	 * @return true, wenn der Nutzer Leserechte besitzt; false, sonst
	 */
	public Boolean getReadAllowed() {
		return readAllowed;
	}

	/**
	 * Setzt die Leserechte und feuert eine PERMISSION_CHANGED Notification auf dem fEntry.
	 *
	 * @param readAllowed Der neue Wert für die Leserechte.
	 */
	public void setReadAllowed(final Boolean readAllowed) {
		this.readAllowed = readAllowed;

		fEntry.fireChangeNotification(FEntryObserver.ChangeType.PERMISSION_CHANGED);
	}

	/**
	 * Bestimmt ob der Nutzer auf dem FEntry Schreibrechte besitzt.
	 *
	 * @return true, wenn der Nutzer Schreibrechte besitzt; false, sonst
	 */
	public Boolean getWriteAllowed() {
		return writeAllowed;
	}

	/**
	 * Setzt die Schreibrechte und feuert eine PERMISSION_CHANGED Notification auf dem fEntry.
	 *
	 * @param writeAllowed Der neue Wert für die Schreibrechte.
	 */
	public void setWriteAllowed(final Boolean writeAllowed) {
		this.writeAllowed = writeAllowed;

		fEntry.fireChangeNotification(FEntryObserver.ChangeType.PERMISSION_CHANGED);
	}

	/**
	 * Bestimmt ob der Nutzer auf dem FEntry Verwaltungsrechte besitzt.
	 *
	 * @return true, wenn der Nutzer Verwaltungsrechte besitzt; false, sonst
	 */
	public Boolean getManageAllowed() {
		return manageAllowed;
	}

	/**
	 * Setzt die Verwaltungsrechte und feuert eine PERMISSION_CHANGED Notification auf dem fEntry.
	 *
	 * @param manageAllowed Der neue Wert für die Verwaltungsrechte.
	 */
	public void setManageAllowed(final Boolean manageAllowed) {
		this.manageAllowed = manageAllowed;

		fEntry.fireChangeNotification(FEntryObserver.ChangeType.PERMISSION_CHANGED);
	}

	/**
	 * Setzt alle 3 Rechte auf einmal und feuert somit die PERMISSION_CHANGED Notification auf dem FEntry nur einmal.
	 *
	 * @param read   Der neue Wert für Leserechte.
	 * @param write  Der neue Wert für Schreibrechte.
	 * @param manage Der neue Wert für Verwaltungsrechte.
	 */
	public void setPermissions(final Boolean read, final Boolean write, final Boolean manage) {
		readAllowed = read;
		writeAllowed = write;
		manageAllowed = manage;

		fEntry.fireChangeNotification(FEntryObserver.ChangeType.PERMISSION_CHANGED);
	}
}
