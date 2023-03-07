package org.palladiosimulator.analyzer.slingshot.core;

import org.palladiosimulator.analyzer.slingshot.core.api.SimulationDriver;
import org.palladiosimulator.analyzer.slingshot.core.api.SimulationEngine;
import org.palladiosimulator.analyzer.slingshot.core.api.SimulationScheduling;
import org.palladiosimulator.analyzer.slingshot.core.driver.SlingshotSimulationDriver;
import org.palladiosimulator.analyzer.slingshot.core.engine.SimulationEngineSSJ;

import com.google.inject.AbstractModule;

/**
 * Provides Simulation Run specific instances for simulation driver and engine.
 *
 * Explicit Bindings are necessary, or else the classes are bound in the parent.
 * Bound at parent injector is a problem, because the classes should be {link
 * {@link Singleton} for each Simulation run, not per Simulator instance.
 *
 * TODO : maybe we can pull some more injection related things from inside the simulation driver to up here.
 *
 * @author stiesssh
 *
 */
public class SimulationModule extends AbstractModule {

	@Override
	protected void configure() {
		// explicit bindings
		bind(SimulationEngineSSJ.class);
		bind(SlingshotSimulationDriver.class);

		// linked binding
		bind(SimulationDriver.class).to(SlingshotSimulationDriver.class);
		bind(SimulationScheduling.class).to(SlingshotSimulationDriver.class);
		bind(SimulationEngine.class).to(SimulationEngineSSJ.class);
	}
}
