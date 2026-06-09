package org.palladiosimulator.analyzer.slingshot.core.events.snapshot;

import java.util.Objects;

import org.palladiosimulator.analyzer.slingshot.common.events.AbstractSimulationEvent;
import org.palladiosimulator.analyzer.slingshot.common.snapshot.SimulationSnapshot;

public final class ExtensionSimulationSnapshotCaptured extends AbstractSimulationEvent {

	private final String requestId;
	private final SimulationSnapshot snapshot;

	public ExtensionSimulationSnapshotCaptured(final String requestId, final SimulationSnapshot snapshot) {
		super();
		this.requestId = Objects.requireNonNull(requestId);
		this.snapshot = Objects.requireNonNull(snapshot);
	}

	public String getRequestId() {
		return this.requestId;
	}

	public SimulationSnapshot getSnapshot() {
		return this.snapshot;
	}

}
