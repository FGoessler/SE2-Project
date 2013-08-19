package de.sharebox.user.controller;

import de.sharebox.api.UserAPI;
import de.sharebox.helpers.OptionPaneHelper;
import de.sharebox.helpers.SwingEngineHelper;
import de.sharebox.user.model.User;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class ChangeCredentialsController {
	private JFrame frame;
	public JTextField eMailField;
	public JPasswordField oldPasswordField;
	public JPasswordField newPasswordField;
	public JPasswordField newPasswordField1;
	protected OptionPaneHelper optionPane = new OptionPaneHelper();


	public ChangeCredentialsController() {
		frame = (JFrame) new SwingEngineHelper().render(this, "changeCredentials");
		frame.setVisible(true);

		User user = UserAPI.getUniqueInstance().getCurrentUser();
		eMailField.setText(user.getEmail());
	}
	
	public Action save = new AbstractAction() {
		public void actionPerformed( ActionEvent save ) {
			UserAPI userApi = UserAPI.getUniqueInstance();
			User user = new User();
			user.setEmail(eMailField.getText());
			user.setPassword(newPasswordField.toString());

			User currentUser = userApi.getCurrentUser();
			currentUser.setPassword(oldPasswordField.toString());

			if (newPasswordField.toString() == newPasswordField1.toString()){
				if (userApi.authenticateUser(currentUser)) {
					if (userApi.changeCredential(currentUser, user)) {
						frame.setVisible(false);
						optionPane.showMessageDialog("Ihre Änderungen wurden gespeichert!");
					}
					else {
						optionPane.showMessageDialog("Sie haben keine Daten verändert oder ihre neue E-Mail Adresse ist bereits vergeben!");
					}
				}
			}

		};
	};
}
