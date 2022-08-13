package org.palladiosimulator.analyzer.slingshot.core;

import java.util.Objects;

import org.palladiosimulator.analyzer.slingshot.core.api.SimulationDriver;
import org.palladiosimulator.analyzer.slingshot.core.api.SimulationEngine;
import org.palladiosimulator.analyzer.slingshot.core.driver.SlingshotSimulationDriver;
import org.palladiosimulator.analyzer.slingshot.core.engine.SimulationEngineSSJ;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

/**
 * This is the central class where all the Slingshot modules are defined, and where
 * the initial {@link Injector} is defined.
 * 
 * @author Julijan Katic
 *
 */
public class SlingshotSystem extends AbstractModule {

	@Override
	protected void configure() {
		bind(SimulationDriver.class).to(SlingshotSimulationDriver.class);
		bind(SimulationEngine.class).to(SimulationEngineSSJ.class);
	}
	
}
