package de.sharebox.mainui;

import de.sharebox.file.controller.DirectoryViewController;
import de.sharebox.user.User;
import de.sharebox.user.controller.AccountingController;
import de.sharebox.user.controller.EditProfileController;
import de.sharebox.user.controller.InvitationController;
import org.swixml.SwingEngine;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Dieser Controller kümmert sich um das Hauptfenster. Er reagiert auf Events aus dem zentralen Menü und stellt dem
 * DirectoryVewController einen Container zur Verfügung um die Darstellung der Verzeichnisstruktur durchzuführen.
 * Dieser Controller besitzt außerdem eine Referenz auf den aktuell eingeloggten User, dessen Daten dargestellt werden.
 */
public class MainViewController extends WindowAdapter {

	/**
	 * Der aktuell eingeloggte Nutzer.
	 */
	private User currentUser;

	/**
	 * Referenz auf die SwingEngine, die das generieren der Swing Objekte übernimmt und später nach diversen
	 * Eigenschaften gefragt werden kann. (siehe swixml.org)
	 */
	protected SwingEngine swix;

	protected DirectoryViewController directoryViewController;
	protected EditProfileController editProfileController;
	protected AccountingController accountController;
	protected InvitationController invitationController;

	/**
	 * Der JTree, in dem der DirectoryViewController seine Inhalte darstellt.
	 */
	public JTree tree;

	/**
	 * Handler um auf die Auswahl des "Profil ändern"-Buttons im Menü zu reagieren.
	 */
	public Action showEditProfile = new AbstractAction() {
		public void actionPerformed(ActionEvent event) {
			editProfileController = new EditProfileController();
		}
	};

	/**
	 * Handler um auf die Auswahl des "Accounting ändern"-Buttons im Menü zu reagieren.
	 */
	public Action showEditAccounting = new AbstractAction() {
		public void actionPerformed(ActionEvent event) {
			accountController = new AccountingController();
		}
	};

	/**
	 * Handler um auf die Auswahl des "Einladen"-Buttons im Menü zu reagieren.
	 */
	public Action showInvitationView = new AbstractAction() {
		public void actionPerformed(ActionEvent event) {
			invitationController = new InvitationController();
		}
	};

	/**
	 * Handler um auf die Auswahl das "Ausloggen"-Buttons im Menü zu reagieren.
	 */
	public Action logout = new AbstractAction() {
		public void actionPerformed(ActionEvent event) {
			//TODO: logout Prozess starten
		}
	};

	/**
	 * Reagiert auf das Schließen des Fensters und beendet das Program.
	 */
	@Override
	public void windowClosing(WindowEvent event) {
		super.windowClosing(event);
		System.exit(0);
	}

	/**
	 * Erstellt ein neues Hauptfenster und zeigt es an. Das UI wird dabei aus der mainwindow.xml
	 * Datei mittels SWIxml generiert.
	 *
	 * @param user Der Nutzer dessen Daten angezeigt werden sollen.
	 */
	public MainViewController(User user) {
		super();

		this.currentUser = user;

		//create window
		try {
			swix = new SwingEngine(this);
			swix.render("resources/xml/mainWindow.xml").setVisible(true);
		} catch (Exception exception) {
			System.out.println("Couldn't create Swing Window!");
		}

		directoryViewController = new DirectoryViewController(tree);
	}

	/**
	 * Liefert den aktuell eingeloggten Benutzer, dessen Daten angezeigt werden.
	 *
	 * @return Der aktuell eingeloggte Benutzer.
	 */
	public User getCurrentUser() {
		return currentUser;
	}

}
