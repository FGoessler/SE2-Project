package de.sharebox.file.controller;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.assistedinject.Assisted;
import de.sharebox.api.FileAPI;
import de.sharebox.api.UserAPI;
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
	 * Sollte nur mittels Dependency Injection durch Guice erstellt werden. Siehe auch DirectoryViewControllerFactory.
	 *
	 * @param tree                          Der TreeView, in dem der Inhalt dargestellt werden soll - muss über die Factory gesetzt werden
	 *                                      und kann nicht automatisch injectet werden.
	 * @param directoryViewSelectionService Ein DirectoryViewSelectionService der von Guice als Singleton erstellt wird
	 *                                      und hier lediglich mit dem JTree verbunden wird.
	 * @param contextMenuController         Ein ContextMenuController der für das per Rechtsklick aufrufbare Kontextmenü verantwortlich ist.
	 * @param userAPI                       Die UserAPI zum Herausfinden des aktuell eingeloggten Benutzers.
	 * @param fileAPI                       Die FileAPI zum Erhalten des Root-Directories des Benutzers.
	 */
	@Inject
	DirectoryViewController(final @Assisted JTree tree,
							final DirectoryViewSelectionService directoryViewSelectionService,
							final ContextMenuController contextMenuController,
							final UserAPI userAPI,
							final FileAPI fileAPI) {

		this.contextMenuController = contextMenuController;

		final Directory rootDirectory = (Directory) fileAPI.getFEntryWithId(userAPI.getCurrentUser().getRootDirectoryIdentifier());

		this.treeView = tree;
		final DefaultTreeModel treeModel = new DefaultTreeModel(new DefaultMutableTreeNode("Root"), true);
		this.treeView.setModel(treeModel);
		treeModel.setRoot(new FEntryTreeNode(treeModel, rootDirectory));

		this.treeView.addMouseListener(contextMenuMA);

		directoryViewSelectionService.setTreeView(treeView);
	}

	/**
	 * Dieser MouseAdapter dient dazu das Kontextmenü anzuzeigen bzw. wieder auszublenden.
	 */
	protected MouseAdapter contextMenuMA = new MouseAdapter() {
		@Override
		public void mouseReleased(final MouseEvent event) {
			if (event.getButton() == MouseEvent.BUTTON3 && !contextMenuController.isMenuVisible()) {
				final TreePath currentContextMenuTreePath = treeView.getPathForLocation(event.getX(), event.getY());
				if (currentContextMenuTreePath != null) {
					contextMenuController.showMenu(currentContextMenuTreePath, event.getX(), event.getY());
				}
			} else {
				contextMenuController.hideMenu();
			}
		}
	};
}
