package de.sharebox.user.model;

import de.sharebox.user.enums.Gender;

import javax.swing.*;

/**
 * Diese Klasse wird im Register- und EditProfile-Fenster benötigt, um die ComboBox zur Auswahl des Geschlechts zu füllen.
 */
public class GenderComboBoxModel extends DefaultComboBoxModel<Gender> {
	public GenderComboBoxModel() {
		super(Gender.values());
	}
}
