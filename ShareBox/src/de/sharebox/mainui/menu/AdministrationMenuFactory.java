package de.sharebox.mainui.menu;

import de.sharebox.mainui.MainViewController;

import javax.swing.*;

/**
 * Diese Factory dient dazu AdminstrationMenu-Objekte inkl. ihrer Abhängigkeiten zu erzeugen.<br/>
 * Lediglich eine JMenuBar-Instanz muss direkt gesetzt werden.<br/>
 * Die Implementierung dieses Interface erfolgt automatisch durch Assisted-Inject von Guice.
 */
public interface AdministrationMenuFactory {
	/**
	 * Erstellt ein neues AdministrationMenu. Alle Abhängigkeiten werden dabei von Guice aufgelöst.
	 *
	 * @param menuBar            Die JMenuBar, zu der das Menü hinzugefügt werden soll.
	 * @param mainViewController Der MainViewController dessen Methoden durch die Aktionen im Menü aufgerufen werden.
	 * @return Ein neues AdministrationMenu.
	 */
	AdministrationMenu create(JMenuBar menuBar, MainViewController mainViewController);
}
