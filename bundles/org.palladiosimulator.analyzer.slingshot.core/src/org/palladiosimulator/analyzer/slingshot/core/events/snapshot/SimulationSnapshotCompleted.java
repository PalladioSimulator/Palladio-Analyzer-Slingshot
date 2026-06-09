package org.palladiosimulator.analyzer.slingshot.core.events.snapshot;

import java.util.Objects;

import org.palladiosimulator.analyzer.slingshot.common.events.AbstractSimulationEvent;
import org.palladiosimulator.analyzer.slingshot.common.snapshot.CompositeSimulationSnapshot;

public final class SimulationSnapshotCompleted extends AbstractSimulationEvent {

	private final String requestId;
	private final CompositeSimulationSnapshot snapshot;
	private final boolean successful;

	public SimulationSnapshotCompleted(final String requestId, final CompositeSimulationSnapshot snapshot,
			final boolean successful) {
		super();
		this.requestId = Objects.requireNonNull(requestId);
		this.snapshot = snapshot;
		this.successful = successful;
	}

	public String getRequestId() {
		return this.requestId;
	}

	public CompositeSimulationSnapshot getSnapshot() {
		return this.snapshot;
	}

	public boolean isSuccessful() {
		return this.successful;
	}

}
