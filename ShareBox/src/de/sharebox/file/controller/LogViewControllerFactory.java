package de.sharebox.file.controller;

import javax.swing.*;

/**
 * Diese Factory dient der Erzeugung von LogViewController inkl. ihrer Abhängigkeiten. Lediglich eine JSplitPane-
 * Instanz muss direkt gesetzt werden.<br/>
 * Die Implementierung dieses Interface geschiet automatisch durch Assisted-Inject von Guice.
 */
public interface LogViewControllerFactory {
	/**
	 * Erstellt einen neuen LogViewController. Alle Abhängigkeiten werden dabei von Guice aufgelöst.
	 *
	 * @param splitPane Das JSplitPane in deessen linker/oberer Hälfte der Controller seine Inhalte darstellt.
	 * @return Einen neuen LogViewController.
	 */
	LogViewController create(final JSplitPane splitPane);
}
