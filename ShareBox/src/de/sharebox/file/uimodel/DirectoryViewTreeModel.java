package de.sharebox.file.uimodel;

import com.google.common.base.Optional;
import de.sharebox.file.model.Directory;
import de.sharebox.file.model.FEntry;
import de.sharebox.file.model.FEntryObserver;
import de.sharebox.file.model.File;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Dieses TreeModel ist für die Darstellung der Files und Directories im JTree verantwortlich.
 */
public class DirectoryViewTreeModel implements TreeModel {
	private final transient List<TreeModelListener> treeModelListener = new ArrayList<TreeModelListener>();

	private Directory rootDirectory;

	/**
	 * Setzt das RootDirectory auf Basis dessen das TreeModel erstellt wird.
	 *
	 * @param rootDirectory Das RootDirectory das (und dessen Unterverzeichnisse und Dateien) dargestellt werden sollen.
	 */
	public void setRootDirectory(Directory rootDirectory) {
		this.rootDirectory = rootDirectory;
	}

	@Override
	public TreeNode getRoot() {
		//lazily add observer - get sure to not add the observer twice!
		rootDirectory.removeObserver(observer);
		rootDirectory.addObserver(observer);

		return new TreeNode(rootDirectory);
	}

	@Override
	public TreeNode getChild(Object parent, int index) {
		TreeNode child = null;
		FEntry parentFEntry = ((TreeNode) parent).getFEntry();

		if (parentFEntry instanceof Directory) {
			FEntry childFEntry = ((Directory) parentFEntry).getFEntries().get(index);
			child = new TreeNode(childFEntry);

			//lazily add observer - get sure not to add the observer twice!
			childFEntry.removeObserver(observer);
			childFEntry.addObserver(observer);
		}

		return child;
	}

	@Override
	public int getChildCount(Object parent) {
		int childCount = 0;
		FEntry parentFEntry = ((TreeNode) parent).getFEntry();

		if (parentFEntry instanceof Directory) {
			childCount = ((Directory) parentFEntry).getFEntries().size();
		}

		return childCount;
	}

	@Override
	public boolean isLeaf(Object node) {
		return ((TreeNode) node).getFEntry() instanceof File;    //only File-Objects are leafs
	}

	@Override
	public int getIndexOfChild(Object parent, Object child) {
		int index;

		if (parent == null || child == null) {
			index = -1;
		} else {
			Directory parentDirectory = (Directory) ((TreeNode) parent).getFEntry();
			FEntry childFEntry = ((TreeNode) child).getFEntry();

			index = parentDirectory.getFEntries().indexOf(childFEntry);
		}

		return index;
	}

	@Override
	public void valueForPathChanged(TreePath path, Object newValue) {
		FEntry changedEntry = ((TreeNode) path.getLastPathComponent()).getFEntry();
		changedEntry.setName((String) newValue);
	}

	@Override
	public void addTreeModelListener(TreeModelListener listener) {
		treeModelListener.add(listener);
	}

	@Override
	public void removeTreeModelListener(TreeModelListener listener) {
		treeModelListener.remove(listener);
	}

	/**
	 * Dieser FEntryObserver reagiert auf Veränderungen der dargestellten FEntries und updated das UI entsprechend.
	 */
	protected final transient FEntryObserver observer = new FEntryObserver() {
		@Override
		public void fEntryChangedNotification(FEntry fEntry, FEntry.ChangeType reason) {
			if (reason.equals(FEntry.ChangeType.NAME_CHANGED)) {
				TreePath treePath = treePathForFEntry(fEntry).get();
				TreeModelEvent event = new TreeModelEvent(fEntry, treePath);
				for (TreeModelListener listener : treeModelListener) {
					listener.treeNodesChanged(event);
				}
			} else if (reason.equals(FEntry.ChangeType.ADDED_CHILDREN)) {
				TreePath treePath = treePathForFEntry(fEntry).get();
				TreeModelEvent event = new TreeModelEvent(fEntry, treePath);
				for (TreeModelListener listener : treeModelListener) {
					listener.treeNodesInserted(event);
				}
			} else if (reason.equals(FEntry.ChangeType.REMOVED_CHILDREN)) {
				TreePath treePath = treePathForFEntry(fEntry).get();
				TreeModelEvent event = new TreeModelEvent(fEntry, treePath);
				for (TreeModelListener listener : treeModelListener) {
					listener.treeNodesRemoved(event);
				}
			}
		}

		@Override
		public void fEntryDeletedNotification(FEntry fEntry) {
			//wird ignoriert, da bereits die Informationen aus einer fEntryChangedNotification ausreichen um den Baum zu aktualisieren.
		}

		/**
		 * Generiert einen TreePath für den gegebenen FEntry - dazu wird eine Breitensuche durchgeführt! Diese Methode bei
		 * großen Bäumen daher möglichst wenig nutzen!
		 *
		 *
		 * @param fEntry Der FEntry dessen Position im Baum gefunden werden soll.
		 * @return Ein TreePath der die Position des FEntries bestimmt. Die Komponenten des TreePaths sind jeweils TreeNode Objekte.
		 */
		private Optional<TreePath> treePathForFEntry(FEntry fEntry) {
			Optional<TreePath> foundPath = Optional.absent();

			//Breitensuche nach dem FEntry im Baum
			Queue<TreePath> pathsToCheck = new LinkedBlockingQueue<TreePath>();
			pathsToCheck.add(new TreePath(new TreeNode(rootDirectory)));
			while (pathsToCheck.size() > 0) {
				TreePath currentPath = pathsToCheck.poll();
				TreeNode currentNode = (TreeNode) currentPath.getLastPathComponent();

				if (currentNode.getFEntry().equals(fEntry)) {
					foundPath = Optional.of(currentPath);
					break;
				} else if (currentNode.getFEntry() instanceof Directory) {
					for (FEntry subFEntry : ((Directory) currentNode.getFEntry()).getFEntries()) {
						pathsToCheck.add(currentPath.pathByAddingChild(new TreeNode(subFEntry)));
					}
				}
			}

			return foundPath;
		}
	};

}
