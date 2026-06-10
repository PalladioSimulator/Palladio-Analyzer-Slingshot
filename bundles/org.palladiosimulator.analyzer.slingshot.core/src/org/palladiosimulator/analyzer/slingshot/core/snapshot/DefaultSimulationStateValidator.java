package org.palladiosimulator.analyzer.slingshot.core.snapshot;

import java.util.Set;

import org.palladiosimulator.analyzer.slingshot.common.snapshot.CompositeSimulationSnapshot;
import org.palladiosimulator.analyzer.slingshot.common.snapshot.SimulationContext;
import org.palladiosimulator.analyzer.slingshot.common.snapshot.SimulationSnapshot;
import org.palladiosimulator.analyzer.slingshot.core.exceptions.SimulationStateIncompatibleException;

/**
 * Default validator that checks contributor set equality, context compatibility, and schema version.
 *
 * <p>
 * In this iteration, schema compatibility requires a {@code 1.} major-version prefix on each
 * contributor snapshot. Stricter semver rules and upcasters may be added in future iterations.
 * </p>
 */
public final class DefaultSimulationStateValidator implements SimulationStateValidator {

	@Override
	public void validate(final CompositeSimulationSnapshot snapshot, final SnapshotContributorRegistry registry) {
		final Set<String> activeContributorIds = registry.getActiveContributorIds();
		final Set<String> snapshotContributorIds = snapshot.contributorIds();

		if (!snapshotContributorIds.equals(activeContributorIds)) {
			throw new SimulationStateIncompatibleException(String.format(
					"Snapshot contributors %s do not match active extensions %s.", snapshotContributorIds,
					activeContributorIds));
		}

		final SimulationContext context = new SimulationContext(activeContributorIds);
		for (final SimulationSnapshot contributor : snapshot.getContributors().values()) {
			if (!contributor.isCompatibleWith(context)) {
				throw new SimulationStateIncompatibleException(String.format(
						"Snapshot contributor %s is not compatible with the current simulation context.",
						contributor.extensionId()));
			}
			if (!contributor.schemaVersion().startsWith("1.")) {
				throw new SimulationStateIncompatibleException(String.format(
						"Unsupported snapshot schema version %s from contributor %s.", contributor.schemaVersion(),
						contributor.extensionId()));
			}
		}
	}

}
