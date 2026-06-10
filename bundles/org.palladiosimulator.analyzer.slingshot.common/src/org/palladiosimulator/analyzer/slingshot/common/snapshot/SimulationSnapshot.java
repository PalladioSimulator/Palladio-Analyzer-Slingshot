package org.palladiosimulator.analyzer.slingshot.common.snapshot;

/**
 * Root contract for simulation state contributed by the core or an extension.
 *
 * <p>
 * Each snapshot-capable extension defines its own implementation. The core assembles
 * contributor snapshots into a {@link CompositeSimulationSnapshot}. The
 * {@link #schemaVersion()} identifies the data layout and follows semver conventions
 * owned by the contributing extension (major version changes indicate incompatible layouts).
 * </p>
 *
 * @see CompositeSimulationSnapshot
 * @see org.palladiosimulator.analyzer.slingshot.core.extension.SnapshotCapableExtension
 */
public interface SimulationSnapshot {

	/**
	 * Returns the stable identifier of the contributing extension or the composite envelope.
	 *
	 * @return a unique extension identifier, e.g. bundle symbolic name
	 */
	String extensionId();

	/**
	 * Returns the schema version of this snapshot's data layout.
	 *
	 * <p>
	 * The version string is semver-formatted and extension-owned. The core validator
	 * checks major compatibility (currently prefix {@code 1.}) before initialization.
	 * </p>
	 *
	 * @return the schema version of this snapshot type
	 */
	String schemaVersion();

	/**
	 * Checks whether this snapshot can be applied in the given run context.
	 *
	 * @param context the current simulation context with active extension identifiers
	 * @return {@code true} iff this snapshot is compatible with the context
	 */
	default boolean isCompatibleWith(final SimulationContext context) {
		return context.getActiveExtensionIds().contains(this.extensionId());
	}

}
