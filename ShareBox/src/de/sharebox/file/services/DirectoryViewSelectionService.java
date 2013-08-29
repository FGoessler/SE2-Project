package de.sharebox.file.services;

import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.sharebox.file.controller.ContextMenuController;
import de.sharebox.file.controller.FEntryTreeNode;
import de.sharebox.file.model.Directory;
import de.sharebox.file.model.FEntry;
import de.sharebox.file.model.File;
import de.sharebox.file.notification.FEntryNotification;
import de.sharebox.file.notification.FEntryObserver;
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
 * Diese Klasse ist als Singleton gedacht, so dass wann immer ein DirectoryViewSelectionService per Guice injected wird,
 * alle Objekte Zugriff auf dieselbe Instanz besitzen. Diese Singleton-Instanz muss allerdings initial im
 * DirectoryViewController mit dem JTree verknüpft werden.
 */
@Singleton
public class DirectoryViewSelectionService {
	private final OptionPaneHelper optionPane;

	private JTree treeView;

	/**
	 * Erstellt einen neuen DirectoryViewSelectionService.<br/>
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
	 * Liefert die Elternverzeichnisse, der aktuell im JTree ausgewählten FEntries, in der selben Reihenfolge wie von getSelectedFEntries().
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
	 * Erstellt eine neue Datei. Wo die Datei eingefügt wird hängt davon ab welche Datei/Verzeichnis der Nutzer gerade
	 * angewählt hat. Hat er keine Datei oder Verzeichnis selektiert wird die Datei als Kind des Root-Verzeichnisses
	 * erstellt.
	 *
	 * @param contextMenuController Ein ContextMenuController dessen aktuelle Klickposition mit berücksichtigt werden
	 *                              soll. Hier kann auch ein Optional.absent() übergeben werden. Dann wird kein
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
	 * Erstellt ein neues Verzeichnis. Wo das Verzeichnis eingefügt wird hängt davon ab welche Datei/Verzeichnis der
	 * Nutzer gerade angewählt hat. Hat er keine Datei oder Verzeichnis selektiert wird das Verzeichnis als Kind des
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

	/**
	 * Löscht die vom Nutzer im DirectoryViewController selektierten FEntries bzw. den/die FEntries, für die per Rechtsklick
	 * ein Kontextmenü aufgerufen wurde.
	 *
	 * @param contextMenuController Ein ContextMenuController dessen aktuelle Klickposition mit berücksichtigt werden
	 *                              soll. Hier kann auch ein Optional.absent() übergeben werden. Dann wird kein
	 *                              ContextMenuController betrachtet.
	 */
	public void deleteFEntryBasedOnUserSelection(final Optional<ContextMenuController> contextMenuController) {
		Optional<FEntry> selectedFEntry = Optional.absent();
		if (contextMenuController.isPresent()) {
			selectedFEntry = contextMenuController.get().getSelectedFEntry();
		}

		final List<FEntry> selectedFEntries = new ArrayList<FEntry>(getSelectedFEntries());

		if (contextMenuController.isPresent() && selectedFEntries.contains(selectedFEntry.get()) && selectedFEntries.size() > 1) {
			deleteMultipleFEntries(selectedFEntries, getParentsOfSelectedFEntries());
		} else if (contextMenuController.isPresent()) {
			deleteSingleFEntry(selectedFEntry.get(), contextMenuController.get());
		} else if (!contextMenuController.isPresent()) {
			deleteMultipleFEntries(selectedFEntries, getParentsOfSelectedFEntries());
		}
	}

	private void deleteSingleFEntry(final FEntry selectedFEntry, final ContextMenuController contextMenuController) {
		final Optional<Directory> parentDirectory = contextMenuController.getParentOfSelectedFEntry();
		if (parentDirectory.isPresent() && parentDirectory.get().getPermissionOfCurrentUser().getWriteAllowed()) {
			parentDirectory.get().deleteFEntry(selectedFEntry);
		} else if (parentDirectory.isPresent()) {
			optionPane.showMessageDialog("Sie besitzen leider nicht die erforderlichen Rechte um diese Änderung vorzunehmen.");
		} else {
			optionPane.showMessageDialog("Sie können ihr Hauptverzeichnis nicht löschen.");
		}
	}

	/**
	 * Löscht die gegebenen FEntries aus ihren entsprechenden Elternverzeichnissen.
	 *
	 * @param fEntriesToDelete  Die zu löschenden FEntries.
	 * @param parentDirectories Die Elternverzeichnisse der zu löschenden FEntries.
	 */
	private void deleteMultipleFEntries(final List<FEntry> fEntriesToDelete, final List<Optional<Directory>> parentDirectories) {
		// Add observer to all elements in the list, so they can be removed from the list of items, that
		// should be deleted, if they already got deleted - either directly or indirectly by deleting the parent
		final FEntryObserver observer = new FEntryObserver() {
			@Override
			public void fEntryNotification(final FEntryNotification notification) {
				if (notification.getChangeType().equals(FEntryNotification.ChangeType.DELETED)) {
					//remove FEntry from list
					int index = fEntriesToDelete.indexOf(notification.getChangedFEntry());
					if (index >= 0) {
						fEntriesToDelete.remove(index);
						parentDirectories.remove(index);
					}
				}
			}
		};
		for (final FEntry fEntry : fEntriesToDelete) {
			fEntry.addObserver(observer);
		}

		//delete all selected FEntries
		final List<String> namesOfNotDeletedFEntries = new ArrayList<String>();
		while (!parentDirectories.isEmpty()) {
			if (parentDirectories.get(0).isPresent() && parentDirectories.get(0).get().getPermissionOfCurrentUser().getWriteAllowed()) {
				parentDirectories.get(0).get().deleteFEntry(fEntriesToDelete.get(0));
			} else {
				namesOfNotDeletedFEntries.add(fEntriesToDelete.get(0).getName());
				fEntriesToDelete.remove(0);
				parentDirectories.remove(0);
			}
		}
		if (!namesOfNotDeletedFEntries.isEmpty()) {
			optionPane.showMessageDialog("Folgende Elemente konnten nicht gelöscht werden: " + Joiner.on(", ").skipNulls().join(namesOfNotDeletedFEntries));
		}
	}
}
