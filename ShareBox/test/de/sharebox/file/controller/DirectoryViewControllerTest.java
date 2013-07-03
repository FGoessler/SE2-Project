package de.sharebox.file.controller;

import org.junit.Before;
import org.junit.Test;

import javax.swing.*;

import static org.fest.assertions.Assertions.assertThat;

public class DirectoryViewControllerTest {
	private DirectoryViewController controller;

	@Before
	public void setUp() {
		JPanel parentPanel = new JPanel();
		new JFrame().add(parentPanel);

		controller = new DirectoryViewController(parentPanel);
	}

	@Test
	public void hasAViewToDrawIn() {
		assertThat(controller.parentView).isNotNull();
	}
}
