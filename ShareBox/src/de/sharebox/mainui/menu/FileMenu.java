package de.sharebox.mainui.menu;

import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import de.sharebox.file.controller.ContextMenuController;
import de.sharebox.file.model.Directory;
import de.sharebox.file.services.DirectoryViewClipboardService;
import de.sharebox.file.services.DirectoryViewSelectionService;
import de.sharebox.helpers.SwingEngineHelper;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Diese Klasse erstellt das Datei-Menü in der Menüleiste und reagiert auf die Nutzerinteraktionen.<br/>
 * Das Erstellen und Verlinken der Aktionen mit dieser Klasse führt die SwingEngine durch.
 */
public class FileMenu {
	private final DirectoryViewSelectionService selectionService;
	private final DirectoryViewClipboardService clipboard;

	/**
	 * Erstellt ein neues FileMenu und fügt es der gegebenen JMenuBar hinzu.<br/>
	 * Instanzen dieser Klasse sollten per Dependency Injection durch Guice erstellt werden. Siehe auch FileMenuFactory.
	 *
	 * @param menuBar          Die JMenuBar zu dem das Menü hinzugefügt werden soll.
	 * @param selectionService Ein DirectoryViewSelectionService mit dem die Auswahl des Nutzer im JTree des
	 *                         DirectoryViewControllers festgestellt werden kann.
	 * @param clipboard        Ein DirectoryViewClipboardService um Zugriff auf die Zwischenablage für FEntries zu erhalten.
	 */
	@Inject
	FileMenu(final @Assisted JMenuBar menuBar,
			 final DirectoryViewSelectionService selectionService,
			 final DirectoryViewClipboardService clipboard) {
		this.selectionService = selectionService;
		this.clipboard = clipboard;

		final JMenu menu = (JMenu) new SwingEngineHelper().render(this, "menu/fileMenu");
		menuBar.add(menu);
	}

	/**
	 * Handler - um auf die Auswahl des "Neue Datei erstellen"-Buttons im Datei-Menü zu reagieren.
	 */
	public Action createNewFile = new AbstractAction() {
		@Override
		public void actionPerformed(final ActionEvent event) {
			selectionService.createNewFileBasedOnUserSelection(Optional.<ContextMenuController>absent());
		}
	};

	/**
	 * Handler - um auf die Auswahl des "Neues Verzeichnis erstellen"-Buttons im Datei-Menü zu reagieren.
	 */
	public Action createNewDirectory = new AbstractAction() {
		@Override
		public void actionPerformed(final ActionEvent event) {
			selectionService.createNewDirectoryBasedOnUserSelection(Optional.<ContextMenuController>absent());
		}
	};

	/**
	 * Handler - um auf Kopieren-Buttons im Datei-Menü zu reagieren.
	 */
	public Action copyFEntry = new AbstractAction() {
		@Override
		public void actionPerformed(final ActionEvent event) {
			clipboard.addToClipboard(selectionService.getSelectedFEntries());
		}
	};

	/**
	 * Handler - um auf Einfügen-Buttons im Datei-Menü zu reagieren.
	 */
	public Action pasteFEntry = new AbstractAction() {
		@Override
		public void actionPerformed(final ActionEvent event) {
			if (!selectionService.getSelectedFEntries().isEmpty()) {
				Directory targetDirectory;
				if (selectionService.getSelectedFEntries().get(0) instanceof Directory) {
					targetDirectory = (Directory) selectionService.getSelectedFEntries().get(0);
				} else {
					targetDirectory = selectionService.getParentsOfSelectedFEntries().get(0).get();
				}
				clipboard.pasteClipboardContent(targetDirectory);
			}
		}
	};
}
