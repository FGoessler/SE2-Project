package de.sharebox.api;

import de.sharebox.file.model.FEntry;

/**
 *
 * @author Julius Mertens
 */

public class storageEntry {
        public long timestamp;
        public FEntry entry;
        public storageEntry(long time, FEntry add) {
            timestamp = time;
            entry = add;
        }
}
