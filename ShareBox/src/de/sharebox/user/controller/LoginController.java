package de.sharebox.user.controller;

import de.sharebox.Main;
import de.sharebox.api.UserAPI;
import de.sharebox.mainui.MainViewController;
import de.sharebox.user.User;
import org.swixml.SwingEngine;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class LoginController extends WindowAdapter {
	private JFrame frame;

	public JTextField mailField;
	public JPasswordField passwordField;

	public Action submit = new AbstractAction() {
		public void actionPerformed( ActionEvent event ) {
			UserAPI userApi = UserAPI.getUniqueInstance();
			User user = new User();
			user.setEmail(mailField.getText());
			user.setPassword(new String(passwordField.getPassword()));

			if (userApi.login(user)) {
				Main.mainWindowViewController = new MainViewController(user);
				frame.setVisible(false);
			}
			else {
				System.out.println("Error");
			}
		}
	};


	public Action register = new AbstractAction() {
		public void actionPerformed( ActionEvent event ) {
			try {
				SwingEngine swix = new SwingEngine(this);
				swix.render("resources/xml/register.xml").setVisible(true);
			} catch(Exception exception) {
				System.out.println("Couldn't create register menu!");
			}
		}
	};

	public LoginController() {
		super();

		try {
			SwingEngine swix = new SwingEngine( this );
			frame = (JFrame)swix.render("resources/xml/login.xml");
			frame.setVisible(true);
		} catch(Exception e) {
			System.out.println("Couldn't create Swing Window!");
		}
	}

	@Override
	public void windowClosing(WindowEvent event) {
		super.windowClosing(event);
		System.exit(0);
	}
}
