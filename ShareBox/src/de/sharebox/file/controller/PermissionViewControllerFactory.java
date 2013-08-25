package de.sharebox.file.controller;

import javax.swing.*;

/**
 * Diese Factory dient dazu den PermissionViewController inkl. ihrer Abhängigkeiten zu erzeugen.<br/>
 * Lediglich eine JSplitPane-Instanz muss direkt gesetzt werden.<br/>
 * Die Implementierung dieses Interface erfolgt automatisch durch Assisted-Inject von Guice.
 */
public interface PermissionViewControllerFactory {
	/**
	 * Erstellt einen neuen PermissionViewController. Alle Abhängigkeiten werden dabei von Guice aufgelöst.
	 *
	 * @param splitPane Das JSplitPane in dessen rechter Hälfte der Controller seine Inhalte darstellt.
	 * @return Einen neuen PermissionViewController
	 */
	PermissionViewController create(final JSplitPane splitPane);
}