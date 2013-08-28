package de.sharebox.user.enums;

/**
  * Die ist das Enum-Objekt f�r die (kaufbaren) Speichergr��en von Sharebox.
  */
public enum StorageLimit {
	GB_5("5 GB"), GB_10("10 GB"), GB_20("20 GB"), GB_50("50 GB"), GB_100("100 GB");

	private final String text;

	private StorageLimit(final String text) {
		this.text = text;
	}

	@Override
	public String toString() {
		return text;
	}
}
