package de.sharebox.user.controller;

import com.google.inject.Inject;
import de.sharebox.api.UserAPI;
import de.sharebox.helpers.OptionPaneHelper;
import de.sharebox.helpers.SwingEngineHelper;
import de.sharebox.mainui.MainViewControllerFactory;
import de.sharebox.user.model.User;

import javax.swing.*;
import java.awt.event.ActionEvent;

/*
 * TODO Klassenbeschreibung (Eingabefelder für das Einloggen?)
 */
public class LoginController {
	private final MainViewControllerFactory mainViewControllerFactory;
	private final OptionPaneHelper optionPane;
	private final RegisterController registerController;
	private final UserAPI userAPI;

	private JFrame frame;

	protected JTextField mailField;
	protected JPasswordField passwordField;

	/**
	 * Erstellt einen neuen LoginController.<br/>
	 * Instanzen dieser Klasse sollten per Dependency Injection durch Guice erstellt werden.
	 *
	 * @param userAPI                   Die UserAPI zur Kommunikation mit dem Server.
	 * @param mainViewControllerFactory Mittels dieser Factory wird nach einem erfolgreichen Login das Hauptfenster
	 *                                  (MainViewController) erzeugt und der eingeloggte Benutzer gesetzt.
	 * @param optionPaneHelper          Ein OptionPaneHelper zum Anzeigen von Dialog-Fenstern
	 * @param registerController        Ein RegisterController mit dessen Hilfe sich neue Nutzer registrieren können.
	 */
	@Inject
	LoginController(final UserAPI userAPI,
					final MainViewControllerFactory mainViewControllerFactory,
					final OptionPaneHelper optionPaneHelper,
					final RegisterController registerController) {
		this.userAPI = userAPI;
		this.mainViewControllerFactory = mainViewControllerFactory;
		this.optionPane = optionPaneHelper;
		this.registerController = registerController;
	}

	/**
	 * Erstellt das GUI des LoginControllers und zeigt ihn an.
	 */
	public void show() {
		frame = (JFrame) new SwingEngineHelper().render(this, "user/login");
		frame.setVisible(true);
	}

	/**
	 * Handler - reagiert auf einen Klick auf den Login-Button. Überprüft die eingegebenen Daten und erstellt, im
	 * Erfolgsfall, das Hauptfenster (MainViewController). Bei Falscheingabe wird der Nutzer aufgefordert seine Daten korrekt einzugeben.
	 */
	public Action submit = new AbstractAction() {
		public void actionPerformed(final ActionEvent event) {
			final User user = new User();
			user.setEmail(mailField.getText());
			user.setPassword(new String(passwordField.getPassword()));

			if (userAPI.login(user)) {
				mainViewControllerFactory.create(user, LoginController.this);
				frame.setVisible(false);
			} else {
				optionPane.showMessageDialog("Login-Informationen falsch! Bitte geben sie ihre Daten erneut ein.");
			}
		}
	};

	/**
	 * Der Registrieren-Button bringt den Nutzer in das Registrieren-Fenster, wo er einen Account erstellen kann.
	 */
	public Action register = new AbstractAction() {
		public void actionPerformed(final ActionEvent event) {
			registerController.show();
		}
	};


}
