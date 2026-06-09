package org.palladiosimulator.analyzer.slingshot.common.snapshot;

/**
 * Core-owned simulation engine state.
 */
public final class CoreSimulationSnapshot implements SimulationSnapshot {

	public static final String EXTENSION_ID = "org.palladiosimulator.analyzer.slingshot.core";
	public static final String SCHEMA_VERSION = "1.0.0";

	private final double simulationTime;
	private final int consumedEvents;

	public CoreSimulationSnapshot(final double simulationTime, final int consumedEvents) {
		this.simulationTime = simulationTime;
		this.consumedEvents = consumedEvents;
	}

	public double getSimulationTime() {
		return this.simulationTime;
	}

	public int getConsumedEvents() {
		return this.consumedEvents;
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
