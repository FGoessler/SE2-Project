package de.sharebox.user.model;


import com.google.common.base.Objects;

/**
 * Dieses Objekt speichert Adressinformationen eines Nutzers. Dies wird für die Rechnungsadresse benötigt, die der
 * Benutzer zwingend angeben muss, wenn er mehr als den kostenlosen Default-Speicher nutzen möchte.
 */
public class AddressInfo {
	public static final String EMPTY_STRING = "";

	private String street = EMPTY_STRING, additionalAddressInfo = EMPTY_STRING, zipCode = EMPTY_STRING, city = EMPTY_STRING, country = EMPTY_STRING;

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
		this.setAdditionalAddressInfo(addressInfoToCopy.getAdditionalAddressInfo());
		this.setZipCode(addressInfoToCopy.getZipCode());
		this.setCity(addressInfoToCopy.getCity());
		this.setCountry(addressInfoToCopy.getCountry());

	}

	/**
	 * @return Die Straße und Hausnummer
	 */
	public String getStreet() {
		return street;
	}

	/**
	 * @return Die Adresszusatzinformtionen (zB. "5. Stock bei Müller").
	 */
	public String getAdditionalAddressInfo() {
		return additionalAddressInfo;
	}

	/**
	 * @return Die Postleitzahl
	 */
	public String getZipCode() {
		return zipCode;
	}

	/**
	 * @return Die Stadt
	 */
	public String getCity() {
		return city;
	}

	/**
	 * @return Das Land
	 */
	public String getCountry() {
		return country;
	}

	/**
	 * Ändert die Straße des Objekts.
	 *
	 * @param street Die neue Straße.
	 */
	public void setStreet(final String street) {
		this.street = street;
	}

	/**
	 * Ändert die Adresszusatzinformtionen (zB. "5. Stock bei Müller").
	 *
	 * @param additionalAddressInfo Die neuen Adresszusatzinformationen.
	 */
	public void setAdditionalAddressInfo(final String additionalAddressInfo) {
		this.additionalAddressInfo = additionalAddressInfo;
	}

	/**
	 * Ändert die Postleitzahl.
	 *
	 * @param zipCode Die neue Postleitzahl.
	 */
	public void setZipCode(final String zipCode) {
		this.zipCode = zipCode;
	}

	/**
	 * Ändert die Stadt.
	 *
	 * @param city Die neue Stadt.
	 */
	public void setCity(final String city) {
		this.city = city;
	}

	/**
	 * Ändert das Land.
	 *
	 * @param country Das neue Land.
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
					!Objects.equal(additionalAddressInfo, otherAddressInfo.getAdditionalAddressInfo()) ||
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
		return Objects.hashCode(street, additionalAddressInfo, zipCode, city, country);
	}
}
