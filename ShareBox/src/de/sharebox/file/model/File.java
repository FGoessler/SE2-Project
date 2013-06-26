package de.sharebox.file.model;

public class File extends FEntry {
	private String fileName;

	public void setFileName(String fileName) {
		this.fileName = fileName;

		fireChangeNotification();
	}

	public String getFileName() {
		return fileName;
	}
}
