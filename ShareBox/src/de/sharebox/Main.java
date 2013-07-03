package de.sharebox;

import de.sharebox.mainui.MainViewController;
import de.sharebox.user.User;

/**
 * Dies ist die Main-Klasse, die sich nur um das initiale Erstellen des ersten Programfensters kümmert.
 */
public final class Main {

	private Main() {}

	/** Dies ist nur die standard main method, die beim Programstart aufgerufen wird und das erste Fenster erzeugt.
     *
     * @param args Programargumente - nicht genutzt von uns.
     */
    public static void main(String[] args)
    {
        new MainViewController(new User());
    }
}
