package de.sharebox.file.services;

import de.sharebox.file.model.FEntry;
import de.sharebox.file.model.FEntryPermission;
import de.sharebox.helpers.OptionPaneHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SharingServiceTest {
	private SharingService sharingService;
	private FEntry fEntry1;
	private FEntry fEntry2;

	@Mock
	private OptionPaneHelper mockedOptionPaneHelper;

	@Before
	public void setUp() {
		sharingService = new SharingService();
		sharingService.optionPaneHelper = mockedOptionPaneHelper;

		when(mockedOptionPaneHelper.showInputDialog(anyString(), anyString())).thenReturn("newUser@mail.com");

		fEntry1 = new FEntry();
		fEntry2 = new FEntry();
	}

	@Test
	public void canShowADialogToInviteAUserToAFEntryAndCallsAPIMethods() {
		sharingService.showShareFEntryDialog(fEntry1);

		assertThat(fEntry1.getPermissions()).hasSize(1);
		FEntryPermission permission = fEntry1.getPermissions().get(0);
		assertThat(permission.getUser().getEmail()).isEqualTo("newUser@mail.com");
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

		assertThat(fEntry1.getPermissions()).hasSize(1);
		FEntryPermission permission1 = fEntry1.getPermissions().get(0);
		assertThat(permission1.getUser().getEmail()).isEqualTo("newUser@mail.com");
		assertThat(permission1.getReadAllowed()).isTrue();
		assertThat(permission1.getWriteAllowed()).isTrue();
		assertThat(permission1.getManageAllowed()).isFalse();

		assertThat(fEntry2.getPermissions()).hasSize(1);
		FEntryPermission permission2 = fEntry2.getPermissions().get(0);
		assertThat(permission2.getUser().getEmail()).isEqualTo("newUser@mail.com");
		assertThat(permission2.getReadAllowed()).isTrue();
		assertThat(permission2.getWriteAllowed()).isTrue();
		assertThat(permission2.getManageAllowed()).isFalse();

		//TODO: implement and test API calls!
	}
}
