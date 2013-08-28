package de.sharebox.api;

import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.sharebox.file.model.Directory;
import de.sharebox.user.enums.Gender;
import de.sharebox.user.enums.StorageLimit;
import de.sharebox.user.model.AddressInfo;
import de.sharebox.user.model.User;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Strings.isNullOrEmpty;

/*
 * TODO Klassenbeschreibung (Die Klasse UserAPI kappselt die Zugriffsmethoden auf die User-Daten? ...)
 */
@Singleton
public class UserAPI {
	private final FileAPI fileAPI;

	/**
	 * Eine simulierte Datenbank.
	 */
	private final List<User> userList = new ArrayList<User>();

	private User currentUser;

	/**
	 * Leerer Konstruktor um ein direktes Erstellen zu verhindern. Als Singleton konzipiert.<br/>
	 * Instanzen dieser Klasse sollten nur per Dependecy Injection durch Guice erstellt werden.
	 */
	@Inject
	UserAPI(final FileAPI fileAPI) {
		this.fileAPI = fileAPI;
	}

	/**
	 * Erstellt Beispieldaten, die für Testzwecke benötigt werden.
	 */
	public final void createSampleContent() {

		final User user1 = new User();
		user1.setEmail("Max@Mustermann.de");
		user1.setPassword("maxmuster");
		user1.setFirstname("Max");
		user1.setLastname("Mustermann");
		user1.setRootDirectoryIdentifier(0L);

		final AddressInfo addressInfo = new AddressInfo();
		addressInfo.setStreet("Mustersraße 1");
		addressInfo.setCity("Musterstadt");
		addressInfo.setCountry("Deutschland");
		addressInfo.setZipCode("01234");
		user1.setAddressInfo(addressInfo);

		user1.setStorageLimit(StorageLimit.GB_10);
		user1.setGender(Gender.Male);

		final User user2 = new User();
		user2.setEmail("admin");
		user2.setPassword("root");
		user2.setFirstname("Hans");
		user2.setLastname("Kanns");
		user2.setRootDirectoryIdentifier(2L);

		addressInfo.setStreet("");
		addressInfo.setAdditionalStreet("Haus 4, Zimmer 15");
		addressInfo.setCity("Berlin");
		addressInfo.setCountry("Deutschland");
		addressInfo.setZipCode("14569");
		user2.setAddressInfo(addressInfo);

		user2.setStorageLimit(StorageLimit.GB_20);
		user2.setGender(Gender.Male);

		userList.add(user1);
		userList.add(user2);

		APILogger.logMessage("Registered Sampledata");
	}

	/**
	 * Prüft, ob eine Kombination von E-Mailadresse und Passwort im System enthalten ist.
	 *
	 * @param user zu authentifizierender User
	 * @return ob die Authentifizierung erfolgreich war
	 */
	public boolean authenticateUser(final User user) {
		boolean success = false;

		final Optional<User> foundUser = getUserWithMail(user.getEmail());
		if (foundUser.isPresent() && foundUser.get().getPassword().equals(user.getPassword())) {
			success = true;
		}

		if (success) {
			APILogger.logSuccess("Authentication");
		} else {
			APILogger.logFailure("Authentication", "User/Password combination not found.");
		}
		return success;
	}

	/**
	 * Versucht den User einzuloggen, wenn Authentifizierung erfolgreich war.
	 *
	 * @param user einzuloggender User
	 * @return ob das Einloggen erfolgreich war
	 */
	public boolean login(final User user) {
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
	 * Loggt den eingeloggten User aus.
	 *
	 * @return ob das Ausloggen erfolgreich war
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
	 * Erstellt einen neuen User, sofern noch nicht vorhanden.
	 *
	 * @param user zu registrierender User
	 * @return ob die Registrierung erfolgreich war
	 */
	public boolean registerUser(final User user) {
		Boolean success = false;

		if (!isNullOrEmpty(user.getEmail())
				&& !isNullOrEmpty(user.getPassword())
				&& !getUserWithMail(user.getEmail()).isPresent()) {

			//create new root directory for user
			final Directory rootDir = new Directory(this, "Sharebox", user);
			fileAPI.createNewFEntry(rootDir);
			user.setRootDirectoryIdentifier(rootDir.getIdentifier());

			userList.add(new User(user));
			success = true;
		}

		APILogger.logResult("Registration", success);

		return success;
	}


	/**
	 * Ändert Profil-Informationen.
	 *
	 * @param user zu änderndes User-Profil
	 * @return ob die Änderung erfolgreich war
	 */
	public boolean changeProfile(final User user) {
		Boolean success = false;

		if (currentUser != null
				&& !isNullOrEmpty(user.getFirstname())
				&& user.getGender() != null
				&& !isNullOrEmpty(user.getLastname())) {

			final Optional<User> foundUser = getUserWithMail(currentUser.getEmail());
			if (foundUser.isPresent()) {
				foundUser.get().setFirstname(user.getFirstname());
				foundUser.get().setLastname(user.getLastname());
				foundUser.get().setGender(user.getGender());
				success = true;
				currentUser = new User(foundUser.get());
			}
		}

		APILogger.logResult("Profile updated", success);

		return success;
	}

	/**
	 * Ändert Zahlungs- und Speicherinformationen.
	 *
	 * @param user zu änderndes User-Profil
	 * @return ob die Änderung erfolgreich war
	 */
	public boolean changeAccountingSettings(final User user) {
		Boolean success = false;

		if (currentUser != null
				&& !isNullOrEmpty(user.getAddressInfo().getStreet()) && !isNullOrEmpty(user.getAddressInfo().getCity())
				&& !isNullOrEmpty(user.getAddressInfo().getZipCode()) && !isNullOrEmpty(user.getAddressInfo().getCountry())) {
			final Optional<User> foundUser = getUserWithMail(currentUser.getEmail());
			if (foundUser.isPresent()) {
				foundUser.get().setAddressInfo(user.getAddressInfo());
				foundUser.get().setStorageLimit(user.getStorageLimit());
				success = true;
				currentUser = new User(foundUser.get());
			}
		}

		APILogger.logResult("Accounting settings changed", success);

		return success;
	}

	/**
	 * Ändert E-Mail-Adresse und Passwort.
	 *
	 * @param oldUser zu änderndes User-Profil
	 * @param newUser zu übernehmende Änderung des User-Profils
	 * @return ob die Änderung erfolgreich war
	 */
	public boolean changeCredential(final User oldUser, final User newUser) {
		Boolean success = false;
		//search through existing users
		if (currentUser != null
				&& !isNullOrEmpty(newUser.getEmail())
				&& !isNullOrEmpty(newUser.getPassword())
				&& currentUser.getPassword().equals(oldUser.getPassword())
				&& currentUser.getEmail().equals(oldUser.getEmail())) {

			final Optional<User> foundUser = getUserWithMail(oldUser.getEmail());
			if (foundUser.isPresent() && foundUser.get().getPassword().equals(oldUser.getPassword())) {
				foundUser.get().setEmail(newUser.getEmail());
				foundUser.get().setPassword(newUser.getPassword());
				success = true;
				currentUser = new User(foundUser.get());
			}
		}

		APILogger.logResult("Credentials changed", success);

		return success;
	}

	/**
	 * Lädt neuen Benutzer zu Sharebox ein.
	 *
	 * @param invitingUser einladender User
	 * @param invitedUser  eingeladener User
	 * @return ob die Einladung erfolgreich war
	 */
	public boolean inviteUser(final User invitingUser, final User invitedUser) {
		Boolean success = true;
		//search through existing users to test if user already exists
		if (currentUser == null || isNullOrEmpty(invitedUser.getEmail())) {
			success = false;
		} else {
			if (getUserWithMail(invitedUser.getEmail()).isPresent()) {
				success = false;
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
	 * Prüft, ob ein User eingeloggt ist.
	 *
	 * @return ob ein User eingeloggt ist
	 */
	public boolean isLoggedIn() {
		return currentUser != null;
	}

	/**
	 * Gibt dem eingeloggten User zurück.
	 *
	 * @return zur Zeit eingeloggter User
	 */
	public User getCurrentUser() {
		User user = null;

		if (isLoggedIn()) {
			final Optional<User> foundUser = getUserWithMail(currentUser.getEmail());
			if (foundUser.isPresent()) {
				currentUser = new User(foundUser.get());
			}
			user = new User(currentUser);
			user.setPassword("");
		}

		return user;
	}

	private Optional<User> getUserWithMail(final String mail) {
		Optional<User> foundUser = Optional.absent();

		for (final User aUser : userList) {
			if (aUser.getEmail().equals(mail)) {
				foundUser = Optional.of(aUser);
				break;
			}
		}

		return foundUser;
	}
}