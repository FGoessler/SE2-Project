package de.sharebox.file.controller;

import de.sharebox.file.model.Directory;
import de.sharebox.file.model.FEntry;
import de.sharebox.file.model.FEntryObserver;
import de.sharebox.file.model.File;

import javax.swing.*;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;
import java.util.concurrent.LinkedBlockingQueue;

public class DirectoryViewController implements TreeModel, FEntryObserver {
	private transient List<TreeModelListener> treeModelListener = new ArrayList<TreeModelListener>();

	protected transient JTree treeView;

	protected transient Directory rootDirectory;

	public DirectoryViewController(JTree treeView) {
		rootDirectory = createMockDirectoryTree();

		this.treeView = treeView;
		this.treeView.setModel(this);
	}

	//use this method to set up some mock data for the tree view
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

	@Override
	public TreeNode getRoot() {
		//lazily add observer - get sure to not add the observer twice!
		rootDirectory.removeObserver(this);
		rootDirectory.addObserver(this);

		return new TreeNode(rootDirectory);
	}

	@Override
	public TreeNode getChild(Object parent, int index) {
		TreeNode child = null;
		FEntry parentFEntry = ((TreeNode)parent).getFEntry();

		if(parentFEntry instanceof Directory) {
			FEntry childFEntry = ((Directory) parentFEntry).getFEntries().get(index);
			child = new TreeNode(childFEntry);

			//lazily add observer - get sure to not add the observer twice!
			childFEntry.removeObserver(this);
			childFEntry.addObserver(this);
		}

		return child;
	}

	@Override
	public int getChildCount(Object parent) {
		int childCount = 0;
		FEntry parentFEntry = ((TreeNode)parent).getFEntry();

		if(parentFEntry instanceof Directory) {
			childCount = ((Directory) parentFEntry).getFEntries().size();
		}

		return childCount;
	}

	@Override
	public boolean isLeaf(Object node) {
		return ((TreeNode)node).getFEntry() instanceof File;	//only File-Objects are leafs
	}

	@Override
	public int getIndexOfChild(Object parent, Object child) {
		int index;

		if(parent == null || child == null) {
			index = -1;
		} else {
			Directory parentDirectory = (Directory)((TreeNode)parent).getFEntry();
			FEntry childFEntry = ((TreeNode)child).getFEntry();

			index = parentDirectory.getFEntries().indexOf(childFEntry);
		}

		return index;
	}

	@Override
	public void valueForPathChanged(TreePath path, Object newValue) {
		FEntry changedEntry = ((TreeNode)path.getLastPathComponent()).getFEntry();
		if(changedEntry instanceof Directory) {
			((Directory) changedEntry).setName((String)newValue);
		} else if(changedEntry instanceof  File) {
			((File) changedEntry).setFileName((String)newValue);
		}
	}

	@Override
	public void addTreeModelListener(TreeModelListener listener) {
		treeModelListener.add(listener);
	}

	@Override
	public void removeTreeModelListener(TreeModelListener listener) {
		treeModelListener.remove(listener);
	}

	@Override
	public void fEntryChangedNotification(FEntry fEntry, FEntry.ChangeType reason) {
		if(reason.equals(FEntry.ChangeType.NAME_CHANGED)) {
			TreePath treePath = treePathForFEntry(fEntry);
			TreeModelEvent event = new TreeModelEvent(fEntry, treePath);
			for(TreeModelListener listener : treeModelListener) {
				listener.treeNodesChanged(event);
			}
		} else if(reason.equals(FEntry.ChangeType.ADDED_CHILDREN)) {
			TreePath treePath = treePathForFEntry(fEntry);
			TreeModelEvent event = new TreeModelEvent(fEntry, treePath);
			for(TreeModelListener listener : treeModelListener) {
				listener.treeNodesInserted(event);
			}
		} else if(reason.equals(FEntry.ChangeType.REMOVED_CHILDREN)) {
			TreePath treePath = treePathForFEntry(fEntry);
			TreeModelEvent event = new TreeModelEvent(fEntry, treePath);
			for(TreeModelListener listener : treeModelListener) {
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
	 * @param fEntry Der FEntry dessen Position im Baum gefunden werden soll.
	 * @return Ein TreePath der die Position des FEntries bestimmt. Die Komponenten des TreePaths sind jeweils TreeNode Objekte.
	 */
	private TreePath treePathForFEntry(FEntry fEntry) {
		TreeNode nodeOfRequestedFEntry = null;

		//Breitensuche nach dem FEntry im Baum
		Queue<TreeNode> nodesToCheck = new LinkedBlockingQueue<TreeNode>();
		nodesToCheck.add(new TreeNode(rootDirectory, null));
		while(nodesToCheck.size() > 0) {
			TreeNode currentNode = nodesToCheck.poll();

			if(currentNode.getFEntry().equals(fEntry)) {
				nodeOfRequestedFEntry = currentNode;
				break;
			} else if(currentNode.getFEntry() instanceof Directory) {
				for(FEntry subFEntry : ((Directory) currentNode.getFEntry()).getFEntries()) {
					nodesToCheck.add(new TreeNode(subFEntry, currentNode));
				}
			}
		}

		//create TreePath
		Stack<TreeNode> pathToFoundFEntry = new Stack<TreeNode>();
		pathToFoundFEntry.push(nodeOfRequestedFEntry);
		while (pathToFoundFEntry.peek().getParent() != null) {
			pathToFoundFEntry.push(pathToFoundFEntry.peek().getParent());
		}
		return new TreePath(pathToFoundFEntry.toArray());
	}

	/**
	 * Diese Klasse dient als Wrapper der FEntries, die im Tree dargestellt werden, um nicht die toString-Methode
	 * der Directory- und File-Klasse für Darstellungsspezifische Zwecke zu missbrauchen.
	 */
	public class TreeNode {
		private FEntry fEntry;
		private TreeNode parent;

		public TreeNode(FEntry fEntry) {
			this.fEntry = fEntry;
		}
		public TreeNode(FEntry fEntry, TreeNode parent) {
			this.parent = parent;
			this.fEntry = fEntry;
		}

		public TreeNode getParent() {
			return parent;
		}

		public FEntry getFEntry() {
			return fEntry;
		}

		@Override
		public String toString() {
			String text = "";

			if(fEntry instanceof File) {
				text = ((File) fEntry).getFileName();
			} else if(fEntry instanceof Directory) {
				text = ((Directory) fEntry).getName();
			}

			return text;
		}
	}
}
