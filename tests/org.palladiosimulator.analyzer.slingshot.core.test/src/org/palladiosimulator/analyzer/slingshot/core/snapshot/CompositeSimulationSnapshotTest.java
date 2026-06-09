package org.palladiosimulator.analyzer.slingshot.core.snapshot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.palladiosimulator.analyzer.slingshot.common.snapshot.CompositeSimulationSnapshot;
import org.palladiosimulator.analyzer.slingshot.common.snapshot.CoreSimulationSnapshot;
import org.palladiosimulator.analyzer.slingshot.common.snapshot.SimulationSnapshot;

class CompositeSimulationSnapshotTest {

	private static final class StubSnapshot implements SimulationSnapshot {

		private final String extensionId;

		private StubSnapshot(final String extensionId) {
			this.extensionId = extensionId;
		}

		@Override
		public String extensionId() {
			return this.extensionId;
		}

		@Override
		public String schemaVersion() {
			return "1.0.0";
		}

	}

	@Test
	void assemblesCoreAndContributorSnapshots() {
		final CoreSimulationSnapshot core = new CoreSimulationSnapshot(42.0, 7);
		final StubSnapshot contributor = new StubSnapshot("test.extension");
		final CompositeSimulationSnapshot composite = new CompositeSimulationSnapshot(core,
				Map.of(contributor.extensionId(), contributor));

		assertEquals(42.0, composite.getCore().getSimulationTime());
		assertEquals(1, composite.contributorIds().size());
		assertTrue(composite.get(StubSnapshot.class).isPresent());
		assertEquals("test.extension", composite.getByExtensionId("test.extension").map(SimulationSnapshot::extensionId)
				.orElseThrow());
	}

}
