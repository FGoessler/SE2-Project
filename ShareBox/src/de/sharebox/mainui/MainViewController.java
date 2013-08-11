package de.sharebox.mainui;

import de.sharebox.Main;
import de.sharebox.api.UserAPI;
import de.sharebox.file.controller.DirectoryViewController;
import de.sharebox.user.model.User;
import de.sharebox.user.controller.AccountingController;
import de.sharebox.user.controller.EditProfileController;
import de.sharebox.user.controller.InvitationController;
import de.sharebox.user.controller.LoginController;
import org.swixml.SwingEngine;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Dieser Controller kümmert sich um das Hauptfenster. Er erstellt das zentrale Menü und stellt dem
 * DirectoryVewController einen Container zur Verfügung um die Darstellung der Verzeichnisstruktur durchzuführen.
 * Dieser Controller besitzt außerdem eine Referenz auf den aktuell eingeloggten User, dessen Daten dargestellt werden.
 */
public class MainViewController extends WindowAdapter {

	private JFrame frame;

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
	 * Wird über die SwingEngine gesetzt.
	 */
	public JTree tree;

	/**
	 * Die zentrale Menüleiste.
	 */
	protected final JMenuBar menuBar;
	protected final FileMenu fileMenu;
	protected final AdministrationMenu administrationMenu;

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
			frame = (JFrame)swix.render("resources/xml/mainWindow.xml");
			frame.setVisible(true);
		} catch (Exception exception) {
			System.out.println("Couldn't create Swing Window!");
		}

		directoryViewController = new DirectoryViewController(tree);

		menuBar = frame.getJMenuBar();
		fileMenu = new FileMenu(menuBar, directoryViewController);
		administrationMenu = new AdministrationMenu(menuBar, this);
	}

	/**
	 * Liefert den aktuell eingeloggten Benutzer, dessen Daten angezeigt werden.
	 *
	 * @return Der aktuell eingeloggte Benutzer.
	 */
	public User getCurrentUser() {
		return currentUser;
	}

	/**
	 * Erstellt und öfnet ein EditProfile-Fenster zum Bearbeiten der Profildaten.
	 */
	public void openEditProfileController() {
		editProfileController = new EditProfileController();
	}

	/**
	 * Erstellt und öffnet ein Accounting-Fenster zum Ändern der AccoutingDaten.
	 */
	public void openAccountController() {
		accountController = new AccountingController();
	}

	/**
	 * Erstellt und öffnet ein Invitation-Fenster zum Einladen neuer Benutzer.
	 */
	public void openInvitationController() {
		invitationController = new InvitationController();
	}

	/**
	 * Schließt das Hauptfenster und loggt den Benutzer aus.
	 */
	public void close() {
		UserAPI.getUniqueInstance().logout();

		frame.setVisible(false);

		Main.loginController = new LoginController();
	}

	/**
	 * Reagiert auf das Schließen des Fensters und beendet das Program.
	 */
	@Override
	public void windowClosing(WindowEvent event) {
		super.windowClosing(event);
		System.exit(0);
	}
}
