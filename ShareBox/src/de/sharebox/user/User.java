package de.sharebox.user;

/**
*
* @author Benjamin Barth
* @author Kay Thorsten Meißner
*/

public class User {
	
	private String email, password, firstname, lastname, paymentInfo, storageLimit, gender;

	
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
	public String getPaymentInfo() {
		return paymentInfo;
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
	public void setPaymentInfo(String paymentInfo) {
		this.paymentInfo = paymentInfo;
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
	
	/**
	 * kopiert das Object 
	 * @param oldUser 
	 * @return Kopie des Users
	 */
	public User copy(User oldUser){
		User newUser = new User();
		
		newUser.setEmail(oldUser.getEmail());
		newUser.setPassword(oldUser.getPassword());
		newUser.setFirstname(oldUser.getFirstname());
		newUser.setLastname(oldUser.getLastname());
		newUser.setPaymentInfo(oldUser.getPaymentInfo());
		newUser.setStorageLimit(oldUser.getStorageLimit());
		newUser.setGender(oldUser.getGender());
		
		return newUser;
	}
	
}
