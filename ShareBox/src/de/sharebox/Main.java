package de.sharebox;

import com.google.inject.Guice;
import com.google.inject.Injector;
import de.sharebox.api.FileAPI;
import de.sharebox.api.UserAPI;
import de.sharebox.user.controller.LoginController;

/**
 * Dies ist die Main-Klasse, die sich nur um das initiale Erstellen des ersten Programfensters kümmert.
 */
public final class Main {

	/**
	 * Dies ist der zentrale Injector um erste Objekte mittels Dependency Injection zu erstellen.
	 */
	private static Injector injector = Guice.createInjector(new ShareboxModule());

	private Main() {
	}

	/**
	 * Dies ist die standard main-method, die beim Programstart aufgerufen wird und das erste Fenster erzeugt.
	 *
	 * @param args Programargumente - von uns nicht genutzt.
	 */
	public static void main(final String[] args) {
		//create sample content
		final UserAPI userAPI = injector.getInstance(UserAPI.class);
		userAPI.createSampleContent();
		injector.getInstance(FileAPI.class).createSampleContent(userAPI);

		injector.getInstance(LoginController.class).show();
	}
}
