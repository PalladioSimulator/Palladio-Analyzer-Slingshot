package org.palladiosimulator.analyzer.slingshot.common.snapshot;

/**
 * Result status of an extension's response to {@link org.palladiosimulator.analyzer.slingshot.core.events.snapshot.SimulationStateInitializationRequested}.
 */
public enum InitializationStatus {

	/** The extension successfully applied its snapshot. */
	SUCCESS,

	/** The extension failed to apply its snapshot. */
	FAILED,

	/** The extension had no matching snapshot in the composite and was skipped. */
	SKIPPED

}
