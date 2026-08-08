package org.palladiosimulator.analyzer.slingshot.common.snapshot;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * Aggregate root combining core engine state and extension-owned snapshots.
 *
 * <p>
 * This is the object passed to {@link org.palladiosimulator.analyzer.slingshot.core.api.SimulationDriver#init(de.uka.ipd.sdq.simucomframework.core.SimuComConfig, org.eclipse.core.runtime.IProgressMonitor, java.util.Optional)}
 * for state initialization and produced by the capture barrier when complete.
 * </p>
 */
public final class CompositeSimulationSnapshot implements SimulationSnapshot {

	/** Extension identifier of the composite envelope itself. */
	public static final String EXTENSION_ID = "org.palladiosimulator.analyzer.slingshot.core.composite";

	/** Schema version of the composite envelope layout. */
	public static final String SCHEMA_VERSION = "1.0.0";

	private final CoreSimulationSnapshot core;
	private final Map<String, SimulationSnapshot> contributors;

	/**
	 * Creates a composite snapshot from core and extension contributions.
	 *
	 * @param core          the core engine snapshot
	 * @param contributors  extension snapshots keyed by {@link SimulationSnapshot#extensionId()}
	 */
	public CompositeSimulationSnapshot(final CoreSimulationSnapshot core,
			final Map<String, SimulationSnapshot> contributors) {
		this.core = Objects.requireNonNull(core);
		this.contributors = Collections.unmodifiableMap(new LinkedHashMap<>(Objects.requireNonNull(contributors)));
	}

	/**
	 * Returns the core engine snapshot.
	 *
	 * @return the core snapshot
	 */
	public CoreSimulationSnapshot getCore() {
		return this.core;
	}

	/**
	 * Returns all extension contributor snapshots keyed by extension identifier.
	 *
	 * @return an unmodifiable map of contributor snapshots
	 */
	public Map<String, SimulationSnapshot> getContributors() {
		return this.contributors;
	}

	/**
	 * Returns the set of extension identifiers that contributed to this composite.
	 *
	 * @return contributor extension identifiers
	 */
	public Set<String> contributorIds() {
		return this.contributors.keySet();
	}

	/**
	 * Returns the contributor snapshot for the given extension identifier.
	 *
	 * @param extensionId the extension identifier to look up
	 * @return the matching snapshot, or empty if not present
	 */
	public Optional<SimulationSnapshot> getByExtensionId(final String extensionId) {
		return Optional.ofNullable(this.contributors.get(extensionId));
	}

	/**
	 * Returns the first contributor snapshot of the given type.
	 *
	 * @param <T>   the snapshot type
	 * @param type  the class to look up
	 * @return the matching snapshot, or empty if not present
	 */
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
