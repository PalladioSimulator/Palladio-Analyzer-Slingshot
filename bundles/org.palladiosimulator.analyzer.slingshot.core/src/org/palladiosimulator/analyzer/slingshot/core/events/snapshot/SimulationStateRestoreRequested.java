package org.palladiosimulator.analyzer.slingshot.core.events.snapshot;

import java.util.Objects;

import org.palladiosimulator.analyzer.slingshot.common.events.AbstractSimulationEvent;
import org.palladiosimulator.analyzer.slingshot.common.snapshot.CompositeSimulationSnapshot;

public final class SimulationStateRestoreRequested extends AbstractSimulationEvent {

	private final CompositeSimulationSnapshot snapshot;

	public SimulationStateRestoreRequested(final CompositeSimulationSnapshot snapshot) {
		super();
		this.snapshot = Objects.requireNonNull(snapshot);
	}

	public CompositeSimulationSnapshot getSnapshot() {
		return this.snapshot;
	}

}
