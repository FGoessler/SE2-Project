package de.sharebox.user.controller;

import de.sharebox.api.UserAPI;
import de.sharebox.helpers.OptionPaneHelper;
import de.sharebox.helpers.SwingEngineHelper;
import de.sharebox.user.model.User;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class InvitationController {
	protected OptionPaneHelper optionPane = new OptionPaneHelper();
	private JFrame frame;
	public JTextField mailField;

	public InvitationController() {
		frame = (JFrame) new SwingEngineHelper().render(this, "invite");
		frame.setVisible(true);
	}
	
	
	/**
	 * Handler um auf die Auswahl das "Einladen"-Buttons zu reagieren.
	 */
	public Action invite = new AbstractAction() {
		public void actionPerformed(ActionEvent event) {

			User user = new User();
			user.setEmail(mailField.getText());
			if(UserAPI.getUniqueInstance().inviteUser(UserAPI.getUniqueInstance().getCurrentUser(), user)){
				frame.setVisible(false);
				optionPane.showMessageDialog(mailField.getText() + " wurde eingeladen!");
			}
			else {
				optionPane.showMessageDialog(mailField.getText() + " ist bereits registriert.");
			}
				
			
			
		}
	};
	
}
