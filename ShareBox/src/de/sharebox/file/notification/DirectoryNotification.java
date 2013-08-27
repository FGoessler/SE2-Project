package de.sharebox.file.notification;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import de.sharebox.file.model.Directory;
import de.sharebox.file.model.FEntry;

/**
 * Dieses Objekt wird generiert wenn eine Notification eines DirectoryObservers ausgelöst wird. Es enthält mehr
 * Informationen als eine FEntryNotification.
 */
public class DirectoryNotification extends FEntryNotification {
	//TODO: write test

	private final ImmutableList<FEntry> affectedChildren;

	/**
	 * Erstellt eine neue DirectoryNotification.
	 *
	 * @param changedDirectory Das Directory, das sich geändert hat.
	 * @param changeType       Die Art der Änderung - ADDED_CHILDREN oder REMOVED_CHILDREN.
	 * @param source           Das Objekt, das die Änderung ausgelöst hat.
	 * @param affectedChildren Die von den Änderungen betroffenen FEntries des Verzeichnisses - also eine Liste der
	 *                         gelöschten bzw. hinzugefügten FEntries.
	 */
	public DirectoryNotification(Directory changedDirectory,
								 ChangeType changeType,
								 Object source,
								 ImmutableList<FEntry> affectedChildren) {
		super(changedDirectory, changeType, source);
		this.affectedChildren = affectedChildren;
	}

	/**
	 * Liefert eine ImmutableList der von den Änderungen betroffenen FEntries des Verzeichnisses - also eine Liste der
	 * gelöschten bzw. hinzugefügten FEntries.
	 *
	 * @return Die von den Änderungen betroffenen FEntries des Verzeichnisses.
	 */
	public ImmutableList<FEntry> getAffectedChildren() {
		return affectedChildren;
	}

	//TODO: refactor
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;

		DirectoryNotification that = (DirectoryNotification) o;

		if (affectedChildren != null ? !affectedChildren.equals(that.affectedChildren) : that.affectedChildren != null)
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(super.hashCode(), affectedChildren);
	}
}