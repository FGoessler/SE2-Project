package de.sharebox.file.services;

import de.sharebox.api.FileAPI;
import de.sharebox.api.UserAPI;
import de.sharebox.file.model.FEntry;
import de.sharebox.file.model.Permission;
import de.sharebox.helpers.OptionPaneHelper;
import de.sharebox.user.model.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
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
	private User mockedUser;

	@Mock
	private OptionPaneHelper mockedOptionPaneHelper;
	@Mock
	private FileAPI fileAPI;
	@Mock
	private UserAPI mockedAPI;

	@InjectMocks
	private SharingService sharingService;

	@Before
	public void setUp() {
		when(mockedOptionPaneHelper.showInputDialog(anyString(), anyString())).thenReturn(NEW_USER_MAIL);

		mockedUser = mock(User.class);
		when(mockedUser.getEmail()).thenReturn("test@mail.de");
		when(mockedAPI.getCurrentUser()).thenReturn(mockedUser);

		fEntry1 = spy(new FEntry(mockedAPI));
		fEntry1.setPermission(mockedUser, true, true, true);
		fEntry2 = new FEntry(mockedAPI);
		fEntry2.setPermission(mockedUser, true, true, true);
	}

	@Test
	public void canShowADialogToInviteAUserToAFEntryAndCallsAPIMethods() {
		sharingService.showShareFEntryDialog(fEntry1);

		assertThat(fEntry1.getPermissions()).hasSize(2);    //contains initial and added permission
		final Permission permission = fEntry1.getPermissions().get(1);
		assertThat(permission.getUser().getEmail()).isEqualTo(NEW_USER_MAIL);
		assertThat(permission.getReadAllowed()).isTrue();
		assertThat(permission.getWriteAllowed()).isTrue();
		assertThat(permission.getManageAllowed()).isFalse();

		final ArgumentCaptor<User> invitedUser = ArgumentCaptor.forClass(User.class);
		verify(mockedAPI).inviteUser(same(mockedUser), invitedUser.capture());
		verify(fileAPI).shareFEntry(mockedAPI, invitedUser.getValue(), fEntry1);
		assertThat(invitedUser.getValue().getEmail()).isEqualTo(NEW_USER_MAIL);
	}

	@Test
	public void canShowADialogToInviteAUserToMultipleFEntriesAndCallsAPIMethods() {
		final List<FEntry> fEntries = new ArrayList<FEntry>();
		fEntries.add(fEntry1);
		fEntries.add(fEntry2);

		sharingService.showShareFEntryDialog(fEntries);

		assertThat(fEntry1.getPermissions()).hasSize(2);    //contains initial and added permission
		final Permission permission1 = fEntry1.getPermissions().get(1);
		assertThat(permission1.getUser().getEmail()).isEqualTo(NEW_USER_MAIL);
		assertThat(permission1.getReadAllowed()).isTrue();
		assertThat(permission1.getWriteAllowed()).isTrue();
		assertThat(permission1.getManageAllowed()).isFalse();

		assertThat(fEntry2.getPermissions()).hasSize(2);
		final Permission permission2 = fEntry2.getPermissions().get(1);
		assertThat(permission2.getUser().getEmail()).isEqualTo(NEW_USER_MAIL);
		assertThat(permission2.getReadAllowed()).isTrue();
		assertThat(permission2.getWriteAllowed()).isTrue();
		assertThat(permission2.getManageAllowed()).isFalse();

		final ArgumentCaptor<User> invitedUser = ArgumentCaptor.forClass(User.class);
		verify(mockedAPI).inviteUser(same(mockedUser), invitedUser.capture());
		verify(fileAPI).shareFEntry(mockedAPI, invitedUser.getValue(), fEntry1);
		verify(fileAPI).shareFEntry(mockedAPI, invitedUser.getValue(), fEntry2);
		assertThat(invitedUser.getValue().getEmail()).isEqualTo(NEW_USER_MAIL);
	}

	@Test
	public void sharingFEntriesWithoutManagePermissionIsNotPossible() {
		final FEntry fEntryWithoutPermissions = new FEntry(mockedAPI);
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
