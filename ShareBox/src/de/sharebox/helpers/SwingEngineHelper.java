package de.sharebox.helpers;

import org.swixml.SwingEngine;

import java.awt.*;

/**
 * Diese Klasse kapselt den Standardaufruf der SwingEngine von SWIxml. Dadurch müssen mögliche Fehler nicht mehr in den<br/>
 * eigentlichen Controller-Klassen abgefangen werden.<br/>
 * (zur Laufzeit kann auf eine fehlende/defekte Resource ohnehin nicht mehr reagiert werden)
 */
public class SwingEngineHelper {

	/**
	 * Rendert die gegebene Resource mittels der SwingEngine von SWIxml.
	 *
	 * @param client       Der Client, der normalerweise bei Erstellung der SwingEngine übergeben wird.
	 * @param resourceName Der Name der Resource die gerendert werden soll. Die Pfadangabe zum Resourceverzeichnis
	 *                     ("resources/xml/") sowie die Dateiendung werden automatisch ergänzt.
	 * @return Die gerenderte Komponente/Container
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
