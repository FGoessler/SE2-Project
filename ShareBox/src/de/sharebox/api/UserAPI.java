package de.sharebox.api;

import java.util.ArrayList;
import java.util.List;
import de.sharebox.user.*;

import de.sharebox.api.FileAPI.StorageEntry;

/**
*
* @author Benjamin Barth
* @author Kay Thorsten Meißner
*/


public class UserAPI {
	

	/**
	 * simulated storage; 
	 */
	protected transient List<User> userList = new ArrayList<User>();
	
	protected transient User currentUser;
	
	private static UserAPI instance = new UserAPI();

	private static final String USER_NOT_FOUND = "User not found.";
	private static final String USER_EXISTS = "User already exists!";

	/**
	 * Methode um das Singleton-Objekt zu erhalten.
	 * @return Das Singleton-Objekt.
	 */
    public static UserAPI getUniqueInstance() {
        return instance;
	}
    
    /** Checks if Username and Password are valid
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
    
    
    /** Creates new not already existing User
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
    
    /** Changes general Information
     * @param user zu ändernder user 
     * @return ob erfolgreich **/
    public boolean changeProfile(User user){
    	Boolean back = false;
    	//search through existing users
		for (User aUser : userList) {
			if (aUser.getEmail().equals(user.getEmail())  ) {

				aUser.setFirstname(user.getFirstname());
				aUser.setLastname(user.getLastname());
				aUser.setGender(user.getGender());
				back = true;
			}
		}
		if (back) {
	        APILogger.logMessage("Profile updated");
		}
		else {
			APILogger.logMessage("Profile update failed");
		}
		return back;
    }
    
    /** Changes Accounting Information
     * @param user zu ändernder user 
     * @return ob erfolgreich **/
    public boolean changeAccountingSettings(User user){
    	Boolean back = false;
    	//search through existing users
		for (User aUser : userList) {
			if (aUser.getEmail().equals(user.getEmail())  ) {

				aUser.setPaymentInfo(user.getPaymentInfo());
				aUser.setStorageLimit(user.getStorageLimit());
				back = true;
			}
		}
		if (back) {
	        APILogger.logMessage("Accounting settings changed");
		}
		else {
			APILogger.logMessage("Accounting settings change failed");
		}
		return back;
    }
    
    /** Changes E-Mailadress and Password
     * @param oldUser zu ändernder user 
     * @param newUser zu übernehmende Informationen
     * @return ob erfolgreich **/
    public boolean changeCredential(User oldUser, User newUser){
    	Boolean back = false;
    	//search through existing users
		for (User aUser : userList) {
			if ((aUser.getEmail().equals(oldUser.getEmail())) && (aUser.getPassword().equals(oldUser.getPassword()))  ) {

				aUser.setEmail(newUser.getEmail());
				aUser.setPassword(newUser.getPassword());
				back = true;
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
    
    /** Invites new User to ShareBox
     * @param oldUser zu ändernder user 
     * @param newUser zu übernehmende Informationen
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
	
}
