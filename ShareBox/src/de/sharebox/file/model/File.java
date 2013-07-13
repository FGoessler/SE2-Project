package de.sharebox.file.model;

/**
 * Diese Klasse repräsentiert eine Datei, die von der Sharebox verwaltet und mit dem Server synchronisiert wird.
 */
public class File extends FEntry {
	/**
	 * Ändert den Dateinamen und benachrichtigt alle Observer über die Änderung.
	 * @param fileName Der neue Dateiname.
	 * @deprecated Verwende stattdessen setName der FEntry Klasse.
	 */
	@Deprecated
	public void setFileName(String fileName) {
		this.setName(fileName);
	}

	/**
	 * Liefert den aktuellen Dateinamen.
	 * @return Der aktuelle Dateiname.
	 * @deprecated Verwende stattdessen getName der FEntry Klasse.
	 */
	@Deprecated
	public String getFileName() {
		return this.getName();
	}
}
