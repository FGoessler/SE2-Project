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
 * Diese Klasse liefert Informationen über die aktuelle Auswahl des Benutzers im JTree des DirectoryViewControllers und<br/>
 * stellt Methoden für einige Aktionen bereit, die auf dieser Auswahl basieren.<br/>
 * Diese Klasse ist als Singleton gedacht, so dass wann immer ein DirectoryViewSelectionService per Guice injected wird,<br/>
 * alle Objekte Zugriff auf dieselbe Instanz besitzen. Diese Singleton-Instanz muss allerdings inital im<br/>
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
	DirectoryViewSelectionService(final OptionPaneHelper optionPane) {
		this.optionPane = optionPane;
	}

	/**
	 * Setzt den JTree dieser Instanz.
	 *
	 * @param treeView Ein JTree dessen Selektionen betrachtet werden.
	 */
	public void setTreeView(final JTree treeView) {
		this.treeView = treeView;
	}

	/**
	 * Liefert den aktuell gesetzten JTree.
	 *
	 * @return Der JTree dessen Selektionen betrachtet werden.
	 */
	public JTree getTreeView() {
		return treeView;
	}

	/**
	 * Liefert die aktuell im JTree ausgewählten FEntries.
	 *
	 * @return Die im JTree ausgewählten FEntries.
	 */
	public List<FEntry> getSelectedFEntries() {        //TODO: evtl. immutable List?
		final ArrayList<FEntry> selectedFEntries = new ArrayList<FEntry>();

		if (treeView.getSelectionCount() > 0) {
			for (final TreePath path : treeView.getSelectionPaths()) {
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
	public List<Optional<Directory>> getParentsOfSelectedFEntries() {    //TODO: evtl. immutable List?
		final ArrayList<Optional<Directory>> selectedFEntriesParents = new ArrayList<Optional<Directory>>();

		if (treeView.getSelectionCount() > 0) {
			for (final TreePath path : treeView.getSelectionPaths()) {
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
	 * @param selectionListener Ein TreeSelectionListener um auf Änderungen der Selektierung im JTree zu reagieren.
	 */
	public void addTreeSelectionListener(final TreeSelectionListener selectionListener) {
		treeView.addTreeSelectionListener(selectionListener);
	}

	/**
	 * Erstellt eine neue Datei. Wo die Datei eingefügt wird hängt davon ab welche Datei/Verzeichnis der Nutzer gerade<br/>
	 * angewählt hat. Hat er keine Datei oder Verzeichnis selektiert wird die Datei als Kind des Root-Verzeichnisses<br/>
	 * erstellt.
	 *
	 * @param contextMenuController Ein ContextMenuController dessen aktuelle Klickposition mit berücksichtigt werden<br/>
	 *                              soll. Hier kann auch ein Optional.absent() übergeben werden. Dann wird kein<br/>
	 *                              ContextMenuController betrachtet.
	 * @return Die neu erstellte Datei als Optional. Optional.absent() falls der Nutzer keinen korrekten Namen eingegeben
	 *         hat, eine Datei mit diesem Namen bereits existiert oder er nicht die erforderlichen Rechte besitzt.
	 */
	public Optional<File> createNewFileBasedOnUserSelection(final Optional<ContextMenuController> contextMenuController) {
		Optional<File> createdFile = Optional.absent();

		final String newFilename = optionPane.showInputDialog("Geben Sie einen Namen für die neue Datei ein:", "");

		if (!isNullOrEmpty(newFilename)) {
			final Directory parentDirectory = getParentDirectoryForFEntryCreation(contextMenuController);
			if (parentDirectory.getPermissionOfCurrentUser().getWriteAllowed()) {
				createdFile = parentDirectory.createNewFile(newFilename);
				if (!createdFile.isPresent()) {
					optionPane.showMessageDialog("Eine Datei oder Verzeichnis mit diesem Namen existiert bereits.");
				}
			} else {
				optionPane.showMessageDialog("Leider besitzen Sie nicht die nötigen Rechte für diese Operation.");
			}
		}

		return createdFile;
	}

	/**
	 * Erstellt ein neues Verzeichnis. Wo das Verzeichnis eingefügt wird hängt davon ab welche Datei/Verzeichnis der<br/>
	 * Nutzer gerade angewählt hat. Hat er keine Datei oder Verzeichnis selektiert wird das Verzeichnis als Kind des<br/>
	 * Root-Verzeichnisses erstellt.
	 *
	 * @param contextMenuController Ein ContextMenuController dessen aktuelle Klickposition mit berücksichtigt werden
	 *                              soll. Hier kann auch ein Optional.absent() übergeben werden. Dann wird kein
	 *                              ContextMenuController betrachtet.
	 * @return Das neu erstellte Verzeichnis als Optional. Optional.absent() falls der Nutzer keinen korrekten Namen
	 *         eingegeben hat, ein Verzeichnis mit diesem Namen bereits existiert oder er nicht die erfoderlichen Rechte besitzt.
	 */
	public Optional<Directory> createNewDirectoryBasedOnUserSelection(final Optional<ContextMenuController> contextMenuController) {
		Optional<Directory> createdDir = Optional.absent();

		final String newDirectoryName = optionPane.showInputDialog("Geben Sie einen Namen für das neue Verzeichnis ein:", "");

		if (!isNullOrEmpty(newDirectoryName)) {
			final Directory parentDirectory = getParentDirectoryForFEntryCreation(contextMenuController);
			if (parentDirectory.getPermissionOfCurrentUser().getWriteAllowed()) {
				createdDir = parentDirectory.createNewDirectory(newDirectoryName);
				if (!createdDir.isPresent()) {
					optionPane.showMessageDialog("Eine Datei oder Verzeichnis mit diesem Namen existiert bereits.");
				}
			} else {
				optionPane.showMessageDialog("Leider besitzen Sie nicht die nötigen Rechte für diese Operation.");
			}
		}

		return createdDir;
	}

	private Directory getParentDirectoryForFEntryCreation(final Optional<ContextMenuController> contextMenuController) {
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
