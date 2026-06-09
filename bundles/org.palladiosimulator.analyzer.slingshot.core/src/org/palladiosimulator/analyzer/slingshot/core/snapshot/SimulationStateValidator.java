package org.palladiosimulator.analyzer.slingshot.core.snapshot;

import org.palladiosimulator.analyzer.slingshot.common.snapshot.CompositeSimulationSnapshot;

/**
 * Validates whether a snapshot matches the current simulation run.
 */
public interface SimulationStateValidator {

	void validate(CompositeSimulationSnapshot snapshot, SnapshotContributorRegistry registry);

}
