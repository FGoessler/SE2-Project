package de.sharebox.helpers;

import javax.swing.*;

/**
 * Diese Klasse wird im Register-window benutzt, um die ComboBox zu füllen. 
 * Sie benötigt daher keinen Test.
 */
public class ComboModelHelper extends DefaultComboBoxModel {
 
  public ComboModelHelper() {
    super( new Object[]{"5GB", "10GB", "20GB", "50GB", "100GB"} );
  }
}
