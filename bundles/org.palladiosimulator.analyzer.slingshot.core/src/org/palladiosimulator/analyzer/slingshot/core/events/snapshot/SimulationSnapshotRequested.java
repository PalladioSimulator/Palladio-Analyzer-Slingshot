package org.palladiosimulator.analyzer.slingshot.core.events.snapshot;

import java.util.Objects;
import java.util.UUID;

import org.palladiosimulator.analyzer.slingshot.common.events.AbstractSimulationEvent;
import org.palladiosimulator.analyzer.slingshot.common.snapshot.SnapshotScope;

/**
 * DES event that starts a coordinated snapshot capture across all active contributors.
 *
 * <p>
 * Dispatched by the core (or driver in a future {@code requestSnapshot()} API).
 * {@link org.palladiosimulator.analyzer.slingshot.core.behavior.CoreSnapshotBehavior} opens a
 * capture session; snapshot-capable extensions respond with
 * {@link ExtensionSimulationSnapshotCaptured}. The barrier completes with
 * {@link SimulationSnapshotCompleted}.
 * </p>
 */
public final class SimulationSnapshotRequested extends AbstractSimulationEvent {

	private final String requestId;
	private final SnapshotScope scope;

	/**
	 * Creates a capture request with a random identifier and {@link SnapshotScope#FULL}.
	 */
	public SimulationSnapshotRequested() {
		this(UUID.randomUUID().toString(), SnapshotScope.FULL);
	}

	/**
	 * Creates a capture request with the given identifier and scope.
	 *
	 * @param requestId  correlates contributor responses with this capture session
	 * @param scope      the capture scope
	 */
	public SimulationSnapshotRequested(final String requestId, final SnapshotScope scope) {
		super();
		this.requestId = Objects.requireNonNull(requestId);
		this.scope = Objects.requireNonNull(scope);
	}

	/**
	 * Returns the correlation identifier for this capture session.
	 *
	 * @return the request identifier
	 */
	public String getRequestId() {
		return this.requestId;
	}

	/**
	 * Returns the scope of this capture request.
	 *
	 * @return the snapshot scope
	 */
	public SnapshotScope getScope() {
		return this.scope;
	}

}
