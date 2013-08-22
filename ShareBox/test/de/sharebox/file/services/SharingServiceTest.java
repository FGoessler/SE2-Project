package de.sharebox.file.services;

import de.sharebox.api.UserAPI;
import de.sharebox.file.model.FEntry;
import de.sharebox.file.model.FEntryPermission;
import de.sharebox.helpers.OptionPaneHelper;
import de.sharebox.user.model.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SharingServiceTest {
	public static final String NEW_USER_MAIL = "newUser@mail.com";

	private FEntry fEntry1;
	private FEntry fEntry2;

	@Mock
	private OptionPaneHelper mockedOptionPaneHelper;

	@InjectMocks
	private SharingService sharingService;

	@Before
	public void setUp() {
		when(mockedOptionPaneHelper.showInputDialog(anyString(), anyString())).thenReturn(NEW_USER_MAIL);

		User mockedUser = mock(User.class);
		when(mockedUser.getEmail()).thenReturn("test@mail.de");
		UserAPI mockedAPI = mock(UserAPI.class);
		when(mockedAPI.getCurrentUser()).thenReturn(mockedUser);
		UserAPI.injectSingletonInstance(mockedAPI);

		fEntry1 = spy(new FEntry());
		fEntry1.setPermission(mockedUser, true, true, true);
		fEntry2 = new FEntry();
		fEntry2.setPermission(mockedUser, true, true, true);
	}

	@After
	public void tearDown() {
		UserAPI.resetSingletonInstance();
	}

	@Test
	public void canShowADialogToInviteAUserToAFEntryAndCallsAPIMethods() {
		sharingService.showShareFEntryDialog(fEntry1);

		assertThat(fEntry1.getPermissions()).hasSize(2);    //contains initial and added permission
		FEntryPermission permission = fEntry1.getPermissions().get(1);
		assertThat(permission.getUser().getEmail()).isEqualTo(NEW_USER_MAIL);
		assertThat(permission.getReadAllowed()).isTrue();
		assertThat(permission.getWriteAllowed()).isTrue();
		assertThat(permission.getManageAllowed()).isFalse();

		//TODO: implement and test API calls!
	}

	@Test
	public void canShowADialogToInviteAUserToMultipleFEntriesAndCallsAPIMethods() {
		List<FEntry> fEntries = new ArrayList<FEntry>();
		fEntries.add(fEntry1);
		fEntries.add(fEntry2);

		sharingService.showShareFEntryDialog(fEntries);

		assertThat(fEntry1.getPermissions()).hasSize(2);    //contains initial and added permission
		FEntryPermission permission1 = fEntry1.getPermissions().get(1);
		assertThat(permission1.getUser().getEmail()).isEqualTo(NEW_USER_MAIL);
		assertThat(permission1.getReadAllowed()).isTrue();
		assertThat(permission1.getWriteAllowed()).isTrue();
		assertThat(permission1.getManageAllowed()).isFalse();

		assertThat(fEntry2.getPermissions()).hasSize(2);
		FEntryPermission permission2 = fEntry2.getPermissions().get(1);
		assertThat(permission2.getUser().getEmail()).isEqualTo(NEW_USER_MAIL);
		assertThat(permission2.getReadAllowed()).isTrue();
		assertThat(permission2.getWriteAllowed()).isTrue();
		assertThat(permission2.getManageAllowed()).isFalse();

		//TODO: implement and test API calls!
	}

	@Test
	public void sharingFEntriesWithoutManagePermissionIsNotPossible() {
		FEntry fEntryWithoutPermissions = new FEntry();
		fEntryWithoutPermissions.setName("Testfile");

		sharingService.showShareFEntryDialog(fEntryWithoutPermissions);

		verify(mockedOptionPaneHelper).showMessageDialog(contains("Testfile"));
		assertThat(fEntryWithoutPermissions.getPermissions()).hasSize(0);    //no permission added
	}

	@Test
	public void canHandleCanceledDialogAndInvalidData() {
		when(mockedOptionPaneHelper.showInputDialog(anyString(), anyString())).thenReturn(null);
		sharingService.showShareFEntryDialog(fEntry1);
		assertThat(fEntry1.getPermissions()).hasSize(1);    //only contains initial permission

		when(mockedOptionPaneHelper.showInputDialog(anyString(), anyString())).thenReturn("");
		sharingService.showShareFEntryDialog(fEntry1);
		assertThat(fEntry1.getPermissions()).hasSize(1);

		when(mockedOptionPaneHelper.showInputDialog(anyString(), anyString())).thenReturn("keine-email");
		sharingService.showShareFEntryDialog(fEntry1);
		assertThat(fEntry1.getPermissions()).hasSize(1);
	}
}
