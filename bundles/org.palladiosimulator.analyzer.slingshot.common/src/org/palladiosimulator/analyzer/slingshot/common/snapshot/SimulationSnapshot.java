package org.palladiosimulator.analyzer.slingshot.common.snapshot;

/**
 * Root contract for simulation state contributed by the core or an extension.
 */
public interface SimulationSnapshot {

	/**
	 * Stable identifier of the contributing extension or the composite envelope.
	 */
	String extensionId();

	/**
	 * Schema version of this snapshot's data layout (semver, extension-owned).
	 */
	String schemaVersion();

	/**
	 * Checks whether this snapshot can be applied in the given run context.
	 */
	default boolean isCompatibleWith(final SimulationContext context) {
		return context.getActiveExtensionIds().contains(this.extensionId());
	}

}
