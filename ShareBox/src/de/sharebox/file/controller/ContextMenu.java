package de.sharebox.file.controller;

import de.sharebox.file.model.Directory;
import de.sharebox.file.model.FEntry;
import de.sharebox.helpers.OptionPaneHelper;
import org.swixml.SwingEngine;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.awt.event.ActionEvent;

public class ContextMenu {
	protected OptionPaneHelper optionPane = new OptionPaneHelper();

	protected JPopupMenu popupMenu;
	private TreePath currentTreePath;

	/**
	 * Erstellt ein neues ContextMenu. Das Menü wird dazu aus der contextMenu.xml Datei generiert.
	 */
	public ContextMenu() {
		try {
			SwingEngine swix = new SwingEngine(this);
			swix.render("resources/xml/contextMenu.xml").setVisible(true);
		} catch(Exception exception) {
			System.out.println("Couldn't create context menu!");
		}
		popupMenu.setVisible(false);
	}

	/**
	 * Zeigt das ContextMenu als Popup Menü an. Dazu muss ein TreePath zum angewählten Objekt im
	 * TreeView übergeben werden, sowie die Koordinaten des Klicks, um das Menü zu positionieren.
	 * @param treePath Der TreePath anhand dem das angewählte Objekt im TreeView gefunden werden kann.
	 * @param xPos Die X Koordinate des Klicks.
	 * @param yPos Die Y Koordinate des Klicks.
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
	}

	/**
	 * Gibt aus ob das Kontextmenü gerade sichtbar ist oder nicht.
	 * @return True - das Kontextmenü ist sichtbar. False - sonst.
	 */
	public Boolean isMenuVisible() {
		return popupMenu.isVisible();
	}

	/**
	 * Gibt den TreePath zu dem Objekt zurück, das vom Nutzer angewählt wurde, also den TreePath,
	 * der in der showMenu Methode übergeben wurde und auf dessen letzte Komponente sich alle Aktionen
	 * dieses Kontextmenüs beziehen.
	 * @return Der TreePath zum aktuell ausgewählten Objekt.
	 */
	public TreePath getCurrentTreePath() {
		return currentTreePath;
	}

	/**
	 * Gibt den FEntry zurück, auf den sich alle Aktionen des Kontextmenüs beziehen.
	 * @return Der FEntry auf den sich alle Aktionen des Kontextmenüs beziehen.
	 */
	public FEntry getSelectedFEntry() {
		return ((TreeNode)currentTreePath.getLastPathComponent()).getFEntry();
	}

	/**
	 * ActionHandler um auf den Klick auf den "Löschen"-Eintrag im Kontextmenü zu reagieren.
	 */
	public Action deleteFEntry = new AbstractAction() {
		@Override
		public void actionPerformed(ActionEvent event) {
			FEntry selectedFEntry = getSelectedFEntry();
			Directory parentDirectory = (Directory)((TreeNode)currentTreePath.getParentPath().getLastPathComponent()).getFEntry();

			parentDirectory.deleteFEntry(selectedFEntry);

			hideMenu();
		}
	};

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
			//TODO: Kopieren von FEntries
			optionPane.showMessageDialog("This function is not yet supported!");

			hideMenu();
		}
	};

	/**
	 * ActionHandler um auf den Klick auf den "Einfügen"-Eintrag im Kontextmenü zu reagieren.
	 */
	public Action pasteFEntry = new AbstractAction() {
		@Override
		public void actionPerformed(ActionEvent event) {
			//TODO: Einfügen von FEntries
			optionPane.showMessageDialog("This function is not yet supported!");

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
