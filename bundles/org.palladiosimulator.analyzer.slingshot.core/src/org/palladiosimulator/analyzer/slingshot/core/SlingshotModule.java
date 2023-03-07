package org.palladiosimulator.analyzer.slingshot.core;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Singleton;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.palladiosimulator.analyzer.slingshot.core.annotations.SimulationBehaviorExtensions;
import org.palladiosimulator.analyzer.slingshot.core.annotations.SystemBehaviorExtensions;
import org.palladiosimulator.analyzer.slingshot.core.api.SystemDriver;
import org.palladiosimulator.analyzer.slingshot.core.driver.SlingshotSystemDriver;
import org.palladiosimulator.analyzer.slingshot.core.extension.PCMResourceSetPartitionProvider;
import org.palladiosimulator.analyzer.slingshot.core.extension.SimulationBehaviorContainer;
import org.palladiosimulator.analyzer.slingshot.core.extension.SystemBehaviorContainer;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

/**
 * This is the central class where all the Slingshot modules are defined, and where
 * the initial {@link Injector} is defined.
 *
 * @author Julijan Katic
 *
 */
public class SlingshotModule extends AbstractModule {

	private final Logger LOGGER = LogManager.getLogger(SlingshotModule.class);

	private final List<SystemBehaviorContainer> systemContainers;
	private final List<SimulationBehaviorContainer> simulationContainers;

	public SlingshotModule() {
		this.systemContainers = Slingshot.getInstance().getSystemExtensions().stream()
				.map(extension -> new SystemBehaviorContainer(extension))
				.collect(Collectors.toList());
		this.simulationContainers = Slingshot.getInstance().getSimulationExtensions().stream()
				.map(SimulationBehaviorContainer::new)
				.collect(Collectors.toList());
	}

	@Override
	protected void configure() {
		bind(PCMResourceSetPartitionProvider.class);
		bind(SystemDriver.class).to(SlingshotSystemDriver.class);
	}

	@Singleton
	@Provides
	@SystemBehaviorExtensions
	public List<SystemBehaviorContainer> getSystemBehaviorContainers() {
		return this.systemContainers;
	}

	@Singleton
	@Provides
	@SimulationBehaviorExtensions
	public List<SimulationBehaviorContainer> getSimulationBehaviorContainer() {
		return this.simulationContainers;
	}
}
