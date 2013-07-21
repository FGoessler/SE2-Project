package de.sharebox.file.controller;

import de.sharebox.file.model.FEntry;

/**
 * Diese Klasse dient als Wrapper der FEntries, die im TreeView eines DirectoryViewControllers dargestellt werden um
 * nicht die toString-Methode der Directory- und File-Klasse für Darstellungsspezifische Zwecke zu missbrauchen.
 */
class TreeNode {
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
		return fEntry.getName();
	}
}
