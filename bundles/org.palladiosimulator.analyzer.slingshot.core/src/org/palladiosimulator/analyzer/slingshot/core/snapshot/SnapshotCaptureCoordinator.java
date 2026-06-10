package org.palladiosimulator.analyzer.slingshot.core.snapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.palladiosimulator.analyzer.slingshot.common.snapshot.CompositeSimulationSnapshot;
import org.palladiosimulator.analyzer.slingshot.common.snapshot.CoreSimulationSnapshot;
import org.palladiosimulator.analyzer.slingshot.common.snapshot.SimulationSnapshot;

/**
 * Collects extension snapshots for a single capture request until the barrier completes.
 *
 * <p>
 * Used by {@link org.palladiosimulator.analyzer.slingshot.core.behavior.CoreSnapshotBehavior} to
 * correlate {@link org.palladiosimulator.analyzer.slingshot.core.events.snapshot.ExtensionSimulationSnapshotCaptured}
 * events with an active {@link org.palladiosimulator.analyzer.slingshot.core.events.snapshot.SimulationSnapshotRequested}.
 * </p>
 */
public final class SnapshotCaptureCoordinator {

	private String activeRequestId;
	private Set<String> expectedContributorIds = Set.of();
	private CoreSimulationSnapshot coreSnapshot;
	private final Map<String, SimulationSnapshot> received = new HashMap<>();

	/**
	 * Starts a new capture session.
	 *
	 * @param requestId               the capture session identifier
	 * @param expectedContributorIds  extension identifiers that must respond
	 * @param coreSnapshot            the core engine snapshot captured at session start
	 */
	public void startCapture(final String requestId, final Set<String> expectedContributorIds,
			final CoreSimulationSnapshot coreSnapshot) {
		this.activeRequestId = Objects.requireNonNull(requestId);
		this.expectedContributorIds = Set.copyOf(Objects.requireNonNull(expectedContributorIds));
		this.coreSnapshot = Objects.requireNonNull(coreSnapshot);
		this.received.clear();
	}

	/**
	 * Returns whether a capture session is currently active.
	 *
	 * @return {@code true} iff a capture is in progress
	 */
	public boolean isCaptureActive() {
		return this.activeRequestId != null;
	}

	/**
	 * Returns the identifier of the active capture session.
	 *
	 * @return the active request identifier, or {@code null} if no capture is active
	 */
	public String getActiveRequestId() {
		return this.activeRequestId;
	}

	/**
	 * Adds a contributor snapshot to the active capture session.
	 *
	 * <p>
	 * When all expected contributors have responded, assembles and returns a
	 * {@link CompositeSimulationSnapshot} and resets the coordinator.
	 * </p>
	 *
	 * @param snapshot the contributor snapshot to add
	 * @return the assembled composite when the barrier completes, or empty otherwise
	 */
	public Optional<CompositeSimulationSnapshot> addContributorSnapshot(final SimulationSnapshot snapshot) {
		Objects.requireNonNull(snapshot);
		this.received.put(snapshot.extensionId(), snapshot);
		if (this.received.keySet().containsAll(this.expectedContributorIds)) {
			final CompositeSimulationSnapshot composite = new CompositeSimulationSnapshot(this.coreSnapshot,
					Map.copyOf(this.received));
			this.reset();
			return Optional.of(composite);
		}
		return Optional.empty();
	}

	private void reset() {
		this.activeRequestId = null;
		this.expectedContributorIds = Set.of();
		this.coreSnapshot = null;
		this.received.clear();
	}

}
