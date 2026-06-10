package org.palladiosimulator.analyzer.slingshot.common.snapshot;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Minimal context for snapshot compatibility checks during initialization.
 *
 * <p>
 * Captures which snapshot-capable extensions are active in the current simulation run.
 * </p>
 */
public final class SimulationContext {

	private final Set<String> activeExtensionIds;

	/**
	 * Creates a new simulation context.
	 *
	 * @param activeExtensionIds identifiers of active snapshot-capable extensions
	 */
	public SimulationContext(final Set<String> activeExtensionIds) {
		this.activeExtensionIds = Collections.unmodifiableSet(new LinkedHashSet<>(Objects.requireNonNull(activeExtensionIds)));
	}

	/**
	 * Returns the identifiers of snapshot-capable extensions active in this run.
	 *
	 * @return an unmodifiable set of extension identifiers
	 */
	public Set<String> getActiveExtensionIds() {
		return this.activeExtensionIds;
	}

}
