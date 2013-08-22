package de.sharebox.api;

import de.sharebox.user.model.PaymentInfo;
import de.sharebox.user.model.User;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Benjamin Barth
 * @author Kay Thorsten Meißner
 */

public class UserAPI {
	/**
	 * simuliert Datenbank;
	 */
	protected transient List<User> userList = new ArrayList<User>();

	private transient User currentUser;

	private static UserAPI instance = new UserAPI();

	public UserAPI() {
		//createSampleContent();
	}

	/**
	 * Methode um das Singleton-Objekt zu erhalten.
	 *
	 * @return Das Singleton-Objekt.
	 */
	public static UserAPI getUniqueInstance() {
		return instance;
	}

	/**
	 * Setzt die Singleton Instanz von außen. Soll nur in Test Cases verwendet werden, um die die UserAPI zu mocken.
	 *
	 * @param newUserAPI Das neue UserAPI Objekt, das ab sofort beim Aufruf von getUniqueInstance() zurückgegeben wird.
	 */
	public static void injectSingletonInstance(UserAPI newUserAPI) {
		instance = newUserAPI;
	}

	/**
	 * Erstellt Beispiel-Daten, die für Testzwecke benötigt werden.
	 */
	public final void createSampleContent() {

		User user = new User();
		user.setEmail("Max@Mustermann.de");
		user.setPassword("maxmuster");
		user.setFirstname("Max");
		user.setLastname("Mustermann");
		
		PaymentInfo paymentInfo = new PaymentInfo();
		paymentInfo.setStreet("Mustersraße 1");
		paymentInfo.setCity("Musterstadt");
		paymentInfo.setCountry("Deutschland");
		paymentInfo.setZipCode("01234");
		user.setPaymentInfo(paymentInfo);
		
		user.setStorageLimit("10GB");
		user.setGender("m");

		User user2 = new User();
		user2.setEmail("admin");
		user2.setPassword("root");
		user2.setFirstname("Hans");
		user2.setLastname("Kanns");

		paymentInfo.setStreet("");
		paymentInfo.setAdditionalStreet("Haus 4, Zimmer 15");
		paymentInfo.setCity("Berlin");
		paymentInfo.setCountry("Deutschland");
		paymentInfo.setZipCode("14569");
		user2.setPaymentInfo(paymentInfo);
		
		user2.setStorageLimit("20GB");
		user2.setGender("m");

		if (registerUser(user) && registerUser(user2)) {
			APILogger.logMessage("Registered Sampledata");
		} else {
			APILogger.logMessage("Registering Sampledata failed");
		}
	}

	/**
	 * Prüft, ob eine Kombination von E-Mailadresse und Passwort im System enthalten ist.
	 *
	 * @param user zu authentifizierender user
	 * @return ob erfolgreich *
	 */
	public boolean authenticateUser(User user) {
		boolean success = false;
		//search through existing users
		for (User aUser : userList) {
			if ((aUser.getEmail().equals(user.getEmail())) && (aUser.getPassword().equals(user.getPassword()))) {
				success = true;
			}
		}
		if (success) {
			APILogger.logMessage("Authentication successful");
		} else {
			APILogger.logMessage("Authentication failed: User not found");
		}
		return success;
	}

	/**
	 * Versucht, den User einzuloggen, wenn Authentifizierung erfolgreich ist.
	 *
	 * @param user einzulogender user
	 * @return ob erfolgreich *
	 */
	public boolean login(User user) {
		boolean success = false;
		if (authenticateUser(user) && !isLoggedIn()) {
			currentUser = new User(user);
			APILogger.logMessage("Login successful");
			success = true;
		} else {
			if (isLoggedIn()) {
				APILogger.logMessage("Login failed. Please Logout first.");
			} else {
				APILogger.logMessage("Login failed. Username/Password not correct.");
			}
		}
		return success;
	}

	/**
	 * Logt den eingelogten User aus.
	 *
	 * @return ob erfolgreich *
	 */
	public boolean logout() {
		boolean success = false;
		if (currentUser != null) {
			currentUser = null;
			APILogger.logMessage("Logout successful");
			success = true;
		} else {
			APILogger.logMessage("Logout failed: No User loged in");
		}
		return success;
	}


	/**
	 * Erstellt neuen User, sofern noch nicht vorhanden.
	 *
	 * @param user zu registrierender user
	 * @return ob erfolgreich *
	 */
	public  boolean registerUser(User user) {
		Boolean userAlreadyExists = false;
		Boolean success = false;
		
		//search through existing users
		if ((user.getEmail() != "") && (user.getPassword() != "")){
			for (User aUser : userList) {
				if (aUser.getEmail().equals(user.getEmail())) {
					userAlreadyExists = true;
				}
			}
			if (!userAlreadyExists) {
				userList.add(new User(user));		
				success = true;
			}
		}
		else {
			success = false;
		}	
		if (success) {
			APILogger.logMessage("Registration successful");
		} else {
			APILogger.logMessage("Registration failed.");
		}
		return success;
	}


	/**
	 * Ändert Profil-Informationen.
	 *
	 * @param user zu ändernder user
	 * @return ob erfolgreich *
	 */
	public boolean changeProfile(User user) {
		Boolean success = false;
		//search through existing users
		if (currentUser != null) {
			if (user.getFirstname() != "" && user.getGender() != "" && user.getLastname() != ""){
				for (User aUser : userList) {
					if (aUser.getEmail().equals(currentUser.getEmail())) {
						aUser.setFirstname(user.getFirstname());
						aUser.setLastname(user.getLastname());
						aUser.setGender(user.getGender());
						success = true;
						currentUser = new User(aUser);
					}
				}
			}
			else{
				success = false;
			}
		}
		if (success) {
			APILogger.logMessage("Profile updated");
		} else {
			APILogger.logMessage("Profile update failed");
		}
		return success;
	}

	/**
	 * Ändert Zahlungs- und Speicherinformationen
	 *
	 * @param user zu ändernder user
	 * @return ob erfolgreich *
	 */
	public boolean changeAccountingSettings(User user) {
		Boolean success = false;
		//search through existing users
		if (currentUser != null) {
			if(user.getPaymentInfo().getStreet() != "" && user.getPaymentInfo().getCity() != "" && user.getPaymentInfo().getZipCode() != "" && user.getPaymentInfo().getCountry() != ""){
				for (User aUser : userList) {
					if (aUser.getEmail().equals(currentUser.getEmail())) {
						aUser.setPaymentInfo(user.getPaymentInfo());
						aUser.setStorageLimit(user.getStorageLimit());
						success = true;
						currentUser = new User(aUser);
					}
				}
			}
			else{
				success = false;
			}
		}
		if (success) {
			APILogger.logMessage("Accounting settings changed");
		} else {
			APILogger.logMessage("Accounting settings change failed");
		}
		return success;
	}

	/**
	 * Ändert E-Mailadresse und Password
	 *
	 * @param oldUser zu ändernder user
	 * @param newUser zu übernehmende Informationen
	 * @return ob erfolgreich *
	 */
	public boolean changeCredential(User oldUser, User newUser) {
		Boolean success = false;
		//search through existing users
		if (currentUser != null) {
			if(newUser.getEmail() != "" && newUser.getPassword() != ""){
				for (User aUser : userList) {
					if (currentUser.getEmail().equals(oldUser.getEmail()) && currentUser.getPassword().equals(oldUser.getPassword())
							&& aUser.getEmail().equals(oldUser.getEmail()) && aUser.getPassword().equals(oldUser.getPassword())) 
					{
						aUser.setEmail(newUser.getEmail());
						aUser.setPassword(newUser.getPassword());
						success = true;
						currentUser = new User(aUser);
					}
				}
			}
			else{
				success = false;
			}
		}
		if (success) {
			APILogger.logMessage("Credentials changed");
		} else {
			APILogger.logMessage("Credentials change failed");
		}
		return success;
	}

	/**
	 * Lädt neuen Benutzer zu Sharebox ein.
	 *
	 * @param invitingUser werbende User
	 * @param invitedUser  geworbene User
	 * @return ob erfolgreich *
	 */
	public boolean inviteUser(User invitingUser, User invitedUser) {
		Boolean success = true;
		//search through existing users
		if (currentUser != null) {
			if(invitedUser.getEmail() != ""){
				for (User aUser : userList) {
					if (aUser.getEmail().equals(invitedUser.getEmail())) {
						success = false;
					}
				}
			}
			else{
				success = false;
			}
		}
		if (success) {
			APILogger.logMessage("User invited");
		} else {
			APILogger.logMessage("Invitation failed: User already exists");
		}
		return success;
	}

	/**
	 * prüft, ob ein User eingeloggt ist.
	 *
	 * @return ob ein User eingeloggt ist *
	 */
	public boolean isLoggedIn() {
		return currentUser != null;
	}

	/**
	 * gibt eingeloggten User zurück
	 *
	 * @return zur Zeit eingeloggter User *
	 */
	public User getCurrentUser() {
		User user = new User();

		if (isLoggedIn()) {
			for (User aUser : userList) {
				if (aUser.getEmail().equals(currentUser.getEmail())) {
					currentUser = new User (aUser);
				}
			}
			user = new User(currentUser);
			user.setPassword("");
		}

		return user;
	}
	
	 /**
	  * Resettet die Singleton Instanz auf das Standard-Objekt.
	  * Wird benötigt um einen injectSingletonInstance rückgängig zu machen.
	  */
	 public static void resetSingletonInstance() {
	  instance = new UserAPI();
	 }


}
	
