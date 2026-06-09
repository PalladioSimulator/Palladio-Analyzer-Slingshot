package org.palladiosimulator.analyzer.slingshot.core.snapshot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.Test;
import org.palladiosimulator.analyzer.slingshot.common.snapshot.CompositeSimulationSnapshot;
import org.palladiosimulator.analyzer.slingshot.common.snapshot.CoreSimulationSnapshot;
import org.palladiosimulator.analyzer.slingshot.common.snapshot.SimulationSnapshot;

class SnapshotCaptureCoordinatorTest {

	@Test
	void completesWhenAllContributorsArePresent() {
		final SnapshotCaptureCoordinator coordinator = new SnapshotCaptureCoordinator();
		final CoreSimulationSnapshot core = new CoreSimulationSnapshot(10.0, 3);
		coordinator.startCapture("request-1", Set.of("ext.a", "ext.b"), core);

		assertFalse(coordinator.addContributorSnapshot(stub("ext.a")).isPresent());

		final var composite = coordinator.addContributorSnapshot(stub("ext.b"));
		assertTrue(composite.isPresent());
		assertEquals(2, composite.get().contributorIds().size());
		assertFalse(coordinator.isCaptureActive());
	}

	private static SimulationSnapshot stub(final String extensionId) {
		return new SimulationSnapshot() {

			@Override
			public String extensionId() {
				return extensionId;
			}

			@Override
			public String schemaVersion() {
				return "1.0.0";
			}

		};
	}

}
