package org.palladiosimulator.analyzer.slingshot.core.extension;

import org.palladiosimulator.analyzer.slingshot.common.snapshot.SimulationSnapshot;

/**
 * Marker for extensions that participate in snapshot capture and state initialization.
 *
 * <p>
 * Implementations typically add {@code @OnEvent} handlers for
 * {@link org.palladiosimulator.analyzer.slingshot.core.events.snapshot.SimulationSnapshotRequested} and
 * {@link org.palladiosimulator.analyzer.slingshot.core.events.snapshot.SimulationStateInitializationRequested}
 * on their existing {@link SimulationBehaviorExtension} class.
 * </p>
 */
public interface SnapshotCapableExtension extends SimulationBehaviorExtension {

	/**
	 * Returns the stable identifier of this extension for snapshot contributor matching.
	 *
	 * @return a unique extension identifier, e.g. bundle symbolic name
	 */
	String getExtensionId();

	/**
	 * Returns the concrete snapshot type produced by this extension.
	 *
	 * @return the snapshot class
	 */
	Class<? extends SimulationSnapshot> snapshotType();

	/**
	 * Returns whether the running extension supports the given snapshot schema version.
	 *
	 * <p>
	 * Default implementation accepts major version {@code 1}.
	 * </p>
	 *
	 * @param schemaVersion the schema version from a captured snapshot
	 * @return {@code true} iff the schema can be applied
	 */
	default boolean supportsSnapshotSchema(final String schemaVersion) {
		return schemaVersion != null && schemaVersion.startsWith("1.");
	}

	/**
	 * Returns whether this extension must participate in initialization when it was active at capture.
	 *
	 * <p>
	 * Default implementation delegates to {@link #isActive()}.
	 * </p>
	 *
	 * @return {@code true} iff initialization requires a snapshot from this extension
	 */
	default boolean isRequiredForInitialization() {
		return this.isActive();
	}

}
