package org.palladiosimulator.analyzer.slingshot.core.snapshot;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.palladiosimulator.analyzer.slingshot.common.snapshot.CompositeSimulationSnapshot;
import org.palladiosimulator.analyzer.slingshot.common.snapshot.CoreSimulationSnapshot;
import org.palladiosimulator.analyzer.slingshot.common.snapshot.SimulationSnapshot;
import org.palladiosimulator.analyzer.slingshot.core.exceptions.SimulationStateIncompatibleException;
import org.palladiosimulator.analyzer.slingshot.core.extension.SnapshotCapableExtension;

class DefaultSimulationStateValidatorTest {

	private static final String EXTENSION_ID = "test.extension";

	private final DefaultSimulationStateValidator validator = new DefaultSimulationStateValidator();

	@Test
	void acceptsMatchingContributorSet() {
		final SnapshotContributorRegistry registry = new SnapshotContributorRegistry();
		registry.register(stubExtension(true));

		final CompositeSimulationSnapshot snapshot = snapshotWithContributor(EXTENSION_ID);

		assertDoesNotThrow(() -> this.validator.validate(snapshot, registry));
	}

	@Test
	void rejectsMismatchedContributorSet() {
		final SnapshotContributorRegistry registry = new SnapshotContributorRegistry();
		registry.register(stubExtension(true));

		final CompositeSimulationSnapshot snapshot = snapshotWithContributor("other.extension");

		assertThrows(SimulationStateIncompatibleException.class, () -> this.validator.validate(snapshot, registry));
	}

	private static CompositeSimulationSnapshot snapshotWithContributor(final String extensionId) {
		final SimulationSnapshot contributor = new SimulationSnapshot() {

			@Override
			public String extensionId() {
				return extensionId;
			}

			@Override
			public String schemaVersion() {
				return "1.0.0";
			}

		};
		return new CompositeSimulationSnapshot(new CoreSimulationSnapshot(0.0, 0),
				Map.of(extensionId, contributor));
	}

	private static SnapshotCapableExtension stubExtension(final boolean active) {
		return new SnapshotCapableExtension() {

			@Override
			public String getExtensionId() {
				return EXTENSION_ID;
			}

			@Override
			public Class<? extends SimulationSnapshot> snapshotType() {
				return SimulationSnapshot.class;
			}

			@Override
			public boolean isActive() {
				return active;
			}

		};
	}

}
