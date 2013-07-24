package de.sharebox.user;

/**
*
* @author Benjamin Barth
* @author Kay Thorsten Mei�ner
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
	 * @return Die Speicherkapazit�t des Users.
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
	 * �ndert die E-Mailadresse des Objekts. 
	 * @param email 
	 */
	public void setEmail(String email) {
		this.email = email;
	}
	
	/**
	 * �ndert das Passwort des Objekts. 
	 * @param password 
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	
	/**
	 * �ndert den Vornamen des Objekts. 
	 * @param firstname 
	 */
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}
	
	/**
	 * �ndert den Nachnamen des Objekts. 
	 * @param lastname 
	 */
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
	
	/**
	 * �ndert die Zahlungsinformationen des Objekts. 
	 * @param paymentInfo 
	 */
	public void setPaymentInfo(String paymentInfo) {
		this.paymentInfo = paymentInfo;
	}
	
	/**
	 * �ndert die Speicherkapazit�t des Objekts. 
	 * @param storageLimit 
	 */
	public void setStorageLimit(String storageLimit) {
		this.storageLimit = storageLimit;
	}
	
	/**
	 * �ndert das Geschlecht des Users. 
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
