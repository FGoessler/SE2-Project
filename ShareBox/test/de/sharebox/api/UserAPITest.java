package de.sharebox.api;

/**
*
* @author Benjamin Barth
* @author Kay Thorsten Meißner
*/

import de.sharebox.user.User;
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

	@Test
	public void creatingDuplicatesCannotBePerformed() {
		

		assertThat(userAPI.registerUser(user)).isTrue();
		assertThat(userAPI.registerUser(user)).isFalse();
	}

	@Test
    public void testUpdatingFile() {
		assertThat(userAPI.registerUser(user)).isTrue();
		assertThat(userAPI.login(user)).isTrue();
		
		assertThat(userAPI.changeProfile(user2)).isTrue();
		
		assertThat(userAPI.getCurrentUser().getFirstname() == user2.getFirstname()).isTrue();
		assertThat(userAPI.getCurrentUser().getLastname() == user2.getLastname()).isTrue();
		assertThat(userAPI.getCurrentUser().getGender() == user2.getGender()).isTrue();
		
		assertThat(userAPI.getCurrentUser().getStorageLimit() == user2.getStorageLimit()).isFalse();
		assertThat(userAPI.getCurrentUser().getPaymentInfo() == user2.getPaymentInfo()).isFalse();
		assertThat(userAPI.getCurrentUser().getEmail() == user2.getEmail()).isFalse();
		assertThat(userAPI.getCurrentUser().getPassword() == user2.getPassword()).isFalse();
		
		assertThat(userAPI.changeAccountingSettings(user2)).isTrue();
		
		assertThat(userAPI.getCurrentUser().getStorageLimit() == user2.getStorageLimit()).isTrue();
		assertThat(userAPI.getCurrentUser().getPaymentInfo() == user2.getPaymentInfo()).isTrue();
		
		assertThat(userAPI.changeCredential(user, user2)).isTrue();
		
		assertThat(userAPI.getCurrentUser().getEmail() == user2.getEmail()).isTrue();
		assertThat(userAPI.getCurrentUser().getPassword() == user2.getPassword()).isTrue();
    }

	@Test
	public void updatingNotExistingFileCannotBePerformed() {
		assertThat(userAPI.login(user)).isFalse();
		assertThat(userAPI.changeProfile(user2)).isFalse();
		assertThat(userAPI.changeAccountingSettings(user2)).isFalse();
		assertThat(userAPI.changeCredential(user, user2)).isFalse();
		assertThat(userAPI.isLoggedIn()).isFalse();
		
		assertThat(userAPI.getCurrentUser().getFirstname() == user2.getFirstname()).isFalse();
		assertThat(userAPI.getCurrentUser().getLastname() == user2.getLastname()).isFalse();
		assertThat(userAPI.getCurrentUser().getGender() == user2.getGender()).isFalse();
		assertThat(userAPI.getCurrentUser().getStorageLimit() == user2.getStorageLimit()).isFalse();
		assertThat(userAPI.getCurrentUser().getPaymentInfo() == user2.getPaymentInfo()).isFalse();
		assertThat(userAPI.getCurrentUser().getEmail() == user2.getEmail()).isFalse();
		assertThat(userAPI.getCurrentUser().getPassword() == user2.getPassword()).isFalse();
	}

	@Test
	public void testInviteUser(){
		assertThat(userAPI.registerUser(user)).isTrue();
		assertThat(userAPI.authenticateUser(user)).isTrue();
		assertThat(userAPI.inviteUser(user, user2)).isTrue();	
	}
	
}
