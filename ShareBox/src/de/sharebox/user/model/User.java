package de.sharebox.user.model;

import de.sharebox.user.enums.Gender;
import de.sharebox.user.enums.StorageLimit;

/**
 * Dieses Objekt repräsentiert einen Nutzer der Sharebox und enthält alle nötigen Informationen. Teilweise werden im
 * Program aus Datenschutzgründen nur unvollstämdig gefüllte User-Objekte verwendet. Um alle Informationen zu beziehen
 * müssen die entsprechenden Request über die UserAPI durchgeführt werden.
 */
public class User {

	private Long rootDirectoryIdentifier;
	private String email, password, firstname, lastname;
	private Gender gender;
	private StorageLimit storageLimit;
	private AddressInfo addressInfo;

	/**
	 * Der Standard Konstruktor.
	 */
	public User() {
		this.setAddressInfo(new AddressInfo());
	}

	/**
	 * Copy Konstruktor.
	 *
	 * @param userToCopy Der zu kopierende Nutzer.
	 */
	public User(final User userToCopy) {
		this.setEmail(userToCopy.getEmail());
		this.setPassword(userToCopy.getPassword());
		this.setFirstname(userToCopy.getFirstname());
		this.setLastname(userToCopy.getLastname());

		this.setRootDirectoryIdentifier(userToCopy.getRootDirectoryIdentifier());

		this.setAddressInfo(new AddressInfo(userToCopy.getAddressInfo()));

		this.setStorageLimit(userToCopy.getStorageLimit());
		this.setGender(userToCopy.getGender());
	}

	/**
	 * @return Die Emailadresse des Users.
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @return Das Passwort des Users.
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @return Der Vornamen des Users.
	 */
	public String getFirstname() {
		return firstname;
	}

	/**
	 * @return Der Nachname des Users.
	 */
	public String getLastname() {
		return lastname;
	}

	/**
	 * @return Die Adressinformationen des Users für Rechnungen.
	 */
	public AddressInfo getAddressInfo() {
		return new AddressInfo(addressInfo);
	}

	/**
	 * @return Die Speicherkapazität des Users.
	 */
	public StorageLimit getStorageLimit() {
		return storageLimit;
	}

	/**
	 * @return Das Geschlecht des Users.
	 */
	public Gender getGender() {
		return gender;
	}

	/**
	 * Liefert den eindeutigen Identifier des Root-Verzeichnisses des Nutzer. Unterhalb dieses Verzeichnisses befinden
	 * sich alle Dateien und Verzeichnisse des Nutzers.
	 *
	 * @return Der Identifier des Root-Verzeichnisses.
	 */
	public Long getRootDirectoryIdentifier() {
		return rootDirectoryIdentifier;
	}


	/**
	 * Ändert die E-Mailadresse des Objekts.
	 *
	 * @param email Die neue E-Mail-Adresse.
	 */
	public void setEmail(final String email) {
		this.email = email;
	}

	/**
	 * Ändert das Passwort des Objekts.
	 *
	 * @param password Das neue Passwort.
	 */
	public void setPassword(final String password) {
		this.password = password;
	}

	/**
	 * Ändert den Vornamen des Objekts.
	 *
	 * @param firstname Der neue Vorname.
	 */
	public void setFirstname(final String firstname) {
		this.firstname = firstname;
	}

	/**
	 * Ändert den Nachnamen des Objekts.
	 *
	 * @param lastname Der neue Nachname.
	 */
	public void setLastname(final String lastname) {
		this.lastname = lastname;
	}

	/**
	 * Ändert die Zahlungsinformationen des Objekts.
	 *
	 * @param addressInfo Die neuen Adressinformationen.
	 */
	public void setAddressInfo(final AddressInfo addressInfo) {
		this.addressInfo = new AddressInfo(addressInfo);
	}

	/**
	 * Ändert die Speicherkapazität des Objekts.
	 *
	 * @param storageLimit Das neue Speicherlimit des Nutzers.
	 */
	public void setStorageLimit(final StorageLimit storageLimit) {
		this.storageLimit = storageLimit;
	}

	/**
	 * Ändert das Geschlecht des Users
	 *
	 * @param gender Das neue Geschlecht.
	 */
	public void setGender(final Gender gender) {
		this.gender = gender;
	}

	/**
	 * Setzt den eindeutigen Identifier des Root-Verzeichnisses des Nutzer. Unterhalb dieses Verzeichnisses befinden
	 * sich alle Dateien und Verzeichnisse des Nutzers.
	 *
	 * @param rootDirectoryIdentifier Der Identifier des Root-Verzeichnisses.
	 */
	public void setRootDirectoryIdentifier(final Long rootDirectoryIdentifier) {
		this.rootDirectoryIdentifier = rootDirectoryIdentifier;
	}
}
