package de.sharebox.api;

/**
*
* @author Benjamin Barth
* @author Kay Thorsten Meißner
*/

import de.sharebox.user.model.PaymentInfo;
import de.sharebox.user.model.User;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class UserAPITest {
    private transient UserAPI userAPI;
	private transient User user;
	private transient User user2;

	/*
	 * Zunächst werden für die Testklasse 2 User angelegt.
	 */
	
    @Before
	public void setUp() {
		userAPI = UserAPI.getUniqueInstance();
		

		user = new User();
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
		
		user.setStorageLimit("Zehn GB");
		user.setGender("m");

		user2 = new User();
		user2.setEmail("admin");
		user2.setPassword("root");
		user2.setFirstname("Hans");
		user2.setLastname("Peter");

		paymentInfo.setStreet("Meinweg 2");
		paymentInfo.setAdditionalStreet("Haus 4, Zimmer 15");
		paymentInfo.setCity("Berlin");
		paymentInfo.setCountry("Deutschland");
		paymentInfo.setZipCode("14569");
		user2.setPaymentInfo(paymentInfo);
		
		user2.setStorageLimit("Zwanzig GB");
		user2.setGender("m");
    }


	@Test
	public void isASingletonButCanInjectMocks() {
		assertThat(UserAPI.getUniqueInstance()).isNotNull();
		
		UserAPI mockedUserAPI = mock(UserAPI.class);
		UserAPI.injectSingletonInstance(mockedUserAPI);

		assertThat(UserAPI.getUniqueInstance()).isSameAs(mockedUserAPI);
	}

	/*
	 * Dieser Test überprüft, ob sich ein Nutzer einloggen kann. Außerdem prüft er auch den Fall was passiert, wenn
	 * man nicht im System registtriert ist.
	 */
	
	@Test
    public void testLoginUser() {
		userAPI = UserAPI.getUniqueInstance();
		
		assertThat(userAPI.login(user)).isFalse();
		assertThat(userAPI.registerUser(user)).isTrue();
		assertThat(userAPI.login(user)).isTrue();
		assertThat(userAPI.registerUser(user2)).isTrue();

		assertThat(userAPI.login(user2)).isFalse();
		assertThat(userAPI.logout()).isTrue();
		assertThat(userAPI.login(user2)).isTrue();
		
    }

	/*
	 * Der Test testet, ob es möglich ist zweimal den gleichen Account zu erstellen.
	 */
	
	@Test
	public void creatingDuplicatesCannotBePerformed() {
		
		assertThat(userAPI.registerUser(user)).isTrue();
		assertThat(userAPI.registerUser(user)).isFalse();
		
	}

	/*
	 * Die Methode testet alle möglichen Änderungen die ein Nutzer an seinem Profil vornehmen kann.
	 */
	
	@Test
    public void testUpdatingUser() {
		assertThat(userAPI.registerUser(user)).isTrue();
		assertThat(userAPI.login(user)).isTrue();
		
		assertThat(userAPI.changeProfile(user2)).isTrue();
		
		assertThat(userAPI.getCurrentUser().getFirstname()).isEqualTo(user2.getFirstname());
		assertThat(userAPI.getCurrentUser().getLastname()).isEqualTo(user2.getLastname());
		assertThat(userAPI.getCurrentUser().getGender()).isEqualTo(user2.getGender());
		
		assertThat(userAPI.getCurrentUser().getStorageLimit()).isNotEqualTo(user2.getStorageLimit());
		assertThat(userAPI.getCurrentUser().getPaymentInfo()).isNotEqualTo(user2.getPaymentInfo());
		assertThat(userAPI.getCurrentUser().getEmail()).isNotEqualTo(user2.getEmail());
		assertThat(userAPI.getCurrentUser().getPassword()).isNotEqualTo(user2.getPassword());
		
		assertThat(userAPI.changeAccountingSettings(user2)).isTrue();
		
		assertThat(userAPI.getCurrentUser().getStorageLimit()).isEqualTo(user2.getStorageLimit());
		assertThat(userAPI.getCurrentUser().getPaymentInfo()).isEqualTo(user2.getPaymentInfo());
		
		assertThat(userAPI.changeCredential(user, user2)).isTrue();
		
		assertThat(userAPI.getCurrentUser().getEmail()).isEqualTo(user2.getEmail());
		
		assertThat(userAPI.authenticateUser(user2)).isTrue();
    }

	/*
	 * testet, dass ein nicht eingeloggter Nutzer nicht Profilinformationen ändern kann.
	 */
	
	@Test
	public void updatingNotExistingUserCannotBePerformed() {
		assertThat(userAPI.login(user)).isFalse();
		assertThat(userAPI.changeProfile(user2)).isFalse();
		assertThat(userAPI.changeAccountingSettings(user2)).isFalse();
		assertThat(userAPI.changeCredential(user, user2)).isFalse();
		assertThat(userAPI.isLoggedIn()).isFalse();
		
		assertThat(userAPI.getCurrentUser().getFirstname()).isNotEqualTo(user2.getFirstname());
		assertThat(userAPI.getCurrentUser().getLastname()).isNotEqualTo(user2.getLastname());
		assertThat(userAPI.getCurrentUser().getGender()).isNotEqualTo(user2.getGender());
		assertThat(userAPI.getCurrentUser().getStorageLimit()).isNotEqualTo(user2.getStorageLimit());
		assertThat(userAPI.getCurrentUser().getPaymentInfo()).isNotEqualTo(user2.getPaymentInfo());
		assertThat(userAPI.getCurrentUser().getEmail()).isNotEqualTo(user2.getEmail());
		assertThat(userAPI.getCurrentUser().getPassword()).isNotEqualTo(user2.getPassword());
		
	}

	/*
	 * Testet, ob man einen neuen Nutzer einladen kann.
	 */
	
	@Test
	public void testInviteUser(){
		assertThat(userAPI.registerUser(user)).isTrue();
		assertThat(userAPI.authenticateUser(user)).isTrue();
		assertThat(userAPI.login(user)).isTrue();
		assertThat(userAPI.inviteUser(user, user2)).isTrue();	
	}
	
	/*
	 * Testet, ob man bereits bekannte Nutzer einladen kann.
	 */
	
	@Test
	public void testInviteSameUser(){
		assertThat(userAPI.registerUser(user)).isTrue();
		assertThat(userAPI.authenticateUser(user)).isTrue();
		assertThat(userAPI.login(user)).isTrue();
		assertThat(userAPI.inviteUser(user, user)).isFalse();	
	}
	
	/*
	 * stellt die Ausgangssituation wieder her.
	 */
	
	@After
	public void tearDown() {
	  UserAPI.resetSingletonInstance();
	}
	
}
