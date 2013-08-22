package de.sharebox.helpers;

import org.swixml.SwingEngine;

import java.awt.*;

/**
 * Diese Klasse kappselt den Standardaufruf der SwingEngine von SWIxml. Somit müssen mögliche Fehler nicht mehr in den
 * Controller Klassen an sich abgefangen werden (zur Laufzeit kann auf eine fehlende/defekte Resource ohnehin nicht mehr
 * regaiert werden).
 */
public class SwingEngineHelper {

	/**
	 * Rendert die gegebene Resource mittels der SwingEngine von SWIxml.
	 *
	 * @param client       Der client, wie normalerweise bei Erstellung der SwingEngine übergeben.
	 * @param resourceName Der Name der Resource die gerendert werden soll. Die Pfadangabe zum Resourceverzeichnis
	 *                     ("resources/xml/") sowie die Dateiendung werden automatisch ergänzt.
	 * @return Die gerenderte Komponente/Container.
	 */
	public Component render(Object client, String resourceName) {
		Component component = null;

		try {
			SwingEngine swix = new SwingEngine(client);
			component = swix.render("resources/xml/" + resourceName + ".xml");
		} catch (Exception exception) {
			System.out.println("Couldn't create " + resourceName);
		}

		return component;
	}
}
