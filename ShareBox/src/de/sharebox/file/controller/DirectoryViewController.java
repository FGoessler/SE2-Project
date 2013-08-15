package de.sharebox.file.controller;

import com.google.common.base.Optional;
import de.sharebox.file.model.Directory;
import de.sharebox.file.model.FEntry;
import de.sharebox.file.model.FEntryObserver;
import de.sharebox.file.model.File;
import de.sharebox.file.services.DirectoryViewClipboardService;
import de.sharebox.helpers.OptionPaneHelper;

import javax.swing.*;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * Diese Klasse verwaltet den zentralen JTree in dem das Verzeichnis des Nutzers angezeigt wird.
 */
public class DirectoryViewController {
	protected transient OptionPaneHelper optionPane = new OptionPaneHelper();
	protected transient DirectoryViewClipboardService clipboard = new DirectoryViewClipboardService();
	protected transient ContextMenuController contextMenuController = new ContextMenuController(this, clipboard);

	protected transient JTree treeView;

	protected transient Directory rootDirectory;

	/**
	 * Dieser MouseAdapter dient dazu das Kontextmenü anzuzeigen bzw. wieder auszublenden.
	 */
	protected transient MouseAdapter contextMenuMA = new MouseAdapter() {
		@Override
		public void mouseReleased(MouseEvent event) {
			if (event.getButton() == MouseEvent.BUTTON3 && !contextMenuController.isMenuVisible()) {
				TreePath currentContextMenuTreePath = treeView.getPathForLocation(event.getX(), event.getY());
				if (currentContextMenuTreePath != null) {
					contextMenuController.showMenu(currentContextMenuTreePath, event.getX(), event.getY());
				}
			} else {
				contextMenuController.hideMenu();
			}
		}
	};

	/**
	 * Erstellt einen neuen DirectoryViewController, der seinen Inhalt in dem gegebnen TreeView
	 * darstellt.
	 *
	 * @param tree Der TreeView, in dem der Inhalt dargestellt werden soll.
	 */
	public DirectoryViewController(JTree tree) {
		rootDirectory = createMockDirectoryTree();

		this.treeView = tree;
		this.treeView.setModel(treeModel);

		this.treeView.addMouseListener(contextMenuMA);
	}


	/**
	 * Use this method to set up some mock data for the tree view.
	 *
	 * @deprecated Is deprecated cause it shouldn't be used in production code!
	 */
	@Deprecated
	private Directory createMockDirectoryTree() {
		Directory root = new Directory();
		root.setName("The main dir");

		Directory subDir1 = root.createNewDirectory("A Subdirectory");
		subDir1.createNewFile("Subdirectory File");
		root.createNewDirectory("Another Subdirectory");

		root.createNewFile("A file");
		root.createNewFile("Oho!");

		return root;
	}

	/**
	 * Erstellt eine neue Datei. Wo die Datei eingefügt wird hängt davon ab welche Datei/Verzeichnis der Nutzer gerade
	 * angewählt hat. Hat er keine Datei oder Verzeichnis selektiert wird die Datei als Kind des Root-Verzeichnisses
	 * erstellt.
	 *
	 * @return Die neu erstellte Datei.
	 */
	public File createNewFileBasedOnUserSelection() {
		File createdFile = null;
		String newFilename = optionPane.showInputDialog("Geben Sie einen Namen für die neue Datei ein:", "");

		if (!isNullOrEmpty(newFilename)) {
			Directory parentDirectory = getParentDirectoryForFEntryCreation();
			createdFile = parentDirectory.createNewFile(newFilename);
		}
		return createdFile;
	}

	/**
	 * Erstellt ein neues Verzeichnis. Wo das Verzeichnis eingefügt wird hängt davon ab welche Datei/Verzeichnis der
	 * Nutzer gerade angewählt hat. Hat er keine Datei oder Verzeichnis selektiert wird das Verzeichnis als Kind des
	 * Root-Verzeichnisses erstellt.
	 *
	 * @return Das neu erstellte Verzeichnis.
	 */
	public Directory createNewDirectoryBasedOnUserSelection() {
		Directory createdDir = null;
		String newDirectoryName = optionPane.showInputDialog("Geben Sie einen Namen für das neue Verzeichnis ein:", "");

		if (!isNullOrEmpty(newDirectoryName)) {
			Directory parentDirectory = getParentDirectoryForFEntryCreation();
			createdDir = parentDirectory.createNewDirectory(newDirectoryName);
		}
		return createdDir;
	}

	private Directory getParentDirectoryForFEntryCreation() {
		Directory parentDirectory = null;

		if (!contextMenuController.getSelectedFEntry().isPresent() && treeView.isSelectionEmpty()) {
			parentDirectory = (Directory) ((TreeNode) treeModel.getRoot()).getFEntry();
		} else if (contextMenuController.getSelectedFEntry().isPresent()) {
			if (contextMenuController.getSelectedFEntry().get() instanceof Directory) {
				parentDirectory = (Directory) contextMenuController.getSelectedFEntry().get();
			} else {
				parentDirectory = contextMenuController.getParentOfSelectedFEntry().get();
			}
		} else if (!treeView.isSelectionEmpty()) {
			if (((TreeNode) treeView.getSelectionPath().getLastPathComponent()).getFEntry() instanceof Directory) {
				parentDirectory = (Directory) ((TreeNode) treeView.getSelectionPath().getLastPathComponent()).getFEntry();
			} else {
				parentDirectory = (Directory) ((TreeNode) treeView.getSelectionPath().getParentPath().getLastPathComponent()).getFEntry();
			}
		}

		return parentDirectory;
	}

	/**
	 * Liefert die aktuell im JTree ausgewählten FEntries.
	 *
	 * @return Die im JTree ausgewählten FEntries.
	 */
	public List<FEntry> getSelectedFEntries() {
		ArrayList<FEntry> selectedFEntries = new ArrayList<FEntry>();

		if (treeView.getSelectionCount() > 0) {
			for (TreePath path : treeView.getSelectionPaths()) {
				selectedFEntries.add(((TreeNode) path.getLastPathComponent()).getFEntry());
			}
		}

		return new ArrayList<FEntry>(selectedFEntries);
	}

	/**
	 * Liefert die Elternverzeichnisse der aktuell im JTree ausgewählten FEntries in der selben Reihenfolge wie von getSelectedFEntries().
	 *
	 * @return Die Elternverzeichnisse der im JTree ausgewählten FEntries.
	 */
	public List<Optional<Directory>> getParentsOfSelectedFEntries() {
		ArrayList<Optional<Directory>> selectedFEntriesParents = new ArrayList<Optional<Directory>>();

		if (treeView.getSelectionCount() > 0) {
			for (TreePath path : treeView.getSelectionPaths()) {
				if (path.getParentPath() == null) {
					selectedFEntriesParents.add(Optional.<Directory>absent());
				} else {
					selectedFEntriesParents.add(Optional.of((Directory) ((TreeNode) path.getParentPath().getLastPathComponent()).getFEntry()));
				}
			}
		}

		return new ArrayList<Optional<Directory>>(selectedFEntriesParents);
	}

	/**
	 * Fügt den gegebenen TreeSelectionListener dem JTree hinzu.
	 *
	 * @param selectionListener Ein TreeSelectionListener um auf Änderungen der Selktierung im JTree zu reagieren.
	 */
	public void addTreeSelectionListener(TreeSelectionListener selectionListener) {
		treeView.addTreeSelectionListener(selectionListener);
	}

	/**
	 * Gibt den aktuell genutzten ClipboardService zurück mit dem Copy-Paste-Operationen von FEntries durchegführt
	 * werden können, die mit diesem DirectoryViewController dargestellt werden.
	 *
	 * @return Der aktuell genutzte ClipboardService.
	 */
	public DirectoryViewClipboardService getClipboard() {
		return clipboard;
	}

	protected transient List<TreeModelListener> treeModelListener = new ArrayList<TreeModelListener>();
	/**
	 * Dieses TreeModel ist für die darstellung der Files und Directories als JTree verantwortlich.
	 */
	protected transient TreeModel treeModel = new TreeModel() {

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

	};


	/**
	 * Dieser FEntryObserver reagiert auf Veränderungen der dargestellten FEntries und updated das UI entsprechend.
	 */
	protected transient FEntryObserver observer = new FEntryObserver() {
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

			treeView.updateUI();
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
