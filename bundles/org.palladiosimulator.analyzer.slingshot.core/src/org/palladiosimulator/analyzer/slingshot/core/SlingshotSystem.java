package org.palladiosimulator.analyzer.slingshot.core;


import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.inject.Singleton;

import org.palladiosimulator.analyzer.slingshot.core.annotations.BehaviorExtensions;
import org.palladiosimulator.analyzer.slingshot.core.api.SimulationDriver;
import org.palladiosimulator.analyzer.slingshot.core.api.SimulationEngine;
import org.palladiosimulator.analyzer.slingshot.core.api.SystemDriver;
import org.palladiosimulator.analyzer.slingshot.core.driver.SlingshotSimulationDriver;
import org.palladiosimulator.analyzer.slingshot.core.driver.SlingshotSystemDriver;
import org.palladiosimulator.analyzer.slingshot.core.engine.SimulationEngineSSJ;
import org.palladiosimulator.analyzer.slingshot.core.extension.BehaviorContainer;

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

	private final List<BehaviorContainer> behaviors;
	
	public SlingshotSystem() {
		this.behaviors = Slingshot.getInstance().getExtensions().stream()
				.map(extension -> new BehaviorContainer(extension))
				.collect(Collectors.toList());
	}
	
	@Override
	protected void configure() {
		//bind(List.class).annotatedWith(BehaviorExtensions.class).toInstance(behaviors);
		bind(SimulationDriver.class).to(SlingshotSimulationDriver.class);
		bind(SimulationEngine.class).to(SimulationEngineSSJ.class);
		bind(SystemDriver.class).to(SlingshotSystemDriver.class);
	}
	
	@Singleton
	@Provides
	@BehaviorExtensions
	public List<BehaviorContainer> getBehaviors() {
		return this.behaviors;
	}
}
