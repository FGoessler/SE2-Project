package de.sharebox.mainui;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.swing.*;
import java.awt.event.ActionEvent;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class AdministrationMenuTest {
	private AdministrationMenu menu;
	@Mock
	private JMenuBar mockedMenuBar;
	@Mock
	private MainViewController mockedMainViewController;

	@Before
	public void setUp() {
		menu = new AdministrationMenu(mockedMenuBar, mockedMainViewController);
	}

	@Test
	public void creatingMenu() {
		verify(mockedMenuBar).add(any(JMenu.class));
	}

	@Test
	public void testShowEditCredentials() {
		menu.showEditCredentials.actionPerformed(mock(ActionEvent.class));
		verify(mockedMainViewController).openEditCredentialsController();
	}

	@Test
	public void testShowEditProfile() {
		menu.showEditProfile.actionPerformed(mock(ActionEvent.class));
		verify(mockedMainViewController).openEditProfileController();
	}

	@Test
	public void testShowEditAccounting() {
		menu.showEditAccounting.actionPerformed(mock(ActionEvent.class));
		verify(mockedMainViewController).openAccountController();
	}

	@Test
	public void testShowInvitationView() {
		menu.showInvitationView.actionPerformed(mock(ActionEvent.class));
		verify(mockedMainViewController).openInvitationController();
	}

	@Test
	public void testLogout() {
		menu.logout.actionPerformed(mock(ActionEvent.class));
		verify(mockedMainViewController).close();
	}
}
