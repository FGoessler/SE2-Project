package de.sharebox.api;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.sharebox.user.model.PaymentInfo;
import de.sharebox.user.model.User;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Strings.isNullOrEmpty;

@Singleton
public class UserAPI {
	/**
	 * simuliert die Datenbank;
	 */
	protected transient List<User> userList = new ArrayList<User>();

	private transient User currentUser;

	/**
	 * Leerer Konstruktor um ein direktes Erstellen zu verhindern. Als Singleton konzipiert.<br/>
	 * Instanzen dieser Klasse sollten nur per Dependecy Injection durch Guice erstellt werden.
	 */
	@Inject
	UserAPI() {
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
	 * @param user zu authentifizierender User
	 * @return ob Authentifizierung erfolgreich war
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
			APILogger.logMessage("Authentication failed: User/Password combination not found.");
		}
		return success;
	}

	/**
	 * Versucht den User einzuloggen, wenn Authentifizierung erfolgreich wae.
	 *
	 * @param user einzuloggender user
	 * @return ob Einloggen erfolgreich war
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
	 * Loggt den eingeloggten User aus.
	 *
	 * @return ob Ausloggen erfolgreich war
	 */
	public boolean logout() {
		boolean success = false;
		if (isLoggedIn()) {
			currentUser = null;
			APILogger.logMessage("Logout successful.");
			success = true;
		} else {
			APILogger.logMessage("Logout failed: No User logged in.");
		}
		return success;
	}


	/**
	 * Erstellt neuen User, sofern noch nicht vorhanden.
	 *
	 * @param user zu registrierender user
	 * @return ob Registrierung erfolgreich war
	 */
	public boolean registerUser(User user) {
		Boolean userAlreadyExists = false;
		Boolean success = false;

		//search through existing users
		if (!isNullOrEmpty(user.getEmail()) && !isNullOrEmpty(user.getPassword())) {
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
	 * @param user zu änderndes User-Profil
	 * @return ob Änderung erfolgreich war
	 */
	public boolean changeProfile(User user) {
		Boolean success = false;
		//search through existing users
		if (currentUser != null &&
				!isNullOrEmpty(user.getFirstname()) && !isNullOrEmpty(user.getGender()) && !isNullOrEmpty(user.getLastname())) {
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
	 * @param user zu änderndes User-Profil
	 * @return ob Änderung erfolgreich war
	 */
	public boolean changeAccountingSettings(User user) {
		Boolean success = false;
		//search through existing users
		if (currentUser != null &&
				!isNullOrEmpty(user.getPaymentInfo().getStreet()) && !isNullOrEmpty(user.getPaymentInfo().getCity()) &&
				!isNullOrEmpty(user.getPaymentInfo().getZipCode()) && !isNullOrEmpty(user.getPaymentInfo().getCountry())) {
			for (User aUser : userList) {
				if (aUser.getEmail().equals(currentUser.getEmail())) {
					aUser.setPaymentInfo(user.getPaymentInfo());
					aUser.setStorageLimit(user.getStorageLimit());
					success = true;
					currentUser = new User(aUser);
				}
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
	 * Ändert E-Mailadresse und Passwort
	 *
	 * @param oldUser zu änderndes User-Profil
	 * @param newUser zu übernehmende Änderung des User-Profils
	 * @return ob Änderung erfolgreich war
	 */
	public boolean changeCredential(User oldUser, User newUser) {
		Boolean success = false;
		//search through existing users
		if (currentUser != null &&
				!isNullOrEmpty(newUser.getEmail()) && !isNullOrEmpty(newUser.getPassword())) {
			for (User aUser : userList) {
				if (currentUser.getEmail().equals(oldUser.getEmail()) && currentUser.getPassword().equals(oldUser.getPassword())
						&& aUser.getEmail().equals(oldUser.getEmail()) && aUser.getPassword().equals(oldUser.getPassword())) {
					aUser.setEmail(newUser.getEmail());
					aUser.setPassword(newUser.getPassword());
					success = true;
					currentUser = new User(aUser);
				}
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
	 * @param invitingUser einladender User
	 * @param invitedUser  eingeladener User
	 * @return ob Einladung erfolgreich war
	 */
	public boolean inviteUser(User invitingUser, User invitedUser) {
		Boolean success = true;
		//search through existing users to test if user already exists
		if (currentUser != null && !isNullOrEmpty(invitedUser.getEmail())) {
			for (User aUser : userList) {
				if (aUser.getEmail().equals(invitedUser.getEmail())) {
					success = false;
				}
			}
		} else {
			success = false;
		}
		if (success) {
			APILogger.logMessage("User '" + invitedUser.getEmail() + "' invited by '" + invitingUser.getEmail() + "'.");
		} else {
			APILogger.logMessage("Invitation failed: User '" + invitedUser.getEmail() + "' already exists");
		}
		return success;
	}

	/**
	 * prüft, ob ein User eingeloggt ist.
	 *
	 * @return ob ein User eingeloggt ist
	 */
	public boolean isLoggedIn() {
		return currentUser != null;
	}

	/**
	 * gibt eingeloggten User zurück
	 *
	 * @return zur Zeit eingeloggter User
	 */
	public User getCurrentUser() {
		User user = new User();

		if (isLoggedIn()) {
			for (User aUser : userList) {
				if (aUser.getEmail().equals(currentUser.getEmail())) {
					currentUser = new User(aUser);
				}
			}
			user = new User(currentUser);
			user.setPassword("");
		}

		return user;
	}
}