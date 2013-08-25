package de.sharebox.file.model;

import com.sun.istack.internal.NotNull;
import de.sharebox.user.model.User;

/**
 * Diese Klasse bildet die Rechte eines Nutzers an einer bestimmten Datei/Verzeichnis ab. Es gibt Lese-, Schreib- und
 * Verwaltungsrechte (= Möglichkeit des Änderns, Hinzufügens und Enfernens von Rechten einzelner Nutzer an der Datei).
 */
public class FEntryPermission {
	private Boolean readAllowed = false;
	private Boolean writeAllowed = false;
	private Boolean manageAllowed = false;

	private final User user;
	private final FEntry fEntry;

	/**
	 * Erstellt ein neues FEntryPermission-Objekt das den gegebenen Nutzer mit dem gegebenen FEntry in Beziehung setzt.<br/>
	 * Die Standardrechte hierbei sind alles auf false.
	 * 
	 * @param user   Der Nutzer dessen Rechte an dem FEntry mit diesem Objekt definiert werden.
	 * @param fEntry Der FEntry für den die Rechte des Users definiert werden.
	 */
	public FEntryPermission(final @NotNull User user, final @NotNull FEntry fEntry) {
		this.user = user;
		this.fEntry = fEntry;
	}

	/**
	 * Erstellt ein neue FEntryPermission Objekt das den gegebenen Nutzer mit dem gegebenen FEntry in Beziehung setzt.
	 * Hinweis: Dieser Konstruktor dient dazu ein initilaes Objekt mit bestimmten Rechten zu erzeugen und feuert
	 * im Gegensatz zu den set-Methoden keine Notification auf dem FEntry und erzeugt auch keinen LogEntry.
	 *
	 * @param user          Der Nutzer dessen Rechte an dem FEntry mit diesem Objekt definiert werden.
	 * @param fEntry        Der FEntry für den die Rechte des Users definiert werden.
	 * @param readAllowed   Der initale Wert für Leserechte.
	 * @param writeAllowed  Der initale Wert für Schreibrechte.
	 * @param manageAllowed Der initale Wert für Verwaltungsrechte.
	 */
	public FEntryPermission(final @NotNull User user, final @NotNull FEntry fEntry,
							final Boolean readAllowed, final Boolean writeAllowed, final Boolean manageAllowed) {
		this.user = user;
		this.fEntry = fEntry;
		this.readAllowed = readAllowed;
		this.writeAllowed = writeAllowed;
		this.manageAllowed = manageAllowed;
	}

	/**
	 * Copy-Konstruktor.
	 *
	 * @param permissionToCopy Das zu kopierende FEntryPermission Objekt.
	 */
	public FEntryPermission(final FEntryPermission permissionToCopy) {
		this.user = permissionToCopy.getUser();
		this.fEntry = permissionToCopy.getFEntry();
		this.readAllowed = permissionToCopy.getReadAllowed();
		this.writeAllowed = permissionToCopy.getWriteAllowed();
		this.manageAllowed = permissionToCopy.getManageAllowed();
	}

	/**
	 * Gibt den Nutzer zurück, für den die Rechte definiert sind.
	 *
	 * @return Der Nutzer für den die Rechte definiert sind.
	 */
	public User getUser() {
		return user;
	}

	/**
	 * Gibt den FEntry zurück, für den die Rechte definiert sind.
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
	 * Setzt die Leserechte und feuert eine PERMISSION_CHANGED-Notifikation auf dem fEntry.
	 * Erstellt zudem einen entsprechenden LogEntry.
	 * 
	 * @param readAllowed Der neue Wert für die Leserechte.
	 */
	public void setReadAllowed(final Boolean readAllowed) {
		setPermissions(readAllowed, this.writeAllowed, this.manageAllowed);
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
	 * Setzt die Schreibrechte und feuert eine PERMISSION_CHANGED-Notifikation auf dem fEntry.
	 * Erstellt zudem einen entsprechenden LogEntry.
	 * 
	 * @param writeAllowed Der neue Wert für die Schreibrechte.
	 */
	public void setWriteAllowed(final Boolean writeAllowed) {
		setPermissions(this.readAllowed, writeAllowed, this.manageAllowed);
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
	 * Setzt die Verwaltungsrechte und feuert eine PERMISSION_CHANGED-Notifikation auf dem fEntry.
	 * Erstellt zudem einen entsprechenden LogEntry.
	 * 
	 * @param manageAllowed Der neue Wert für die Verwaltungsrechte.
	 */
	public void setManageAllowed(final Boolean manageAllowed) {
		setPermissions(this.readAllowed, this.writeAllowed, manageAllowed);
	}

	/**
	 * Setzt alle 3 Rechte auf einmal und feuert somit die PERMISSION_CHANGED-Notifikation auf dem FEntry nur einmal.
	 * Erstellt zudem einen entsprechenden LogEntry.
	 * 
	 * @param read   Der neue Wert für Leserechte.
	 * @param write  Der neue Wert für Schreibrechte.
	 * @param manage Der neue Wert für Verwaltungsrechte.
	 */
	public void setPermissions(final Boolean read, final Boolean write, final Boolean manage) {
		readAllowed = read;
		writeAllowed = write;
		manageAllowed = manage;

		fEntry.addLogEntry(LogEntry.LogMessage.PERMISSION);
		fEntry.fireChangeNotification(FEntryObserver.ChangeType.PERMISSION_CHANGED);
	}
}
