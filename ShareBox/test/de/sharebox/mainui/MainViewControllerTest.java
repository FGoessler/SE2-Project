package de.sharebox.mainui;

import de.sharebox.file.controller.DirectoryViewController;
import de.sharebox.user.User;
import org.junit.Before;
import org.junit.Ignore;
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
	private User currentUser;

	@Before
	public void setUp() {
		mainView = new MainViewController(currentUser);
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

	@Ignore
	@Test
	public void clickingLogoutPerformsLogoutProcess() {
		mainView.logout.actionPerformed(mockedActionEvent);

		//TODO: implement and test!
	}
}
