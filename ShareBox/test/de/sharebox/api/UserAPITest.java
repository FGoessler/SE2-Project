package de.sharebox.api;

/**
*
* @author Benjamin Barth
* @author Kay Thorsten Meißner
*/

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import de.sharebox.api.UserAPI;
import de.sharebox.user.User;

@RunWith(MockitoJUnitRunner.class)
public class UserAPITest {
    private transient UserAPI userAPI;
	private transient User user;
	private transient User user2;

    @Before
	public void setUp() {
		userAPI = UserAPI.getUniqueInstance();

        user = new User();
        user.setEmail("Max@Mustermann.de");
        user.setPassword("maxmuster");
        user.setFirstname("Max");
        user.setLastname("Mustermann");
        user.setPaymentInfo("Kontonummer");
        user.setStorageLimit("Zehn GB");
        user.setGender("m");

        user2 = new User();
        user2.setEmail("Hans@Peter.de");
        user2.setPassword("hans1234");
        user2.setFirstname("Hans");
        user2.setLastname("Peter");
        user2.setPaymentInfo("BLZ");
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

	@Test
    public void testauthenticateUser() {
		userAPI = UserAPI.getUniqueInstance();
		
		assertThat(userAPI.authenticateUser(user)).isFalse();
		assertThat(userAPI.registerUser(user)).isTrue();
		assertThat(userAPI.authenticateUser(user)).isTrue();
    }

	@Test
	public void creatingDuplicatesCannotBePerformed() {
		

		assertThat(userAPI.registerUser(user)).isTrue();
		assertThat(userAPI.registerUser(user)).isFalse();
	}

	@Test
    public void testUpdatingFile() {
		assertThat(userAPI.registerUser(user)).isTrue();
		assertThat(userAPI.authenticateUser(user)).isTrue();
		assertThat(userAPI.changeProfile(user2)).isTrue();
		// überprüfe Änderung
		assertThat(userAPI.changeAccountingSettings(user2)).isTrue();
		// überprüfe Änderung
		assertThat(userAPI.changeCredential(user, user2)).isTrue();
		// überprüfe Änderung	
    }

	@Test
	public void updatingNotExistingFileCannotBePerformed() {
		assertThat(userAPI.authenticateUser(user)).isFalse();
		assertThat(userAPI.changeProfile(user2)).isFalse();
		// überprüfe Änderung
		assertThat(userAPI.changeAccountingSettings(user2)).isFalse();
		// überprüfe Änderung
		assertThat(userAPI.changeCredential(user, user2)).isFalse();
		// überprüfe Änderung
		// sinnvoll?
	}

	@Test
	public void testInviteUser(){
		assertThat(userAPI.registerUser(user)).isTrue();
		assertThat(userAPI.authenticateUser(user)).isTrue();
		assertThat(userAPI.inviteUser(user, user2)).isTrue();	
	}
	
}
