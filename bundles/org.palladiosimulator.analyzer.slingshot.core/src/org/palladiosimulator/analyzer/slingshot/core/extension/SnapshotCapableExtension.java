package org.palladiosimulator.analyzer.slingshot.core.extension;

import org.palladiosimulator.analyzer.slingshot.common.snapshot.SimulationSnapshot;

/**
 * Marker for extensions that participate in snapshot capture and restore.
 */
public interface SnapshotCapableExtension extends SimulationBehaviorExtension {

	String getExtensionId();

	Class<? extends SimulationSnapshot> snapshotType();

	default boolean supportsSnapshotSchema(final String schemaVersion) {
		return schemaVersion != null && schemaVersion.startsWith("1.");
	}

	default boolean isRequiredForRestore() {
		return this.isActive();
	}

}
