package org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.snapshot;

import org.palladiosimulator.analyzer.slingshot.common.snapshot.SimulationSnapshot;

/**
 * Resource simulation state snapshot. Schema 1.0.0 captures table sizes only.
 */
public final class ResourceSimulationSnapshot implements SimulationSnapshot {

	public static final String EXTENSION_ID = "org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation";
	public static final String SCHEMA_VERSION = "1.0.0";

	private final int activeResourceCount;
	private final int passiveResourceCount;
	private final int linkingResourceCount;

	public ResourceSimulationSnapshot(final int activeResourceCount, final int passiveResourceCount,
			final int linkingResourceCount) {
		this.activeResourceCount = activeResourceCount;
		this.passiveResourceCount = passiveResourceCount;
		this.linkingResourceCount = linkingResourceCount;
	}

	public int getActiveResourceCount() {
		return this.activeResourceCount;
	}

	public int getPassiveResourceCount() {
		return this.passiveResourceCount;
	}

	public int getLinkingResourceCount() {
		return this.linkingResourceCount;
	}

	@Override
	public String extensionId() {
		return EXTENSION_ID;
	}

	@Override
	public String schemaVersion() {
		return SCHEMA_VERSION;
	}

}
