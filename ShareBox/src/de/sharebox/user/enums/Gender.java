package de.sharebox.user.enums;

/**
 * Dieses Enum dient zum Speichern des Geschlechts eines Nutzers.
 */
public enum Gender {
	Male("Herr"), Female("Frau");

	private final String text;

	private Gender(final String text) {
		this.text = text;
	}

	@Override
	public String toString() {
		return text;
	}
}
