package de.sharebox.helpers;


import javax.swing.*;

/**
 * Diese Klasse ist eine Helferklasse um mittels JOptionPane Dialog-Fenster zu erstellen.
 * Dies wird benötigt um in Unit Tests den Aufruf durch Mocks mit Mockito zu ersetzen und
 * Testdaten auszuliefren, bzw. einen korrekten Aufruf zu gewährleisten.
 * Daher besitzt diese Klasse auch keine eigenen Unit-Test.
 */
public class OptionPaneHelper {

	/**
	 * Erstellt und zeigt einen JOptionPane.showInputDialog() an.
	 *
	 * @param msg         Die angezeigte Meldung.
	 * @param placeholder Der Platzhalterwert des Eingabefeldes.
	 * @return Der String, den der Nutzer eingegeben hat.
	 */
	public String showInputDialog(String msg, String placeholder) {
		return JOptionPane.showInputDialog(msg, placeholder);
	}

	/**
	 * Erstellt und zeigt einen JOptionPane.showMessageDialog()
	 *
	 * @param msg Die angezeigte Meldung.
	 */
	public void showMessageDialog(String msg) {
		JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), msg);
	}
	
	
	
}
