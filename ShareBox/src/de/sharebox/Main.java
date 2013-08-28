package de.sharebox;

import com.google.inject.Guice;
import com.google.inject.Injector;
import de.sharebox.api.FileAPI;
import de.sharebox.api.UserAPI;
import de.sharebox.user.controller.LoginController;

/**
 * Diese Main-Klasse erstellt nur das erste Programmfenster.
 */
public final class Main {

	/**
	 * Dies ist der zentrale Injector um erste Objekte mittels Dependency Injection zu erstellen.
	 * (Reglementierung von Abhängigkeiten eines Objekts zur Laufzeit)
	 */
	private static Injector injector = Guice.createInjector(new ShareboxModule());

	private Main() {
	}

	/**
	 * Dies ist die Standard Main-Methode, die beim Programmstart aufgerufen wird und das erste Fenster erzeugt.
	 *
	 * @param args Programmargumente werden für ShareBox nicht benötigt.
	 */
	public static void main(final String[] args) {
		//create sample content
		final UserAPI userAPI = injector.getInstance(UserAPI.class);
		userAPI.createSampleContent();
		injector.getInstance(FileAPI.class).createSampleContent(userAPI);

		injector.getInstance(LoginController.class).show();
	}
}
