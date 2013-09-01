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

/**
 * Die UserAPI ist verantwortlich für die Kommunikation mit dem Server um Änderungen und Aktionen, die User-Objekte
 * betreffen, druchzuführen.
 * <br/><br/>
 * Für die Zwecke dieses Prototypen werden keine echten Request abgesetzt und nur über den APILogger Meldungen
 * ausgegeben, sowie die Daten lokal im Objekt gespeichert.
 */
@Singleton
public class UserAPI {
	private final FileAPI fileAPI;

	/**
	 * Eine simulierte Datenbank.
	 */
	private final List<User> userList = new ArrayList<User>();

	private Optional<User> currentUser = Optional.absent();

	/**
	 * Leerer Konstruktor um ein direktes Erstellen zu verhindern. Als Singleton konzipiert.<br/>
	 * Instanzen dieser Klasse sollten nur per Dependecy Injection durch Guice erstellt werden.
	 */
	@Inject
	UserAPI(final FileAPI fileAPI) {
		this.fileAPI = fileAPI;
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
			currentUser = Optional.of(new User(user));
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
			currentUser = Optional.absent();
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
			rootDir.setIdentifier(fileAPI.createNewFEntry(rootDir));
			user.setRootDirectoryIdentifier(rootDir.getIdentifier());

			userList.add(new User(user));
			success = true;

			APILogger.logMessage("Created User: " + user.getEmail() + " with password: " + user.getPassword());
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

		if (isLoggedIn()
				&& !isNullOrEmpty(user.getFirstname())
				&& user.getGender() != null
				&& !isNullOrEmpty(user.getLastname())) {

			final Optional<User> foundUser = getUserWithMail(currentUser.get().getEmail());
			if (foundUser.isPresent()) {
				foundUser.get().setFirstname(user.getFirstname());
				foundUser.get().setLastname(user.getLastname());
				foundUser.get().setGender(user.getGender());
				success = true;
				currentUser = Optional.of(new User(foundUser.get()));
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

		if (isLoggedIn()
				&& !isNullOrEmpty(user.getAddressInfo().getStreet()) && !isNullOrEmpty(user.getAddressInfo().getCity())
				&& !isNullOrEmpty(user.getAddressInfo().getZipCode()) && !isNullOrEmpty(user.getAddressInfo().getCountry())) {
			final Optional<User> foundUser = getUserWithMail(currentUser.get().getEmail());
			if (foundUser.isPresent()) {
				foundUser.get().setAddressInfo(user.getAddressInfo());
				foundUser.get().setStorageLimit(user.getStorageLimit());
				success = true;
				currentUser = Optional.of(new User(foundUser.get()));
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
		if (isLoggedIn()
				&& !isNullOrEmpty(newUser.getEmail())
				&& !isNullOrEmpty(newUser.getPassword())
				&& currentUser.get().getPassword().equals(oldUser.getPassword())
				&& currentUser.get().getEmail().equals(oldUser.getEmail())) {

			final Optional<User> foundUser = getUserWithMail(oldUser.getEmail());
			if (foundUser.isPresent() && foundUser.get().getPassword().equals(oldUser.getPassword())) {
				foundUser.get().setEmail(newUser.getEmail());
				foundUser.get().setPassword(newUser.getPassword());
				success = true;
				currentUser = Optional.of(new User(foundUser.get()));
			}
		}

		APILogger.logResult("Credentials changed", success);

		return success;
	}

	/**
	 * Lädt neuen Benutzer zu Sharebox Ultimate ein.
	 *
	 * @param invitingUser einladender User
	 * @param invitedUser  eingeladener User
	 * @return ob die Einladung erfolgreich war
	 */
	public boolean inviteUser(final User invitingUser, final User invitedUser) {
		Boolean success = true;
		//search through existing users to test if user already exists
		if (!isLoggedIn() || isNullOrEmpty(invitedUser.getEmail())) {
			success = false;
		} else {
			if (getUserWithMail(invitedUser.getEmail()).isPresent()) {
				success = false;
			}
		}
		if (success) {
			invitedUser.setPassword("PW" + Math.random());
			registerUser(invitedUser);
			APILogger.logMessage("User '" + invitedUser.getEmail() + "' invited by '" + invitingUser.getEmail() + "'.");
		} else {
			APILogger.logFailure("Invitation", "User '" + invitedUser.getEmail() + "' already exists.");
		}
		return success;
	}

	/**
	 * Liefert die ID des Hauptverzeichnises des Nutzers. Mit dieser ID kann das entsprechende Directory-Objekt bei der
	 * FileAPI angefragt werden.
	 *
	 * @param user Der User dessen Hauptverzeichnises angefragt wird.
	 * @return Die ID des Hauptverzeichnises des Nutzers.
	 */
	public Long getRootDirIDOfUser(final User user) {
		return getUserWithMail(user.getEmail()).get().getRootDirectoryIdentifier();
	}


	/**
	 * Prüft, ob ein User eingeloggt ist.
	 *
	 * @return ob ein User eingeloggt ist
	 */
	public boolean isLoggedIn() {
		return currentUser.isPresent();
	}

	/**
	 * Gibt dem eingeloggten User zurück.
	 *
	 * @return zur Zeit eingeloggter User
	 */
	public User getCurrentUser() {
		User user = null;

		if (isLoggedIn()) {
			final Optional<User> foundUser = getUserWithMail(currentUser.get().getEmail());
			if (foundUser.isPresent()) {
				currentUser = Optional.of(new User(foundUser.get()));
			}
			user = new User(currentUser.get());
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


	/**
	 * Erstellt Beispieldaten, die zum Testen des Protypen verwendet werden können.
	 */
	public final void createSampleContent() {
		final User user = new User();
		user.setEmail("admin@test.de");
		user.setPassword("root");
		user.setFirstname("Hans");
		user.setLastname("Kanns");
		user.setRootDirectoryIdentifier(2L);

		final AddressInfo addressInfo = new AddressInfo();
		addressInfo.setStreet("Meierstraße 5");
		addressInfo.setAdditionalAddressInfo("Haus 4, Zimmer 15");
		addressInfo.setCity("Berlin");
		addressInfo.setCountry("Deutschland");
		addressInfo.setZipCode("14569");
		user.setAddressInfo(addressInfo);

		user.setStorageLimit(StorageLimit.GB_20);
		user.setGender(Gender.Male);

		registerUser(user);

		APILogger.logMessage("Registered Testuser");
	}
}