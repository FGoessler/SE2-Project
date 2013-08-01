package de.sharebox.file.model;

/**
 * Diese Klasse repräsentiert eine Datei, die von der Sharebox verwaltet und mit dem Server synchronisiert wird.
 */
public class File extends FEntry {

	/**
	 * Konstruktor
	 */
	public File() {
	}

	/**
	 * Copy Konstruktor
	 *
	 * @param sourceFile Das Quell-Objekt.
	 */
	public File(File sourceFile) {
		File newFile = new File();
		newFile.setIdentifier(sourceFile.getIdentifier());
		newFile.setName(sourceFile.getName());
	}
}
