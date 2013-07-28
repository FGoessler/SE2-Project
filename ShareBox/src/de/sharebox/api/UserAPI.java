package de.sharebox.api;

import java.util.ArrayList;
import java.util.List;
import de.sharebox.user.*;

/**
*
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

	/**
	 * Methode um das Singleton-Objekt zu erhalten.
	 * @return Das Singleton-Objekt.
	 */
    public static UserAPI getUniqueInstance() {
        return instance;
	}
    
    /**
	 * Erstellt Beispiel-Daten, die für Testzwecke benötigt werden.
	 */
    public void createSampleContent() {

    	User user = new User();
        user.setEmail("Max@Mustermann.de");
        user.setPassword("maxmuster");
        user.setFirstname("Max");
        user.setLastname("Mustermann");
        user.setPaymentInfo("Kontonummer");
        user.setStorageLimit("Zehn GB");
        user.setGender("m");
    	
    	User user2 = new User();
        user2.setEmail("Hans@Peter.de");
        user2.setPassword("hans1234");
        user2.setFirstname("Hans");
        user2.setLastname("Peter");
        user2.setPaymentInfo("BLZ");
        user2.setStorageLimit("Zwanzig GB");
        user2.setGender("m");
        
        if (registerUser(user) && registerUser(user2)) {
    		APILogger.logMessage("Registered Sampledata");
    	}
        else {
        	APILogger.logMessage("Registering Sampledata failed");
        }
	}
    
    
    /**
	 * Setzt die Singleton Instanz von auÃŸen. Soll nur in Test Cases verwendet werden, um die die UserAPI zu mocken.
	 * @param newUserAPI Das neue UserAPI Objekt, das ab sofort beim Aufruf von getUniqueInstance() zurÃ¼ckgegeben wird.
	 */
    public static void injectSingletonInstance(UserAPI newUserAPI) {
		instance = newUserAPI;
	}
    
    /** Prüft, ob eine Kombination von E-Mailadresse und Passwort im System enthalten ist.
     * @param user zu authentifizierender user 
     * @return ob erfolgreich **/
    public boolean authenticateUser(User user){
    	boolean back = false;
    	//search through existing users
		for (User aUser : userList) {
			if ((aUser.getEmail().equals(user.getEmail())) && (aUser.getPassword().equals(user.getPassword())) ) {
				back = true;
			}
		}
		if (back) {
	        APILogger.logMessage("Authentication successful");
		}
		else {
			APILogger.logMessage("Authentication failed: User not found");
		}
		return back;
    }
    
    /** Versucht, den User einzuloggen, wenn Authentifizierung erfolgreich ist.
     * @param user einzulogender user 
     * @return ob erfolgreich **/
    public boolean login(User user){
    	boolean back;
    	if (authenticateUser(user) && !isLoggedIn()){
    		currentUser = user.copy(user);
    		APILogger.logMessage("Login successful");
    		back = true;
    	}
    	else{
    		if (isLoggedIn()){
    			APILogger.logMessage("Login failed. Please Logout first.");
    		}
    		else{
    			APILogger.logMessage("Login failed. Username/Password not correct.");
    		}
    		back = false;
    	}
    	return back;
    }
    
    /** Logt den eingelogten User aus.
     * @return ob erfolgreich **/
    public boolean logout(){
    	boolean back;
    	if (currentUser != null ){
    		currentUser = null;
    		APILogger.logMessage("Logout successful");
    		back = true;
    	}
    	else{
    		APILogger.logMessage("Logout failed: No User loged in");
    		back = false;
    	}
    	return back;
    }
    
    
    /** Erstellt neuen User, sofern noch nicht vorhanden.
     * @param user zu registrierender user 
     * @return ob erfolgreich **/
    public boolean registerUser(User user){
    	Boolean userAlreadyExists = false;
    	Boolean back = false;
    	//search through existing users
		for (User aUser : userList) {
			if (aUser.getEmail().equals(user.getEmail())  ) {
				userAlreadyExists = true;
			}
			if (userAlreadyExists == false) {
				userList.add(user.copy(user));
				back = true;
			}
		}
		if (back) {
	        APILogger.logMessage("Registration successful");
		}
		else {
			APILogger.logMessage("Registsration failed: User already exists");
		}
		return back;
    }
    
    /** Ändert Profil-Informationen
     * @param user zu ändernder user 
     * @return ob erfolgreich **/
    public boolean changeProfile(User user){
    	Boolean back = false;
    	//search through existing users
		if(currentUser != null){
    		for (User aUser : userList) {
				if (aUser.getEmail().equals(user.getEmail())  ) {
					aUser.setFirstname(user.getFirstname());
					aUser.setLastname(user.getLastname());
					aUser.setGender(user.getGender());
					back = true;
				}
			}
    	}	
		else {
			back = false;
		}
		if (back) {
	        APILogger.logMessage("Profile updated");
		}
		else {
			APILogger.logMessage("Profile update failed");
		}
		return back;
    }
    
    /** Ändert Zahlungs- und Speicherinformationen
     * @param user zu ändernder user 
     * @return ob erfolgreich **/
    public boolean changeAccountingSettings(User user){
    	Boolean back = false;
    	//search through existing users
		if(currentUser != null){	
    		for (User aUser : userList) {
				if (aUser.getEmail().equals(user.getEmail())  ) {
					aUser.setPaymentInfo(user.getPaymentInfo());
					aUser.setStorageLimit(user.getStorageLimit());
					back = true;
				}
			}
		}
		else{
			back = false;
		}
		if (back) {
	        APILogger.logMessage("Accounting settings changed");
		}
		else {
			APILogger.logMessage("Accounting settings change failed");
		}
		return back;
    }
    
    /** Ändert E-Mailadresse und Password
     * @param oldUser zu ändernder user 
     * @param newUser zu übernehmende Informationen
     * @return ob erfolgreich **/
    public boolean changeCredential(User oldUser, User newUser){
    	Boolean back = false;
    	//search through existing users
		for (User aUser : userList) {
			if ((currentUser.getEmail().equals(oldUser.getEmail())) && (currentUser.getPassword().equals(oldUser.getPassword()))  ){
				if ((aUser.getEmail().equals(oldUser.getEmail())) && (aUser.getPassword().equals(oldUser.getPassword()))  ) {
					aUser.setEmail(newUser.getEmail());
					aUser.setPassword(newUser.getPassword());
					back = true;
				}
			}
		}
		if (back) {
	        APILogger.logMessage("Credentials changed");
		}
		else {
			APILogger.logMessage("Credentials change failed");
		}
		return back;
    }
    
    /** Lädt neuen Benutzer zu Sharebox ein.
     * @param oldUser werbende User 
     * @param newUser geworbene User
     * @return ob erfolgreich **/ 
    
    public boolean inviteUser(User oldUser, User newUser){
    	Boolean back = true;
    	//search through existing users
		for (User aUser : userList) {
			if (aUser.getEmail().equals(newUser.getEmail())  ) {
				back = false;
			}
		}
		if (back) {
	        APILogger.logMessage("User invited");
		}
		else {
			APILogger.logMessage("Invitation failed: User already exists");
		}
		return back;
    }

    /** prüft, ob ein User eingeloggt ist.
     * @return ob ein User eingeloggt ist **/ 
	public boolean isLoggedIn(){
		boolean back = false;
	if (currentUser != null){
		back = true;	
		}
	return back;
	}
	
	   /** gibt eingeloggten User zurück
     * @return zur Zeit eingeloggter User **/ 
	public User getCurrentUser(){
		User backUser = new User();
		
		if (isLoggedIn()){
			backUser = currentUser.copy(currentUser);
			backUser.setPassword("");
		}
		
		return backUser;
	}
	
	
	
}
	
