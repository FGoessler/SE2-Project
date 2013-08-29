package de.sharebox.file.controller;

import com.google.common.collect.ImmutableList;
import de.sharebox.file.model.Directory;
import de.sharebox.file.model.FEntry;
import de.sharebox.file.notification.DirectoryNotification;
import de.sharebox.file.notification.DirectoryObserver;
import de.sharebox.file.notification.FEntryNotification;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * Diese Klasse dient als Wrapper der FEntries, die im TreeView eines DirectoryViewControllers dargestellt werden. Um
 * nicht die toString-Methode der Directory- und File-Klasse für darstellungsspezifische Zwecke zu verwenden.
 */
public class FEntryTreeNode extends DefaultMutableTreeNode {
	private final DefaultTreeModel treeModel;

	/**
	 * Erstellt einen neuen FEntryTreeNode mit dem gegebenen FEntry.
	 *
	 * @param fEntry Der FEntry der durch diesen FEntryTreeNode dargestellt werden soll.
	 */
	public FEntryTreeNode(final DefaultTreeModel treeModel, final FEntry fEntry) {
		super(fEntry);

		this.treeModel = treeModel;

		if (fEntry instanceof Directory) {
			setAllowsChildren(true);
			for (final FEntry childFEntry : ((Directory) fEntry).getFEntries()) {
				add(new FEntryTreeNode(this.treeModel, childFEntry));
			}
		} else {
			setAllowsChildren(false);
		}

		fEntry.addObserver(observer);
	}

	@Override
	public boolean isLeaf() {
		return !(getFEntry() instanceof Directory) || ((Directory) getFEntry()).getFEntries().isEmpty();
	}

	/**
	 * Liefert den FEntry dieses TreeNodes.
	 *
	 * @return Der FEntry der durch diesen FEntryTreeNode dargestellt wird.
	 */
	public FEntry getFEntry() {
		return (FEntry) getUserObject();
	}

	/**
	 * Die toString-Methode wurde hier überschrieben, da diese vom JTree benutzt wird um die Beschriftung der Knoten
	 * zu bestimmen.
	 *
	 * @return Den Namen das FEntries.
	 */
	@Override
	public String toString() {
		return getFEntry().getName();
	}

	/**
	 * Dieser DirectoryObserver reagiert auf Veränderungen der dargestellten FEntries und aktualisiert das UI-Model entsprechend.
	 */
	private final DirectoryObserver observer = new DirectoryObserver() {
		@Override
		public void fEntryNotification(final FEntryNotification notification) {
			if (notification.getChangeType().equals(FEntryNotification.ChangeType.NAME_CHANGED)) {
				setUserObject(notification.getChangedFEntry());        //set to trigger UI update
			}
		}

		@Override
		public void directoryNotification(final DirectoryNotification notification) {
			if (notification.getChangeType().equals(FEntryNotification.ChangeType.ADDED_CHILDREN)) {
				for (final FEntry child : notification.getAffectedChildren()) {
					treeModel.insertNodeInto(new FEntryTreeNode(treeModel, child), FEntryTreeNode.this, 0);
				}
			} else if (notification.getChangeType().equals(FEntryNotification.ChangeType.REMOVE_CHILDREN)) {
				final ImmutableList<FEntry> removedChildren = notification.getAffectedChildren();
				final List<FEntry> remainingChildrenToRemove = new ArrayList<FEntry>(removedChildren);
				while (!remainingChildrenToRemove.isEmpty()) {
					FEntryTreeNode nodeToRemove = null;
					final Enumeration children = children();
					while (children.hasMoreElements()) {
						final FEntryTreeNode child = (FEntryTreeNode) children.nextElement();
						final FEntry childFEntry = (FEntry) child.getUserObject();
						if (childFEntry.equals(removedChildren.get(0))) {
							nodeToRemove = child;
							break;
						}
					}
					treeModel.removeNodeFromParent(nodeToRemove);
					remainingChildrenToRemove.remove(0);
				}
			}
		}
	};
}
