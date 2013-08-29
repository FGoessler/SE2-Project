package de.sharebox.file.controller;

import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.sun.istack.internal.NotNull;
import de.sharebox.file.model.Directory;
import de.sharebox.file.model.FEntry;
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
	 * Das JPopupMenu dieses Kontextmenüs.<br/>
	 * Wird mittels SWIxml gesetzt.
	 */
	protected JPopupMenu popupMenu;

	/**
	 * Erstellt ein neues Kontextmenü. Das Menü wird dazu aus der contextMenu.xml Datei generiert.
	 *
	 * @param selectionService Ein DirectoryViewSelectionService um die aktuelle Auswahl im JTree erhalten zu können.
	 * @param clipboard        Ein DirectoryViewClipboardService um Zugriff auf den Inhalt der Zwischenablage für FEntries zu
	 *                         erhalten.
	 * @param sharingService   Ein SharingService mit dem FEntries für andere Nutzer freigegeben werden können.
	 * @param optionPaneHelper Ein OptionPaneHelper zum Erzeugen von Dialogfenstern.
	 */
	@Inject
	ContextMenuController(final DirectoryViewSelectionService selectionService,
						  final DirectoryViewClipboardService clipboard,
						  final SharingService sharingService,
						  final OptionPaneHelper optionPaneHelper) {

		this.selectionService = selectionService;
		this.clipboard = clipboard;
		this.sharingService = sharingService;
		this.optionPane = optionPaneHelper;

		new SwingEngineHelper().render(this, "directory/contextMenu");

		popupMenu.setVisible(false);
	}

	/**
	 * Zeigt das Kontextmenü als Popup-Menü an. Dazu muss ein TreePath zum angewählten Objekt im
	 * TreeView übergeben werden, sowie die Koordinaten des Klicks, um das Menü zu positionieren.
	 *
	 * @param treePath Der TreePath zum angewählten Objekt im TreeView.
	 * @param xPos     Die X Koordinate des Klicks.
	 * @param yPos     Die Y Koordinate des Klicks.
	 */
	public void showMenu(final @NotNull TreePath treePath, final int xPos, final int yPos) {
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
	 * Gibt den TreePath des vom Nutzer gewählten Objekts zurück. Dieser wurde in der showMenu Methode übergeben
	 * und alle Aktionen dieses Kontextmenüs beziehen sich auf dessen letzte Komponente (Treepath).
	 *
	 * @return Der TreePath zum aktuell ausgewählten Objekt
	 */
	public Optional<TreePath> getCurrentTreePath() {
		return currentTreePath;
	}

	/**
	 * Gibt den FEntry zurück, auf den sich alle Aktionen des Kontextmenüs beziehen.
	 *
	 * @return Der FEntry auf den sich alle Aktionen des Kontextmenüs beziehen
	 */
	public Optional<FEntry> getSelectedFEntry() {
		Optional<FEntry> foundFEntry = Optional.absent();
		if (currentTreePath.isPresent()) {
			foundFEntry = Optional.of(((FEntryTreeNode) currentTreePath.get().getLastPathComponent()).getFEntry());
		}
		return foundFEntry;
	}

	/**
	 * Gibt das Oberverzeichnis des FEntry zurück, auf den sich alle Aktionen des Kontextmenüs beziehen.
	 *
	 * @return Das Oberverzeichnis des FEntries auf den sich alle Aktionen des Kontextmenüs beziehen
	 */
	public Optional<Directory> getParentOfSelectedFEntry() {
		Optional<Directory> foundDirectory = Optional.absent();
		if (currentTreePath.isPresent()) {
			TreePath parentPath = currentTreePath.get().getParentPath();
			if (parentPath != null) {
				foundDirectory = Optional.of((Directory) ((FEntryTreeNode) parentPath.getLastPathComponent()).getFEntry());
			}
		}
		return foundDirectory;
	}

	/**
	 * ActionHandler - um auf den Klick auf den "Neue Datei erstellen"-Eintrag im Kontextmenü zu reagieren.
	 * Wird per SWIxml an das GUI Element gebunden.
	 */
	public Action createNewFile = new AbstractAction() {
		@Override
		public void actionPerformed(final ActionEvent event) {
			selectionService.createNewFileBasedOnUserSelection(Optional.of(ContextMenuController.this));

			hideMenu();
		}
	};

	/**
	 * ActionHandler - um auf den Klick auf den "Neues Verzeichnis erstellen"-Eintrag im Kontextmenü zu reagieren.
	 * Wird per SWIxml an das GUI Element gebunden.
	 */
	public Action createNewDirectory = new AbstractAction() {
		@Override
		public void actionPerformed(final ActionEvent event) {
			selectionService.createNewDirectoryBasedOnUserSelection(Optional.of(ContextMenuController.this));

			hideMenu();
		}
	};

	/**
	 * ActionHandler - um auf den Klick auf den Löschen-Eintrag im Kontextmenü zu reagieren.
	 * Wird per SWIxml an das GUI Element gebunden.
	 */
	public Action deleteFEntry = new AbstractAction() {
		@Override
		public void actionPerformed(final ActionEvent event) {
			selectionService.deleteFEntryBasedOnUserSelection(Optional.of(ContextMenuController.this));

			hideMenu();
		}
	};

	/**
	 * ActionHandler - um auf den Klick auf den Umbennen-Eintrag im Kontextmenü zu reagieren.<br/>
	 * Wird per SWIxml an das GUI Element gebunden.
	 */
	public Action renameFEntry = new AbstractAction() {
		@Override
		public void actionPerformed(final ActionEvent event) {
			final Optional<FEntry> selectedFEntry = getSelectedFEntry();

			if (selectedFEntry.isPresent() && selectedFEntry.get().getPermissionOfCurrentUser().getWriteAllowed()) {
				final String newName = optionPane.showInputDialog("Geben Sie den neuen Namen an:", selectedFEntry.get().getName());

				selectedFEntry.get().setName(newName);
			} else {
				optionPane.showMessageDialog("Sie besitzen leider nicht die erforderlichen Rechte um diese Änderung vorzunehmen.");
			}

			hideMenu();
		}
	};

	/**
	 * ActionHandler - um auf den Klick auf den Kopieren-Eintrag im Kontextmenü zu reagieren.<br/>
	 * Wird per SWIxml an das GUI Element gebunden.
	 */
	public Action copyFEntry = new AbstractAction() {
		@Override
		public void actionPerformed(final ActionEvent event) {
			final Optional<FEntry> selectedFEntry = getSelectedFEntry();
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
	 * ActionHandler - um auf den Klick auf den Einfügen-Eintrag im Kontextmenü zu reagieren.<br/>
	 * Wird per SWIxml an das GUI Element gebunden.
	 */
	public Action pasteFEntry = new AbstractAction() {
		@Override
		public void actionPerformed(final ActionEvent event) {
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
	 * ActionHandler um auf den Klick auf den Teilen-Eintrag im Kontextmenü zu reagieren.<br/>
	 * Wird per SWIxml an das GUI Element gebunden.
	 */
	public Action shareFEntry = new AbstractAction() {
		@Override
		public void actionPerformed(final ActionEvent event) {
			final Optional<FEntry> selectedFEntry = getSelectedFEntry();
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
