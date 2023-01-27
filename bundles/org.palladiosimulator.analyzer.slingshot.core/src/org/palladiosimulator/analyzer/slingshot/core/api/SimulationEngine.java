package org.palladiosimulator.analyzer.slingshot.core.api;

import java.util.function.Function;

import org.palladiosimulator.analyzer.slingshot.common.events.DESEvent;
import org.palladiosimulator.analyzer.slingshot.common.events.SlingshotEvent;
import org.palladiosimulator.analyzer.slingshot.core.extension.SimulationBehaviorExtension;
import org.palladiosimulator.analyzer.slingshot.eventdriver.returntypes.Result;

public interface SimulationEngine {
	
	public void init();
	
	public SimulationInformation getSimulationInformation();
	
	public void start();
	
	public void stop();
	
	public boolean isRunning();
	
	public void scheduleEvent(final DESEvent event);
	
	public void scheduleEventAt(final DESEvent event, final double simulationTime);
	
	public void registerEventListener(final SimulationBehaviorExtension guavaEventClass);
	
	public <T> void registerEventListener(final Class<T> forEvent, final Function<T, Result<?>> handler);


}
