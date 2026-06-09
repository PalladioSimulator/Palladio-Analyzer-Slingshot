package org.palladiosimulator.analyzer.slingshot.common.snapshot;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Minimal context for snapshot compatibility checks.
 */
public final class SimulationContext {

	private final Set<String> activeExtensionIds;

	public SimulationContext(final Set<String> activeExtensionIds) {
		this.activeExtensionIds = Collections.unmodifiableSet(new LinkedHashSet<>(Objects.requireNonNull(activeExtensionIds)));
	}

	public Set<String> getActiveExtensionIds() {
		return this.activeExtensionIds;
	}

}
