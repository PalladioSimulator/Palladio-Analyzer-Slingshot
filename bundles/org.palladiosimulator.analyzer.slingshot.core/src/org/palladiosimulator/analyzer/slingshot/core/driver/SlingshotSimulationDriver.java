package org.palladiosimulator.analyzer.slingshot.core.driver;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Singleton;

import org.eclipse.core.runtime.IProgressMonitor;
import org.palladiosimulator.analyzer.slingshot.common.events.DESEvent;
import org.palladiosimulator.analyzer.slingshot.core.annotations.SimulationBehaviorExtensions;
import org.palladiosimulator.analyzer.slingshot.core.api.SimulationDriver;
import org.palladiosimulator.analyzer.slingshot.core.api.SimulationEngine;
import org.palladiosimulator.analyzer.slingshot.core.behavior.CoreBehavior;
import org.palladiosimulator.analyzer.slingshot.core.events.PreSimulationConfigurationStarted;
import org.palladiosimulator.analyzer.slingshot.core.events.SimulationFinished;
import org.palladiosimulator.analyzer.slingshot.core.events.SimulationStarted;
import org.palladiosimulator.analyzer.slingshot.core.extension.SimulationBehaviorContainer;
import org.palladiosimulator.analyzer.slingshot.core.extension.SimulationBehaviorExtension;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Provides;

import de.uka.ipd.sdq.simucomframework.SimuComConfig;

@Singleton
public class SlingshotSimulationDriver implements SimulationDriver {

	private boolean running = false;
	private boolean initialized = false;

	private final SimulationEngine engine;
	private final Injector parentInjector;

	private final List<SimulationBehaviorContainer> behaviorContainers;

	private IProgressMonitor monitor;
	private SimuComConfig config;

	private Injector childInjector;

	@Inject
	public SlingshotSimulationDriver(final SimulationEngine engine,
			final Injector injector,
			@SimulationBehaviorExtensions final List<SimulationBehaviorContainer> behaviorContainers) {
		this.engine = engine;
		this.parentInjector = injector;
		this.behaviorContainers = behaviorContainers;
	}

	@Override
	public void init(final SimuComConfig config, final IProgressMonitor monitor) {
		if (!this.initialized) {

			final List<Module> partitionIncludedStream = new ArrayList<>(behaviorContainers.size() + 1);
			partitionIncludedStream.add(new SimulationDriverSubModule(monitor, config));
			partitionIncludedStream.addAll(behaviorContainers);

			childInjector = this.parentInjector.createChildInjector(partitionIncludedStream);
		}

		this.monitor = monitor;
		this.config = config;

		behaviorContainers.stream()
			.flatMap(extensions -> extensions.getExtensions().stream())
			.forEach(simExtension -> {
				final Object e = childInjector.getInstance(simExtension);
				if (!(e instanceof SimulationBehaviorExtension)) {
					return;
				}
				engine.registerEventListener((SimulationBehaviorExtension) e);
			});

		engine.registerEventListener(new CoreBehavior(this));
		this.initialized = true;
	}

	@Override
	public void start() {
		if (this.isRunning()  || !this.initialized) {
			return;
		}

		this.running = true;

		this.engine.init();
		this.scheduleEvent(new PreSimulationConfigurationStarted());
		this.scheduleEvent(new SimulationStarted());
		this.scheduleEventAt(new SimulationFinished(), config.getSimuTime());
		this.engine.start();
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
	public void scheduleEvent(final DESEvent event) {
		if (!this.isRunning()) {
			return;
		}

		this.engine.scheduleEvent(event);
	}

	@Override
	public void scheduleEventAt(final DESEvent event, final double simulationTime) {
		if (!this.isRunning()) {
			return;
		}

		this.engine.scheduleEventAt(event, simulationTime);
	}

	private static class SimulationDriverSubModule extends AbstractModule {

		private final IProgressMonitor monitor;
		private final SimuComConfig config;

		public SimulationDriverSubModule(final IProgressMonitor monitor,
										 final SimuComConfig config) {
		//	this.partition = partition;
			this.monitor = monitor;
			this.config = config;
		}


		@Provides
		public IProgressMonitor monitor() {
			return this.monitor;
		}

		//@Provides
		//public SimuComConfig config() {
		//	return this.config;
		//}
	}
}
