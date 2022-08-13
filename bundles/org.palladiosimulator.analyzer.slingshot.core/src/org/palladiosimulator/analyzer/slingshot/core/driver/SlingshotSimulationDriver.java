package org.palladiosimulator.analyzer.slingshot.core.driver;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.palladiosimulator.analyzer.slingshot.core.Slingshot;
import org.palladiosimulator.analyzer.slingshot.core.api.SimulationDriver;
import org.palladiosimulator.analyzer.slingshot.core.api.SimulationEngine;
import org.palladiosimulator.analyzer.slingshot.core.events.DESEvent;
import org.palladiosimulator.analyzer.slingshot.core.events.SlingshotEvent;
import org.palladiosimulator.analyzer.slingshot.core.extension.AbstractSlingshotExtension;
import org.palladiosimulator.analyzer.slingshot.core.extension.BehaviorContainer;
import org.palladiosimulator.analyzer.slingshot.core.extension.ExtensionIds;
import org.palladiosimulator.commons.eclipseutils.ExtensionHelper;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Module;

public class SlingshotSimulationDriver implements SimulationDriver {
	
	private boolean running = false;
	private boolean initialized = false;
	
	private final SimulationEngine engine;
	private final Injector parentInjector;
	
	private final List<AbstractSlingshotExtension> extensions = Slingshot.getInstance().getExtensions();
	
	@Inject
	public SlingshotSimulationDriver(final SimulationEngine engine, final Injector injector) {
		this.engine = engine;
		this.parentInjector = injector;
	}
	
	public void init() {
		if (this.initialized) {
			return;
		}
		
		final List<BehaviorContainer> containers =	this.extensions.stream()
					   .map(extension -> new BehaviorContainer(extension))
					   .collect(Collectors.toList());
		
		final List<Module> finalModules = new ArrayList<>(this.extensions.size() + containers.size());
		this.extensions.forEach(finalModules::add);
		containers.forEach(finalModules::add);
		
		
		final Injector simulationChildInjector = this.parentInjector.createChildInjector(finalModules);
		
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
	}

	@Override
	public void stop() {
		if (!this.isRunning()) {
			return;
		}
		
		this.running = false;
	}

	@Override
	public boolean isRunning() {
		return this.running;
	}


	@Override
	public void scheduleEvent(SlingshotEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void scheduleEventAt(SlingshotEvent event, double simulationTime) {
		// TODO Auto-generated method stub
		
	}

}
