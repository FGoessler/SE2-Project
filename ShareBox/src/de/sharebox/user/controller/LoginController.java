package de.sharebox.user.controller;

import de.sharebox.Main;
import de.sharebox.api.UserAPI;
import de.sharebox.helpers.OptionPaneHelper;
import de.sharebox.mainui.MainViewController;
import de.sharebox.user.model.User;
import org.swixml.SwingEngine;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * @author Benjamin Barth
 * @author Kay Thorsten Meißner
 */

public class LoginController {
	private JFrame frame;

	public JTextField mailField;
	public JPasswordField passwordField;
	protected OptionPaneHelper optionPane = new OptionPaneHelper();

	/*
	 * Öffnen des Login Fensters. Hierbei wird zunächst ein Testprofil als Standard in den TextFields
	 * übernommen.
	 */
	
	public LoginController() {
		super();

		try {
			SwingEngine swix = new SwingEngine(this);
			frame = (JFrame) swix.render("resources/xml/login.xml");
			frame.setVisible(true);
		} catch (Exception e) {
			optionPane.showMessageDialog("Ein Fehler ist aufgetreten!");
		}
	}
	
	/*
	 * Bei richtigen Login-Informationen wird der Nutzer eingeloggt und das Main-Fenster wird geöffnet. 
	 * Bei Falscheingabe wird der Nutzer aufgefordert seine Daten korrekt einzugeben.
	 */
	
	public Action submit = new AbstractAction() {
		public void actionPerformed(ActionEvent event) {
			UserAPI userApi = UserAPI.getUniqueInstance();
			userApi.createSampleContent();
			User user = new User();
			user.setEmail(mailField.getText());
			user.setPassword(new String(passwordField.getPassword()));

			if (userApi.login(user)) {
				Main.mainWindowViewController = new MainViewController(user);
				frame.setVisible(false);
			} else {
				optionPane.showMessageDialog("Login-Informationen falsch! Bitte geben sie ihre Daten erneut ein.");
			}
		}
	};

	/*
	 * Der Registrierenbutton bringt den Nutzer ins Registrierenmenü, hier kann er einen Account erstellen.
	 */
	
	public Action register = new AbstractAction() {
		public void actionPerformed(ActionEvent event) {
			new RegisterController();
		}
	};


}
