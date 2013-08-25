package de.sharebox.helpers;


import javax.swing.*;

/**
 * Diese Klasse ist eine Helferklasse, um Dialog-Fenster mit JOptionPane zu erstellen.<br/>
 * Wird benötigt, um in Unit Tests den Aufruf durch Mocks mit Mockito zu ersetzen und<br/>
 * Testdaten auszuliefern bzw. einen korrekten Aufruf zu gewährleisten.<br/>
 * Deshalb besitzt diese Klasse auch keinen eigenen Unit-Test.
 */
public class OptionPaneHelper {

	/**
	 * Erstellt und zeigt einen JOptionPane InputDialog an.
	 *
	 * @param msg         Die angezeigte Meldung.
	 * @param placeholder Der Platzhalterwert des Eingabefeldes.
	 * @return Der vom Nutzer eingegebene String
	 */
	public String showInputDialog(String msg, String placeholder) {
		return JOptionPane.showInputDialog(msg, placeholder);
	}

	/**
	 * Erstellt und zeigt einen JOptionPane MessageDialog an.
	 *
	 * @param msg Die angezeigte Meldung.
	 */
	public void showMessageDialog(String msg) {
		JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), msg);
	}
}
