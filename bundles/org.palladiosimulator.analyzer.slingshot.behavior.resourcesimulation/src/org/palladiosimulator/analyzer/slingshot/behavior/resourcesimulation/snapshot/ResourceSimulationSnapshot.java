package org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.snapshot;

import org.palladiosimulator.analyzer.slingshot.common.snapshot.SimulationSnapshot;

/**
 * Snapshot of resource simulation state contributed by {@code ResourceSimulation}.
 *
 * <p>
 * Schema {@value #SCHEMA_VERSION} captures resource table sizes only (active, passive,
 * linking). Future schema versions will include job queues, processor-sharing internals,
 * and passive waiting queues.
 * </p>
 *
 * @see org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.ResourceSimulation
 */
public final class ResourceSimulationSnapshot implements SimulationSnapshot {

	/** Extension identifier for the resource simulation contributor. */
	public static final String EXTENSION_ID = "org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation";

	/** Schema version of this snapshot layout. */
	public static final String SCHEMA_VERSION = "1.0.0";

	private final int activeResourceCount;
	private final int passiveResourceCount;
	private final int linkingResourceCount;

	/**
	 * Creates a resource simulation snapshot with the given table sizes.
	 *
	 * @param activeResourceCount   number of active resources in the table
	 * @param passiveResourceCount  number of passive resources in the table
	 * @param linkingResourceCount  number of linking resources in the table
	 */
	public ResourceSimulationSnapshot(final int activeResourceCount, final int passiveResourceCount,
			final int linkingResourceCount) {
		this.activeResourceCount = activeResourceCount;
		this.passiveResourceCount = passiveResourceCount;
		this.linkingResourceCount = linkingResourceCount;
	}

	/**
	 * Returns the number of active resources at capture time.
	 *
	 * @return the active resource table size
	 */
	public int getActiveResourceCount() {
		return this.activeResourceCount;
	}

	/**
	 * Returns the number of passive resources at capture time.
	 *
	 * @return the passive resource table size
	 */
	public int getPassiveResourceCount() {
		return this.passiveResourceCount;
	}

	/**
	 * Returns the number of linking resources at capture time.
	 *
	 * @return the linking resource table size
	 */
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
