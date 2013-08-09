package de.sharebox.user.controller;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JTextField;

import org.swixml.SwingEngine;

import de.sharebox.Main;
import de.sharebox.api.UserAPI;
import de.sharebox.file.controller.DirectoryViewController;
import de.sharebox.helpers.OptionPaneHelper;
import de.sharebox.user.User;

public class InvitationController {
	protected OptionPaneHelper optionPane = new OptionPaneHelper();
	private DirectoryViewController parentDirectoryController;
	private JFrame frame;
	public JTextField mailField;

	public InvitationController() {
		
		try {
			SwingEngine swix = new SwingEngine(this);
			frame = (JFrame) swix.render("resources/xml/invite.xml");
			frame.setVisible(true);
		} catch (Exception exception) {
			System.out.println("Couldn't create context menu!");
		}
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
