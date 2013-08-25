package de.sharebox.user.model;

import de.sharebox.user.enums.Gender;
import de.sharebox.user.enums.StorageLimit;

/**
 * @author Benjamin Barth
 * @author Kay Thorsten Meißner
 */

public class User {

	private Integer rootDirectoryIdentifier;
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
	 * @return Den Vornamen des Users.
	 */
	public String getFirstname() {
		return firstname;
	}

	/**
	 * @return Den Nachnamen des Users.
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

	public Integer getRootDirectoryIdentifier() {
		return rootDirectoryIdentifier;
	}

	/**
	 * Ändert die E-Mailadresse des Objekts.
	 *
	 * @param email
	 */
	public void setEmail(final String email) {
		this.email = email;
	}

	/**
	 * Ändert das Passwort des Objekts.
	 *
	 * @param password
	 */
	public void setPassword(final String password) {
		this.password = password;
	}

	/**
	 * Ändert den Vornamen des Objekts.
	 *
	 * @param firstname
	 */
	public void setFirstname(final String firstname) {
		this.firstname = firstname;
	}

	/**
	 * Ändert den Nachnamen des Objekts.
	 *
	 * @param lastname
	 */
	public void setLastname(final String lastname) {
		this.lastname = lastname;
	}

	/**
	 * Ändert die Zahlungsinformationen des Objekts.
	 *
	 * @param addressInfo
	 */
	public void setAddressInfo(final AddressInfo addressInfo) {
		this.addressInfo = new AddressInfo(addressInfo);
	}

	/**
	 * Ändert die Speicherkapazität des Objekts.
	 *
	 * @param storageLimit
	 */
	public void setStorageLimit(final StorageLimit storageLimit) {
		this.storageLimit = storageLimit;
	}

	/**
	 * Ändert das Geschlecht des Users
	 *
	 * @param gender
	 */
	public void setGender(final Gender gender) {
		this.gender = gender;
	}

	public void setRootDirectoryIdentifier(Integer rootDirectoryIdentifier) {
		this.rootDirectoryIdentifier = rootDirectoryIdentifier;
	}
}
