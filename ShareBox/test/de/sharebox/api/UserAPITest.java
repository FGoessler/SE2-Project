package de.sharebox.api;

/**
*
* @author Benjamin Barth
* @author Kay Thorsten Meißner
*/

import de.sharebox.user.User;

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

    @Before
	public void setUp() {
		userAPI = UserAPI.getUniqueInstance();

        user = new User();
        user.setEmail("Max@Mustermann.de");
        user.setPassword("maxmuster");
        user.setFirstname("Max");
        user.setLastname("Mustermann");
        user.setPaymentInfo("Kontonummer");
        user.setStorageLimit("10");
        user.setGender("m");

        user2 = new User();
        user2.setEmail("Hans@Peter.de");
        user2.setPassword("hans1234");
        user2.setFirstname("Hans");
        user2.setLastname("Peter");
        user2.setPaymentInfo("BLZ");
        user2.setStorageLimit("20");
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

	@Test
	public void testInviteUser(){
		assertThat(userAPI.registerUser(user)).isTrue();
		assertThat(userAPI.authenticateUser(user)).isTrue();
		assertThat(userAPI.inviteUser(user, user2)).isTrue();	
	}
	
	@After
	public void tearDown() {
	  UserAPI.resetSingletonInstance();
	}
	
}
