package de.sharebox;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JTextField;

import de.sharebox.api.UserAPI;
import de.sharebox.user.User;

public class Zahlungsinformationen�ndernWindow {

	 public transient JTextField storageLimitField;
	 public transient JTextField paymentinfoField;

	 public Action save = new AbstractAction() {
			public void actionPerformed( ActionEvent event ) {
	            UserAPI userApi = UserAPI.getUniqueInstance();
	            User user = new User();
	            user.setStorageLimit(storageLimitField.getText());
	            user.setPaymentInfo(paymentinfoField.getText());

	            
	            if (userApi.changeAccountingSettings(user)) {
	            	System.out.println("Die �nderung war erfolgreich");
	            }
	            else {
	            	System.out.println("Das �ndern der Daten ist fehlgeschlagen!");
	            }  
	            // Fenster schlie�en
	        }
	    };
	
	public Zahlungsinformationen�ndernWindow() {
		// TODO Auto-generated constructor stub
	}

}
