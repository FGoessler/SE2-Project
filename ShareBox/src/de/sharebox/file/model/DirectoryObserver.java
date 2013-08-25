package de.sharebox.file.model;

import com.google.common.collect.ImmutableList;

/**
 * TODO interface-Beschreibung (Interface für den DirectoryObserver?)
 */
public interface DirectoryObserver extends FEntryObserver {
	void addedChildrenNotification(Directory parent, ImmutableList<FEntry> newChildren);

	void removedChildrenNotification(Directory parent, ImmutableList<FEntry> removedChildren);
}
