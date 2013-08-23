package de.sharebox.file.controller;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.assistedinject.Assisted;
import de.sharebox.api.UserAPI;
import de.sharebox.file.FileManager;
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
	private final UserAPI userAPI;
	private final FileManager fileManager;

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
	 * @param userAPI                       TODO: sollte wieder entfernt werden - ist hier nur um die Testdaten zu erstellen
	 * @param fileManager                   TODO: sollte wieder entfernt werden - ist hier nur um die Testdaten zu erstellen
	 */
	@Inject
	DirectoryViewController(@Assisted JTree tree,
							DirectoryViewSelectionService directoryViewSelectionService,
							ContextMenuController contextMenuController,
							UserAPI userAPI,
							FileManager fileManager) {

		this.contextMenuController = contextMenuController;
		this.userAPI = userAPI;
		this.fileManager = fileManager;

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
		Directory root = new Directory(userAPI);
		root.setName("The main dir");
		root.setPermission(userAPI.getCurrentUser(), true, true, true);
		// Registriert root directory im FileManager - weitere FEntries werden automatisch durch Reaktion auf Notifications registriert.
		// Dieser Schritt sollte später natürlich nicht hier ausgeführt werden sondern bevor das Root Directory eines Users an diesen
		// Controller übergeben wird. Damit fallen dann auch die Abhänigkeiten zur UserAPI (und dem FileManager) weg. Stattdessen
		// fragt man direkt die FileAPI oder den FileManager nach dem Rootverzeichnis für den eingeloggten Benutzer.
		fileManager.registerFEntry(root);

		Directory subDir1 = root.createNewDirectory("A Subdirectory");
		subDir1.createNewFile("Subdirectory File");
		root.createNewDirectory("Another Subdirectory");

		root.createNewFile("A file");
		root.createNewFile("Oho!");

		return root;
	}
}
