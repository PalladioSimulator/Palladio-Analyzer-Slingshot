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
 * Collects extension snapshots for a single capture request.
 */
public final class SnapshotCaptureCoordinator {

	private String activeRequestId;
	private Set<String> expectedContributorIds = Set.of();
	private CoreSimulationSnapshot coreSnapshot;
	private final Map<String, SimulationSnapshot> received = new HashMap<>();

	public void startCapture(final String requestId, final Set<String> expectedContributorIds,
			final CoreSimulationSnapshot coreSnapshot) {
		this.activeRequestId = Objects.requireNonNull(requestId);
		this.expectedContributorIds = Set.copyOf(Objects.requireNonNull(expectedContributorIds));
		this.coreSnapshot = Objects.requireNonNull(coreSnapshot);
		this.received.clear();
	}

	public boolean isCaptureActive() {
		return this.activeRequestId != null;
	}

	public String getActiveRequestId() {
		return this.activeRequestId;
	}

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
