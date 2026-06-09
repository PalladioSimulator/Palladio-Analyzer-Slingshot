package org.palladiosimulator.analyzer.slingshot.core.snapshot;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.palladiosimulator.analyzer.slingshot.core.extension.SnapshotCapableExtension;

/**
 * Tracks snapshot-capable extensions active in the current simulation run.
 */
public final class SnapshotContributorRegistry {

	private final Set<String> activeContributorIds = new LinkedHashSet<>();

	public void register(final SnapshotCapableExtension extension) {
		if (extension.isActive()) {
			this.activeContributorIds.add(extension.getExtensionId());
		}
	}

	public Set<String> getActiveContributorIds() {
		return Collections.unmodifiableSet(this.activeContributorIds);
	}

}
