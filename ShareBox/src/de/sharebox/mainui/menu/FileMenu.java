package de.sharebox.mainui.menu;

import de.sharebox.file.controller.DirectoryViewController;
import de.sharebox.file.model.Directory;
import org.swixml.SwingEngine;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Diese Klasse erstellt das "Datei"-Menü in der Menüleiste und reagiert auf die Nutzerinteraktionen.
 * Das Erstellen und Verlinken der Aktionen mit dieser Klasse handelt die SwingEngine.
 */
public class FileMenu {
	private DirectoryViewController directoryViewController;

	/**
	 * Erstellt ein neues FileMenu und fügt es der gegebenen JMenuBar hinzu.
	 * @param menuBar Die JMenuBar zu dem das Menü hinzugefügt werden soll.
	 * @param directoryViewController Der DirectoryViewController auf den sich die Aktionen des Menüs beziehen.
	 */
	public FileMenu(JMenuBar menuBar, DirectoryViewController directoryViewController) {
		this.directoryViewController = directoryViewController;
		try {
			SwingEngine swix = new SwingEngine(this);
			JMenu menu = (JMenu)swix.render("resources/xml/menu/fileMenu.xml");
			menuBar.add(menu);
		} catch (Exception exception) {
			System.out.println("Couldn't create Swing FileMenu!");
		}
	}

	/**
	 * Handler um auf die Auswahl des "Neue Datei erstellen"-Buttons im "Datei"-Menü zu reagieren.
	 */
	public Action createNewFile = new AbstractAction() {
		@Override
		public void actionPerformed(ActionEvent event) {
			directoryViewController.createNewFileBasedOnUserSelection();
		}
	};

	/**
	 * Handler um auf die Auswahl des "Neues Verzeichnis erstellen"-Buttons im "Datei"-Menü zu reagieren.
	 */
	public Action createNewDirectory = new AbstractAction() {
		@Override
		public void actionPerformed(ActionEvent event) {
			directoryViewController.createNewDirectoryBasedOnUserSelection();
		}
	};

	/**
	 * Handler um auf "Kopieren" im Datei-Menü zu reagieren.
	 */
	public Action copyFEntry = new AbstractAction() {
		@Override
		public void actionPerformed(ActionEvent event) {
			directoryViewController.getClipboard().addToClipboard(directoryViewController.getSelectedFEntries());
		}
	};

	/**
	 * Handler um auf "Einfügen" im Datei-Menü zu reagieren.
	 */
	public Action pasteFEntry = new AbstractAction() {
		@Override
		public void actionPerformed(ActionEvent event) {
			if (!directoryViewController.getSelectedFEntries().isEmpty()) {
				Directory targetDirectory;
				if(directoryViewController.getSelectedFEntries().get(0) instanceof Directory) {
					targetDirectory = (Directory) directoryViewController.getSelectedFEntries().get(0);
				} else {
					targetDirectory = directoryViewController.getParentsOfSelectedFEntries().get(0).get();
				}
				directoryViewController.getClipboard().pasteClipboardContent(targetDirectory);
			}
		}
	};
}
