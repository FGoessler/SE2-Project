package de.sharebox.user.model;

import org.junit.Before;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

public class AddressInfoTest {
	private AddressInfo addressInfo;


	@Before
	public void setUp() {
		addressInfo = new AddressInfo();

		addressInfo.setStreet("Mainstreet 1");
		addressInfo.setAdditionalAddressInfo("2nd Floor");
		addressInfo.setZipCode("10243");
		addressInfo.setCity("Berlin");
		addressInfo.setCountry("Germany");
	}

	@Test
	public void testGetterAndSetter() {
		assertThat(addressInfo.getStreet()).isEqualTo("Mainstreet 1");
		assertThat(addressInfo.getAdditionalAddressInfo()).isEqualTo("2nd Floor");
		assertThat(addressInfo.getZipCode()).isEqualTo("10243");
		assertThat(addressInfo.getCity()).isEqualTo("Berlin");
		assertThat(addressInfo.getCountry()).isEqualTo("Germany");
	}

	@Test
	public void testCopyConstructor() {
		final AddressInfo copiedAddressInfo = new AddressInfo(addressInfo);

		assertThat(copiedAddressInfo).isNotSameAs(addressInfo);
		assertThat(copiedAddressInfo.getStreet()).isEqualTo("Mainstreet 1");
		assertThat(copiedAddressInfo.getAdditionalAddressInfo()).isEqualTo("2nd Floor");
		assertThat(copiedAddressInfo.getZipCode()).isEqualTo("10243");
		assertThat(copiedAddressInfo.getCity()).isEqualTo("Berlin");
		assertThat(copiedAddressInfo.getCountry()).isEqualTo("Germany");
	}

	@Test
	public void testEqualsAndHashCode() {
		assertThat(addressInfo).isEqualTo(new AddressInfo(addressInfo));
		assertThat(addressInfo).isNotEqualTo(new AddressInfo());
		assertThat(addressInfo).isNotEqualTo(new Object());

		assertThat(addressInfo.hashCode()).isEqualTo(new AddressInfo(addressInfo).hashCode());
		assertThat(addressInfo.hashCode()).isNotEqualTo(new AddressInfo().hashCode());
	}
}
