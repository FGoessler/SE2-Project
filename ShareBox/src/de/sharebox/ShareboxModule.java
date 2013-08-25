package de.sharebox;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import de.sharebox.file.controller.*;
import de.sharebox.mainui.MainViewController;
import de.sharebox.mainui.MainViewControllerFactory;
import de.sharebox.mainui.menu.AdministrationMenu;
import de.sharebox.mainui.menu.AdministrationMenuFactory;
import de.sharebox.mainui.menu.FileMenu;
import de.sharebox.mainui.menu.FileMenuFactory;

/**
 * Dieses Modul dient dazu Guice die Factories bekannt zu machen und diverse andere Bindungen zu spezifizieren.
 * Die meisten Bindungen kann Guice allerdings automatisch erkennen und müssen somit hier nicht mehr aufgeführt werden.
 */
public class ShareboxModule extends AbstractModule {

	@Override
	protected void configure() {
		//Controller Factories
		install(new FactoryModuleBuilder()
				.implement(MainViewController.class, MainViewController.class)
				.build(MainViewControllerFactory.class));

		install(new FactoryModuleBuilder()
				.implement(DirectoryViewController.class, DirectoryViewController.class)
				.build(DirectoryViewControllerFactory.class));

		install(new FactoryModuleBuilder()
				.implement(PermissionViewController.class, PermissionViewController.class)
				.build(PermissionViewControllerFactory.class));
		install(new FactoryModuleBuilder()
				.implement(LogViewController.class, LogViewController.class)
				.build(LogViewControllerFactory.class));


		//Menu Factories
		install(new FactoryModuleBuilder()
				.implement(FileMenu.class, FileMenu.class)
				.build(FileMenuFactory.class));
		install(new FactoryModuleBuilder()
				.implement(AdministrationMenu.class, AdministrationMenu.class)
				.build(AdministrationMenuFactory.class));
	}
}
