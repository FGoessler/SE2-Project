package de.sharebox.mainui.menu;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import de.sharebox.mainui.MainViewController;
import org.swixml.SwingEngine;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Diese Klasse erstellt das "Datei"-Menü in der Menüleiste und reagiert auf die Nutzerinteraktionen.<br/>
 * Das Erstellen und Verlinken der Aktionen mit dieser Klasse führt die SwingEngine durch.
 */
public class AdministrationMenu {
	private final MainViewController mainViewController;

	/**
	 * Erstellt ein neues AdministrationMenu und fügt es in die gegebene JMenuBar ein.
	 *
	 * @param menuBar            Die JMenuBar zu dem das Menü hinzugefügt werden soll.
	 * @param mainViewController Der MainViewController dessen Methoden durch die Aktionen im Menü aufgerufen werden.
	 */
	@Inject
	AdministrationMenu(@Assisted JMenuBar menuBar, @Assisted MainViewController mainViewController) {
		this.mainViewController = mainViewController;
		try {
			SwingEngine swix = new SwingEngine(this);
			JMenu menu = (JMenu) swix.render("resources/xml/menu/administrationMenu.xml");
			menuBar.add(menu);
		} catch (Exception exception) {
			System.out.println("Couldn't create Swing AdministrationMenu!");
		}
	}

	/**
	 * Handler um auf die Auswahl des "Profil ändern"-Buttons im Menü zu reagieren.
	 */
	public Action showEditProfile = new AbstractAction() {
		public void actionPerformed(ActionEvent event) {
			mainViewController.openEditProfileController();
		}
	};

	/**
	 * Handler um auf die Auswahl des "Accounting ändern"-Buttons im Menü zu reagieren.
	 */
	public Action showEditAccounting = new AbstractAction() {
		public void actionPerformed(ActionEvent event) {
			mainViewController.openAccountController();
		}
	};

	/**
	 * Handler um auf die Auswahl des "Einladen"-Buttons im Menü zu reagieren.
	 */
	public Action showInvitationView = new AbstractAction() {
		public void actionPerformed(ActionEvent event) {
			mainViewController.openInvitationController();
		}
	};

	/**
	 * Handler um auf die Auswahl das "Ausloggen"-Buttons im Menü zu reagieren.
	 */
	public Action logout = new AbstractAction() {
		public void actionPerformed(ActionEvent event) {
			mainViewController.close();
		}
	};
}
