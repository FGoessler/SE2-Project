package de.sharebox.user.controller;

import de.sharebox.api.UserAPI;
import de.sharebox.helpers.OptionPaneHelper;
import de.sharebox.user.model.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class InvitationControllerTest {

	@Mock
	private UserAPI mockedAPI;
	@Mock
	private OptionPaneHelper optionPaneHelper;

	@InjectMocks
	private InvitationController invitationController;


	@Before
	public void setUp() {
		UserAPI.injectSingletonInstance(mockedAPI);
	}

	@After
	public void tearDown() {
		UserAPI.resetSingletonInstance();
	}

	/**
	 * Testet das erfolgreiche Einladen einer fremden Person.
	 */
	@Test
	public void testSuccessfulInvite() {
		when(mockedAPI.inviteUser(Matchers.any(User.class), Matchers.any(User.class))).thenReturn(true);
		when(optionPaneHelper.showInputDialog(anyString(), anyString())).thenReturn("test@mail.de");

		invitationController.show();

		verify(mockedAPI).inviteUser(Matchers.any(User.class), Matchers.any(User.class));
		verify(optionPaneHelper).showMessageDialog("test@mail.de wurde eingeladen!");
	}

	/**
	 * Testet, den Fall, dass der Nutzer eine falsche E-Mail Adresse angibt.
	 */
	@Test
	public void testInvalidMailAddressInvite() {
		when(mockedAPI.inviteUser(Matchers.any(User.class), Matchers.any(User.class))).thenReturn(false);
		when(optionPaneHelper.showInputDialog(anyString(), anyString())).thenReturn("kurt.de");

		invitationController.show();

		verify(mockedAPI, never()).inviteUser(Matchers.any(User.class), Matchers.any(User.class));
		verify(optionPaneHelper).showMessageDialog("Die eingegebene Emailadresse war ungültig!");
	}

	/**
	 * Testet, den Fall, dass der Nutzer eine bereits bekannte E-Mail Adresse angibt.
	 */
	@Test
	public void testAlreadyUsedMailAddressInvite() {
		when(mockedAPI.inviteUser(Matchers.any(User.class), Matchers.any(User.class))).thenReturn(false);
		when(optionPaneHelper.showInputDialog(anyString(), anyString())).thenReturn("kurt@haha.de");

		invitationController.show();

		verify(mockedAPI).inviteUser(Matchers.any(User.class), Matchers.any(User.class));
		verify(optionPaneHelper).showMessageDialog("kurt@haha.de ist bereits registriert.");
	}
}

