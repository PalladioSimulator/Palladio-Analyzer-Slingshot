package org.palladiosimulator.analyzer.slingshot.core.api;

import org.palladiosimulator.analyzer.slingshot.core.events.DESEvent;
import org.palladiosimulator.analyzer.slingshot.core.events.SlingshotEvent;

public interface SimulationScheduling {
	
	public void scheduleEvent(final SlingshotEvent event);
	
	public void scheduleEventAt(final SlingshotEvent event, final double simulationTime);
	
}
