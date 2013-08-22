package de.sharebox.user.controller;

import de.sharebox.api.UserAPI;
import de.sharebox.helpers.OptionPaneHelper;
import de.sharebox.user.model.User;

import javax.swing.*;

import org.swixml.SwingEngine;

import java.awt.event.ActionEvent;

/**
 * @author Benjamin Barth
 * @author Kay Thorsten Meißner
 */

public class ChangeCredentialsController {
	private JFrame frame;
	public JTextField eMailField;
	public JPasswordField oldPasswordField;
	public JPasswordField newPasswordField;
	public JPasswordField newPasswordField1;
	int i;
	int j; 
	String hilf;
	String hilf2;
	String hilf3;
	protected OptionPaneHelper optionPane = new OptionPaneHelper();


	/*
	 * Öffnen des Login-Daten-bearbeiten Fensters. Hierbei werden alle bereits bekannten Informationen in 
	 * die TextFields direkt mit übernommen. Außerdem wird überprüft,ob bereits Informationen zu den einzelnen Feldern
	 * vorhanden waren oder nicht.
	 */
	
	public ChangeCredentialsController() {
		try {
			SwingEngine swix = new SwingEngine(this);
			frame = (JFrame) swix.render("resources/xml/changeCredentials.xml");
			frame.setVisible(true);
		} catch (Exception exception) {
			System.out.println("Couldn't create change credentials window!");
		}
		
		User user = UserAPI.getUniqueInstance().getCurrentUser();
		eMailField.setText(user.getEmail());
	}
	
	/*
	 * Speichern der Änderungen an den Login Informationen. Für jedes einzelne Feld wird überprüft, ob etwas hinein
	 * geschrieben werden muss oder nicht. Wenn nichts drin stehen muss kann auch beim Speichern nichts drin stehen
	 * Wenn zuvor was drin Stand, dann muss auch wieder eine Information eingetragen werden. 
	 * Hierbei wird weiterhin getestet, ob die eigegeben E-Mail Adresse mit der Login-Adresse übereinstimmt.
	 * Die E-Mail Adresse kann als einziges nicht verändert werden. 
	 * Außerdem wird das alte Passwort überprüft, darauf getestet, dass das neue und das alte Passwort nicht gleich sind
	 * und dass die Passwörter in den neuen Feldern übereinstimmen
	 */
	
	public Action save = new AbstractAction() {
		public void actionPerformed( ActionEvent save ) {
			UserAPI userApi = UserAPI.getUniqueInstance();
			User user = new User();
			
			i = eMailField.getSelectionStart();
			j = eMailField.getSelectionEnd();
			if (i == 0 && j == 0){
				user.setEmail("");
				}
			else
				{
					user.setEmail(eMailField.getText());
				}
			hilf = new String(newPasswordField.getPassword());
			hilf2 = new String(newPasswordField1.getPassword());
			hilf3 = new String(oldPasswordField.getPassword());
			i = newPasswordField.getSelectionStart();
			j = newPasswordField.getSelectionEnd();
			if(hilf.equals(hilf3)){
				user.setPassword("");
			}
			else
				{
					if (hilf.equals(hilf2)){
						if (i == 0 && j == 0){
								user.setPassword("");
							}
						else
							{
								user.setPassword(hilf);
							}
					}
				}
			
			
			User currentUser = userApi.getCurrentUser();
			currentUser.setPassword(hilf3);
			if (currentUser.getEmail().equals(eMailField.getText())){
					if (userApi.changeCredential(currentUser, user)) {
						frame.setVisible(false);
						optionPane.showMessageDialog("Ihre Änderungen wurden gespeichert!");
					}
					else {
						optionPane.showMessageDialog("Das ändern der Daten ist fehlgeschlagen!");
					}
				
			}
			else 
			{
				optionPane.showMessageDialog("Die eingegebene E-Mail Adresse ist falsch!");
			}

		};
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
