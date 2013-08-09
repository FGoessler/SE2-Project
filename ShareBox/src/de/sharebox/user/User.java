package de.sharebox.user;

/**
*
* @author Benjamin Barth
* @author Kay Thorsten Meißner
*/

public class User {
	
	private String email, password, firstname, lastname, storageLimit, gender;
	private PaymentInfo paymentInfo;

	/**
	 * Der Standard Konstruktor.
	 */
	public User() {
		this.setPaymentInfo(new PaymentInfo());
	}

	/**
	 * Copy Konstruktor.
	 * @param userToCopy Der zu kopierende Nutzer.
	 */
	public User(User userToCopy) {
		this.setEmail(userToCopy.getEmail());
		this.setPassword(userToCopy.getPassword());
		this.setFirstname(userToCopy.getFirstname());
		this.setLastname(userToCopy.getLastname());

		this.setPaymentInfo(new PaymentInfo(userToCopy.getPaymentInfo()));
		
		this.setStorageLimit(userToCopy.getStorageLimit());
		this.setGender(userToCopy.getGender());
		
	}

	/**
	 * 
	 * @return Die Emailadresse des Users.
	 */
	public String getEmail() {
		return email;
	}
	
	/**
	 * 
	 * @return Das Passwort des Users.
	 */
	public String getPassword() {
		return password;
	}
	
	/**
	 * 
	 * @return Den Vornamen des Users.
	 */
	public String getFirstname() {
		return firstname;
	}
	
	/**
	 * 
	 * @return Den Nachnamen des Users.
	 */
	public String getLastname() {
		return lastname;
	}
	
	/**
	 * 
	 * @return Die Zahlungsinformationen des Users.
	 */
	public PaymentInfo getPaymentInfo() {
		return new PaymentInfo(paymentInfo);
	}
	
	/**
	 * 
	 * @return Die Speicherkapazität des Users.
	 */
	public String getStorageLimit() {
		return storageLimit;
	}
	
	/**
	 * 
	 * @return Das Geschlecht des Users.
	 */
	public String getGender() {
		return gender;
	}

	
	/**
	 * Ändert die E-Mailadresse des Objekts. 
	 * @param email 
	 */
	public void setEmail(String email) {
		this.email = email;
	}
	
	/**
	 * Ändert das Passwort des Objekts. 
	 * @param password 
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	
	/**
	 * Ändert den Vornamen des Objekts. 
	 * @param firstname 
	 */
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}
	
	/**
	 * Ändert den Nachnamen des Objekts. 
	 * @param lastname 
	 */
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
	
	/**
	 * Ändert die Zahlungsinformationen des Objekts. 
	 * @param paymentInfo 
	 */
	public void setPaymentInfo(PaymentInfo paymentInfo) {
		this.paymentInfo = new PaymentInfo(paymentInfo);
	}
	
	/**
	 * Ändert die Speicherkapazität des Objekts. 
	 * @param storageLimit 
	 */
	public void setStorageLimit(String storageLimit) {
		this.storageLimit = storageLimit;
	}
	
	/**
	 * Ändert das Geschlecht des Users. 
	 * @param gender
	 */
	public void setGender(String gender) {
		this.gender = gender;
	}
}
