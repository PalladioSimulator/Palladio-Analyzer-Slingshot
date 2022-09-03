package org.palladiosimulator.analyzer.slingshot.core.driver;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.palladiosimulator.analyzer.slingshot.common.events.DESEvent;
import org.palladiosimulator.analyzer.slingshot.common.events.SlingshotEvent;
import org.palladiosimulator.analyzer.slingshot.core.Slingshot;
import org.palladiosimulator.analyzer.slingshot.core.annotations.BehaviorExtensions;
import org.palladiosimulator.analyzer.slingshot.core.api.SimulationDriver;
import org.palladiosimulator.analyzer.slingshot.core.api.SimulationEngine;
import org.palladiosimulator.analyzer.slingshot.core.events.SimulationStarted;
import org.palladiosimulator.analyzer.slingshot.core.extension.AbstractSlingshotExtension;
import org.palladiosimulator.analyzer.slingshot.core.extension.BehaviorContainer;
import org.palladiosimulator.analyzer.slingshot.core.extension.ExtensionIds;
import org.palladiosimulator.analyzer.slingshot.core.extension.SimulationBehaviorExtension;
import org.palladiosimulator.commons.eclipseutils.ExtensionHelper;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Module;

public class SlingshotSimulationDriver implements SimulationDriver {
	
	private boolean running = false;
	private boolean initialized = false;
	
	private final SimulationEngine engine;
	private final Injector parentInjector;
	private final List<BehaviorContainer> behaviorContainers;
	
	@Inject
	public SlingshotSimulationDriver(final SimulationEngine engine,
			final Injector injector,
			@BehaviorExtensions final List<BehaviorContainer> behaviorContainers) {
		this.engine = engine;
		this.parentInjector = injector;
		this.behaviorContainers = behaviorContainers;
	}
	
	public void init() {
		if (this.initialized) {
			return;
		}
		
		final Injector childInjector = this.parentInjector.createChildInjector(behaviorContainers);
		
		behaviorContainers.stream()
			.flatMap(extensions -> extensions.getExtensions().stream())
			.forEach(simExtension -> {
				final Object e = childInjector.getInstance(simExtension);
				if (!(e instanceof SimulationBehaviorExtension)) {
					return;
				}
				engine.registerEventListener((SimulationBehaviorExtension) e);
			});
		
		this.initialized = true;
	}

	@Override
	public void start() {
		if (this.isRunning()) {
			return;
		}
		
		this.running = true;
		
		this.engine.init();
		this.engine.start();
		this.scheduleEvent(new SimulationStarted());
	}

	@Override
	public void stop() {
		if (!this.isRunning()) {
			return;
		}
		
		this.running = false;
		this.engine.stop();
	}

	@Override
	public boolean isRunning() {
		return this.running;
	}


	@Override
	public void scheduleEvent(DESEvent event) {
		this.engine.scheduleEvent(event);
	}

	@Override
	public void scheduleEventAt(DESEvent event, double simulationTime) {
		this.engine.scheduleEventAt(event, simulationTime);
	}

}
