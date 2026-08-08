package org.palladiosimulator.analyzer.slingshot.core.snapshot;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.palladiosimulator.analyzer.slingshot.core.extension.SnapshotCapableExtension;

/**
 * Tracks snapshot-capable extensions active in the current simulation run.
 *
 * <p>
 * Populated during {@link org.palladiosimulator.analyzer.slingshot.core.driver.SlingshotSimulationDriver#init}
 * and used by the capture barrier and {@link SimulationStateValidator} to determine
 * expected contributors.
 * </p>
 */
public final class SnapshotContributorRegistry {

	private final Set<String> activeContributorIds = new LinkedHashSet<>();

	/**
	 * Registers a snapshot-capable extension if it is active.
	 *
	 * @param extension the extension to register
	 */
	public void register(final SnapshotCapableExtension extension) {
		if (extension.isActive()) {
			this.activeContributorIds.add(extension.getExtensionId());
		}
	}

	/**
	 * Returns the identifiers of active snapshot-capable extensions in this run.
	 *
	 * @return an unmodifiable set of extension identifiers
	 */
	public Set<String> getActiveContributorIds() {
		return Collections.unmodifiableSet(this.activeContributorIds);
	}

}
