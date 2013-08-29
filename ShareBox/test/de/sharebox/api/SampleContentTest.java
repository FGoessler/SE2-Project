package de.sharebox.api;

import org.junit.Test;

import static org.fest.assertions.Fail.fail;

/**
 * Dieser Test stellt lediglich sicher, das die Testinhalte der gemockten APIs ohne das auslösen einer Exception
 * erstellt werden können.
 */
public class SampleContentTest {

	@Test
	public void testSampleContent() {
		try {
			final FileAPI fileAPI = new FileAPI();
			final UserAPI userAPI = new UserAPI(fileAPI);
			userAPI.createSampleContent();
		} catch (Exception exception) {
			fail("Exception should not have been thrown! " + exception.getLocalizedMessage());
		}
	}
}
