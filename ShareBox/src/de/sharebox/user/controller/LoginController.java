package de.sharebox.user.controller;

import de.sharebox.api.UserAPI;
import de.sharebox.user.User;
import org.swixml.SwingEngine;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class LoginController {
	public transient JTextField eMailField;
	public transient JTextField passwordField;

	public Action submit = new AbstractAction() {
		public void actionPerformed( ActionEvent event ) {
			UserAPI userApi = UserAPI.getUniqueInstance();
			User user = new User();
			user.setEmail(eMailField.getText());
			user.setPassword(passwordField.getText());

			if (userApi.authenticateUser(user)) {
				System.out.println("Hurra");
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
		try {
			new SwingEngine( this ).render("resources/xml/login.xml").setVisible(true);
		} catch(Exception e) {
			System.out.println("Couldn't create Swing Window!");
		}
	}
}
