package org.palladiosimulator.analyzer.slingshot.common.snapshot;

/**
 * Scope selector for a snapshot capture request.
 *
 * <p>
 * Additional scope values may be introduced in future iterations to support
 * partial captures (e.g. extension-only or scheduled-events-only).
 * </p>
 */
public enum SnapshotScope {

	/** Capture core engine state and all active extension snapshots. */
	FULL

}
