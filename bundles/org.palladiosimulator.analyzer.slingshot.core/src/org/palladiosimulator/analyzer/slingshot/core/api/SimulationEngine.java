package org.palladiosimulator.analyzer.slingshot.core.api;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.palladiosimulator.analyzer.slingshot.common.events.DESEvent;
import org.palladiosimulator.analyzer.slingshot.common.events.SlingshotEvent;
import org.palladiosimulator.analyzer.slingshot.core.extension.SimulationBehaviorExtension;
import org.palladiosimulator.analyzer.slingshot.eventdriver.returntypes.Result;

/**
 * An abstraction for an engine (for example, SSJ) that handles the events
 * within the simulation.
 * 
 * @author Julijan Katic
 */
public interface SimulationEngine {
	
	public void init();
	
	public SimulationInformation getSimulationInformation();
	
	public void start();
	
	public void stop();
	
	public boolean isRunning();
	
	public void schedule(final DESEvent event, final Consumer<DESEvent> onDispatch);
	
}
