package org.palladiosimulator.analyzer.slingshot.core.events.snapshot;

import java.util.Objects;

import org.palladiosimulator.analyzer.slingshot.common.events.AbstractSimulationEvent;
import org.palladiosimulator.analyzer.slingshot.common.snapshot.CompositeSimulationSnapshot;

/**
 * Dispatched in pre-simulation to initialize extension state from a snapshot.
 */
public final class SimulationStateInitializationRequested extends AbstractSimulationEvent {

	private final CompositeSimulationSnapshot snapshot;

	public SimulationStateInitializationRequested(final CompositeSimulationSnapshot snapshot) {
		super();
		this.snapshot = Objects.requireNonNull(snapshot);
	}

	public CompositeSimulationSnapshot getSnapshot() {
		return this.snapshot;
	}

}
