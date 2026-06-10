package org.palladiosimulator.analyzer.slingshot.core.events.snapshot;

import java.util.Objects;

import org.palladiosimulator.analyzer.slingshot.common.events.AbstractSimulationEvent;
import org.palladiosimulator.analyzer.slingshot.common.snapshot.CompositeSimulationSnapshot;

/**
 * DES event dispatched in pre-simulation to initialize extension state from a snapshot.
 *
 * <p>
 * Scheduled by {@link org.palladiosimulator.analyzer.slingshot.core.driver.SlingshotSimulationDriver}
 * after validation and before {@link org.palladiosimulator.analyzer.slingshot.core.events.PreSimulationConfigurationStarted}.
 * Snapshot-capable extensions apply their typed snapshot and respond with
 * {@link ExtensionSimulationSnapshotInitialized}.
 * </p>
 */
public final class SimulationStateInitializationRequested extends AbstractSimulationEvent {

	private final CompositeSimulationSnapshot snapshot;

	/**
	 * Creates an initialization request carrying the snapshot to apply.
	 *
	 * @param snapshot the composite snapshot to initialize from
	 */
	public SimulationStateInitializationRequested(final CompositeSimulationSnapshot snapshot) {
		super();
		this.snapshot = Objects.requireNonNull(snapshot);
	}

	/**
	 * Returns the composite snapshot to initialize extension state from.
	 *
	 * @return the initialization snapshot
	 */
	public CompositeSimulationSnapshot getSnapshot() {
		return this.snapshot;
	}

}
