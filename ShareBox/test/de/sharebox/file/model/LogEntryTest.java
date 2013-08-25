package de.sharebox.file.model;

import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

public class LogEntryTest {

	@Test
	public void canBeCreatedWithAMessageAndTheCurrentDate() {
		final LogEntry logEntry = new LogEntry(LogEntry.LogMessage.CHANGED);

		assertThat(logEntry.getMessage()).isEqualTo(LogEntry.LogMessage.CHANGED);
		assertThat(logEntry.getDate()).isNotNull();
	}

	@Test
	public void providesACopyConstructor() {
		final LogEntry logEntry = new LogEntry(LogEntry.LogMessage.CHANGED);

		final LogEntry copiedLogEntry = new LogEntry(logEntry);

		assertThat(copiedLogEntry.getMessage()).isEqualTo(LogEntry.LogMessage.CHANGED);
		assertThat(copiedLogEntry.getDate()).isEqualTo(logEntry.getDate()).
				isNotSameAs(logEntry.getDate());
	}

	@Test
	public void logMessagesProvideAReadableString() {
		assertThat(LogEntry.LogMessage.CHANGED.toString()).isEqualTo("Geändert");
		for (final LogEntry.LogMessage message : LogEntry.LogMessage.values()) {
			assertThat(message.toString()).isNotNull()
					.isNotEqualTo("");
		}
	}
}
