package de.sharebox.mainui.menu;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import de.sharebox.helpers.SwingEngineHelper;
import de.sharebox.mainui.MainViewController;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Diese Klasse erstellt das Administration-Menü in der Menüleiste und reagiert auf Nutzerinteraktionen.<br/>
 * Das Erstellen und Verlinken der Aktionen mit dieser Klasse führt die SwingEngine durch.
 */
public class AdministrationMenu {
	private final MainViewController mainViewController;

	/**
	 * Erstellt ein neues Administration-Menü und fügt es in die gegebene JMenuBar ein.
	 * Instanzen dieser Klasse sollten per Dependency Injection durch Guice erstellt werden. Siehe auch AdministrationMenuFactory.
	 *
	 * @param menuBar            Die JMenuBar zu dem das Menü hinzugefügt werden soll.
	 * @param mainViewController Der MainViewController dessen Methoden durch die Aktionen im Menü aufgerufen werden.
	 */
	@Inject
	AdministrationMenu(@Assisted JMenuBar menuBar, @Assisted MainViewController mainViewController) {
		this.mainViewController = mainViewController;

		JMenu menu = (JMenu) new SwingEngineHelper().render(this, "menu/administrationMenu");
		menuBar.add(menu);
	}

	/**
	 * Handler - um auf die Auswahl des "Logindaten ändern"-Buttons im Menü zu reagieren.
	 */
	public Action showEditCredentials = new AbstractAction() {
		public void actionPerformed(ActionEvent event) {
			mainViewController.openEditCredentialsController();
		}
	};

	/**
	 * Handler - um auf die Auswahl des "Profil ändern"-Buttons im Menü zu reagieren.
	 */
	public Action showEditProfile = new AbstractAction() {
		public void actionPerformed(ActionEvent event) {
			mainViewController.openEditProfileController();
		}
	};

	/**
	 * Handler - um auf die Auswahl des "Accounting ändern"-Buttons im Menü zu reagieren.
	 */
	public Action showEditAccounting = new AbstractAction() {
		public void actionPerformed(ActionEvent event) {
			mainViewController.openAccountController();
		}
	};

	/**
	 * Handler - um auf die Auswahl des Einladen-Buttons im Menü zu reagieren.
	 */
	public Action showInvitationView = new AbstractAction() {
		public void actionPerformed(ActionEvent event) {
			mainViewController.openInvitationController();
		}
	};

	/**
	 * Handler - um auf die Auswahl das Ausloggen-Buttons im Menü zu reagieren.
	 */
	public Action logout = new AbstractAction() {
		public void actionPerformed(ActionEvent event) {
			mainViewController.close();
		}
	};
}
