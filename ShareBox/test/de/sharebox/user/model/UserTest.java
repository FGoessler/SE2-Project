package de.sharebox.user.model;

import org.junit.Before;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

public class UserTest {
	private User user;
	private AddressInfo addressInfo;

	@Before
	public void setUp() {
		user = new User();

		user.setEmail("test@mail.com");
		user.setPassword("testPassword");
		user.setFirstname("Hans");
		user.setLastname("Peter");
		user.setStorageLimit("5GB");
		user.setGender("w");
		addressInfo = new AddressInfo();
		user.setAddressInfo(addressInfo);
	}

	@Test
	public void testGetterAndSetter() {
		assertThat(user.getEmail()).isEqualTo("test@mail.com");
		assertThat(user.getPassword()).isEqualTo("testPassword");
		assertThat(user.getFirstname()).isEqualTo("Hans");
		assertThat(user.getLastname()).isEqualTo("Peter");
		assertThat(user.getStorageLimit()).isEqualTo("5GB");
		assertThat(user.getGender()).isEqualTo("w");
		assertThat(user.getAddressInfo()).isEqualTo(addressInfo);
	}


	@Test
	public void testCopyConstructor() {
		User copiedUser = new User(user);

		assertThat(copiedUser).isNotSameAs(user);
		assertThat(copiedUser.getEmail()).isEqualTo("test@mail.com");
		assertThat(copiedUser.getPassword()).isEqualTo("testPassword");
		assertThat(copiedUser.getFirstname()).isEqualTo("Hans");
		assertThat(copiedUser.getLastname()).isEqualTo("Peter");
		assertThat(copiedUser.getStorageLimit()).isEqualTo("5GB");
		assertThat(copiedUser.getGender()).isEqualTo("w");
		assertThat(copiedUser.getAddressInfo()).isEqualTo(addressInfo)
				.isNotSameAs(addressInfo);
	}
}
