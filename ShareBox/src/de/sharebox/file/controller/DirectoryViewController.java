package de.sharebox.file.controller;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.assistedinject.Assisted;
import de.sharebox.file.model.Directory;
import de.sharebox.file.services.DirectoryViewSelectionService;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Diese Klasse verwaltet den zentralen JTree in dem das Verzeichnis des Nutzers angezeigt wird.
 */
@Singleton
public class DirectoryViewController {
	private final ContextMenuController contextMenuController;

	/**
	 * Der JTree zur Darstellung des Sharebox Verzeichnisses des Nutzers.
	 */
	protected final JTree treeView;


	/**
	 * Erstellt einen neuen DirectoryViewController, der seinen Inhalt in dem gegebenen TreeView darstellt.<br/>
	 * Sollte nur mittel Dependency Injection durch Guice erstellt werden. Siehe auch DirectoryViewControllerFactory.
	 *
	 * @param tree                          Der TreeView, in dem der Inhalt dargestellt werden soll - muss über die Factory gesetzt werden und
	 *                                      kann nicht automatisch injectet werden.
	 * @param directoryViewSelectionService Ein DirectoryViewSelectionService der von Guice als Singleton erstelt wird
	 *                                      und hier lediglich mit dem JTree verbunden wird.
	 * @param contextMenuController         Ein ContextMenuController der für das per Rechtsklick aufrufbare Kontextmenü
	 *                                      verantwortlich ist.
	 */
	@Inject
	DirectoryViewController(@Assisted JTree tree,
							DirectoryViewSelectionService directoryViewSelectionService,
							ContextMenuController contextMenuController) {

		this.contextMenuController = contextMenuController;

		this.treeView = tree;
		DefaultTreeModel treeModel = new DefaultTreeModel(new DefaultMutableTreeNode("Root"), true);
		this.treeView.setModel(treeModel);
		treeModel.setRoot(new FEntryTreeNode(treeModel, createMockDirectoryTree()));

		this.treeView.addMouseListener(contextMenuMA);

		directoryViewSelectionService.setTreeView(treeView);
	}

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
}
