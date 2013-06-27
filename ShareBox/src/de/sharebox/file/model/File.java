package de.sharebox.file.model;

/**
 * Diese Klasse repräsentiert eine Datei, die von der Sharebox verwaltet und mit dem Server synchronisiert wird.
 */
public class File extends FEntry {
	private String fileName;

	/**
	 * Ändert den Dateinamen und benachrichtigt alle Observer über die Änderung.
	 * @param fileName Der neue Dateiname.
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;

		fireChangeNotification();
	}

	/**
	 * Liefert den aktuellen Dateinamen.
	 * @return Der aktuelle Dateiname.
	 */
	public String getFileName() {
		return fileName;
	}
}
