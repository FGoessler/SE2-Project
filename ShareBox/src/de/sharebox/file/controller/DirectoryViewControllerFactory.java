package de.sharebox.file.controller;

import javax.swing.*;

/**
 * Diese Factory dient dazu den DirectoryViewController inkl. ihrer Abhängigkeiten zu erzeugen.<br/>
 * Lediglich eine JTree-Instanz muss direkt gesetzt werden.<br/>
 * Die Implementierung dieses Interface erfolgt automatisch durch Assisted-Inject von Guice.
 */
public interface DirectoryViewControllerFactory {
	/**
	 * Erstellt einen neuen DirectoryViewController. Alle Abhängigkeiten werden dabei von Guice aufgelöst.
	 *
	 * @param treeView Der JTree in dem das Sharebox-Verzeichnis des Nutzers dargestellt werden soll.
	 * @return Ein neuer DirectoryViewController
	 */
	DirectoryViewController create(JTree treeView);
}
