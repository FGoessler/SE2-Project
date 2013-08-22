package de.sharebox.user.controller;

import de.sharebox.api.UserAPI;
import de.sharebox.user.model.User;
import de.sharebox.helpers.OptionPaneHelper;
import javax.swing.*;

import org.swixml.SwingEngine;

import java.awt.event.ActionEvent;

/**
 * @author Benjamin Barth
 * @author Kay Thorsten Meißner
 */

public class EditProfileController {
	private JFrame frame;
	public JTextField lastnameField;
	public JTextField firstnameField;
	public JTextField genderField;
	int i;
	int j; 
	int x = 0;
	int y = 0;
	int z = 0;
	protected OptionPaneHelper optionPane = new OptionPaneHelper();

	/*
	 * Öffnen des Profil-bearbeiten Fensters. Hierbei werden alle bereits bekannten Informationen in 
	 * die TextFields direkt mit übernommen. Außerdem wird überprüft,ob bereits Informationen zu den einzelnen Feldern
	 * vorhanden waren oder nicht.
	 */
	
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
		
		i = firstnameField.getSelectionStart();
		j = firstnameField.getSelectionEnd();
		if (i == 0 && j == 0){
			x = 1;
			}
		else 
		{
			x = 0;
		}
		i = lastnameField.getSelectionStart();
		j = lastnameField.getSelectionEnd();
		if (i == 0 && j == 0){
			y = 1;
			}
		else 
		{
			y = 0;
		}
		i = genderField.getSelectionStart();
		j = genderField.getSelectionEnd();
		if (i == 0 && j == 0){
			z = 1;
			}
		else 
		{
			z = 0;
		}
	}
	
	/*
	 * Speichern der Änderungen an den Profil Informationen. Für jedes einzelne Feld wird überprüft, ob etwas hinein
	 * geschrieben werden muss oder nicht. Wenn nichts drin stehen muss kann auch beim Speichern nichts drin stehen
	 * Wenn zuvor was drin Stand, dann muss auch wieder eine Information eingetragen werden. 
	 */
	
	public Action save = new AbstractAction() {
		public void actionPerformed( ActionEvent event ) {
			UserAPI userApi = UserAPI.getUniqueInstance();
			User user = new User();
			
			i = firstnameField.getSelectionStart();
			j = firstnameField.getSelectionEnd();
			if (x == 1)
			{
				user.setFirstname(firstnameField.getText());
			}
			else{
				if (i == 0 && j == 0){
					user.setFirstname("");
					}
				else
					{
						user.setFirstname(firstnameField.getText());
					}
				}
			i = lastnameField.getSelectionStart();
			j = lastnameField.getSelectionEnd();
			if (y == 1)
			{
				user.setLastname(lastnameField.getText());
			}
			else{
				if (i == 0 && j == 0){
					user.setLastname("");
					}
				else
					{
						user.setLastname(lastnameField.getText());
					}
			}
		
			i = genderField.getSelectionStart();
			j = genderField.getSelectionEnd();
			if (z == 1)
			{
				user.setGender(genderField.getText());
			}
			else{
				if (i == 0 && j == 0){
					user.setGender("");
					}
				else
					{
						user.setGender(genderField.getText());
					}
			}

			if (userApi.changeProfile(user)) {
				frame.setVisible(false);
				optionPane.showMessageDialog("Ihre Änderungen wurden gespeichert!");
			}
			else {
				optionPane.showMessageDialog("Die Änderung der Daten ist fehlgeschlagen!");
			}
		}
	};
	
	/*
	 * Ein einfacher Abbrechen Button, der das Fenster schließt und nichts ändert.
	 */
	
	public Action stop = new AbstractAction() {
		public void actionPerformed( ActionEvent event ) {
				frame.setVisible(false);
				optionPane.showMessageDialog("Sie haben den Vorgang abgebrochen!");
		}
	};
}
