package de.sharebox.mainui;

import de.sharebox.user.controller.LoginController;
import de.sharebox.user.model.User;

/**
 * Diese Factory dient dazu einen MainViewController inkl. seiner Abhängigkeiten zu erzeugen. Lediglich der aktuelle
 * User muss direkt gesetzt werden.<br/>
 * Die Implementierung dieses Interface geschiet automatisch durch Assisted-Inject von Guice.
 */
public interface MainViewControllerFactory {
	/**
	 * Erstellt einen neuen MainViewController. Alle Abhängigkeiten werden dabei von Guice aufgelöst.
	 *
	 * @param user                   Der aktuell eingeloggte Nutzer, dessen Daten im MainViewController angezeigt werden sollen.
	 * @param callingLoginController Der LoginController der diesen mainViewController erstellt. Wird benötigt um
	 *                               den diesen LoginController wieder anzuzeigen, wenn der Nutzer sich ausloggt.
	 * @return Ein neuer MainViewController.
	 */
	MainViewController create(User user, LoginController callingLoginController);
}
