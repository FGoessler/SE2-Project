package de.sharebox.user.controller;

import com.google.inject.Inject;
import de.sharebox.api.UserAPI;
import de.sharebox.helpers.OptionPaneHelper;
import de.sharebox.helpers.SwingEngineHelper;
import de.sharebox.mainui.MainViewControllerFactory;
import de.sharebox.user.model.User;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class LoginController {
	private final MainViewControllerFactory mainViewControllerFactory;
	private final OptionPaneHelper optionPane;
	private final RegisterController registerController;

	private JFrame frame;

	public JTextField mailField;
	public JPasswordField passwordField;

	/**
	 * Erstellt einen neuen LoginController.<br/>
	 * Instanzen dieser Klasse solten per Dependency Injection durch Guice erstellt werden.
	 *
	 * @param mainViewControllerFactory Mittels dieser Factory wird nach einem erfolgreichen Login das Hauptfenster
	 *                                  (MainViewController) erzeugt und der eingeloggte Benutzer gesetzt.
	 * @param optionPaneHelper          Ein OptionPaneHelper zum Anzeigen von Dialog-Fenstern
	 */
	@Inject
	LoginController(MainViewControllerFactory mainViewControllerFactory,
					OptionPaneHelper optionPaneHelper,
					RegisterController registerController) {
		this.mainViewControllerFactory = mainViewControllerFactory;
		this.optionPane = optionPaneHelper;
		this.registerController = registerController;

		UserAPI.getUniqueInstance().createSampleContent();

		frame = (JFrame) new SwingEngineHelper().render(this, "login");
		frame.setVisible(true);
	}

	/**
	 * Handler um auf einen Klick auf den Login-Button zu reagieren. Überprüft die eingegebenen Daten und erstellt im
	 * Erfolgsfall das Hauptfenster (MainViewController). Bei Falscheingabe wird der Nutzer aufgefordert seine Daten korrekt einzugeben.
	 */
	public Action submit = new AbstractAction() {
		public void actionPerformed(ActionEvent event) {
			UserAPI userApi = UserAPI.getUniqueInstance();
			User user = new User();
			user.setEmail(mailField.getText());
			user.setPassword(new String(passwordField.getPassword()));

			if (userApi.login(user)) {
				mainViewControllerFactory.create(user);
				frame.setVisible(false);
			} else {
				optionPane.showMessageDialog("Login-Informationen falsch! Bitte geben sie ihre Daten erneut ein.");
			}
		}
	};

	/**
	 * Der Registrierenbutton bringt den Nutzer ins Registrierenmenü, hier kann er einen Account erstellen.
	 */
	public Action register = new AbstractAction() {
		public void actionPerformed(ActionEvent event) {
			registerController.show();
		}
	};
}
