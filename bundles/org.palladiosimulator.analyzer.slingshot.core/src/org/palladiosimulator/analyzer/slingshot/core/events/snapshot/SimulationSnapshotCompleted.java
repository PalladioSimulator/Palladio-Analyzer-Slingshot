package org.palladiosimulator.analyzer.slingshot.core.events.snapshot;

import java.util.Objects;

import org.palladiosimulator.analyzer.slingshot.common.events.AbstractSimulationEvent;
import org.palladiosimulator.analyzer.slingshot.common.snapshot.CompositeSimulationSnapshot;

/**
 * DES event emitted by the core when a snapshot capture barrier completes.
 *
 * <p>
 * Terminal event of the capture phase. Carries the assembled
 * {@link CompositeSimulationSnapshot} when {@link #isSuccessful()} is {@code true}.
 * </p>
 */
public final class SimulationSnapshotCompleted extends AbstractSimulationEvent {

	private final String requestId;
	private final CompositeSimulationSnapshot snapshot;
	private final boolean successful;

	/**
	 * Creates a completion event for a capture session.
	 *
	 * @param requestId   the capture session identifier
	 * @param snapshot    the assembled composite snapshot, or {@code null} on failure
	 * @param successful  whether the capture completed successfully
	 */
	public SimulationSnapshotCompleted(final String requestId, final CompositeSimulationSnapshot snapshot,
			final boolean successful) {
		super();
		this.requestId = Objects.requireNonNull(requestId);
		this.snapshot = snapshot;
		this.successful = successful;
	}

	/**
	 * Returns the capture session identifier.
	 *
	 * @return the request identifier
	 */
	public String getRequestId() {
		return this.requestId;
	}

	/**
	 * Returns the assembled composite snapshot.
	 *
	 * @return the composite snapshot, or {@code null} if capture failed
	 */
	public CompositeSimulationSnapshot getSnapshot() {
		return this.snapshot;
	}

	/**
	 * Returns whether the capture barrier completed successfully.
	 *
	 * @return {@code true} iff all expected contributors responded
	 */
	public boolean isSuccessful() {
		return this.successful;
	}

}
