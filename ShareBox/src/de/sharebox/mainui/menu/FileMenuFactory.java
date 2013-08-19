package de.sharebox.mainui.menu;

import javax.swing.*;

/**
 * Diese Factory dient dazu FileMenu Objekte inkl. ihrer Abhängigkeiten zu erzeugen. Lediglich eine JMenuBar-Instanz
 * muss direkt gesetzt werden.<br/>
 * Die Implementierung dieses Interface geschiet automatisch durch Assisted-Inject von Guice.
 */
public interface FileMenuFactory {
	FileMenu create(JMenuBar menuBar);
}
