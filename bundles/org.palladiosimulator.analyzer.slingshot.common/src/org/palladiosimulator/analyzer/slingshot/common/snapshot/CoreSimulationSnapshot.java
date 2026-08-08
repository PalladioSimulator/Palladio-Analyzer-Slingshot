package org.palladiosimulator.analyzer.slingshot.common.snapshot;

/**
 * Core-owned simulation engine state captured at a point in time.
 *
 * <p>
 * Included in every {@link CompositeSimulationSnapshot}. Extension-specific state
 * is stored in separate {@link SimulationSnapshot} implementations.
 * </p>
 */
public final class CoreSimulationSnapshot implements SimulationSnapshot {

	/** Extension identifier for the core contributor. */
	public static final String EXTENSION_ID = "org.palladiosimulator.analyzer.slingshot.core";

	/** Schema version of the core snapshot layout. */
	public static final String SCHEMA_VERSION = "1.0.0";

	private final double simulationTime;
	private final int consumedEvents;

	/**
	 * Creates a core snapshot with the given engine metrics.
	 *
	 * @param simulationTime  the simulation time at capture
	 * @param consumedEvents  the number of DES events consumed at capture
	 */
	public CoreSimulationSnapshot(final double simulationTime, final int consumedEvents) {
		this.simulationTime = simulationTime;
		this.consumedEvents = consumedEvents;
	}

	/**
	 * Returns the simulation time at which this snapshot was captured.
	 *
	 * @return the simulation time
	 */
	public double getSimulationTime() {
		return this.simulationTime;
	}

	/**
	 * Returns the number of DES events consumed at capture time.
	 *
	 * @return the consumed event count
	 */
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
