package de.sharebox.file.model;

import com.google.common.collect.ImmutableList;

/**
 * TODO interface-Beschreibung (Interface für den DirectoryObserver?)
 */
public interface DirectoryObserver extends FEntryObserver {
	void addedChildrenNotification(final Directory parent, final ImmutableList<FEntry> newChildren);

	void removedChildrenNotification(final Directory parent, final ImmutableList<FEntry> removedChildren);
}
