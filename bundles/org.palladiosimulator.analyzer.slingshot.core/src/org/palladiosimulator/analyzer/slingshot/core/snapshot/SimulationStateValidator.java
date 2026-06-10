package org.palladiosimulator.analyzer.slingshot.core.snapshot;

import org.palladiosimulator.analyzer.slingshot.common.snapshot.CompositeSimulationSnapshot;
import org.palladiosimulator.analyzer.slingshot.core.exceptions.SimulationStateIncompatibleException;

/**
 * Validates whether a snapshot can be applied to the current simulation run.
 *
 * @see DefaultSimulationStateValidator
 * @see SimulationStateIncompatibleException
 */
public interface SimulationStateValidator {

	/**
	 * Validates the given snapshot against the active contributor registry.
	 *
	 * @param snapshot  the composite snapshot to validate
	 * @param registry  the active snapshot contributors for this run
	 * @throws SimulationStateIncompatibleException if the snapshot cannot be applied
	 */
	void validate(CompositeSimulationSnapshot snapshot, SnapshotContributorRegistry registry);

}
