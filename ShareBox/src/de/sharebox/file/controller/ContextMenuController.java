package de.sharebox.file.controller;

import de.sharebox.file.model.Directory;
import de.sharebox.file.model.FEntry;
import de.sharebox.file.model.FEntryObserver;
import de.sharebox.file.services.DirectoryViewClipboardService;
import de.sharebox.helpers.OptionPaneHelper;
import org.swixml.SwingEngine;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public class ContextMenuController {
	private final DirectoryViewController parentDirectoryController;
	private final DirectoryViewClipboardService clipboard;
	private TreePath currentTreePath;

	protected OptionPaneHelper optionPane = new OptionPaneHelper();
	protected JPopupMenu popupMenu;

	/**
	 * Erstellt ein neues ContextMenu. Das Menü wird dazu aus der contextMenu.xml Datei generiert.
	 *
	 * @param parentDirectoryController Der DirectoryViewController auf den sich das Kontextmenü bezieht.
	 */
	public ContextMenuController(DirectoryViewController parentDirectoryController, DirectoryViewClipboardService clipboard) {
		this.parentDirectoryController = parentDirectoryController;
		this.clipboard = clipboard;

		try {
			SwingEngine swix = new SwingEngine(this);
			swix.render("resources/xml/contextMenu.xml").setVisible(true);
		} catch (Exception exception) {
			System.out.println("Couldn't create context menu!");
		}
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
	public void showMenu(TreePath treePath, int xPos, int yPos) {
		popupMenu.setLocation(xPos, yPos);
		popupMenu.setVisible(true);
		currentTreePath = treePath;
	}

	/**
	 * Blendet das Kontextmenü aus.
	 */
	public void hideMenu() {
		popupMenu.setVisible(false);
		currentTreePath = null;
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
	public TreePath getCurrentTreePath() {
		return currentTreePath;
	}

	/**
	 * Gibt den FEntry zurück, auf den sich alle Aktionen des Kontextmenüs beziehen.
	 *
	 * @return Der FEntry auf den sich alle Aktionen des Kontextmenüs beziehen.
	 */
	public FEntry getSelectedFEntry() {
		FEntry foundFEntry = null;
		if (currentTreePath != null) {
			foundFEntry = ((TreeNode) currentTreePath.getLastPathComponent()).getFEntry();
		}
		return foundFEntry;
	}

	/**
	 * Gibt das Oververzeichnis des FEntry zurück, auf den sich alle Aktionen des Kontextmenüs beziehen.
	 *
	 * @return Das Oberverzeichnis des FEntries auf den sich alle Aktionen des Kontextmenüs beziehen.
	 */
	public Directory getParentOfSelectedFEntry() {
		Directory foundDirectory = null;
		if (currentTreePath != null) {
			foundDirectory = (Directory) ((TreeNode) currentTreePath.getParentPath().getLastPathComponent()).getFEntry();
		}
		return foundDirectory;
	}

	/**
	 * ActionHandler um auf den Klick auf den "Neue Datei erstellen"-Eintrag im Kontextmenü zu reagieren.
	 */
	public Action createNewFile = new AbstractAction() {
		@Override
		public void actionPerformed(ActionEvent event) {
			parentDirectoryController.createNewFileBasedOnUserSelection();

			hideMenu();
		}
	};

	/**
	 * ActionHandler um auf den Klick auf den "Neues Verzeichnis erstellen"-Eintrag im Kontextmenü zu reagieren.
	 */
	public Action createNewDirectory = new AbstractAction() {
		@Override
		public void actionPerformed(ActionEvent event) {
			parentDirectoryController.createNewDirectoryBasedOnUserSelection();

			hideMenu();
		}
	};

	/**
	 * ActionHandler um auf den Klick auf den "Löschen"-Eintrag im Kontextmenü zu reagieren.
	 */
	public Action deleteFEntry = new AbstractAction() {
		@Override
		public void actionPerformed(ActionEvent event) {
			FEntry selectedFEntry = getSelectedFEntry();
			final List<FEntry> selectedFEntries = new ArrayList<FEntry>(parentDirectoryController.getSelectedFEntries());

			if(selectedFEntries.contains(selectedFEntry) && selectedFEntries.size() > 1) {
				final List<Directory> selectedFEntriesParents =  parentDirectoryController.getParentsOfSelectedFEntries();

				deleteMultipleFEntries(selectedFEntries, selectedFEntriesParents);
			} else {
				Directory parentDirectory = (Directory) ((TreeNode) currentTreePath.getParentPath().getLastPathComponent()).getFEntry();
				parentDirectory.deleteFEntry(selectedFEntry);
			}

			hideMenu();
		}
	};

	/**
	 * Löscht die gegebenen FEntries aus ihren entsprechenden Elternverzeichnissen.
	 * @param fEntriesToDelete Die zu löschenden FEntries.
	 * @param parentDirectories Die Elternverzeichnisse der zu löschenden FEntries.
	 */
	private void deleteMultipleFEntries(final List<FEntry> fEntriesToDelete, final List<Directory> parentDirectories) {
		// Add observer to all elements in the list, so they can be removed from the list of items, that
		// should be deleted, if they already got deleted - either directly or indirectly by deleting the parent
		FEntryObserver observer = new FEntryObserver() {
			@Override
			public void fEntryChangedNotification(FEntry fEntry, FEntry.ChangeType reason) {}

			@Override
			public void fEntryDeletedNotification(FEntry fEntry) {
				//remove FEntry from list
				int index = fEntriesToDelete.indexOf(fEntry);
				   if(index >= 0) {
					fEntriesToDelete.remove(index);
					parentDirectories.remove(index);
				}
			}
		};
		for(FEntry fEntry : fEntriesToDelete) {
			fEntry.addObserver(observer);
		}

		//delete all selected FEntries
		while(!parentDirectories.isEmpty()) {
			if(parentDirectories.get(0) != null && fEntriesToDelete.get(0) != null) {
				parentDirectories.get(0).deleteFEntry(fEntriesToDelete.get(0));
			}
		 }
	}

	/**
	 * ActionHandler um auf den Klick auf den "Umbennen"-Eintrag im Kontextmenü zu reagieren.
	 */
	public Action renameFEntry = new AbstractAction() {
		@Override
		public void actionPerformed(ActionEvent event) {
			FEntry selectedFEntry = getSelectedFEntry();

			String newName = optionPane.showInputDialog("Geben Sie den neuen Namen an:", getSelectedFEntry().getName());

			selectedFEntry.setName(newName);

			hideMenu();
		}
	};

	/**
	 * ActionHandler um auf den Klick auf den "Kopieren"-Eintrag im Kontextmenü zu reagieren.
	 */
	public Action copyFEntry = new AbstractAction() {
		@Override
		public void actionPerformed(ActionEvent event) {
			FEntry selectedFEntry = getSelectedFEntry();
			final List<FEntry> selectedFEntries = new ArrayList<FEntry>(parentDirectoryController.getSelectedFEntries());

			if(selectedFEntries.contains(selectedFEntry) && selectedFEntries.size() > 1) {
				clipboard.resetClipboard();
				clipboard.addToClipboard(selectedFEntries);
			} else {
				clipboard.resetClipboard();
				clipboard.addToClipboard(selectedFEntry);
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
			if(getSelectedFEntry() instanceof Directory) {
				pasteDirectory = (Directory)getSelectedFEntry();
			} else {
				pasteDirectory = getParentOfSelectedFEntry();
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
			//TODO: Teilen von FEntries
			optionPane.showMessageDialog("This function is not yet supported!");

			hideMenu();
		}
	};
}
