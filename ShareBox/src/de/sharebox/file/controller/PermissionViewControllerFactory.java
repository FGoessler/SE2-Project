package de.sharebox.file.controller;

import javax.swing.*;

/**
 * Diese Factory dient dazu PermissionViewController inkl. ihrer Abhängigkeiten zu erzeugen. Lediglich eine JSplitPane-
 * Instanz muss direkt gesetzt werden.<br/>
 * Die Implementierung dieses Interface geschiet automatisch durch Assisted-Inject von Guice.
 */
public interface PermissionViewControllerFactory {
	/**
	 * Erstellt einen neuen PermissionViewController. Alle Abhängigkeiten werden dabei von Guice aufgelöst.
	 *
	 * @param splitPane Das JSplitPane in deessen rechter Hälfte der Controller seine Inhalte darstellt.
	 * @return Einen neuen PermissionViewController.
	 */
	PermissionViewController create(final JSplitPane splitPane);
}