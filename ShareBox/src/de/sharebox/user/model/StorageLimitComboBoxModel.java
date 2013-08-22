package de.sharebox.user.model;

import javax.swing.*;

/**
 * Diese Klasse wird im Registrierungs- und Accounting-Fenster benötigt, um die ComboBoxen für das
 * zu wählende Speicherlimit zu füllen.
 */
public class StorageLimitComboBoxModel extends DefaultComboBoxModel {

	public StorageLimitComboBoxModel() {
		super(new Object[]{"5GB", "10GB", "20GB", "50GB", "100GB"});
	}
}
