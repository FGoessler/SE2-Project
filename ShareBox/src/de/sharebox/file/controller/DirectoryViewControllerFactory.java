package de.sharebox.file.controller;

import javax.swing.*;

/**
 * Diese Factory dient zur Erzeugung des DirectoryViewController inkl. seiner Abhängigkeiten.<br/>
 * Lediglich eine JTree-Instanz muss direkt gesetzt werden.<br/>
 * Die Implementierung dieses Interface erfolgt automatisch durch Assisted-Inject von Guice.
 */
public interface DirectoryViewControllerFactory {
	/**
	 * Erstellt einen neuen DirectoryViewController.<br/>Alle Abhängigkeiten werden dabei von Guice aufgelöst.
	 *
	 * @param treeView Der JTree in dem das Sharebox-Verzeichnis des Nutzers dargestellt werden soll.
	 * @return Ein neuer DirectoryViewController
	 */
	DirectoryViewController create(final JTree treeView);
}
