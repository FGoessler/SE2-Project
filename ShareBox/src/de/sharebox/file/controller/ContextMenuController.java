package de.sharebox.file.controller;

import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.sun.istack.internal.NotNull;
import de.sharebox.file.model.Directory;
import de.sharebox.file.model.FEntry;
import de.sharebox.file.model.FEntryObserver;
import de.sharebox.file.services.DirectoryViewClipboardService;
import de.sharebox.file.services.DirectoryViewSelectionService;
import de.sharebox.file.services.SharingService;
import de.sharebox.helpers.OptionPaneHelper;
import de.sharebox.helpers.SwingEngineHelper;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Dieser Controller ist für das Kontextmenü verantwortlich, das per Rechtsklick auf einen Eintrag im JTree des
 * DirectoryViewControllers aufgerufen werden kann.
 */
public class ContextMenuController {
	private final DirectoryViewSelectionService selectionService;
	private final DirectoryViewClipboardService clipboard;
	private final SharingService sharingService;
	private final OptionPaneHelper optionPane;

	private Optional<TreePath> currentTreePath = Optional.absent();

	/**
	 * Das JPopupMenu dieses Kontextmenüs.
	 * Wird mittels SWIxml gesetzt.
	 */
	protected JPopupMenu popupMenu;

	/**
	 * Erstellt ein neues ContextMenu. Das Menü wird dazu aus der contextMenu.xml Datei generiert.<br/>
	 * Dieses Objekt sollte im Produktivcode nur per Dependency Injection von Guice erstellt werden.
	 *
	 * @param selectionService Ein DirectoryViewSelectionService um die aktuelle Auswahl im JTree erhalten zu können.
	 * @param clipboard        Ein DirectoryViewClipboardService um Zugriff auf den Inhalt der Zwischenablage für FEntries zu
	 *                         erhalten.
	 * @param sharingService   Ein SharingService mit dem FEntries für andere Nutzer freigegeben werden können.
	 * @param optionPaneHelper Ein OptionPaneHelper zum Erzeugen von Dialogfenstern.
	 */
	@Inject
	ContextMenuController(DirectoryViewSelectionService selectionService,
						  DirectoryViewClipboardService clipboard,
						  SharingService sharingService,
						  OptionPaneHelper optionPaneHelper) {

		this.selectionService = selectionService;
		this.clipboard = clipboard;
		this.sharingService = sharingService;
		this.optionPane = optionPaneHelper;

		new SwingEngineHelper().render(this, "contextMenu");

		popupMenu.setVisible(false);
	}

	/**
	 * Zeigt das ContextMenu als Popup Menü an. Dazu muss ein TreePath zum angewählten Objekt im
	 * TreeView übergeben werden, sowie die Koordinaten des Klicks, um das Menü zu positionieren.
	 *
	 * @param treePath Der TreePath anhand dem das angewählte Objekt im TreeView gefunden werden kann.
	 * @param xPos     Die X Koordinate des Klicks.
	 * @param yPos     Die Y Koordinate des Klicks.
	 */
	public void showMenu(@NotNull TreePath treePath, int xPos, int yPos) {
		popupMenu.setLocation(xPos, yPos);
		popupMenu.setVisible(true);
		currentTreePath = Optional.of(treePath);
	}

	/**
	 * Blendet das Kontextmenü aus.
	 */
	public void hideMenu() {
		popupMenu.setVisible(false);
		currentTreePath = Optional.absent();
	}

	/**
	 * Gibt aus ob das Kontextmenü gerade sichtbar ist oder nicht.
	 *
	 * @return True - das Kontextmenü ist sichtbar. False - sonst.
	 */
	public Boolean isMenuVisible() {
		return popupMenu.isVisible();
	}

	/**
	 * Gibt den TreePath zu dem Objekt zurück, das vom Nutzer angewählt wurde, also den TreePath,
	 * der in der showMenu Methode übergeben wurde und auf dessen letzte Komponente sich alle Aktionen
	 * dieses Kontextmenüs beziehen.
	 *
	 * @return Der TreePath zum aktuell ausgewählten Objekt.
	 */
	public Optional<TreePath> getCurrentTreePath() {
		return currentTreePath;
	}

	/**
	 * Gibt den FEntry zurück, auf den sich alle Aktionen des Kontextmenüs beziehen.
	 *
	 * @return Der FEntry auf den sich alle Aktionen des Kontextmenüs beziehen.
	 */
	public Optional<FEntry> getSelectedFEntry() {
		Optional<FEntry> foundFEntry = Optional.absent();
		if (currentTreePath.isPresent()) {
			foundFEntry = Optional.of(((FEntryTreeNode) currentTreePath.get().getLastPathComponent()).getFEntry());
		}
		return foundFEntry;
	}

	/**
	 * Gibt das Oververzeichnis des FEntry zurück, auf den sich alle Aktionen des Kontextmenüs beziehen.
	 *
	 * @return Das Oberverzeichnis des FEntries auf den sich alle Aktionen des Kontextmenüs beziehen.
	 */
	public Optional<Directory> getParentOfSelectedFEntry() {
		Optional<Directory> foundDirectory = Optional.absent();
		if (currentTreePath.isPresent()) {
			foundDirectory = Optional.of((Directory) ((FEntryTreeNode) currentTreePath.get().getParentPath().getLastPathComponent()).getFEntry());
		}
		return foundDirectory;
	}

	/**
	 * ActionHandler um auf den Klick auf den "Neue Datei erstellen"-Eintrag im Kontextmenü zu reagieren.
	 */
	public Action createNewFile = new AbstractAction() {
		@Override
		public void actionPerformed(ActionEvent event) {
			selectionService.createNewFileBasedOnUserSelection(Optional.of(ContextMenuController.this));

			hideMenu();
		}
	};

	/**
	 * ActionHandler um auf den Klick auf den "Neues Verzeichnis erstellen"-Eintrag im Kontextmenü zu reagieren.
	 */
	public Action createNewDirectory = new AbstractAction() {
		@Override
		public void actionPerformed(ActionEvent event) {
			selectionService.createNewDirectoryBasedOnUserSelection(Optional.of(ContextMenuController.this));

			hideMenu();
		}
	};

	/**
	 * ActionHandler um auf den Klick auf den "Löschen"-Eintrag im Kontextmenü zu reagieren.
	 */
	public Action deleteFEntry = new AbstractAction() {
		@Override
		public void actionPerformed(ActionEvent event) {
			Optional<FEntry> selectedFEntry = getSelectedFEntry();
			final List<FEntry> selectedFEntries = new ArrayList<FEntry>(selectionService.getSelectedFEntries());

			if (selectedFEntries.contains(selectedFEntry.get()) && selectedFEntries.size() > 1) {
				final List<Optional<Directory>> selectedFEntriesParents = selectionService.getParentsOfSelectedFEntries();

				deleteMultipleFEntries(selectedFEntries, selectedFEntriesParents);
			} else {
				Directory parentDirectory = (Directory) ((FEntryTreeNode) currentTreePath.get().getParentPath().getLastPathComponent()).getFEntry();
				if(parentDirectory.getPermissionOfCurrentUser().getWriteAllowed()) {
					parentDirectory.deleteFEntry(selectedFEntry.get());
				} else {
					optionPane.showMessageDialog("Sie besitzen leider nicht die erforderlichen Rechte um diese Änderung vorzunehmen.");
				}
			}

			hideMenu();
		}
	};

	/**
	 * Löscht die gegebenen FEntries aus ihren entsprechenden Elternverzeichnissen.
	 *
	 * @param fEntriesToDelete  Die zu löschenden FEntries.
	 * @param parentDirectories Die Elternverzeichnisse der zu löschenden FEntries.
	 */
	private void deleteMultipleFEntries(final List<FEntry> fEntriesToDelete, final List<Optional<Directory>> parentDirectories) {
		// Add observer to all elements in the list, so they can be removed from the list of items, that
		// should be deleted, if they already got deleted - either directly or indirectly by deleting the parent
		FEntryObserver observer = new FEntryObserver() {
			@Override
			public void fEntryChangedNotification(FEntry fEntry, ChangeType reason) {
				//not used here
			}

			@Override
			public void fEntryDeletedNotification(FEntry fEntry) {
				//remove FEntry from list
				int index = fEntriesToDelete.indexOf(fEntry);
				if (index >= 0) {
					fEntriesToDelete.remove(index);
					parentDirectories.remove(index);
				}
			}
		};
		for (FEntry fEntry : fEntriesToDelete) {
			fEntry.addObserver(observer);
		}

		//delete all selected FEntries
		List<String> namesOfNotDeletedFEntries = new ArrayList<String>();
		while (!parentDirectories.isEmpty()) {
			if(parentDirectories.get(0).get().getPermissionOfCurrentUser().getWriteAllowed()) {
				parentDirectories.get(0).get().deleteFEntry(fEntriesToDelete.get(0));
			} else {
				namesOfNotDeletedFEntries.add(fEntriesToDelete.get(0).getName());
				fEntriesToDelete.remove(0);
				parentDirectories.remove(0);
			}
		}
		if(!namesOfNotDeletedFEntries.isEmpty()) {
			optionPane.showMessageDialog("Folgende Elemente konnten nicht gelöscht werden: " + Joiner.on(", ").skipNulls().join(namesOfNotDeletedFEntries));
		}
	}

	/**
	 * ActionHandler um auf den Klick auf den "Umbennen"-Eintrag im Kontextmenü zu reagieren.
	 */
	public Action renameFEntry = new AbstractAction() {
		@Override
		public void actionPerformed(ActionEvent event) {
			Optional<FEntry> selectedFEntry = getSelectedFEntry();

			if (selectedFEntry.isPresent() && selectedFEntry.get().getPermissionOfCurrentUser().getWriteAllowed()) {
				String newName = optionPane.showInputDialog("Geben Sie den neuen Namen an:", selectedFEntry.get().getName());

				selectedFEntry.get().setName(newName);
			} else {
				optionPane.showMessageDialog("Sie besitzen leider nicht die erforderlichen Rechte um diese Änderung vorzunehmen.");
			}

			hideMenu();
		}
	};

	/**
	 * ActionHandler um auf den Klick auf den "Kopieren"-Eintrag im Kontextmenü zu reagieren.
	 */
	public Action copyFEntry = new AbstractAction() {
		@Override
		public void actionPerformed(ActionEvent event) {
			Optional<FEntry> selectedFEntry = getSelectedFEntry();
			final List<FEntry> selectedFEntries = new ArrayList<FEntry>(selectionService.getSelectedFEntries());

			if (selectedFEntries.contains(selectedFEntry.get()) && selectedFEntries.size() > 1) {
				clipboard.resetClipboard();
				clipboard.addToClipboard(selectedFEntries);
			} else {
				clipboard.resetClipboard();
				clipboard.addToClipboard(selectedFEntry.get());
			}

			hideMenu();
		}
	};

	/**
	 * ActionHandler um auf den Klick auf den "Einfügen"-Eintrag im Kontextmenü zu reagieren.
	 */
	public Action pasteFEntry = new AbstractAction() {
		@Override
		public void actionPerformed(ActionEvent event) {
			Directory pasteDirectory;
			if (getSelectedFEntry().get() instanceof Directory) {
				pasteDirectory = (Directory) getSelectedFEntry().get();
			} else {
				pasteDirectory = getParentOfSelectedFEntry().get();
			}

			clipboard.pasteClipboardContent(pasteDirectory);

			hideMenu();
		}
	};

	/**
	 * ActionHandler um auf den Klick auf den "Teilen"-Eintrag im Kontextmenü zu reagieren.
	 */
	public Action shareFEntry = new AbstractAction() {
		@Override
		public void actionPerformed(ActionEvent event) {
			Optional<FEntry> selectedFEntry = getSelectedFEntry();
			final List<FEntry> selectedFEntries = selectionService.getSelectedFEntries();

			if (selectedFEntries.contains(selectedFEntry.get()) && selectedFEntries.size() > 1) {
				sharingService.showShareFEntryDialog(selectionService.getSelectedFEntries());
			} else {
				sharingService.showShareFEntryDialog(selectedFEntry.get());
			}

			hideMenu();
		}
	};
}
