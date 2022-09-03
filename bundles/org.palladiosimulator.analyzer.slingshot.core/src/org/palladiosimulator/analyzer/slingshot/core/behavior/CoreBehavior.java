package org.palladiosimulator.analyzer.slingshot.core.behavior;

import javax.inject.Inject;

import org.palladiosimulator.analyzer.slingshot.core.api.SimulationDriver;
import org.palladiosimulator.analyzer.slingshot.core.events.SimulationFinished;
import org.palladiosimulator.analyzer.slingshot.core.extension.SimulationBehaviorExtension;
import org.palladiosimulator.analyzer.slingshot.core.extension.SystemBehaviorExtension;

import org.palladiosimulator.analyzer.slingshot.eventdriver.annotations.Subscribe;

public class CoreBehavior implements SimulationBehaviorExtension, SystemBehaviorExtension {
	
	private final SimulationDriver simulationDriver;
	
	@Inject
	public CoreBehavior(final SimulationDriver simulationDriver) {
		this.simulationDriver = simulationDriver;
	}

	@Subscribe
	public void onSimulationFinished(final SimulationFinished simulationFinished) {
		this.simulationDriver.stop();
	}
	
}
