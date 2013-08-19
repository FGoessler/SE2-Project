package de.sharebox.user.controller;

import com.google.inject.Inject;
import de.sharebox.api.UserAPI;
import de.sharebox.mainui.MainViewControllerFactory;
import de.sharebox.user.model.User;
import org.swixml.SwingEngine;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class LoginController {
	private JFrame frame;

	public JTextField mailField;
	public JPasswordField passwordField;

	private final MainViewControllerFactory mainViewControllerFactory;

	/**
	 * Erstellt einen neuen LoginController.<br/>
	 * Instanzen dieser Klasse solten per Dependency Injection durch Guice erstellt werden.
	 *
	 * @param mainViewControllerFactory Mittels dieser Factory wird nach einem erfolgreichen Login das Hauptfenster
	 *                                  (MainViewController) erzeugt und der eingeloggte Benutzer gesetzt.
	 */
	@Inject
	LoginController(MainViewControllerFactory mainViewControllerFactory) {
		this.mainViewControllerFactory = mainViewControllerFactory;

		try {
			SwingEngine swix = new SwingEngine(this);
			frame = (JFrame) swix.render("resources/xml/login.xml");
			frame.setVisible(true);
		} catch (Exception e) {
			System.out.println("Couldn't create Swing Window!");
		}
	}

	/**
	 * Handler um auf einen Klick auf den Login-Button zu reagieren. Überprüft die eingegebenen Daten und erstellt im
	 * Erfolgsfall das Hauptfenster (MainViewController).
	 */
	public Action submit = new AbstractAction() {
		public void actionPerformed(ActionEvent event) {
			UserAPI userApi = UserAPI.getUniqueInstance();
			userApi.createSampleContent();
			User user = new User();
			user.setEmail(mailField.getText());
			user.setPassword(new String(passwordField.getPassword()));

			if (userApi.login(user)) {
				mainViewControllerFactory.create(user);
				frame.setVisible(false);
			} else {
				System.out.println("Error");
			}
		}
	};

	public Action register = new AbstractAction() {
		public void actionPerformed(ActionEvent event) {
			new RegisterController();
		}
	};
}
