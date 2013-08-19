package de.sharebox.file.uimodel;

import de.sharebox.file.model.FEntry;

/**
 * Diese Klasse dient als Wrapper der FEntries, die im TreeView eines DirectoryViewControllers dargestellt werden um
 * nicht die toString-Methode der Directory- und File-Klasse für Darstellungsspezifische Zwecke zu missbrauchen.
 */
public class TreeNode {
	private final FEntry fEntry;

	/**
	 * Erstellt einen neuen TreeNode mit dem gegebenen FEntry.
	 *
	 * @param fEntry Der FEntry der durch diesen TreeNode dargestellt werden soll.
	 */
	public TreeNode(FEntry fEntry) {
		this.fEntry = fEntry;
	}

	/**
	 * Liefert den FEntry dieses TreeNodes.
	 *
	 * @return Der FEntry der durch diesen TreeNode dargestellt wird.
	 */
	public FEntry getFEntry() {
		return fEntry;
	}

	/**
	 * Die toString-Methode wurde hier überschrieben, da diese vom JTree benutzt wird um die Beschriftung der Knoten
	 * zu bestimmen.
	 *
	 * @return Den Namen das FEntries.
	 */
	@Override
	public String toString() {
		return fEntry.getName();
	}
}
