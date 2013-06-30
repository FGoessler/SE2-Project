package de.sharebox.api;

public final class APILogger {
	private APILogger() {}

	public static void debugSuccess(String action) {
		if (FileAPI.DEBUG) {
			System.out.println(action + " successful at " + System.currentTimeMillis() + ".");
		}
	}

	public static void debugFailure(String action) {
		if (FileAPI.DEBUG) {
			System.out.println(action + " failed.");
		}
	}

	public static void debugFailure(String action, String reason) {
		if (FileAPI.DEBUG) {
			System.out.println(action + " failed. Reason: " + reason);
		}
	}
}