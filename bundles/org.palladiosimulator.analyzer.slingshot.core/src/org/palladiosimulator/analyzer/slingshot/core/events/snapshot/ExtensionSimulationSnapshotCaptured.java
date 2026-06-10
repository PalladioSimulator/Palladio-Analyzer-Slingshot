package org.palladiosimulator.analyzer.slingshot.core.events.snapshot;

import java.util.Objects;

import org.palladiosimulator.analyzer.slingshot.common.events.AbstractSimulationEvent;
import org.palladiosimulator.analyzer.slingshot.common.snapshot.SimulationSnapshot;

/**
 * DES event emitted by a snapshot-capable extension in response to
 * {@link SimulationSnapshotRequested}.
 *
 * <p>
 * Carries the extension's concrete {@link SimulationSnapshot} implementation.
 * {@link org.palladiosimulator.analyzer.slingshot.core.behavior.CoreSnapshotBehavior} collects
 * these events until all expected contributors have responded, then assembles
 * {@link SimulationSnapshotCompleted}.
 * </p>
 */
public final class ExtensionSimulationSnapshotCaptured extends AbstractSimulationEvent {

	private final String requestId;
	private final SimulationSnapshot snapshot;

	/**
	 * Creates a captured-snapshot response for the given capture session.
	 *
	 * @param requestId  the capture session identifier from {@link SimulationSnapshotRequested}
	 * @param snapshot   the extension-owned snapshot
	 */
	public ExtensionSimulationSnapshotCaptured(final String requestId, final SimulationSnapshot snapshot) {
		super();
		this.requestId = Objects.requireNonNull(requestId);
		this.snapshot = Objects.requireNonNull(snapshot);
	}

	/**
	 * Returns the capture session identifier this response belongs to.
	 *
	 * @return the request identifier
	 */
	public String getRequestId() {
		return this.requestId;
	}

	/**
	 * Returns the captured extension snapshot.
	 *
	 * @return the snapshot contributed by the extension
	 */
	public SimulationSnapshot getSnapshot() {
		return this.snapshot;
	}

}
