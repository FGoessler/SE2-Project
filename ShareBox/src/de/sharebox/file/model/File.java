package de.sharebox.file.model;

/**
 * Diese Klasse repräsentiert eine Datei, die von der Sharebox verwaltet und mit dem Server synchronisiert wird.
 */
public class File extends FEntry {

	/**
	 * Konstruktor
	 */
	public File() {
		super();
	}

	/**
	 * Copy Konstruktor
	 *
	 * @param sourceFile Das Quell-Objekt.
	 */
	public File(File sourceFile) {
		super(sourceFile);
	}

}
