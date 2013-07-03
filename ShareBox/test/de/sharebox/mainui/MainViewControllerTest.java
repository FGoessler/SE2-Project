package de.sharebox.mainui;

import de.sharebox.user.User;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.awt.event.ActionEvent;

import static org.fest.assertions.Assertions.assertThat;

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
