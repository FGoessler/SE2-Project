package de.sharebox.mainui;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import de.sharebox.api.UserAPI;
import de.sharebox.file.controller.DirectoryViewControllerFactory;
import de.sharebox.file.controller.PermissionViewControllerFactory;
import de.sharebox.helpers.SwingEngineHelper;
import de.sharebox.mainui.menu.AdministrationMenuFactory;
import de.sharebox.mainui.menu.FileMenuFactory;
import de.sharebox.user.controller.AccountingController;
import de.sharebox.user.controller.EditProfileController;
import de.sharebox.user.controller.InvitationController;
import de.sharebox.user.model.User;

import javax.swing.*;

/**
 * Dieser Controller kümmert sich um das Hauptfenster. Er erstellt das zentrale Menü und stellt dem
 * DirectoryVewController einen Container zur Verfügung um die Darstellung der Verzeichnisstruktur durchzuführen.
 * Dieser Controller besitzt außerdem eine Referenz auf den aktuell eingeloggten User, dessen Daten dargestellt werden.
 */
public class MainViewController {

	private JFrame frame;

	/**
	 * Der aktuell eingeloggte Nutzer.
	 */
	private User currentUser;

	protected EditProfileController editProfileController;
	protected AccountingController accountController;
	protected InvitationController invitationController;

	/**
	 * Der JTree, in dem der DirectoryViewController seine Inhalte darstellt.
	 * Wird über die SwingEngine gesetzt.
	 */
	public JTree tree;
	public JSplitPane splitPane;

	/**
	 * Die zentrale Menüleiste.
	 */
	protected final JMenuBar menuBar;

	/**
	 * Erstellt ein neues Hauptfenster und zeigt es an. Das UI wird dabei aus der mainwindow.xml Datei mittels SWIxml
	 * generiert.<br/>
	 * Instanzen dieser Klasse solten per Dependency Injection durch Guice erstellt werden.
	 * Siehe auch MainViewControllerFactory.
	 *
	 * @param user                           Der Nutzer dessen Daten angezeigt werden sollen.
	 * @param permissionViewControllerFactory
	 *                                       Mittels dieser Factory wird ein PermissionViewController erzeugt,
	 *                                       der in der rechten Hälfte des JSplitPanes dargestellt wird.
	 * @param directoryViewControllerFactory Mittels dieser Factory wird ein DirectoryViewController erzeugt,
	 *                                       der im JTree in der linken Hälfte des JSplitPane seinen Inhalt darstellt.
	 * @param fileMenuFactory                Mittels dieser Factory wird das FileMenu erzeugt.
	 * @param administrationMenuFactory      Mittels dieser Factory wird das AdministrationMenu erzeugt.
	 */
	@Inject
	MainViewController(@Assisted User user,
					   PermissionViewControllerFactory permissionViewControllerFactory,
					   DirectoryViewControllerFactory directoryViewControllerFactory,
					   FileMenuFactory fileMenuFactory,
					   AdministrationMenuFactory administrationMenuFactory) {

		this.currentUser = user;

		//create window
		frame = (JFrame) new SwingEngineHelper().render(this, "mainWindow");
		frame.setVisible(true);

		directoryViewControllerFactory.create(tree);

		menuBar = frame.getJMenuBar();
		fileMenuFactory.create(menuBar);
		administrationMenuFactory.create(menuBar, this);

		permissionViewControllerFactory.create(splitPane);
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
	}
}
