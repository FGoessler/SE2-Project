package de.sharebox.user;


/**
*
* @author Benjamin Barth
*/


public class PaymentInfo {
	
	private String street, additionalStreet, zipCode, city, country;

	/**
	 * Copy Konstruktor.
	 * @param PaymentInfoToCopy Die zu kopierenden Zahlungsinformationen.
	 */
	public PaymentInfo(PaymentInfo PaymentInfoToCopy) {
		this.setStreet(PaymentInfoToCopy.getStreet());
		this.setAdditionalStreet(PaymentInfoToCopy.getAdditionalStreet());
		this.setZipCode(PaymentInfoToCopy.getZipCode());
		this.setCity(PaymentInfoToCopy.getCity());
		this.setCountry(PaymentInfoToCopy.getCountry());
		
	}
	
	public PaymentInfo() {
		this.setStreet("");
		this.setAdditionalStreet("");
		this.setZipCode("");
		this.setCity("");
		this.setCountry("");
	}
	
	/**
	 * 
	 * @return Die Straße und Hausnummer des Objekts.
	 */
	public String getStreet() {
		return street;
	}
	
	/**
	 * 
	 * @return Die zusätzlichen Informationen des Objekts.
	 */
	public String getAdditionalStreet() {
		return additionalStreet;
	}
	
	/**
	 * 
	 * @return Die Postleitzahl des Objekts.
	 */
	public String getZipCode() {
		return zipCode;
	}
	
	/**
	 * 
	 * @return Die Stadt des Objekts.
	 */
	public String getCity() {
		return city;
	}
	
	/**
	 * 
	 * @return Das Land des Objekts.
	 */
	public String getCountry() {
		return country;
	}
	
	/**
	 * Ändert die Straße des Objekts. 
	 * @param street 
	 */
	public void setStreet(String street) {
		this.street = street;
	}
	
	/**
	 * Ändert die zusätzlichen Informationen des Objekts. 
	 * @param additionalStreet 
	 */
	public void setAdditionalStreet(String additionalStreet) {
		this.additionalStreet = additionalStreet;
	}
	
	/**
	 * Ändert die Postleitzahl des Objekts. 
	 * @param zipCode 
	 */
	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}
	
	/**
	 * Ändert die Stadt des Objekts. 
	 * @param city 
	 */
	public void setCity(String city) {
		this.city = city;
	}
	
	/**
	 * Ändert das Land des Objekts. 
	 * @param country 
	 */
	public void setCountry(String country) {
		this.country = country;
	}
	
	/**
	 * Überprüft, ob die objekte den gleichen Inhalt haben 
	 * @param o 
	 */
	@Override
	public boolean equals(Object o){
		boolean equal = true;
		PaymentInfo otherPaymentInfo;
		
		if (o instanceof PaymentInfo){
			
			otherPaymentInfo = (PaymentInfo) o;
			
			if (!this.street.equals(otherPaymentInfo.getStreet())){
				equal = false;
			}
			if (!this.additionalStreet.equals(otherPaymentInfo.getAdditionalStreet())){
				equal = false;
			}
			if (!this.zipCode.equals(otherPaymentInfo.getZipCode())){
				equal = false;
			}
			if (!this.city.equals(otherPaymentInfo.getCity())){
				equal = false;
			}
			if (!this.country.equals(otherPaymentInfo.getCountry())){
				equal = false;
			}
		}
		else{
			equal = false;
		}
		
		return equal;
	}

}
