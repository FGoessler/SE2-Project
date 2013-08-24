package de.sharebox.api;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.sharebox.user.model.AddressInfo;
import de.sharebox.user.model.User;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Strings.isNullOrEmpty;

@Singleton
public class UserAPI {
	/**
	 * simuliert Datenbank;
	 */
	protected transient List<User> userList = new ArrayList<User>();

	private transient User currentUser;

	/**
	 * Leerer Konstruktor um ein direktes erstellen zu verhindern. Als Singleton konzipiert.<br/>
	 * Instanzen dieser Klasse sollte nur per Dependecy Injection durch Guice erstellt werden.
	 */
	@Inject
	UserAPI() {
		//empty constructor to avoid direct instantiation!
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

		AddressInfo addressInfo = new AddressInfo();
		addressInfo.setStreet("Mustersraße 1");
		addressInfo.setCity("Musterstadt");
		addressInfo.setCountry("Deutschland");
		addressInfo.setZipCode("01234");
		user.setAddressInfo(addressInfo);

		user.setStorageLimit("10GB");
		user.setGender("m");

		User user2 = new User();
		user2.setEmail("admin");
		user2.setPassword("root");
		user2.setFirstname("Hans");
		user2.setLastname("Kanns");

		addressInfo.setStreet("");
		addressInfo.setAdditionalStreet("Haus 4, Zimmer 15");
		addressInfo.setCity("Berlin");
		addressInfo.setCountry("Deutschland");
		addressInfo.setZipCode("14569");
		user2.setAddressInfo(addressInfo);

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
			APILogger.logSuccess("Authentication");
		} else {
			APILogger.logFailure("Authentication", "User/Password combination not found.");
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
			APILogger.logSuccess("Login");
			success = true;
		} else {
			if (isLoggedIn()) {
				APILogger.logFailure("Login", "Please Logout first.");
			} else {
				APILogger.logFailure("Login", "Username/Password not correct.");
			}
		}
		return success;
	}

	/**
	 * Loggt den eingelogten User aus.
	 *
	 * @return ob erfolgreich *
	 */
	public boolean logout() {
		boolean success = false;
		if (isLoggedIn()) {
			currentUser = null;
			APILogger.logSuccess("Logout");
			success = true;
		} else {
			APILogger.logFailure("Logout", "No User logged in.");
		}
		return success;
	}


	/**
	 * Erstellt neuen User, sofern noch nicht vorhanden.
	 *
	 * @param user zu registrierender user
	 * @return ob erfolgreich *
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
			APILogger.logSuccess("Registration");
		} else {
			APILogger.logFailure("Registration");
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
			APILogger.logSuccess("Profile updated");
		} else {
			APILogger.logFailure("Profile update");
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
		if (currentUser != null &&
				!isNullOrEmpty(user.getAddressInfo().getStreet()) && !isNullOrEmpty(user.getAddressInfo().getCity()) &&
				!isNullOrEmpty(user.getAddressInfo().getZipCode()) && !isNullOrEmpty(user.getAddressInfo().getCountry())) {
			for (User aUser : userList) {
				if (aUser.getEmail().equals(currentUser.getEmail())) {
					aUser.setAddressInfo(user.getAddressInfo());
					aUser.setStorageLimit(user.getStorageLimit());
					success = true;
					currentUser = new User(aUser);
				}
			}
		}
		if (success) {
			APILogger.logSuccess("Accounting settings changed");
		} else {
			APILogger.logFailure("Accounting settings change");
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
			APILogger.logSuccess("Credentials changed");
		} else {
			APILogger.logFailure("Credentials change");
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
		//search through existing users to test if user already exists
		if (currentUser == null || isNullOrEmpty(invitedUser.getEmail())) {
			success = false;
		} else {
			for (User aUser : userList) {
				if (aUser.getEmail().equals(invitedUser.getEmail())) {
					success = false;
				}
			}
		}
		if (success) {
			APILogger.logMessage("User '" + invitedUser.getEmail() + "' invited by '" + invitingUser.getEmail() + "'.");
		} else {
			APILogger.logFailure("Invitation", "User '" + invitedUser.getEmail() + "' already exists.");
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
					currentUser = new User(aUser);
				}
			}
			user = new User(currentUser);
			user.setPassword("");
		}

		return user;
	}
}