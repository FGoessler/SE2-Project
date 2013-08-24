package de.sharebox.user.model;

import javax.swing.*;

/**
 * Diese Klasse wird im Accounting-Fenster benötigt, um die ComboBox für das
 * zu wählende Speicherlimit zu füllen.
 */
public class StorageLimitComboBoxModel extends DefaultComboBoxModel {

	public StorageLimitComboBoxModel() {
		super(new Object[]{"5GB", "10GB", "20GB", "50GB", "100GB"});
	}
}
