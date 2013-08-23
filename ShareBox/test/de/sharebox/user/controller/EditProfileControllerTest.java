package de.sharebox.user.controller;

import de.sharebox.api.UserAPI;
import de.sharebox.helpers.OptionPaneHelper;
import de.sharebox.user.model.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.awt.event.ActionEvent;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class EditProfileControllerTest {

	private User testUser;

	@Mock
	private UserAPI mockedAPI;
	@Mock
	private OptionPaneHelper optionPaneHelper;

	@InjectMocks
	private EditProfileController editProfileController;

	@Before
	public void setUp() {
		testUser = new User();
		when(mockedAPI.getCurrentUser()).thenReturn(testUser);

		editProfileController.show();
	}


	/**
	 * Testet das ändern von Daten. Simuliert dabei einen eingeloggten Nutzer, der seine Daten ändern möchte und
	 * ändert diese dann.
	 */
	@Test
	public void testSuccessfulChange() {
		when(mockedAPI.changeProfile(Matchers.any(User.class))).thenReturn(true);

		editProfileController.firstnameField.setText("hanna");
		editProfileController.lastnameField.setText("spanna");
		editProfileController.genderField.setText("w");

		editProfileController.save.actionPerformed(mock(ActionEvent.class));

		ArgumentCaptor<User> newUserData = ArgumentCaptor.forClass(User.class);
		verify(mockedAPI).changeProfile(newUserData.capture());

		assertThat(newUserData.getValue().getFirstname()).isEqualTo("hanna");
		assertThat(newUserData.getValue().getLastname()).isEqualTo("spanna");
		assertThat(newUserData.getValue().getGender()).isEqualTo("w");

		verify(optionPaneHelper).showMessageDialog("Ihre Änderungen wurden gespeichert!");
	}

	/**
	 * Testet, den Fall, dass der Nutzer beim ändern seiner Daten irgendwas falsch oder gar nicht angibt.
	 */
	@Test
	public void testInvalidChange() {
		when(mockedAPI.changeProfile(Matchers.any(User.class))).thenReturn(false);

		editProfileController.save.actionPerformed(mock(ActionEvent.class));

		verify(mockedAPI).changeProfile(any(User.class));
		verify(optionPaneHelper).showMessageDialog("Das Ändern der Daten ist fehlgeschlagen!");
	}

	/**
	 * Testet den Abbrechen-Button und die Aktion die dabei ausgeführt werden soll.
	 */
	@Test
	public void testStop() {
		editProfileController.stop.actionPerformed(mock(ActionEvent.class));
		verify(optionPaneHelper).showMessageDialog(anyString());
	}

}

