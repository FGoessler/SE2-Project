package de.sharebox.file.notification;

import com.google.common.base.Objects;
import de.sharebox.file.model.FEntry;

/**
 * Dieses Objekt wird generiert wenn eine Notification eines FEntryObservers ausgelöst wird. Es enthält alle nötigen
 * Informationen.
 */
public class FEntryNotification {
	//TODO: write test

	/**
	 * Diese Enum wird verwendet um die Art der Änderung bei einer fEntryNotification zu spezifizieren.
	 */
	public static enum ChangeType {
		NAME_CHANGED,
		PERMISSION_CHANGED,
		ADDED_CHILDREN,
		REMOVE_CHILDREN,
		DELETED
	}

	private final FEntry changedFEntry;
	private final ChangeType changeType;
	private final Object source;

	/**
	 * Erstellt eine neue FEntryNotification.
	 *
	 * @param changedFEntry Der FEntry, der sich geändert hat.
	 * @param changeType    Die Art der Änderung am FEntry. Für ADDED_CHILDREN und REMOVED_CHILDREN siehe DirectoryNotification.
	 * @param source        Das Objekt, das die Änderung ausgelöst hat - im Zeifel hier auch den geänderten FEntry setzen.
	 *                      Andere sinnvolle Werte sind zB. das Elternverzeichnis, das den FENtry gelöscht hat, oder die
	 *                      FileAPI, falls diese die Änderung auslöst.
	 */
	public FEntryNotification(final FEntry changedFEntry,
							  final ChangeType changeType,
							  final Object source) {
		this.changedFEntry = changedFEntry;
		this.changeType = changeType;
		this.source = source;
	}

	/**
	 * Liefert den FEntry, der sich geändert hat.
	 *
	 * @return Der FEntry, der sich geändert hat.
	 */
	public FEntry getChangedFEntry() {
		return changedFEntry;
	}

	/**
	 * Liefert die Art der Änderung.
	 *
	 * @return Die Art der Änderung.
	 */
	public ChangeType getChangeType() {
		return changeType;
	}

	/**
	 * Liefert das Objekt, das die Änderung ausgelöst hat. Meist der FEntry selbst, bei einer DELETED-Notification kann
	 * es aber auch das Elternverzeichnis sein, auf dem die deleteFEntry-Methode aufgerufen wurde. Bei Änderungen, die von
	 * der API kommen ist hier die FileAPI hinterlegt.
	 *
	 * @return Das Objekt, das die Änderung ausgelöst hat.
	 */
	public Object getSource() {
		return source;
	}

	//TODO: refactor
	@Override
	public boolean equals(final Object otherObj) {
		if (this == otherObj) return true;
		if (otherObj == null || getClass() != otherObj.getClass()) return false;

		FEntryNotification that = (FEntryNotification) otherObj;

		if (changeType != that.changeType) return false;
		if (changedFEntry != null ? !changedFEntry.equals(that.changedFEntry) : that.changedFEntry != null)
			return false;
		if (source != null ? !source.equals(that.source) : that.source != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(changedFEntry, changeType, source);
	}
}
