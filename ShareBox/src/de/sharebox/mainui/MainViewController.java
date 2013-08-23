package de.sharebox.mainui;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import de.sharebox.api.UserAPI;
import de.sharebox.file.controller.DirectoryViewControllerFactory;
import de.sharebox.file.controller.PermissionViewControllerFactory;
import de.sharebox.helpers.SwingEngineHelper;
import de.sharebox.mainui.menu.AdministrationMenuFactory;
import de.sharebox.mainui.menu.FileMenuFactory;
import de.sharebox.user.controller.*;
import de.sharebox.user.model.User;

import javax.swing.*;

/**
 * Dieser Controller kümmert sich um das Hauptfenster. Er erstellt das zentrale Menü und stellt dem
 * DirectoryVewController einen Container zur Verfügung um die Darstellung der Verzeichnisstruktur durchzuführen.
 * Dieser Controller besitzt außerdem eine Referenz auf den aktuell eingeloggten User, dessen Daten dargestellt werden.
 */
public class MainViewController {
	private final EditProfileController editProfileController;
	private final AccountingController accountingController;
	private final InvitationController invitationController;
	private final ChangeCredentialsController changeCredentialsController;
	private final LoginController loginController;
	private final UserAPI userAPI;

	private JFrame frame;

	/**
	 * Der aktuell eingeloggte Nutzer.
	 */
	private User currentUser;

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
	 * @param user                           Der Nutzer dessen Daten angezeigt werden sollen. Kann nicht von Guice
	 *                                       injecten werden und wird daher per Factory gesetzt.
	 * @param userAPI                        Die UserAPI zur Kommunikation mit dem Server.
	 * @param callingLoginController         Der LoginController der diesen mainViewController erstellt. Wird benötigt um
	 *                                       den diesen LoginController wieder anzuzeigen, wenn der Nutzer sich ausloggt.
	 *                                       Kann nicht von Guice injecten werden und wird daher per Factory gesetzt.
	 * @param permissionViewControllerFactory
	 *                                       Mittels dieser Factory wird ein PermissionViewController erzeugt,
	 *                                       der in der rechten Hälfte des JSplitPanes dargestellt wird.
	 * @param directoryViewControllerFactory Mittels dieser Factory wird ein DirectoryViewController erzeugt,
	 *                                       der im JTree in der linken Hälfte des JSplitPane seinen Inhalt darstellt.
	 * @param fileMenuFactory                Mittels dieser Factory wird das FileMenu erzeugt.
	 * @param administrationMenuFactory      Mittels dieser Factory wird das AdministrationMenu erzeugt.
	 * @param accountingController           Ein AccountingController für Änderungen an den Rechnungsdaten.
	 */
	@Inject
	MainViewController(@Assisted User user,
					   @Assisted LoginController callingLoginController,
					   UserAPI userAPI,
					   PermissionViewControllerFactory permissionViewControllerFactory,
					   DirectoryViewControllerFactory directoryViewControllerFactory,
					   FileMenuFactory fileMenuFactory,
					   AdministrationMenuFactory administrationMenuFactory,
					   AccountingController accountingController,
					   ChangeCredentialsController changeCredentialsController,
					   EditProfileController editProfileController,
					   InvitationController invitationController) {

		this.currentUser = user;
		this.userAPI = userAPI;
		this.loginController = callingLoginController;
		this.accountingController = accountingController;
		this.changeCredentialsController = changeCredentialsController;
		this.editProfileController = editProfileController;
		this.invitationController = invitationController;

		//create window
		frame = (JFrame) new SwingEngineHelper().render(this, "directory/mainWindow");
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
	 * Öffnet ein ChangeCredentials-Fenster zum Bearbeiten der Logindaten.
	 */
	public void openEditCredentialsController() {
		changeCredentialsController.show();
	}

	/**
	 * Öffnet ein EditProfile-Fenster zum Bearbeiten der Profildaten.
	 */
	public void openEditProfileController() {
		editProfileController.show();
	}

	/**
	 * Öffnet ein Accounting-Fenster zum Ändern der AccoutingDaten.
	 */
	public void openAccountController() {
		accountingController.show();
	}

	/**
	 * Öffnet ein Invitation-Fenster zum Einladen neuer Benutzer.
	 */
	public void openInvitationController() {
		invitationController.show();
	}

	/**
	 * Schließt das Hauptfenster und loggt den Benutzer aus. Anschließend ist wieder das Login-Fenster sichtbar.
	 */
	public void close() {
		userAPI.logout();

		frame.setVisible(false);

		loginController.show();
	}
}
