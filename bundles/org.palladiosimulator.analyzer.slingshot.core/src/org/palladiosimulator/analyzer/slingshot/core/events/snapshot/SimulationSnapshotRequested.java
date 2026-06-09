package org.palladiosimulator.analyzer.slingshot.core.events.snapshot;

import java.util.Objects;
import java.util.UUID;

import org.palladiosimulator.analyzer.slingshot.common.events.AbstractSimulationEvent;
import org.palladiosimulator.analyzer.slingshot.common.snapshot.SnapshotScope;

public final class SimulationSnapshotRequested extends AbstractSimulationEvent {

	private final String requestId;
	private final SnapshotScope scope;

	public SimulationSnapshotRequested() {
		this(UUID.randomUUID().toString(), SnapshotScope.FULL);
	}

	public SimulationSnapshotRequested(final String requestId, final SnapshotScope scope) {
		super();
		this.requestId = Objects.requireNonNull(requestId);
		this.scope = Objects.requireNonNull(scope);
	}

	public String getRequestId() {
		return this.requestId;
	}

	public SnapshotScope getScope() {
		return this.scope;
	}

}
