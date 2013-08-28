package de.sharebox.file.notification;

import com.google.common.base.Objects;
import de.sharebox.file.model.FEntry;

/**
 * Dieses Objekt wird generiert, wenn eine Notification eines FEntryObservers ausgelöst wird. Es enthält alle nötigen
 * Informationen.
 */
public class FEntryNotification {

	/**
	 * Diese Enum wird verwendet, um die Art der Änderung bei einer FEntryNotification zu spezifizieren.
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

	@Override
	public boolean equals(final Object otherObj) {
		Boolean equals = true;

		if (otherObj.getClass().equals(getClass())) {
			final FEntryNotification otherNotification = (FEntryNotification) otherObj;

			if (!Objects.equal(changedFEntry, otherNotification.getChangedFEntry()) ||
					!Objects.equal(changeType, otherNotification.getChangeType()) ||
					!Objects.equal(source, otherNotification.getSource())) {
				equals = false;
			}
		} else {
			equals = false;
		}

		return equals;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(changedFEntry, changeType, source);
	}
}
