package de.sharebox.user.model;

import de.sharebox.user.enums.StorageLimit;

import javax.swing.*;

/**
 * Diese Klasse wird im Accounting-Fenster benötigt, um die ComboBox für das
 * zu wählende Speicherlimit zu füllen.
 */
public class StorageLimitComboBoxModel extends DefaultComboBoxModel<StorageLimit> {
	public StorageLimitComboBoxModel() {
		super(StorageLimit.values());
	}
}
