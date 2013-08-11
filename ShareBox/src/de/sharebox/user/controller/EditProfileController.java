package de.sharebox.user.controller;

import de.sharebox.api.UserAPI;
import de.sharebox.user.model.User;
import de.sharebox.helpers.OptionPaneHelper;
import javax.swing.*;

import org.swixml.SwingEngine;

import java.awt.event.ActionEvent;

public class EditProfileController {
	private JFrame frame;
	public JTextField lastnameField;
	public JTextField firstnameField;
	public JTextField genderField;
	protected OptionPaneHelper optionPane = new OptionPaneHelper();

	
	public EditProfileController() {
		try {
			SwingEngine swix = new SwingEngine(this);
			frame = (JFrame) swix.render("resources/xml/editProfile.xml");
			frame.setVisible(true);
		} catch (Exception exception) {
			System.out.println("Couldn't create register window!");
		}
		
		User user = UserAPI.getUniqueInstance().getCurrentUser();
		lastnameField.setText(user.getLastname());
		firstnameField.setText(user.getFirstname());
		genderField.setText(user.getGender());
	}
	
	public Action save = new AbstractAction() {
		public void actionPerformed( ActionEvent event ) {
			UserAPI userApi = UserAPI.getUniqueInstance();
			User user = new User();
			user.setLastname(lastnameField.getText());
			user.setFirstname(firstnameField.getText());
			user.setGender(genderField.getText());

			if (userApi.changeProfile(user)) {
				frame.setVisible(false);
				optionPane.showMessageDialog("Ihre Änderungen wurden gespeichert!");
			}
			else {
				frame.setVisible(false);
				optionPane.showMessageDialog("Sie haben keine Informationen geändert!");
			}
		}
	};
}
