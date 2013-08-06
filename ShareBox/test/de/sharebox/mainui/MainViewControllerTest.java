package de.sharebox.mainui;

import de.sharebox.Main;
import de.sharebox.api.UserAPI;
import de.sharebox.file.controller.DirectoryViewController;
import de.sharebox.user.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.awt.event.ActionEvent;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class MainViewControllerTest {
	private MainViewController mainView;

	@Mock
	private ActionEvent mockedActionEvent;
	@Mock
	private UserAPI mockedUserAPI;
	@Mock
	private User currentUser;

	@Before
	public void setUp() {
		Main.loginController = null;
		mainView = new MainViewController(currentUser);
	}

	@After
	public void tearDown() {
		Main.loginController = null;
	}

	@Test
	public void providesAUserObject() {
		assertThat(mainView.getCurrentUser()).isSameAs(currentUser);
	}

	@Test
	public void containsADirectoryViewControllerAndIsVisible() {
		assertThat(mainView.directoryViewController).isNotNull();

		assertThat(mainView.swix.getRootComponent().isVisible()).isTrue();
	}

	@Test
	public void userCanCreateANewFile() {
		mainView.directoryViewController = mock(DirectoryViewController.class);
		mainView.createNewFile.actionPerformed(mock(ActionEvent.class));

		verify(mainView.directoryViewController).createNewFileBasedOnUserSelection();
	}

	@Test
	public void userCanCreateANewDirectory() {
		mainView.directoryViewController = mock(DirectoryViewController.class);
		mainView.createNewDirectory.actionPerformed(mock(ActionEvent.class));

		verify(mainView.directoryViewController).createNewDirectoryBasedOnUserSelection();
	}

	@Test
	public void clickingEditAccountingShowsAccountingController() {
		mainView.showEditAccounting.actionPerformed(mockedActionEvent);

		assertThat(mainView.accountController).isNotNull();
	}

	@Test
	public void clickingEditProfileShowsEditProfileController() {
		mainView.showEditProfile.actionPerformed(mockedActionEvent);

		assertThat(mainView.editProfileController).isNotNull();
	}

	@Test
	public void clickingInviteUserShowsInvitationController() {
		mainView.showInvitationView.actionPerformed(mockedActionEvent);

		assertThat(mainView.invitationController).isNotNull();
	}

	@Test
	public void clickingLogoutPerformsLogoutProcess() {
		UserAPI.injectSingletonInstance(mockedUserAPI);

		mainView.logout.actionPerformed(mockedActionEvent);

		verify(mockedUserAPI).logout();
		assertThat(Main.loginController).isNotNull();
	}
}
