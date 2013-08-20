package de.sharebox.file.services;

import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.sharebox.file.controller.ContextMenuController;
import de.sharebox.file.controller.FEntryTreeNode;
import de.sharebox.file.model.Directory;
import de.sharebox.file.model.FEntry;
import de.sharebox.file.model.File;
import de.sharebox.helpers.OptionPaneHelper;

import javax.swing.*;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * Diese Klasse liefert Informationen über die aktuelle Auswahl des Benutzers im JTree des DirectoryViewControllers und
 * stellt Methoden für einige Aktionen bereit, die auf dieser Auswahl basieren.<br/>
 * Diese Klasse ist als Singleton gedacht, sodass wann immer ein DirectoryViewSelectionService per Guice injected wird
 * alle Objekte Zugriff auf die selbe Instanz besitzen. Diese Singleton-Instanz muss allerdings inital im
 * DirectoryViewController mit dem JTree verknüpft werden.
 */
@Singleton
public class DirectoryViewSelectionService {
	private final OptionPaneHelper optionPane;

	private JTree treeView;

	/**
	 * Erstellt einen neuen DirectoryViewSelectionService.
	 * Instanzen dieser Klasse sollten nur per Dependency Injection durch Guice erstellt werden.
	 *
	 * @param optionPane Ein OptionPaneHelper zum Erstellen von Dialogfenstern.
	 */
	@Inject
	DirectoryViewSelectionService(OptionPaneHelper optionPane) {
		this.optionPane = optionPane;
	}

	/**
	 * Setzt den JTree dieses Instanz.
	 *
	 * @param treeView Ein JTree dessen Selections betrachtet werden.
	 */
	public void setTreeView(JTree treeView) {
		this.treeView = treeView;
	}

	/**
	 * Liefert den aktuell gesetzten JTree.
	 *
	 * @return Der JTree dessen Selections betrachtet werden.
	 */
	public JTree getTreeView() {
		return treeView;
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
				selectedFEntries.add(((FEntryTreeNode) path.getLastPathComponent()).getFEntry());
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
					selectedFEntriesParents.add(Optional.of((Directory) ((FEntryTreeNode) path.getParentPath().getLastPathComponent()).getFEntry()));
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
	 * Erstellt eine neue Datei. Wo die Datei eingefügt wird hängt davon ab welche Datei/Verzeichnis der Nutzer gerade
	 * angewählt hat. Hat er keine Datei oder Verzeichnis selektiert wird die Datei als Kind des Root-Verzeichnisses
	 * erstellt.
	 *
	 * @param contextMenuController Ein ContextMenuController dessen aktuelle Klickposition mit berücksichtigt werden
	 *                              soll. Hier kann auch ein Optional.absent() übergeben werden. Dann wird kein
	 *                              ContextMenuController betrachtet.
	 * @return Die neu erstellte Datei.
	 */
	public File createNewFileBasedOnUserSelection(Optional<ContextMenuController> contextMenuController) {
		File createdFile = null;
		String newFilename = optionPane.showInputDialog("Geben Sie einen Namen für die neue Datei ein:", "");

		if (!isNullOrEmpty(newFilename)) {
			Directory parentDirectory = getParentDirectoryForFEntryCreation(contextMenuController);
			createdFile = parentDirectory.createNewFile(newFilename);		//TODO: evaluate success
		}
		return createdFile;
	}

	/**
	 * Erstellt ein neues Verzeichnis. Wo das Verzeichnis eingefügt wird hängt davon ab welche Datei/Verzeichnis der
	 * Nutzer gerade angewählt hat. Hat er keine Datei oder Verzeichnis selektiert wird das Verzeichnis als Kind des
	 * Root-Verzeichnisses erstellt.
	 *
	 * @param contextMenuController Ein ContextMenuController dessen aktuelle Klickposition mit berücksichtigt werden
	 *                              soll. Hier kann auch ein Optional.absent() übergeben werden. Dann wird kein
	 *                              ContextMenuController betrachtet.
	 * @return Das neu erstellte Verzeichnis.
	 */
	public Directory createNewDirectoryBasedOnUserSelection(Optional<ContextMenuController> contextMenuController) {
		Directory createdDir = null;
		String newDirectoryName = optionPane.showInputDialog("Geben Sie einen Namen für das neue Verzeichnis ein:", "");

		if (!isNullOrEmpty(newDirectoryName)) {
			Directory parentDirectory = getParentDirectoryForFEntryCreation(contextMenuController);
			createdDir = parentDirectory.createNewDirectory(newDirectoryName);		//TODO: evaluate success
		}
		return createdDir;
	}

	private Directory getParentDirectoryForFEntryCreation(Optional<ContextMenuController> contextMenuController) {
		Directory parentDirectory = null;

		if ((!contextMenuController.isPresent() || !contextMenuController.get().getSelectedFEntry().isPresent())
				&& treeView.isSelectionEmpty()) {
			parentDirectory = (Directory) ((FEntryTreeNode) treeView.getModel().getRoot()).getFEntry();
		} else if (contextMenuController.isPresent() && contextMenuController.get().getSelectedFEntry().isPresent()) {
			if (contextMenuController.get().getSelectedFEntry().get() instanceof Directory) {
				parentDirectory = (Directory) contextMenuController.get().getSelectedFEntry().get();
			} else {
				parentDirectory = contextMenuController.get().getParentOfSelectedFEntry().get();
			}
		} else if (!treeView.isSelectionEmpty()) {
			if (((FEntryTreeNode) treeView.getSelectionPath().getLastPathComponent()).getFEntry() instanceof Directory) {
				parentDirectory = (Directory) ((FEntryTreeNode) treeView.getSelectionPath().getLastPathComponent()).getFEntry();
			} else {
				parentDirectory = (Directory) ((FEntryTreeNode) treeView.getSelectionPath().getParentPath().getLastPathComponent()).getFEntry();
			}
		}

		return parentDirectory;
	}
}
