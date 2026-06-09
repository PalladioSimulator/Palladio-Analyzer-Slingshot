package org.palladiosimulator.analyzer.slingshot.common.snapshot;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * Aggregate root combining core engine state and extension-owned snapshots.
 */
public final class CompositeSimulationSnapshot implements SimulationSnapshot {

	public static final String EXTENSION_ID = "org.palladiosimulator.analyzer.slingshot.core.composite";
	public static final String SCHEMA_VERSION = "1.0.0";

	private final CoreSimulationSnapshot core;
	private final Map<String, SimulationSnapshot> contributors;

	public CompositeSimulationSnapshot(final CoreSimulationSnapshot core,
			final Map<String, SimulationSnapshot> contributors) {
		this.core = Objects.requireNonNull(core);
		this.contributors = Collections.unmodifiableMap(new LinkedHashMap<>(Objects.requireNonNull(contributors)));
	}

	public CoreSimulationSnapshot getCore() {
		return this.core;
	}

	public Map<String, SimulationSnapshot> getContributors() {
		return this.contributors;
	}

	public Set<String> contributorIds() {
		return this.contributors.keySet();
	}

	public Optional<SimulationSnapshot> getByExtensionId(final String extensionId) {
		return Optional.ofNullable(this.contributors.get(extensionId));
	}

	public <T extends SimulationSnapshot> Optional<T> get(final Class<T> type) {
		Objects.requireNonNull(type);
		return this.contributors.values()
				.stream()
				.filter(type::isInstance)
				.map(type::cast)
				.findFirst();
	}

	@Override
	public String extensionId() {
		return EXTENSION_ID;
	}

	@Override
	public String schemaVersion() {
		return SCHEMA_VERSION;
	}

	@Override
	public boolean isCompatibleWith(final SimulationContext context) {
		return context.getActiveExtensionIds().containsAll(this.contributorIds());
	}

}
