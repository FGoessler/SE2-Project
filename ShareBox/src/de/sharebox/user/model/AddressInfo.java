package de.sharebox.user.model;


import com.google.common.base.Objects;

/**
 * Dieses Objekt speichert Adressinformationen eines Nutzers. Dies wird für die Rechnungsadresse benötigt, die der
 * Benutzer zwingend angeben muss, wenn er mehr als den kostenlosen Default-Speicher nutzen möchte.
 * TODO Getter und Setter vollständig kommentieren.
 */
public class AddressInfo {
	public static final String EMPTY_STRING = "";

	private String street = EMPTY_STRING, additionalStreet = EMPTY_STRING, zipCode = EMPTY_STRING, city = EMPTY_STRING, country = EMPTY_STRING;

	/**
	 * Der Standard-Konstruktor.
	 */
	public AddressInfo() {
		//default constructor
	}

	/**
	 * Copy Konstruktor.
	 *
	 * @param addressInfoToCopy Die zu kopierenden Zahlungsinformationen.
	 */
	public AddressInfo(final AddressInfo addressInfoToCopy) {
		this.setStreet(addressInfoToCopy.getStreet());
		this.setAdditionalStreet(addressInfoToCopy.getAdditionalStreet());
		this.setZipCode(addressInfoToCopy.getZipCode());
		this.setCity(addressInfoToCopy.getCity());
		this.setCountry(addressInfoToCopy.getCountry());

	}

	/**
	 * @return Die Straße und Hausnummer des Objekts.
	 */
	public String getStreet() {
		return street;
	}

	/**
	 * @return Die zusätzlichen Informationen des Objekts.
	 */
	public String getAdditionalStreet() {
		return additionalStreet;
	}

	/**
	 * @return Die Postleitzahl des Objekts.
	 */
	public String getZipCode() {
		return zipCode;
	}

	/**
	 * @return Die Stadt des Objekts.
	 */
	public String getCity() {
		return city;
	}

	/**
	 * @return Das Land des Objekts.
	 */
	public String getCountry() {
		return country;
	}

	/**
	 * Ändert die Straße des Objekts.
	 *
	 * @param street
	 */
	public void setStreet(final String street) {
		this.street = street;
	}

	/**
	 * Ändert die zusätzlichen Informationen des Objekts.
	 *
	 * @param additionalStreet
	 */
	public void setAdditionalStreet(final String additionalStreet) {
		this.additionalStreet = additionalStreet;
	}

	/**
	 * Ändert die Postleitzahl des Objekts.
	 *
	 * @param zipCode
	 */
	public void setZipCode(final String zipCode) {
		this.zipCode = zipCode;
	}

	/**
	 * Ändert die Stadt des Objekts.
	 *
	 * @param city
	 */
	public void setCity(final String city) {
		this.city = city;
	}

	/**
	 * Ändert das Land des Objekts.
	 *
	 * @param country
	 */
	public void setCountry(final String country) {
		this.country = country;
	}

	@Override
	public boolean equals(final Object otherObj) {
		boolean equal = true;

		if (otherObj.getClass().equals(getClass())) {
			final AddressInfo otherAddressInfo = (AddressInfo) otherObj;

			if (!Objects.equal(street, otherAddressInfo.getStreet()) ||
					!Objects.equal(additionalStreet, otherAddressInfo.getAdditionalStreet()) ||
					!Objects.equal(zipCode, otherAddressInfo.getZipCode()) ||
					!Objects.equal(city, otherAddressInfo.getCity()) ||
					!Objects.equal(country, otherAddressInfo.getCountry())) {
				equal = false;
			}
		} else {
			equal = false;
		}

		return equal;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(street, additionalStreet, zipCode, city, country);
	}
}
