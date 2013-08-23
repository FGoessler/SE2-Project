package de.sharebox.helpers;

import org.junit.Before;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

public class SwingEngineHelperTest {
	private SwingEngineHelper swingEngineHelper;

	@Before
	public void setUp() {
		swingEngineHelper = new SwingEngineHelper();
	}

	@Test
	public void canRenderAXMLLayoutFileViaSWIxml() {
		assertThat(swingEngineHelper.render(this, "user/login")).isNotNull();
	}

	@Test
	public void returnsNullOnAnError() {
		assertThat(swingEngineHelper.render(this, "gibtsnicht")).isNull();
	}
}
