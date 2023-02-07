package org.palladiosimulator.analyzer.slingshot.core.api;

import java.util.Objects;

import org.palladiosimulator.analyzer.slingshot.common.events.DESEvent;

public abstract class SimulationEngineEvent {

	/** 
	 * Tells to simulate at the next possible event. That is,
	 * the engine should simply put the event at the next
	 * possible time-frame after the delay.
	 */
	public static final double SIMULATE_NEXT = -1;
	
	private final DESEvent event;
	private final double simulateAt;
	private final double delay;
	
	protected SimulationEngineEvent(final DESEvent event) {
		this.event = Objects.requireNonNull(event);
		this.simulateAt = event.time();
		this.delay = event.delay();
	}
	
	/**
	 * A handler for the engine when an engine's event is happening.
	 */
	public abstract void onEvent();

	public DESEvent getEvent() {
		return event;
	}

	public double getSimulateAt() {
		return simulateAt;
	}

	public double getDelay() {
		return delay;
	}
	
	
}
