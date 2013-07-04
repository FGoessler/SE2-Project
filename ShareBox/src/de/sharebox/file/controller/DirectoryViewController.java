package de.sharebox.file.controller;

import de.sharebox.file.model.Directory;
import de.sharebox.file.model.FEntry;
import de.sharebox.file.model.File;

import javax.swing.*;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

public class DirectoryViewController implements TreeModel {
	protected JTree treeView;

	protected Directory rootDirectory;

	public DirectoryViewController(JTree treeView) {
		//rootDirectory = createMockDirectoryTree();

		this.treeView = treeView;
		this.treeView.setModel(this);
	}

	/*
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
	*/

	@Override
	public TreeNode getRoot() {
		return new TreeNode(rootDirectory);
	}

	@Override
	public TreeNode getChild(Object parent, int index) {
		TreeNode child = null;
		FEntry parentFEntry = ((TreeNode)parent).getFEntry();

		if(parentFEntry instanceof Directory) {
			child = new TreeNode( ((Directory)parentFEntry).getFEntries().get(index) );
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
		return ((TreeNode)node).getFEntry() instanceof File;	//only Files are leafs
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
	}

	@Override
	public void addTreeModelListener(TreeModelListener l) {
	}

	@Override
	public void removeTreeModelListener(TreeModelListener l) {
	}

	/**
	 * Diese Klasse dient als Wrapper der FEntries, die im Tree dargestellt werden, um nicht die toString-Methode
	 * der Directory- und File-Klasse für Darstellungsspezifische Zwecke zu missbrauchen.
	 */
	public class TreeNode {
		private FEntry fEntry;

		public TreeNode(FEntry fEntry) {
			this.fEntry = fEntry;
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
