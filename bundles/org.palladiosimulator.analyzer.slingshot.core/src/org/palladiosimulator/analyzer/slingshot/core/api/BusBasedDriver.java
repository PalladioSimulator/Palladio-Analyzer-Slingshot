package org.palladiosimulator.analyzer.slingshot.core.api;

import java.util.function.Function;

import org.palladiosimulator.analyzer.slingshot.core.extension.SimulationBehaviorExtension;
import org.palladiosimulator.analyzer.slingshot.eventdriver.returntypes.Result;

public interface BusBasedDriver {


	public void registerHandlers(final SimulationBehaviorExtension handlers);
	
	public <T> void registerHandler(
			final String id,
			final Class<T> forEvent, 
			final Function<T, Result<?>> handler);
	
}
